package com.prosthetics;

import android.util.Log;

/**
 * This class represents an abstraction of location data.
 * @author jbanford, based on prior work by
 * @author Jakub Konka
 * @version 1.0
 */
public class LocationWrapper
{
  /** Tag for Log statements in this class. */
  public final static String TAG = "LocationWrapper";
  private static final boolean D = true;

  private int oid;
  private String timestamp;
  private String date;
  private String time;
  private String provider;
  private double accuracy;
  private double latitude;
  private double longitude;

  /**
   * Constructs an object of type {@code LocationWrapper}.
   * @param timestamp The timestamp of adding the interaction to the interactions db.
   * @param provider The name of the location provider. Can be one of the three values:
   * cell, wifi, gps.
   * @param latitude The latitude of the location reading.
   * @param longitude The longitude of the location reading.
   * @param accuracy The accuracy of the location reading in metres.
   */
  public LocationWrapper( String timestamp, String provider, double latitude, double longitude, double accuracy)
  {
    if(D) Log.i(TAG, "Create a LocationWrapper.");
    this.timestamp = timestamp;
    this.date = timestamp.split(" ")[0];
    this.time = timestamp.split(" ")[1];
    this.provider = provider;
    this.latitude = latitude;
    this.longitude = longitude;
    this.accuracy = accuracy;
  }
  
  /**
   * @return The recorded-at timestamp of the location.
   */
  public String getTimestamp()
  {
    return timestamp;
  }

  /**
   * @return The name of the location provider. Can be one of the three values:
   * cell, wifi, gps.
   */
  public String getProvider()
  {
    return provider;
  }

  /**
   * @return The accuracy of the location reading (in metres).
   */
  public double getAccuracy()
  {
    return accuracy;
  }

  /**
   * @return The latitude of the location reading.
   */
  public double getLatitude()
  {
    return latitude;
  }

  /**
   * @return The longitude of the location reading.
   */
  public double getLongitude()
  {
    return longitude;
  }
}