
package cz.vutbr.fit.gja.proj.utils;

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
   * URL DB serveru
   */
  private String serverURL;

  /**
   * port DB serveru
   */
  private int serverPort;

  /**
   * SID DB serveru
   */
  private String serverSID;

  /**
   * login pro DB server
   */
  private String login;

  /**
   * heslo pro DB server
   */
  private String password;

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
    this.serverPort = 1521;
    this.serverURL = "";
    this.serverSID = "";
    this.login = "";
    this.password = "";
    this.mapCenter = new GPSPoint(50.119048, 17.05384);
    this.mapZoom = 11;
  }

  /**
   * Vraci login
   * @return
   */
  public String getLogin() {
    return login;
  }

  /**
   * Nastavi login pro pripojeni k DB
   * @param login
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * Vraci heslo
   * @return
   */
  public String getPassword() {
    return password;
  }

  /**
   * Nastavi heslo pro pripojeni k DB
   * @param password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Vraci port pro pripojeni k DB
   * @return
   */
  public int getServerPort() {
    return serverPort;
  }

  /**
   * Nastavi port pro pripojeni k DB
   * @param serverPort
   */
  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Vraci SID pro pripojeni k DB
   * @return 
   */
  public String getServerSID() {
    return serverSID;
  }

  /**
   * Nastavi SID pro pripojeni k DB
   * @param serverSID
   */
  public void setServerSID(String serverSID) {
    this.serverSID = serverSID;
  }

  /**
   * Vraci URL adresu DB serveru
   * @return
   */
  public String getServerURL() {
    return serverURL;
  }

  /**
   * Nastavi URL adresu DB serveru
   * @param serverURL
   */
  public void setServerURL(String serverURL) {
    this.serverURL = serverURL;
  }

  /**
   * Vraci vychozi stred mapy
   * @return
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
   * @return
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


  /**
   * Vrati databazovy connection string
   * @return
   */
  public String getConnectionString() {
    // jdbc:oracle:thin:@berta.fit.vutbr.cz:1521:stud
    String cs = "jdbc:oracle:thin:@" + this.serverURL + ":" + this.serverPort + ":" + this.serverSID;

    return cs;
  }

}
