package net.hova_it.barared.brio.apis.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import java.io.File;

/**
 * En esta clase estan metodos de diferentes utilidades para pos
 */
public class MediaUtils {
    private static MediaPlayer player;

    /**
     * Se ocupaba para el sonido del pos pero por el momento se deshabilito
     * @param ctx contexto de clase
     */
    public static void playCash(Context ctx) {
        playAsset(ctx, false, "pos_cash.mp3");
    }

    public static void playScannerBeep(Context ctx) {
        //playAsset(ctx, false, "pos_scanner_beep.wav");
    }

    private static void playAsset(Context ctx, boolean loop, String soundFileNameInAssets) {
        try {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            player = new MediaPlayer();
            player.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            player.release();
                        }
                    }

            );

            AssetFileDescriptor descriptor = ctx.getAssets().openFd(soundFileNameInAssets);
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            player.prepare();
            player.setVolume(1f, 1f);
            player.setLooping(loop);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Regresa la carpeta de imagenes con el separador
     *
     * @return
     */
    public static String getBrioImagenesPath(){
        return Utils.getBrioPath() + File.separator + "Imagenes" + File.separator;
    }

    /**
     * Obtener una imagen de file system
     * se debe agregar el siguiente permiso para que funcione:
     *
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     *
     * Funciona con: JPG, PNG
     *
     * El uso es:
     *
     * Bitmap img = getImageFromFS("path to image");
     * if(img != null) { myImageView.setImageBitmap(myBitmap); }
     *
     * Con android.os.Environment.getExternalStorageDirectory() se puede obtener la raiz de la memoria externa
     *
     * Created by Herman Peralta
     *
     * @param fullpath - la ruta completa de la imagen (incluye el nombre y extensión)
     * @return Bitmap si pudo leer la imagen o null en caso contrario.
     */
    public static Bitmap getImageFromFS(String fullpath) {
        File img = new  File(fullpath);
        if(img.exists()) {
            return BitmapFactory.decodeFile(img.getAbsolutePath());
        }
        return null;
    }

    /**
     * Metodo para poner el background de un dialog con transparencia
     * @param dialogFragment al dialog se le pone la transparencia
     */
    public static void setDialogTransparentBackground(DialogFragment dialogFragment) {
        dialogFragment.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    /**
     * <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
     * @param context contexto de la clase
     * @return
     */
    public static boolean audifonosConectados(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.isWiredHeadsetOn();
    }

    /**
     * Ocultar la barra de sistema inferior de Android
     * http://stackoverflow.com/questions/18512688/full-screen-application-android
     *
     * @param activity
     */
    public static void hideSystemUI(AppCompatActivity activity) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    /**
     * This snippet shows the system bars. It does this by removing all the flags
     * except for the ones that make the content appear under the system bars.
     * @param activity
     */
    public static void showSystemUI(AppCompatActivity activity) {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    /**
     * Se ocupa para enviar el ticket por correo electronico
     * @param webView vista en la que se manda el ticket
     * @param html para darle el formato al ticket
     */
    public static void loadStringInWebView(WebView webView, String html) {
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

}
