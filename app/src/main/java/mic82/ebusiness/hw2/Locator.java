package mic82.ebusiness.hw2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by chiming on 16/2/23.
 *
 * reference: https://gist.github.com/emil2k/5381596
 *
 * doesn't really work on the latest sdk because google changed the check permission process
 * now have to check permission manually
 *
 * And google changed location service apis so currently have to wait for better google updates
 *
 */

public class Locator implements LocationListener {

    static private final String LOG_TAG = "locator";

    static private final int TIME_INTERVAL = 100; // minimum time between updates in milliseconds
    static private final int DISTANCE_INTERVAL = 1; // minimum distance between updates in meters

    static public enum Method {
        NETWORK,
        GPS,
        NETWORK_THEN_GPS
    }

    private Context context;
    private LocationManager locationManager;
    private Locator.Method method;
    private Locator.Listener callback;

    public Locator(Context context) {
        super();
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getLocation(Locator.Method method, Locator.Listener callback) {
        this.method = method;
        this.callback = callback;
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        switch (this.method) {
            case NETWORK:
            case NETWORK_THEN_GPS:
                Location networkLocation = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (networkLocation != null) {
                    Log.d(LOG_TAG, "Last known location found for network provider : " + networkLocation.toString());
                    this.callback.onLocationFound(networkLocation);
                } else {
                    Log.d(LOG_TAG, "Request updates from network provider.");
                    this.requestUpdates(LocationManager.NETWORK_PROVIDER);
                }
                break;
            case GPS:
                Location gpsLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    Log.d(LOG_TAG, "Last known location found for GPS provider : " + gpsLocation.toString());
                    this.callback.onLocationFound(gpsLocation);
                } else {
                    Log.d(LOG_TAG, "Request updates from GPS provider.");
                    this.requestUpdates(LocationManager.GPS_PROVIDER);
                }
                break;
        }
    }

    private void requestUpdates(String provider) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        if (this.locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(LOG_TAG, "Network connected, start listening : " + provider);
                this.locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)) {
                Log.d(LOG_TAG, "Mobile network connected, start listening : " + provider);
                this.locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else {
                Log.d(LOG_TAG, "Proper network not connected for provider : " + provider);
                this.onProviderDisabled(provider);
            }
        } else {
            this.onProviderDisabled(provider);
        }
    }

    public void cancel() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        Log.d(LOG_TAG, "Locating canceled.");
        this.locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        Log.d(LOG_TAG, "Location found : " + location.getLatitude() + ", " + location.getLongitude() + (location.hasAccuracy() ? " : +- " + location.getAccuracy() + " meters" : ""));
        this.locationManager.removeUpdates(this);
        this.callback.onLocationFound(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        Log.d(LOG_TAG, "Provider disabled : " + provider);
        if (this.method == Locator.Method.NETWORK_THEN_GPS
                && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            // Network provider disabled, try GPS
            Log.d(LOG_TAG, "Requesst updates from GPS provider, network provider disabled.");
            this.requestUpdates(LocationManager.GPS_PROVIDER);
        } else {
            this.locationManager.removeUpdates(this);
            this.callback.onLocationNotFound();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "Provider enabled : " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOG_TAG, "Provided status changed : " + provider + " : status : " + status);
    }

    public interface Listener {
        void onLocationFound(Location location);

        void onLocationNotFound();
    }

}