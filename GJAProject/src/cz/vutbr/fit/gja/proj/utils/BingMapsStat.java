/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Popdora mapovych podkladu z Microsoft Bing maps static.
 * 
 * @author Pavel Dziazio, Martin Falticko
 */
public class BingMapsStat {

  /**
   * Typ mapy - silnice
   */
  public static String TYPE_ROAD = "Road";
  /**
   * Typ mapy - satelitni
   */
  public static String TYPE_Aerial = "AerialWithLabels";


  /**
   * API klic pro mapy
   */
  private String apiKey = null;

  /**
   * Projekce - vypocty souradnic nad mapou a pouzitym meritkem
   */
  private BingMapsProjection projection = null;

  /**
   * Zoom naposledy nactene mapy
   */
  private int zoom;

  /**
   * Stred naposledy nactene mapy
   */
  private GPSPoint mapCenter;

  /**
   * Stazeny obrazek - binarni data
   */
  private byte[] imageData = null;

  /**
   * Sirka naposledy stazeneho obrazku
   */
  private int width;

  /**
   * Vyska naposledy stazeneho obrazku
   */
  private int height;

  /**
   * Format naposledy stazeneho obrazku
   */
  private String format;


  /**
   * Standardni konstruktor
   * @param key API klic pro mapy
   */
  public BingMapsStat(String key) {
      apiKey = key;
  }

  /**
   * Vrati GPS souradnice stredu mapy
   * @return
   */
  public GPSPoint getCenter() {
    return this.mapCenter;
  }

  /**
   * Vrati zoom mapy
   * @return
   */
  public int getZoom() {
    return this.zoom;
  }

  /**
   * Zazoomuje mapu
   *
   * @param  z Novy zoom
   */
  public void setZoom(int z)
  {
      this.zoom=z;
  }

  /**
   * Vrati sirku mapy
   * @return
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * Vrati vysku mapy
   * @return
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Vrati typ mapy (TYPE_xxxx)
   * @return
   */
  public String getFormat() {
    return this.format;
  }

  /**
   * Vraci true, pokud je obrazek mapy korektne nacten a lze jej pouzit.
   * @return
   */
  public boolean isMapLoaded() {
    return (this.imageData != null);
  }

  /**
   * Stahne obrazek s mapou nebo ho nacte ze souboroveho systemu
   * @param width   sirka obrazku
   * @param height  vyska obrazku
   * @param gps     GPS souradnice stredu mapy
   * @param zoom    uroven priblizeni (1-22)
   * @param format  typ mapy (BingMapsStat.TYPE_xxx)
   * @param x   Relativni vzdalenost od kontrolniho bodu v ose X
   * @param y   Relativni vzdalenost od kontrolniho bodu v ose Y
   * @return
   * @throws IOException
   */
  public boolean loadMapImage(int width, int height, GPSPoint gps, int zoom, String format,int x, int y) throws IOException {
    // ulozeni informaci o obrazku
    this.zoom = zoom;
    this.mapCenter = gps;
    this.width = width;
    this.height = height;
    this.format = format;

    // invalidace hodnot vypoctenych v projekci
    if (this.projection != null)
      this.projection.invalidate();

    File file=new File("maps/"+x+"-"+y+"-"+zoom+".png");
    if(file.isFile())
    {
        imageData = this.loadFile(file);
    }
    else
    {
        imageData = loadHttpFile(getMapUrl(width, height, gps, zoom, format));
        this.saveImageCache(x, y);
    }
    return (imageData != null);
  }


