package com.prosthetics;

import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a helper class for opening and creating the patient db.
 * It should be used in conjunction with {@link PatientDB} helper class.
 * @author aumar and jbanford, based on prior work by 
 * @author Jakub Konka
 * @version 1.0
 * @see LocationsDB
 */
public class PatientDBHelper extends SQLiteOpenHelper
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "PatientDBHelper";
  /** Name of the table in the db for storing accelerometer data. */
  public final static String ACC_TABLE = "accelerometer";
  /** Id column name. */
  public final static String ID = "id";
  /** Timestamp column name */
  public final static String TIMESTAMP = "timestamp";
  /** X axis column name */
  public final static String X_AXIS = "x";
  /** Y axis column name */
  public final static String Y_AXIS = "y";
  /** Z axis column name */
  public final static String Z_AXIS = "z"; 
  /** Name of the table used to store temperature data */
  public final static String TEM_TABLE = "temperature";
  /** Temperature1 column name */
  public final static String VALUE1 = "value1";
  /** Temperature2 column name */
  public final static String VALUE2 = "value2";  

  /** Name of the only table in the db for storing locations of all types. */
  public final static String TABLE = "locations";
  /** Provider column name */
  public final static String PROVIDER = "provider";
  /** Latitude column name */
  public final static String LATITUDE = "latitude";
  /** Longitude column name */
  public final static String LONGITUDE = "longitude";
  /** Accuracy column name */
  public final static String ACCURACY = "accuracy";


  /**
   * Constructs an object of type {@code PatientDBHelper}.
   * @param context The {@code Context} in which the {@code PatientDBHelper}
   * object was created.
   * @param path The absolute path to the interactions db.
   */
  public PatientDBHelper(Context context, String path)
  {
    super(context, path, null, 1);
    Log.i(TAG, "Create a PatientDBHelper.");
  }

  /**
   * Creates new accelerometer table and temperature table inside the 
   * patient db if it doesn't exist already. 
   * @param db {@link android.database.sqlite.SQLiteDatabase} object.
   */
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    Log.i(TAG, "onCreate called.");
    String createAccSQL = "create table " 
                        + ACC_TABLE 
                        + " (" 
                        + ID + " integer primary key autoincrement, " 
                        + TIMESTAMP + " text, "
                        + X_AXIS + " integer, "
                        + Y_AXIS + " integer, "
                        + Z_AXIS + " integer);";
    db.execSQL(createAccSQL);

   String createTemSQL = "create table " 
                       + TEM_TABLE 
                       + " (" 
                       + ID + " integer primary key autoincrement, " 
                       + TIMESTAMP + " text, "
                       + VALUE1 + " integer, "
                       + VALUE2 + " integer);";
    db.execSQL(createTemSQL);

    String interSQL = "create table " 
                    + TABLE 
                    + " (" 
                    + ID + " integer primary key autoincrement, " 
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