package com.prosthetics;

import android.util.Log;

/**
 * This class represents an abstraction of temperature data.
 * @author aumar11 and jbanford, based on prior work by
 * @author Jakub Konka
 * @version 1.0
 */
public class TemperatureWrapper
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "TemperatureWrapper";
  private static final boolean D = true;

  private String timestamp;
  private float temperature;

  /**
   * Constructs an object of type {@code TemperatureWrapper}.
   * @param timestamp The timestamp of adding the temperature
   * data to the patient db.
   * @param temperature The temperature value from the sensor
   */
  public TemperatureWrapper( String timestamp, float temperature)
  {
    if (D) Log.i(TAG, "Create a LocationWrapper.");
    this.timestamp = timestamp;
    this.temperature = temperature;
  }
  
  /**
   * @return The recorded-at timestamp of the location.
   */
  public String getTimestamp()
  {
    return timestamp;
  }

  /**
   * @return The value of tempererature that was recorded.
   */
  public float getValue()
  {
    return temperature;
  }

}