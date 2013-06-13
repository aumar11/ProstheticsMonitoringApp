package com.prosthetics;

import android.util.Log;

/**
 * This class represents an abstraction of accelerometer data.
 * @author aumar11 and jbanford, based on prior work by
 * @author Jakub Konka
 * @version 1.0
 */
public class AccelerometerWrapper
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "AccelerometerWrapper";

  //private int oid;
  private String timestamp;
  private float x;
  private float y;
  private float z;

  /**
   * Constructs an object of type {@code AccelerometerWrapper}.
   * @param timestamp The timestamp of adding the accelerometer 
   * data to the accelerometer db.
   * @param x The x axis accelerometer reading.
   * @param y The y axis accelerometer reading.
   * @param z The z axis accelerometer reading.
   */
  public AccelerometerWrapper( String timestamp, float x, float y, float z)
  {
    Log.i(TAG, "Create a LocationWrapper.");
    this.timestamp = timestamp;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  /**
   * @return The recorded-at timestamp of the location.
   */
  public String getTimestamp()
  {
    return timestamp;
  }

  /**
   * @return The value of the x axis reading.
   */
  public float getX()
  {
    return x;
  }

  /**
   * @return The value of the y axis reading.
   */
  public float getY()
  {
    return y;
  }

  /**
   * @return The value of the z axis reading.
   */
  public float getZ()
  {
    return z;
  }
}