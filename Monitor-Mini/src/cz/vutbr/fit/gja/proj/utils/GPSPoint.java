/*
Copyright (c) 2012, Martin Faltičko, Ondřej Vagner
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Jetimodel s.r.o. nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Martin Faltičko, Ondřej Vagner BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
