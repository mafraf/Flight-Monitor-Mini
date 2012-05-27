/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj.utils;

import java.awt.Point;

/**
 * Implementace nekolik vypoctu nad mapou. Vyuziva vztahu pro Mercator projekci.
 * Umoznuje snadny prevod bodu v GPS souradnicich na souradnice pixelu
 * v obrazku mapy o urcenem rozmeru, stredu a zoomu.
 *
 * @author Martin Falticko
 *
 */
public class BingMapsProjection {

  // konstanty pro prevod vzdalenosti v pixelech na GPS souradnice
  private static final int offset = 268435456;
  private static final double radius = offset / Math.PI;
  private static final double pixelTileSize = 256d;
  private static final double degreesToRadiansRatio = 180d / Math.PI;
  private static final double radiansToDegreesRatio = Math.PI / 180d;

  /**
   * Vzdalenost stredu GPS souradnicoveho systemu od pocatku v pixelech (vodorovne)
   */
  private double pixelGlobeCenterX;
  /**
   * Vzdalenost stredu GPS souradnicoveho systemu od pocatku v pixelech (svisle)
   */
  private double pixelGlobeCenterY;
  /**
   * Pomer pixel/stupen vodorovne
   */
  private double xPixelsToDegreesRatio;
  /**
   * Pomer pixel/stupen svisle
   */
  private double yPixelsToRadiansRatio;

  /**
   * aktualni zoom
   */
  private int zoom;

  /**
   * Mapa, k niz byl objekt vytvoren
   */
  private BingMapsStat map;

  /**
   * Souradnice leveho horniho rohu mapy v pixelech od pocatku souradnic.
   *
   * Pouziva metoda pointFromGPS
   */
  private long[] leftTopPFGC = null;


  /**
   * Konstruktor
   * @param map  Instance BingMapsStat, ktera ma v sobe jiz nactenou nejakou mapu.
   */
  public BingMapsProjection(BingMapsStat map) {
    this.zoom = map.getZoom();
    this.map = map;

    // vypocet udaju pro konkretni zoom pouzity v mape
    double pixelGlobeSize = pixelTileSize * Math.pow(2d, this.zoom);
    this.xPixelsToDegreesRatio = pixelGlobeSize / 360d;
    this.yPixelsToRadiansRatio = pixelGlobeSize / (2d * Math.PI);
    double halfPixelGlobeSize = pixelGlobeSize / 2d;
    this.pixelGlobeCenterX = halfPixelGlobeSize;
    this.pixelGlobeCenterY = halfPixelGlobeSize;
  }

  /**
   * Vraci zoom, pro nez byla projekce inicializovana.
   * @return zoom
   */
  public int getZoom() {
    return this.zoom;
  }

  /**
   * Vrati vzdalenost v pixelech od pocatku souradnic pro bod zadany pomoci GPS
   * @param gps GPS souradnice
   * @return pole bodu
   */
  public long[] GPSToPixelsFromGlobeCenter(GPSPoint gps) {
    double f = Math.min(Math.max(Math.sin(gps.lat * radiansToDegreesRatio), -0.9999d), 0.9999d);
    return new long[]{
        Math.round(this.pixelGlobeCenterX + (gps.lng * this.xPixelsToDegreesRatio)),
        Math.round(this.pixelGlobeCenterY + .5d * Math.log((1d + f) / (1d - f)) * - this.yPixelsToRadiansRatio)
    };
  }


  /**
   * Vrati GPS souradnice podle vzdalenosti v pixelech od pocatku souradnic
   * @param x 
   * @param y 
   * @return Objekt reprezentujici GPS souradnici
   */
  public GPSPoint pixelsFromGlobeCenterToGPS(long x, long y) {
    return new GPSPoint(
        (2 * Math.atan(Math.exp((y - this.pixelGlobeCenterY) / - this.yPixelsToRadiansRatio)) - Math.PI / 2) * degreesToRadiansRatio,
        (x - this.pixelGlobeCenterX) / this.xPixelsToDegreesRatio
    );
  }