  /**
   * Vrati obrazek mapy jako objekt java.awt.Image
   * @return
   * @throws IllegalStateException
   * @throws java.io.IOException
   */
  public Image getImage() throws IllegalStateException, java.io.IOException {
    if (this.imageData == null)
      throw new IllegalStateException("Map is not loaded!");

    InputStream in = new ByteArrayInputStream(imageData);

    BufferedImage image = ImageIO.read(in);
    BufferedImage lowImage = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_USHORT_555_RGB);
    lowImage.getGraphics().drawImage(image,0,0,null);
    return lowImage;
    //return Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
  }

  /**
   * Ulozi stazeny obrazek mapy do souboru
   * @param fileName  Cesta a nazev souboru
   * @throws FileNotFoundException
   * @throws IOException
   * @throws IllegalStateException
   */
  public void saveImage(String fileName) throws FileNotFoundException, IOException, IllegalStateException {
    if (this.imageData == null)
      throw new IllegalStateException("Map is not loaded!");

    FileOutputStream fs = new FileOutputStream(fileName);
    fs.write(imageData);
    fs.close();
  }

  /**
   * Ulozi nacachovany obrazek do docasne slozky
   * @param x   Relativni vzdalenost od kontrolniho bodu v ose X
   * @param y   Relativni vzdalenost od kontrolniho bodu v ose Y
   * @throws FileNotFoundException
   * @throws IOException
   * @throws IllegalStateException
   */
  public void saveImageCache(int x,int y) throws FileNotFoundException, IOException, IllegalStateException {
    if (this.imageData == null)
      throw new IllegalStateException("Map is not loaded!");
    File dir=new File("maps");
    if(!dir.isDirectory())
        dir.mkdir();
    FileOutputStream fs = new FileOutputStream("maps/"+x+"-"+y+"-"+zoom+".png");
    fs.write(imageData);
    fs.close();
  }

  /**
   * Vymaze mapovou cache
   */
  public static void clearImageCache()
  {
      File dir = new File("maps");
      if(!dir.isDirectory()) return;
      String[] list = dir.list();
      File file;
      if (list.length == 0) {
          return;
      }

      for (int i = 0; i < list.length; i++) {
          //file = new File(directory + list[i]);
          file = new File("maps", list[i]);
          file.delete();
      }
  }

 
  /**
   * Vrati a pripadne i vytvori instanci projekce pro aktualni mapu.
   * Tu lze pak pouzit k vypoctum posunu a pozic bodu nad mapou.
   *
   *
   * @return
   * @throws IllegalStateException
   */
  public BingMapsProjection getProjection() throws IllegalStateException {
    if (this.imageData == null)
      throw new IllegalStateException("Map is not loaded!");
    if ((projection == null) || (projection.getZoom() != zoom)) { // pokud se zmenil zoom, je treba vytvorit cely objekt znovu
      projection = new BingMapsProjection(this);
    }
    return projection;
  }


  /**
   * Vrati URL obrazku s mapou
   * @param width   sirka obrazku
   * @param height  vyska obrazku
   * @param gps     GPS souradnice stredu mapy
   * @param zoom    uroven priblizeni (1-22)
   * @param format  typ mapy (BingMapsStat.TYPE_xxx)
   * @return
   */
  public String getMapUrl(int width, int height, GPSPoint gps, int zoom, String format) {
    // pouzivam format s parametrem Locale nastavenym na null, aby byla v cisle des. tecka a ne podle nastaveni systemu pripadne carka
    return "http://dev.virtualearth.net/REST/v1/Imagery/Map/" + format + "/" + String.format((Locale)null, "%.5f", gps.lat) + "," + String.format((Locale)null, "%.5f", gps.lng) + "/" + zoom + "?mapSize=" + width + "," + height + "&key=" + apiKey;
  }

  /**
   * Stahne obrazek s mapou z webu
   * @param url URL adresa
   * @return  data obrazku
   * @throws IOException
   */
  private byte[] loadHttpFile(String url) throws IOException {
    byte[] byteBuffer = null;
    URL fileUrl;

    fileUrl = new URL(url);
    URLConnection URLConn = fileUrl.openConnection();
    InputStream is = URLConn.getInputStream();
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buffer = new byte[512];
      int count;
      while ( (count = is.read(buffer)) >= 0 ) {
        bos.write(buffer, 0, count);
      }
      byteBuffer = bos.toByteArray();
    } finally {
      is.close();
    }

    return byteBuffer;
  }
  
  /**
   * Nacte soubor do byteArray
   * @param file
   * @return
   * @throws IOException
   */
  private byte[] loadFile(File file) throws IOException
  {
        InputStream is = new FileInputStream(file);

        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // Prilis velky soubor
        }

        // Novy byteArray
        byte[] bytes = new byte[(int) length];

        // Precte pole
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Soubor nebyl precten " + file.getName());
        }
        is.close();
        return bytes;
   }


    
}
