package com.prosthetics;

import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a helper class for opening and creating the locations db.
 * It should be used in conjunction with {@link LocationsDB} helper class.
 * @author jbanford, based on prior work by 
 * @author Jakub Konka
 * @version 1.0
 * @see LocationsDB
 */
public class LocationsDBHelper extends SQLiteOpenHelper
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "LocationsDBHelper";
  private static final boolean D = true;

  /** Name of the only table in the db for storing locations of all types. */
  public final static String TABLE = "locations";
  /** Id column name. */
  public final static String ID = "id";
  /** UId column name. */
  public final static String UID = "user_id";
  /** Timestamp column name. */
  public final static String TIMESTAMP = "timestamp";
  /** Provider column name */
  public final static String PROVIDER = "provider";
  /** Latitude column name */
  public final static String LATITUDE = "latitude";
  /** Longitude column name */
  public final static String LONGITUDE = "longitude";
  /** Accuracy column name */
  public final static String ACCURACY = "accuracy";

  /**
   * Constructs an object of type {@code LocationsDBHelper}.
   * @param context The {@code Context} in which the {@code LocationsDBHelper}
   * object was created.
   * @param path The absolute path to the interactions db.
   */
  public LocationsDBHelper(Context context, String path)
  {
    super(context, path, null, 1);
    if(D) Log.i(TAG, "Create a LocationsDBHelper.");
  }

  /**
   * Creates new locations table inside the locations db if it doesn't exist
   * already.
   * @param db {@link android.database.sqlite.SQLiteDatabase} object.
   */
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    if(D) Log.i(TAG, "onCreate called.");
    String interSQL = "create table " 
                    + TABLE 
                    + " (" 
                    + ID + " integer primary key autoincrement, "
                    + UID + " integer, "
                    + TIMESTAMP + " datetime, "
                    + PROVIDER + " text, "
                    + LATITUDE + " real, "
                    + LONGITUDE + " real, "
                    + ACCURACY + " real);";
    db.execSQL(interSQL);
  }

  /**
   * Should upgrade the structure of the database. Stub method only.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}