  /**
   * Vraci souradnice v pixelech na nactene mape odpovidajici GPS souradnicim.
   *
   * @param gps GPS souradnice bodu
   * @return  Souradnice bodu v pixelech vztazene k levemu hornimu rohu mapy.
   */
  public Point pointFromGPS(GPSPoint gps) {

    // vypocet GPS souradnic horniho leveho rohu obrazku
    // jako mirna optimalizace se nepocita pokazde, ale vzdy jen pri zmene mapy
    // (BingMapsStat v ten okamzik vola metodu invalidate)
    if (this.leftTopPFGC == null) {
      GPSPoint leftTop = adjustMap((int)-(this.map.getWidth() / 2), (int)-(this.map.getHeight() / 2));

      this.leftTopPFGC = GPSToPixelsFromGlobeCenter(leftTop);
    }

    long[] lp = GPSToPixelsFromGlobeCenter(gps);

    return new Point(
      (int) (lp[0] - this.leftTopPFGC[0]),
      (int) (lp[1] - this.leftTopPFGC[1])
    );
  }

  /**
   * Invalidace drive vypoctenych udaju pri zmene mapy
   */
  public void invalidate() {
    this.leftTopPFGC = null;  // viz metoda PointFromGPS
  }

  /**
   * Vypocte nove GPS souradnice pro pozadovany posun mapy v pixelech
   * @param deltaX Vodorovny posun
   * @param deltaY Svisly posun
   * @return  GPS souradnice
   */
  public GPSPoint adjustMap(int deltaX, int deltaY) {
    GPSPoint mapCenter = this.map.getCenter();
    return new GPSPoint(
      YToL(LToY(mapCenter.lat) + (deltaY << (21 - this.zoom))),
      XToL(LToX(mapCenter.lng) + (deltaX << (21 - this.zoom)))
    );
  }

   /**
   * Vypocte nove GPS souradnice pro pozadovany posun mapy v pixelech
   * @param p Zadany GPS bod
   * @param zoom Uroven zvetseni 1-21
   * @param deltaX Vodorovny posun
   * @param deltaY Svisly posun
   * @return  GPS souradnice
   */
  public static GPSPoint adjustCoords(GPSPoint p, int deltaX, int deltaY,int zoom) {
    GPSPoint mapCenter = p;
    return new GPSPoint(
      YToL(LToY(mapCenter.lat) + (deltaY << (21 - zoom))),
      XToL(LToX(mapCenter.lng) + (deltaX << (21 - zoom)))
    );
  }

  public static Point getCoords(GPSPoint basePoint, GPSPoint targetPoint, Point controlPoint,int zoom)
  {
      Point p=new Point();
      p.x=(int)(controlPoint.x + (LToX(targetPoint.lng)-LToX(basePoint.lng))/(1<<(21-zoom)));
      p.y=(int)(controlPoint.y + (LToY(targetPoint.lat)-LToY(basePoint.lat))/(1<<(21-zoom)));
      return p;
  }

  public static GPSPoint getGPS(GPSPoint basePoint,  Point controlPoint, Point point,int zoom)
  {
      GPSPoint p=new GPSPoint(
        YToL(LToY(basePoint.lat) + ((point.y-controlPoint.y) * (1 << (21 - zoom)))),
        XToL(LToX(basePoint.lng) + ((point.x-controlPoint.x) * (1 << (21 - zoom)))));
      return p;
  }


//// pomocne metody pro adjustMap...
  
  private static double LToX(double x) {
    return Math.round(offset + radius * x * Math.PI / 180);
  }

  private static double LToY(double y) {
    return Math.round(
      offset - radius *
        Math.log(
        (1 + Math.sin(y * Math.PI / 180))
        /
        (1 - Math.sin(y * Math.PI / 180))
      ) / 2);
  }

  private static double XToL(double x) {
    return ((Math.round(x) - offset) / radius) * 180 / Math.PI;
  }

  private static double YToL(double y) {
    return (Math.PI / 2 - 2 *
          Math.atan(
            Math.exp((Math.round(y)-offset)/radius)
          )
         ) * 180 / Math.PI;
  }

  private double round(double num) {
    double floor = Math.floor(num);

    if(num - floor >= 0.5)
      return Math.ceil(num);
    else
      return floor;
  }


}
