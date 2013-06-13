package com.prosthetics;

import java.io.IOException;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.io.PrintWriter;

import android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.os.Environment;

import com.octothorper.bluetooth.AccelerometerDBHelper;
import com.octothorper.bluetooth.AccelerometerWrapper;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Helper class for accessing the accelerometer db. It can be used to add a new 
 * record, check whether a specified record already exists, count number of 
 * records, or fetch records from the database.
 * @author aumar11 and jbanford, based on prior work by
 * @author Jakub Konka
 * @version 1.0
 * @see AccelerometerDBHelper
 */
public class AccelerometerDB
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "AccelerometerDB";
  private static final boolean D = true;
  /**
   * Base directory holding the public contents of the app on the 
   * external memory card. The absolute path looks like:
   * /path_to_external_memory/BASE_DIR/
   */
  private final static String BASE_DIR = "ProstheticsMonitoringAppProject";
  /** The name of the db. */
  private final static String DB_NAME = "data.sqlite3";
  private AccelerometerDBHelper dbHelper;

  /**
   * Constructs an object of type {@code AccelerometerDB}.
   * @param context The {@code Context} in which the {@code AccelerometerDB} object
   * was created.
   */
  public AccelerometerDB(Context context) 
  {
    Log.i(TAG, "Creating a handler for accelerometer db.");
    String dbPath = AccelerometerDB.getDBPath() + "/" + DB_NAME;
    dbHelper = new AccelerometerDBHelper(context, dbPath);
  }
  
  /**
   * Adds a sample to the accelerometer db.
   * @param location {@code LocationWrapper} object to be added
   */
  public void addAccelerometerSample(AccelerometerWrapper accelerometer)
  {
    // Add interaction to the db
    Log.i(TAG, "Adding record to accelerometer table");
    SQLiteDatabase db = null;
    try
    {
      db = dbHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(AccelerometerDBHelper.TIMESTAMP, accelerometer.getTimestamp());
      values.put(AccelerometerDBHelper.X_AXIS, accelerometer.getX());
      values.put(AccelerometerDBHelper.Y_AXIS, accelerometer.getY());
      values.put(AccelerometerDBHelper.Z_AXIS, accelerometer.getZ());
      db.insertOrThrow(AccelerometerDBHelper.TABLE, AccelerometerDBHelper.TIMESTAMP, values);
      Log.i(TAG, "Value has been inserted");
    } 
    catch (SQLException e)
    {
      Log.i(TAG, "Could not insert data into accelerometer table: " + e);
    } 
    finally
    {
      Log.i(TAG, "Closing db...");
      if (db != null)
        db.close();
    }
  }

  /**
   * Returns the absolute path to the accelerometer db.
   * @return Absolute path to the accelerometer db.
   */
  private static String getDBPath()
  {
    Log.i(TAG, "getDBPath called.");
    String path = Environment.getExternalStorageDirectory().getPath() + "/" + BASE_DIR;
    Log.i(TAG, "Checking if " + path + " exists");
    File dbDir = new File(path);
    if (!dbDir.isDirectory())
    {
      try
      {
        Log.i(TAG, "Trying to create " + path);
        dbDir.mkdirs();
      } 
      catch (Exception e)
      {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        Log.i(TAG, result.toString());
      }
    }
    return path;
  }
}