
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
 * @author Pavel Dziadzio
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
