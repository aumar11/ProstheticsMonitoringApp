package com.prosthetics;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.prosthetics.PatientDB;
import com.prosthetics.LocationWrapper;

/**
 * Listens for location updates from {@link android.location.LocationManager}.
 * @author jbanford, based on prior work by
 * @author Jakub Konka.
 * @version 1.0
 */
public class LocationReceiver implements LocationListener
{
  /** Tag for Log statements in this class. */
  public static final String TAG = "LocationReceiver";
  private static final boolean D = true;

  /** Tag for location provider type. */
  private static final String EXTRAS_KEY = "networkLocationType";

  public static final String LOCATION_ACTION = "com.prosthetics.LocationAction";

  public static final String LOCATION_DATA = "new_location_data";

  Intent intent;

  private Context mContext;

  /**
   * Constructs an object of type {@code LocationReceiver}.
   * @param context {@code Context}
   */
  public LocationReceiver(Context context)
  {
    mContext = context;
  }

  /**
   * Called when the location has changed.
   * @param location The new location, as a Location object.
   */
  public void onLocationChanged(Location location)
  {
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    Bundle extras = location.getExtras();
    String provider = (extras == null || extras.getString(EXTRAS_KEY) == null)
                    ? location.getProvider() : extras.getString(EXTRAS_KEY);
            
    if(D) Log.i(TAG, "Location provider at " 
                      + timestamp + " : " 
                      + provider + " Location: " 
                      + location.getLatitude() + ", " 
                      + location.getLongitude());

    LocationsDB db = new LocationsDB(mContext);

    db.addLocation(new LocationWrapper(
                                        timestamp, 
                                        provider, 
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        location.getAccuracy())
                                      );
    intent = new Intent(LOCATION_ACTION);
    intent.putExtra(LOCATION_DATA, timestamp);
    mContext.sendBroadcast(intent);

  }

  public void onStatusChanged(String provider, int status, Bundle extras) {}

  public void onProviderEnabled(String provider) {}

  public void onProviderDisabled(String provider) {}
}