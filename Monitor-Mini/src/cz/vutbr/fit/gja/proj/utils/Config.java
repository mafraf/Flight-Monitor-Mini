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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Objekt uchovavajici konfiguraci programu
 *
 * @author Martin Falticko, Ondrej Vagner
 */
public class Config implements Serializable {

  /**
   * Vychozi nazev konfiguracniho souboru
   */
  public static String defaultConfigFile = "config";



  /**
   * Cista instance (nevytvorena z ulozeneho kofiguracniho souboru)
   */
  private boolean clearInstance;

  /**
   * Vychozi stred mapy
   */
  private GPSPoint mapCenter;

  /**
   * Vychozi zoom mapy
   */
  private short mapZoom;
  
  /**
   * Cesta k modelum
   */

  private String modelPath;

   /** Typ mapy pouzity v zobrazeni */
  private String mapType;


  /**
   * Vychozi konstruktor
   */
  public Config() {
    this.clearInstance = true;

    implicitValues();
  }

  /**
   * Nastaveni implicitnich hodnot
   */
  private void implicitValues() {

    this.mapCenter = new GPSPoint(50.119048, 17.05384);
    this.mapZoom = 11;
    this.mapType=BingMapsStat.TYPE_ROAD;
    
    try{
      File dir1 = new File (".");
      this.modelPath=dir1.getCanonicalPath();
    }
    catch(IOException _)
    {
      this.modelPath="";
    }
    
  }

  /**
   * Vraci cestu k modelu
   * @return cesta k modelu
   */
  public String getModelPath() {
    return modelPath;
  }

  /**
   * Nastavi cestu modelu
   * @param login jmeno souboru
   */
  public void setModelPath(String pth) {
    this.modelPath=pth;
  }

   /**
   * Vraci typ mapy
   * @return typ mapy
   */
  public String getMapType() {
    return mapType;
  }

  /**
   * Nastavi typ mapy
   * @param type - typ mapy
   */
  public void setMapType(String type) {
    this.mapType=type;
  }


  /**
   * Vraci vychozi stred mapy
   * @return stred mapy
   */
  public GPSPoint getMapCenter() {
    return mapCenter;
  }

  /**
   * Nastavi vychozi stred mapy
   * @param mapCenter
   */
  public void setMapCenter(GPSPoint mapCenter) {
    this.mapCenter = mapCenter;
  }

  /**
   * Vrati vychozi zoom mapy
   * @return zoom
   */
  public short getMapZoom() {
    return mapZoom;
  }

  /**
   * Nastavi vychozi zoom mapy
   * @param mapZoom
   */
  public void setMapZoom(short mapZoom) {
    this.mapZoom = mapZoom;
  }




  /**
   * Zjisti, zda jde o instanci obnovenou z konfiguracniho souboru
   * nebo o novou cistou instanci.
   *
   * @return true pokud jde o novou instanci bez hodnot.
   */
  public boolean isClearInstance() {
    return clearInstance;
  }



  /**
   * Ulozi aktualni hodnoty konfigurace do souboru pro pozdejsi opetovne nascteni.
   *
   * @param fileName  Nazev (cesta k) souboru
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void saveConfig(String fileName) throws FileNotFoundException, IOException {

    this.clearInstance = false;

    FileOutputStream fos = new FileOutputStream(fileName);
    ObjectOutputStream out = new ObjectOutputStream(fos);

    out.writeObject(this);
    out.close();
  }

  /**
   * Nacte ulozenou konfiguraci a vrati novou instanci objektu Config.
   * Pokud soubor s ulozenou konfiguraci neexistuje nebo se ho nepodarilo nacist,
   * vytvori novou prazdnou instanci objektu.
   *
   * @param fileName  Nazev (cesta) k souboru
   * @return Instance objektu Config
   */
  public static Config loadConfig(String fileName) {
    Config cfg;
    try {
      FileInputStream fis = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fis);
      cfg = (Config)in.readObject();
      in.close();

      cfg.clearInstance = false;
    }
    catch (Exception e) {
      cfg = new Config();
      cfg.clearInstance = true;
    }
    return cfg;
  }


}
