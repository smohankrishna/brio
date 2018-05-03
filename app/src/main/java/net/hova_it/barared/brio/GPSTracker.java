package net.hova_it.barared.brio;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double altitud; // altitud
    double latitude; // latitud
    double longitude; // longitud
    double tiempo; // tiempo

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            altitud = location.getAltitude();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            tiempo = location.getTime() / 1000;
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                altitud = location.getAltitude();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                tiempo = location.getTime() / 1000;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double Altitud(){
        if(location != null){
            altitud = location.getAltitude();
        }

        // return latitude
        return altitud;
    }
    /**
     * Function to get latitude
     * */
    public double Latitud(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double Longitud(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public double Tiempo(){
        if(location != null){
            tiempo = location.getTime() / 1000;
        }

        // return longitude
        return tiempo;
    }



    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
/**
 * Created by Delgadillo on 15/05/17.
 *

public class GPSTracker extends Service implements LocationListener {


    private final Context _Contexto;
    private Boolean _Habilitado = false;
    private Integer _Precision = 2; // Metros
    private static final Integer _Actualizacion = 1000 * 10 * 1; // Milisegundos
    private Location _Localizacion = null;
    protected LocationManager _Localizador;

    private double _Alt; // Altitud
    private double _Lat; // Latitud
    private double _Lon; // Longitud

    public GPSTracker(Context Contexto) {
        this._Contexto = Contexto;
        Localizacion();
    }

    public Location Localizacion() {
        Reintentar:
        try {
            _Localizador = (LocationManager) _Contexto.getSystemService(LOCATION_SERVICE);
            _Habilitado = _Localizador.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Si esta deshabilitado, habilitarlo y reintentar
            if (!_Habilitado) {
                Utils.HabilitarGPS(this);
                break Reintentar;
            } else {
                // Validar permisos
                Boolean permisos = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

                if (_Localizacion == null) {
                    _Localizador.requestLocationUpdates(LocationManager.GPS_PROVIDER, _Actualizacion, _Precision, this);
                    if (_Localizador != null) {
                        _Localizacion = _Localizador.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (_Localizacion != null) {
                            _Alt = _Localizacion.getAltitude();
                            _Lat = _Localizacion.getLatitude();
                            _Lon = _Localizacion.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            break Reintentar;
        }
        return _Localizacion;
    }


    // Detener el Tracker GPS
    public void Detener(){
        if(_Localizador != null){
            _Localizador.removeUpdates(GPSTracker.this);
        }
    }

    // Altitud
    public double Altitud(){
        if(_Localizador != null){
            _Alt = _Localizacion.getAltitude();
        }

        return _Alt;
    }

    // Latitud
    public double Latitud(){
        if(_Localizador != null){
            _Lat = _Localizacion.getLatitude();
        }

        return _Lat;
    }


    // Longitud
    public double getLongitude(){
        if(_Localizacion != null){
            _Lon = _Localizacion.getLongitude();
        }

        return _Lon;
    }

    // Esta Habilitado el GPS
    public boolean Habilitado() {
        return this._Habilitado;
    }



    @Override
    public void onLocationChanged(Location Localizacion) {
        if(Localizacion != null){
            _Alt = Localizacion.getAltitude();
            _Lat = Localizacion.getLatitude();
            _Lon = Localizacion.getLongitude();
        }

        Toast.makeText(_Contexto, "La localización cambio: (ALT:" +  LAT:" + _Lat + " LON:" +  _Lon + ");", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Utils.HabilitarGPS(_Contexto);
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}*/