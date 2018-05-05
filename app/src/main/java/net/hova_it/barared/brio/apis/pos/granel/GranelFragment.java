package net.hova_it.barared.brio.apis.pos.granel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.pos.api.POSFragment;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;

import java.util.List;

import lat.brio.core.BrioGlobales;
import lat.brio.core.bussines.BrInventario;

/**
 * Fragment de grid de artículos granel.
 * Implementa un Loader, el cual permite cargar de forma asíncrona los productos
 * desde la base de datos, sin afectar el funcionamiento de los demás fragments del POS.
 *
 * Basado en esto:
 * http://inducesmile.com/android/android-gridlayoutmanager-with-recyclerview-in-material-design/
 *
 * Checar esto para optimizar:
 * http://wiresareobsolete.com/2014/09/building-a-recyclerview-layoutmanager-part-1/
 * http://antonioleiva.com/layout-animations-on-recyclerview/
 * wasabeef https://www.youtube.com/watch?v=MHqpR3yLNfk
 *
 * Created by Herman Peralta on 17/12/2015.
 */
public class GranelFragment extends POSFragment
        implements LoaderManager.LoaderCallbacks<List<GranelItem>> {

    public final static String KEY_LOG = GranelFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private View mPBar;
    private GranelAdapter mAdapter;

    private FragmentListButtonListener fragmentListButtonListener;
    
    private BrInventario brInventario;
    private String TAG = "GranelFragment";

    public static GranelFragment newInstance() {

        return new GranelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DebugLog.log(getClass(), "POSManager", "inicia granel");

        recoverArgs();

        setRetainInstance(true);

        View root = inflater.inflate(R.layout.pos_granel_fragment, container, false);
        loadBussines ();
        configureUI(root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        reload();
    }

    public void reload() {
        mRecyclerView.setVisibility(View.GONE);
        mPBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void recoverArgs() {

    }

    private void configureUI(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        mPBar = root.findViewById(R.id.granel_pb_cargando);

        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.setHasFixedSize(true);

        SimpleItemAnimator animator = new SimpleItemAnimator() {

            @Override
            public boolean animateAppearance(RecyclerView.ViewHolder holder, @Nullable ItemHolderInfo preLayoutInfo, ItemHolderInfo postLayoutInfo) {
                Log.d("ANIMATE", "animateAppearance");

                ViewCompat.animate(holder.itemView)
                        .translationY(holder.itemView.getHeight())
                        .alpha(0)
                        .setDuration(1000)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                dispatchAnimationFinished(holder);

                return true;
            }

            @Override
            public boolean animateRemove(RecyclerView.ViewHolder holder) {
                return false;
            }

            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                Log.d("ANIMATE", "animateAdd");
                return false;
            }

            @Override
            public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
                return false;
            }

            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
                return false;
            }

            @Override
            public void runPendingAnimations() {

            }

            @Override
            public void endAnimation(RecyclerView.ViewHolder item) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
        animator.setSupportsChangeAnimations(true);

        mRecyclerView.setItemAnimator(animator);

        mAdapter = new GranelAdapter(getActivity(), fragmentListButtonListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.GONE);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }
    
    public void loadBussines () {
        try {
            brInventario = BrInventario.getInstance (getActivity ().getApplicationContext (), BrioGlobales.getAccess ());
        } catch (Exception e) {
            BrioGlobales.writeLog (TAG, "LoadBussines", e.getMessage ());
        }
    }

    @Override
    public Loader<List<GranelItem>> onCreateLoader(int id, Bundle args) {
        //mPDialog = ProgressDialog.show(getActivity(), "", "Cargando, espere un momento", true);
        if(BrioBaseActivity.DEBUG_SHOW_TOASTS) { Toast.makeText(getActivity(), "Llenando adapter granel", Toast.LENGTH_SHORT).show(); }
        return new GranelListLoader(brInventario,getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<GranelItem>> loader, List<GranelItem> data) {
        mPBar.setVisibility(View.GONE);

        mAdapter.setData(data);

        mRecyclerView.setVisibility(View.VISIBLE);

        /*
        // The list should now be shown.
        if (isResumed()) {
            mRecyclerViewsetListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        */
    }

    @Override
    public void onLoaderReset(Loader<List<GranelItem>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }

    public void setFragmentListButtonListener(FragmentListButtonListener listener) {
        this.fragmentListButtonListener = listener;

        DebugLog.log(this.getClass(), "POSManager", "adapter null? " + (mAdapter == null));
        DebugLog.log(this.getClass(), "POSManager", "listener null? " + (fragmentListButtonListener == null));
    }

    public void setEnabled(final boolean isEnabled) {
        DebugLog.log(getClass(), "GRANEL", "habilito items? " + isEnabled);
        if(isEnabled) {
            mAdapter.setItemClickListener(fragmentListButtonListener);
        } else {
            mAdapter.setItemClickListener(null);
        }
    }
}
