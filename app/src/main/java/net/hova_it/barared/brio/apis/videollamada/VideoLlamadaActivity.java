package net.hova_it.barared.brio.apis.videollamada;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;

import java.util.ArrayList;

/**
 * This application demonstrates the basic workflow for getting started with the
 * OpenTok 2.0 Android SDK. For more information, see the README.md file in the
 * samples directory.
 */
public class VideoLlamadaActivity extends AppCompatActivity implements
        Session.SessionListener, Publisher.PublisherListener,
        Subscriber.VideoListener {

    private static final String LOGTAG = "demo-hello-world";
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private ArrayList<Stream> mStreams;
    private Handler mHandler = new Handler();

    private RelativeLayout mPublisherViewContainer;
    private RelativeLayout mSubscriberViewContainer;

    // Spinning wheel for loading subscriber view
    private ProgressBar mLoadingSub;

    private boolean resumeHasRun = false;

    private boolean mIsBound = false;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;
    private ServiceConnection mConnection;

    private String API_KEY, SESSION_ID, TOKEN;
    private boolean SUBSCRIBE_TO_SELF = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "ONCREATE");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.videollamada_activity);

        if(!getVideoLlamadaParams()) {
            finish();
        }

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);
        mSubscriberViewContainer = (RelativeLayout) findViewById(R.id.subscriberview);
        mLoadingSub = (ProgressBar) findViewById(R.id.loadingSpinner);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mStreams = new ArrayList<Stream>();

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mSession != null) {
                        mSession.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        });

        sessionConnect();
    }

    private boolean getVideoLlamadaParams() {
        Intent intent = getIntent();
        TOKEN = intent.getStringExtra("Token");
        SESSION_ID = intent.getStringExtra("sessionId");

        API_KEY = Utils.getString(R.string.videollamada_api_key, this);

        DebugLog.log(getClass(), "Videollamada", "\nTOKEN='" + TOKEN + "'\nSESSION_ID='" + SESSION_ID + "'\nAPI_KEY='" + API_KEY + "'");
    
        return TOKEN != null && SESSION_ID != null;
    
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSession != null) {
            mSession.onPause();

            if (mSubscriber != null) {
                mSubscriberViewContainer.removeView(mSubscriber.getView());
            }
        }

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(this.getTitle())
                .setContentText("Notificacion")
                .setSmallIcon(R.drawable.app_ic_launcher).setOngoing(true);

        Intent notificationIntent = new Intent(this, VideoLlamadaActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mNotifyBuilder.setContentIntent(intent);
        if (mConnection == null) {
            mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className, IBinder binder) {
                    ((ClearNotificationService.ClearBinder) binder).service.startService(new Intent(VideoLlamadaActivity.this, ClearNotificationService.class));
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.notify(ClearNotificationService.NOTIFICATION_ID, mNotifyBuilder.build());
                }

                @Override
                public void onServiceDisconnected(ComponentName className) {
                    mConnection = null;
                }

            };
        }

        if (!mIsBound) {
            bindService(new Intent(VideoLlamadaActivity.this,
                            ClearNotificationService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
            mIsBound = true;
            startService(notificationIntent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }

        if (!resumeHasRun) {
            resumeHasRun = true;
            return;
        } else {
            if (mSession != null) {
                mSession.onResume();
            }
        }
        mNotificationManager.cancel(ClearNotificationService.NOTIFICATION_ID);

        reloadInterface();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }

        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        if (isFinishing()) {
            mNotificationManager.cancel(ClearNotificationService.NOTIFICATION_ID);
            if (mSession != null) {
                mSession.disconnect();
            }
        }
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(ClearNotificationService.NOTIFICATION_ID);
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }

        if (mSession != null) {
            mSession.disconnect();
        }

        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mSession != null) {
            mSession.disconnect();
        }

        super.onBackPressed();
    }

    public void reloadInterface() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSubscriber != null) {
                    attachSubscriberView(mSubscriber);
                }
            }
        }, 500);
    }

    private void sessionConnect() {
        if (mSession == null) {
            mSession = new Session(VideoLlamadaActivity.this, API_KEY, SESSION_ID);
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        }
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOGTAG, "Connected to the session.");
        if (mPublisher == null) {
            mPublisher = new Publisher(VideoLlamadaActivity.this, "publisher");
            mPublisher.setPublisherListener(this);
            attachPublisherView(mPublisher);
            mSession.publish(mPublisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOGTAG, "Disconnected from the session.");
        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
        }

        if (mSubscriber != null) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
        }

        mPublisher = null;
        mSubscriber = null;
        mStreams.clear();
        mSession = null;
    }

    private void subscribeToStream(Stream stream) {
        mSubscriber = new Subscriber(VideoLlamadaActivity.this, stream);
        mSubscriber.setVideoListener(this);
        mSession.subscribe(mSubscriber);

        if (mSubscriber.getSubscribeToVideo()) {
            // start loading spinning
            mLoadingSub.setVisibility(View.VISIBLE);
        }
    }

    private void unsubscribeFromStream(Stream stream) {
        mStreams.remove(stream);
        if (mSubscriber.getStream().equals(stream)) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
            mSubscriber = null;
            if (!mStreams.isEmpty()) {
                subscribeToStream(mStreams.get(0));
            }
        }
    }

    private void attachSubscriberView(Subscriber subscriber) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels, getResources()
                .getDisplayMetrics().heightPixels);
        mSubscriberViewContainer.removeView(mSubscriber.getView());
        mSubscriberViewContainer.addView(mSubscriber.getView(), layoutParams);
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
    }

    private void attachPublisherView(Publisher publisher) {
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                320, 240);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                RelativeLayout.TRUE);
        layoutParams.bottomMargin = dpToPx(8);
        layoutParams.rightMargin = dpToPx(8);
        mPublisherViewContainer.addView(mPublisher.getView(), layoutParams);
    }

    @Override
    public void onError(Session session, OpentokError exception) {
        Log.i(LOGTAG, "Session exception: " + exception.getMessage());
        //Toast.makeText(this, "Session exception: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        BrioAlertDialog bad = new BrioAlertDialog(this, "Videollamada", "Error al iniciar sesión");
        bad.show();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        if (!SUBSCRIBE_TO_SELF) {
            mStreams.add(stream);
            if (mSubscriber == null) {
                subscribeToStream(stream);
            }
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (!SUBSCRIBE_TO_SELF) {
            if (mSubscriber != null) {
                unsubscribeFromStream(stream);
            }
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisher, Stream stream) {
        if (SUBSCRIBE_TO_SELF) {
            mStreams.add(stream);
            if (mSubscriber == null) {
                subscribeToStream(stream);
            }
        }
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
        if ((SUBSCRIBE_TO_SELF && mSubscriber != null)) {
            unsubscribeFromStream(stream);
        }
    }

    @Override
    public void onError(PublisherKit publisher, OpentokError exception) {
        Log.i(LOGTAG, "Publisher exception: " + exception.getMessage());
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriber) {
        Log.i(LOGTAG, "First frame received");

        // stop loading spinning
        mLoadingSub.setVisibility(View.GONE);
        attachSubscriberView(mSubscriber);
    }

    /**
     * Converts dp to real pixels, according to the screen density.
     *
     * @param dp A number of density-independent pixels.
     * @return The equivalent number of real pixels.
     */
    private int dpToPx(int dp) {
        double screenDensity = this.getResources().getDisplayMetrics().density;
        return (int) (screenDensity * (double) dp);
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriber, String reason) {
        Log.i(LOGTAG, "Video disabled:" + reason);
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriber, String reason) {
        Log.i(LOGTAG, "Video enabled:" + reason);
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriber) {
        Log.i(LOGTAG, "Video may be disabled soon due to network quality degradation. Add UI handling here.");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriber) {
        Log.i(LOGTAG, "Video may no longer be disabled as stream quality improved. Add UI handling here.");
    }

}
