package net.hova_it.barared.brio.apis.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import net.hova_it.barared.brio.GPSTracker;
import net.hova_it.barared.brio.apis.session.SessionManager;

/**
 * Clase para utilizar el gps
 * Created by Alejandro Gomez on 14/12/2015.
 */
public class TrackService {
    private SessionManager session;
    private final String sessionKey = "DATA_GPS";

    private Context context;
    private LocationManager locationManager;
    private boolean gpsEnabled = false;
    private String gpsLocation = "";
    private int updateTime = 1; // minutes

    public TrackService(Context context, SessionManager session) {
        this.session = session;
        this.context = context;
    }

    /**
     * Activa el gps, pide permisos y actualiza la fecha
     */
    public void start() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            // TODO
            gpsEnabled = false;
        }

        boolean hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;

        if (gpsEnabled) {
            Log.d("BARA_LOG", "GPS :: Buscando ubicación...");
            saveLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, updateTime * 10 * 1000, 1, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            saveLocation(location);
                        }

                        // Cambio la localizacion de el dispositivo
                        // BrioActivityMain Contexto = ((BrioActivityMain) context);

                        // BrioAlertDialog DlgActivarGPS = new BrioAlertDialog(Contexto, "", "");


                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }    // Cambio el estado de la localizacion

                        @Override
                        public void onProviderEnabled(String provider) {
                            // Se activo el proveedor de servicio de localizacion
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            // Se deshabilito el proveedor de el servicio de localizacion

                        }
                    });
        } else {
            Log.d("BARA_LOG", "GPS Deshabilitado!");
        }
    }

    /**
     * @param location guarda la ubicacion donde se encuentre
     */
    private void saveLocation(Location location) {

        GPSTracker gps = new GPSTracker(context);

        if(gps.canGetLocation()){
            gpsLocation = gps.Altitud() + "," + gps.Latitud() + "," + gps.Longitud() + "," + gps.Tiempo();
            session.saveString(sessionKey, gpsLocation);
        }else if (location != null) {
            gpsLocation = (location.getAltitude()) + "," + (location.getLatitude()) + "," + (location.getLongitude()) + "," + (location.getTime() / 1000);
            Log.d("BARA_LOG", gpsLocation);
        }
        session.saveString(sessionKey, gpsLocation);
    }

    private double round(double location) {
        int dp = 100000;
        return (double) Math.round(location * dp) / dp;
    }

}
