/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj.utils;

import java.io.Serializable;

/**
 * GPS souradnice
 *
 * @author Martin Falticko
 */
public class GPSPoint implements Serializable {

  /**
   * latitude
   */
  public double lat;
  /**
   * longtitude
   */
  public double lng;

  /**
   *
   * @param lat latitude
   * @param lng longtitude
   */
  public GPSPoint(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  /**
   * Kopirovaci konstruktor
   * @param p zdrojovy bod
   */
  public GPSPoint(GPSPoint p) {
    this.lat = p.lat;
    this.lng = p.lng;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GPSPoint) {
      return ((new Double(lat).equals(((GPSPoint)obj).lat)) && (new Double(lng).equals(((GPSPoint)obj).lng)));
    }
    else
      return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
    hash = 23 * hash + (int) (Double.doubleToLongBits(this.lng) ^ (Double.doubleToLongBits(this.lng) >>> 32));
    return hash;
  }

  @Override
  public String toString() {
    return "GPSPoint{" + "lat=" + lat + "lng=" + lng + '}';
  }


}
