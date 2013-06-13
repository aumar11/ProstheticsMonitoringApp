package com.prosthetics;

import java.util.HashSet;
import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.String;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Main Activity and entry-point of the app.
 * @author aumar and jbanford
 * @version 1.0
 */

public class ProstheticsMonitoringApp extends Activity
{
  /** Tag for Log statements in this class. */
  public static final String TAG = "ProstheticsMonitoringApp";
  private static final boolean D = true;

  // Message types sent from the BluetoothLinkService Handler
  public static final int MESSAGE_STATE_CHANGE = 1;
  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_WRITE = 3;
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int MESSAGE_TOAST = 5;

  // Key names received from the BluetoothChatService Handler
  public static final String DEVICE_NAME = "device_name";
  public static final String TOAST = "toast";

  // Intent request codes
  private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
  private static final int REQUEST_ENABLE_BT = 2;

  // String buffer for outgoing messages
  private StringBuffer mOutStringBuffer;
  // Name of the connected device
  private String mConnectedDeviceName = null;
  // Array adapter for the conversation thread
  private ArrayAdapter<String> mConversationArrayAdapter;
  // Local Bluetooth adapter
  private BluetoothAdapter mBluetoothAdapter = null;

  // Member object for the link services
  private BluetoothLinkService mLinkService = null;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if(D) Log.e(TAG, "+++ ON CREATE +++");

    // Set up the window layout
    setContentView(R.layout.main);

    // Get local Bluetooth adapter
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // If the adapter is null, then Bluetooth is not supported
    if (mBluetoothAdapter == null)
    {
      Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
      finish();
      return;
    }
  }

    @Override
  public void onStart()
  {
    super.onStart();
    if(D) Log.e(TAG, "++ ON START ++");

    // If BT is not on, request that it be enabled.
    // setupChat() will then be called during onActivityResult
    if (!mBluetoothAdapter.isEnabled())
    {
      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    // Otherwise, setup the link session
    }
    else
    {
      if (mLinkService == null)
        setupLink();
    }
  }

  private void setupLink()
  {
    Log.d(TAG, "setupLink()");

    // Initialize the BluetoothLinkService to perform bluetooth connections
    mLinkService = new BluetoothLinkService(this, mHandler);

    // Initialize the buffer for outgoing messages
    mOutStringBuffer = new StringBuffer("");
  }

  /**
   * Sends a timestamp.
   * @param timestamp the current time.
   */
  private void sendTimestamp()
  {
    // Check that we're actually connected before trying anything
    if (mLinkService.getState() != BluetoothLinkService.STATE_CONNECTED)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }

    Long time = System.currentTimeMillis();
    String timestamp = time.toString() + "\n";

    // Get the message bytes and tell the BluetoothChatService to write
    byte[] send = timestamp.getBytes();
    mLinkService.write(send);

    Log.d(TAG, "Sent time " + timestamp);

    // Reset out string buffer to zero and clear the edit text field
    mOutStringBuffer.setLength(0);
  }

  @Override
  public synchronized void onPause()
  {
    super.onPause();
    if(D) Log.e(TAG, "- ON PAUSE -");
  }

  @Override
  public void onStop()
  {
    super.onStop();
    if(D) Log.e(TAG, "-- ON STOP --");
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    // Stop the Bluetooth chat services
    if (mLinkService != null) mLinkService.stop();
    if(D) Log.e(TAG, "--- ON DESTROY ---");
  }

  // The Handler that gets information back from the BluetoothLinkService
  private final Handler mHandler = new Handler()
  {
    @Override
    public void handleMessage(Message msg)
    {
      switch (msg.what)
      {
        case MESSAGE_STATE_CHANGE:
          if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
          switch (msg.arg1)
          {
            case BluetoothLinkService.STATE_CONNECTED:
              setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
              // Send the current time
              sendTimestamp();
              break;
            case BluetoothLinkService.STATE_CONNECTING:
              setStatus(R.string.title_connecting);
              break;
            case BluetoothLinkService.STATE_LISTEN:
            case BluetoothLinkService.STATE_NONE:
              setStatus(R.string.title_not_connected);
              break;
          }
          break;
        case MESSAGE_WRITE:
          byte[] writeBuf = (byte[]) msg.obj;
          // construct a string from the buffer
          String writeMessage = new String(writeBuf);
          break;
        case MESSAGE_READ:
          byte[] readBuf = (byte[]) msg.obj;
          // construct a string from the valid bytes in the buffer
          String readMessage = new String(readBuf, 0, msg.arg1);
          break;
        case MESSAGE_DEVICE_NAME:
          // save the connected device's name
          mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
          Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
          break;
        case MESSAGE_TOAST:
          Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
          break;
      }
    }
  };

  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(D) Log.d(TAG, "onActivityResult " + resultCode);
    switch (requestCode)
    {
      case REQUEST_CONNECT_DEVICE_SECURE:
        // When DeviceListActivity returns with a device to connect
        if (resultCode == Activity.RESULT_OK)
        {
          connectDevice(data);
        }
        break;
      case REQUEST_ENABLE_BT:
        // When the request to enable Bluetooth returns
        if (resultCode == Activity.RESULT_OK)
        {
          // Bluetooth is now enabled, so set up a link session
          setupLink();
        }
        else
        {
          // User did not enable Bluetooth or an error occurred
          Log.d(TAG, "BT not enabled");
          Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
          finish();
        }
    }
  }

  private void connectDevice(Intent data)
  {
    // Get the device MAC address
    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    // Get the BluetoothDevice object
    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
    // Attempt to connect to the device
    mLinkService.connect(device);
  }

  private final void setStatus(int resId)
  {
    final ActionBar actionBar = getActionBar();
    actionBar.setSubtitle(resId);
  }

  private final void setStatus(CharSequence subTitle)
  {
    final ActionBar actionBar = getActionBar();
    actionBar.setSubtitle(subTitle);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.option_menu, menu);
    return true;
  }

  // Pretty hacky for now: only one item in the options menu
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent serverIntent = null;
    serverIntent = new Intent(this, DeviceListActivity.class);
    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    return true;
  }
}
