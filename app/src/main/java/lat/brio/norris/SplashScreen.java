package lat.brio.norris;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;

import brio.sdk.logger.Business.GlobalesLogger;
import brio.sdk.logger.DataAccess.AccesoArchivosGeneral;
import brio.sdk.logger.util.BrioUtilsFechas;
import lat.brio.core.BrioBussines.BrioBusinessRules;
import lat.brio.core.BrioGlobales;
import lat.brio.core.DataBussines.DataBaseBusiness;
import lat.brio.core.REQUEST;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static lat.brio.core.BrioGlobales.version;


/**
 * Created by Delgadillo on 10/04/17.
 */
public class SplashScreen extends AppCompatActivity {
    
    private String TAG = this.getClass ().getSimpleName ();
    
    private static int SPLASH_TIME_OUT = 500;
    public String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public String CAMERA = Manifest.permission.CAMERA;
    public String MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public int REQUEST_CODE_PERMISSIONS = 1;
    
    @Override
    protected void onCreate (Bundle Instancia) {
        super.onCreate (Instancia);
        // ESTA ES LA PRIMERA LIBERACIÓN DE NFE a 45 de brio por Hova
        
        
        toggleHideyBar ();
        
        setContentView (R.layout.brio_activity_splashscreen);
        initPermisos ();
        
        
        // Norris ahora envuelve a la actividad main de "Brio" by Hova IT
        
    }
    
    private void initPermisos () {
        
        try {
            
            if (ActivityCompat.checkSelfPermission (this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission (this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission (this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission (this, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission (this, MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission (this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    ) {
                ActivityCompat.requestPermissions (this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.RECORD_AUDIO,
                    
                }, REQUEST.REQUEST_CODE_PERMISSIONS);
            } else {
                execute (true);
            }
            
        } catch (Exception e) {
            Log.e (TAG, "Error: " + e.getMessage ());
        }
    }
    
    private void execute (boolean delayed) {
    
    
//        Reglas para iniciar las constantes, crear la BD , crear carpetas y nombrado archivo log
        BrioBusinessRules brioBusinessRules= BrioBusinessRules.getInstance (this,BrioGlobales.getAccess ());
        brioBusinessRules.inicializar ();
        
        
        if (delayed) {
            new Handler ().postDelayed (new Runnable () {
                @Override
                public void run () {
                    startActivityMain ();
                }
            }, SPLASH_TIME_OUT);
        } else {
            startActivityMain ();
        }
        
    }
 
    
    private void startActivityMain () {
        startActivity (new Intent (SplashScreen.this, BrioActivityMain.class));
        finish ();
    }
    
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        
        switch (requestCode) {
            
            case REQUEST.REQUEST_CODE_PERMISSIONS:
                // Inicializar Tracker GPS
                if (ActivityCompat.checkSelfPermission (this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission (this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission (this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ) {
                    
                    execute (false);
                }
                
                break;
        }
    }
    
    /**
     * Detects and toggles immersive mode.
     */
    public void toggleHideyBar () {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow ().getDecorView ().getSystemUiVisibility ();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i (TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i (TAG, "Turning immersive mode mode on.");
        }
        
        // Immersive mode: Backward compatible to KitKat (API 19).
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // This sample uses the "sticky" form of immersive mode, which will let the user swipe
        // the bars back in again, but will automatically make them disappear a few seconds later.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow ().getDecorView ().setSystemUiVisibility (newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
