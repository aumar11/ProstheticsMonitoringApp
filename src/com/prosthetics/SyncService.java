package com.prosthetics;

import com.prosthetics.LocationsDB;
import com.prosthetics.CancelableThread;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


/**
 * This class performs the synchronisation with a central server. It contacts
 * the server using HTTP, and pushes new data as an array of JSON objects.
 * @author aumar11 and jbanford, based on prior work by
 * @author Stephen Bell
 * @author Jakub Konka
 * @version 1.0
 */
public class SyncService extends Service
{
	/**
	 * Tag for use in logging and debugging the output generated by this class.
	 */
	public final static String TAG = "SyncService";

	/**
	 * Encoding type.
	 */
	private final static String CHARSET = "UTF-8";

  /** 
   * Path to test database
   */
  private final static String PATH = "http://sederunt.org/locations/input";

	/**
	 * Server host name.
	 */
	//private String HOST = "http://ancient-cove-5464.herokuapp.com";

	/**
	 * Relative path to the PHP script returning the latest data id on the server.
	 */
	private final static String LATEST_PATH = "http://sederunt.org/locations/latest";

	/**
	 * Connection timeout param.
	 */
	private final static int CONNECTION_TIMEOUT = 10000;

	/**
	 * Maximum number of network retries param.
	 */
	private final static int MAXIMUM_NETWORK_RETRIES = 3;

	/**
	 * Consecutive pushing retry delay values.
	 */
	private final static int[] NETWORK_RETRY_DELAY = new int[] {5000, 10000, 20000};

	private boolean mHasError = false;
	private PowerManager mPowerManager;
	private PowerManager.WakeLock mWakeLock;

	SyncThread mSyncThread; 

	/** Called when the service is first created. */
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "Creating service");
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
	}

	/** Called when the service is started. */                      
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "onStartCommand called");
		mSyncThread = new SyncThread();
		mSyncThread.start();
		return 0;
	}

	/** Called when the service is destroyed. */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(TAG, "Releasing WakeLock");
		mWakeLock.release();
	}

	/**
	 * @param intent {@link android.content.Intent}
	 * @return null.
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	/**
	 * Updates the server with new data.
	 * @param url,
	 * @param json.
	 */
	private int updateServer(String url, String json)
	{
		Log.i(TAG, "Synchronising new location data with server");
		HttpParams myParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(myParams, 10000);
    HttpConnectionParams.setSoTimeout(myParams, 10000);
    HttpClient httpclient = new DefaultHttpClient(myParams);

    try
    {
      HttpPost httppost = new HttpPost(url);
      httppost.setHeader("Content-type", "application/json");

      StringEntity se = new StringEntity(json); 
      se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
      httppost.setEntity(se); 

      HttpResponse response = httpclient.execute(httppost);
     	HttpEntity entity = response.getEntity();
			Log.i(TAG, EntityUtils.toString(entity, SyncService.CHARSET));
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode == HttpStatus.SC_OK)
			{
				Log.i(TAG, "Updated successfully" + PATH);
				return 0;
			} 
			else
			{
				Log.i(TAG, "Statuscode: " + statusCode);
				mHasError = true;
				return -1;
			}
    }
		catch (Exception e)
		{
			Log.i(TAG, "Exception occurred: " + e.getMessage());
			mHasError = true;
			return -1;
		}
	}

	/**
	 * Private nested class implementing cancelable thread. This class performs
	 * the synchronisation with the server using a separate worker thread (to offload
	 * the UI thread).
	 * @see CancelableThread
	 */
	private class SyncThread extends CancelableThread
	{
		/** Constructs object of type {@code SyncThread}. */
		public SyncThread()
		{
			super("SyncThread");
		}

		/**
		 * Starts the thread.
		 * The thread is cancelled after the method hits the end of its code.
		 */
		public void run()
		{
			Log.i(SyncService.TAG, "Synchronising device with external server");
			int tryCount = 0;
			String latestID = "";
			while (tryCount < MAXIMUM_NETWORK_RETRIES + 1)
			{
				Log.i(SyncService.TAG, "Attempt: " + (tryCount + 1));
				String latestData = new String();
				DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
				HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), CONNECTION_TIMEOUT);        
				try
				{
					HttpGet httpGet = new HttpGet(LATEST_PATH);                        
					HttpResponse httpResponse = httpClient.execute(httpGet);
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK)
					{
						HttpEntity httpEntity = httpResponse.getEntity();
						if (httpEntity != null)
						{
							latestID = EntityUtils.toString(httpEntity, SyncService.CHARSET);
							latestID = latestID.replace("\n", "").replace("\r", "");
							Log.i(SyncService.TAG, "Latest ID in locations: " + latestID);
						}
						else
						{
							Log.d(SyncService.TAG, "Server did not respond");
							mHasError = true;
						}
					} else {
						Log.d(SyncService.TAG, "Server responded with status code: " + statusCode);
						mHasError = true;
					}
				} catch (Exception e)
				{
					Log.d(SyncService.TAG, "Exception occurred: " + e.getMessage());
					mHasError = true;
				}               
				if (!mHasError)
				{
					Log.i(SyncService.TAG, "NO ERROR LOL");
					LocationsDB db = new LocationsDB(SyncService.this); // Error :(
					int l = Integer.parseInt(latestID);
					Log.i(SyncService.TAG, "latest id = " + l);
					JSONArray temp = db.getLatestLocations(l);
					Log.i(SyncService.TAG, "JSON ARRAY = " + temp);
					String locations = temp.toString();
					Log.i(SyncService.TAG, "location string = " + locations);
					// String locations = db.getLatestLocations(Integer.parseInt(latestID)).toString();
          // Log.i(SyncService.TAG, "JSONObject looks like: " + locations);
					updateServer(PATH, locations);
				}
				if (mHasError)
				{
					tryCount++;
					if (tryCount >= MAXIMUM_NETWORK_RETRIES)
						break;
					Log.i(SyncService.TAG, "Incrementing try count to " 
						                   + tryCount
						                   + " and sleeping for "
						                   + NETWORK_RETRY_DELAY[tryCount-1]);
					mHasError = false;
					try
					{
						Thread.sleep(NETWORK_RETRY_DELAY[tryCount - 1]);
					}
					catch (InterruptedException e) {}
				} 
				else
				{
					break;
				}
			}

			if (!mHasError) 
				Log.i(SyncService.TAG, "Server updated successfully");
			else 
				Log.i(SyncService.TAG, "Failed to update the server");
			stopSelf();
		}
	}   
}