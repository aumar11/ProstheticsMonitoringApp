package com.prosthetics;

import java.io.IOException;

import android.app.Service;
import android.app.AlarmManager;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.PendingIntent;
import android.os.SystemClock;

import com.prosthetics.LocationReceiver;

/**
 * This class is the master service responsible for starting and stopping
 * gathering location data.
 * @author jbanford
 * @version 1.0
 * @see com.prosthetics.LocationGatherer
 */
public class LocationGathererService extends Service
{
  /** Tag for Log statements in this class. */
  public static final String TAG = "LocationGathererService";
  private static final boolean D = true;

  /** GPS location update period: 5 minutes */
  private final static long GPS_UPDATE_PERIOD = 60000;
  /** Network location update period: 5 minutes */
  private final static long NETWORK_UPDATE_PERIOD = 60000;

  private LocationManager mLocationManager;
  private LocationReceiver mLocationReceiver;

  /** AlarmManager for sync services. */
  private AlarmManager mAlarmManager;
  private Intent mSyncIntent;
  private PendingIntent mSyncPendingIntent;
  private boolean mIsSynching;

  /** Server synchronisation period (ATM, once every 5 minutes). */
  private final static long SYNC_PERIOD = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 7;

  /** Called when the service is first created. */
  @Override
  public void onCreate()
  {
    if(D) Log.i(TAG, "onCreate called.");
    super.onCreate();
  }

  /**
   * @param intent {@link android.content.Intent}
   * @return {@code null}.
   */
  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }

   /** Called when the service is destroyed. */
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    if(D) Log.i(TAG, "onDestroy called.");
    stopGathering();
    stopSync();
  }
  
  /** Called when the service is started. */
  @Override
  public int onStartCommand(Intent i, int flags, int startId)
  {
    super.onStartCommand(i, flags, startId);
    if(D) Log.i(TAG, "onStartCommand called with startId " + startId + ": " + i);
    mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    startGathering();
    startSync();

    return 0;
  }

  /**
   * Starts synchronisation service. This method schedules for repeating the execution of
   * the server synchronisation service.
   */
  private void startSync()
  {
    mSyncIntent = new Intent(getApplicationContext(), SyncAlarmReceiver.class);
    mSyncPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mSyncIntent, 0);
    mAlarmManager.setInexactRepeating(
                                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        SystemClock.elapsedRealtime(),
                                        SYNC_PERIOD, mSyncPendingIntent
                                     );
    mIsSynching = true;
    Log.i(TAG, "Setting synchronisation interval to " + SYNC_PERIOD + "ms");
  }

  /**
   * Stops synchronisation service.
   */
  private void stopSync()
  {
    if (mSyncPendingIntent != null)
      mAlarmManager.cancel(mSyncPendingIntent);
    mIsSynching = false;
  }

  /** Starts gathering location data. */
  private void startGathering()
  {
    if(D) Log.i(TAG, "startGathering called.");
    mLocationReceiver = new LocationReceiver(getApplicationContext());
    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    mLocationManager.requestLocationUpdates(
                                              LocationManager.NETWORK_PROVIDER,
                                              NETWORK_UPDATE_PERIOD, 
                                              0, 
                                              mLocationReceiver
                                            );
    mLocationManager.requestLocationUpdates(
                                              LocationManager.GPS_PROVIDER,
                                              GPS_UPDATE_PERIOD,
                                              0,
                                              mLocationReceiver
                                            );
  }
  
  /** Stop gathering location data */
  private void stopGathering() 
  {
    if(D) Log.i(TAG, "stopGathering called.");
    mLocationManager.removeUpdates(mLocationReceiver);
  }
}