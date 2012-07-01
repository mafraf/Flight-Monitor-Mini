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
package cz.vutbr.fit.gja.proj;

import cz.vutbr.fit.gja.proj.utils.BingMapsProjection;
import cz.vutbr.fit.gja.proj.utils.BingMapsStat;
import cz.vutbr.fit.gja.proj.utils.Globals;
import cz.vutbr.fit.gja.proj.utils.GPSPoint;
import java.awt.*;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.*;
import java.awt.event.*;
import org.jdesktop.application.ResourceMap;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.event.EventListenerList;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.jdesktop.application.Application;


public class DrawPanel extends JPanel implements Runnable
{

    /**
     * Privatni promenne
     */
    private JPanel content;
    private MainMouseListener mouseListener;
    private MainKeyListener keyListener;
    private MainCompListener compListener;
    private Thread mapThread;
    /** Casovac udalosti pro vykreslovani mapy */
    private Timer T;

    /**Objekty cekajici na udalost zmenu stavu*/
    private EventListenerList statusListeners = new EventListenerList();







    /**Struktura s ulozenymi mapami*/
    protected volatile LinkedList<MapPicture> maps=new LinkedList<MapPicture>();
    /**Fronta bodu (relativne k ridicimu bodu), na jejichz souradnice semaji nahrat obrazky*/
    protected volatile LinkedList<Point> queueTile=new LinkedList<Point>();
    /**GPS informace*/
    protected volatile GPSPoint geo;
    /** Aktualne nastaveny Zoom */
    protected volatile int zoom;



     //Pole s body, kdy jednotlive souradnice jsou zadane s puvodnim zvetsenim pri spusteni
    //protected ArrayList<ArrayList<GPSPoint>> polygons=new ArrayList<ArrayList<GPSPoint>>();


    /**
     * Dulezite pro vykresleni
     */

    /**Vykreslovana data*/
    protected ArrayList<DrawObject> objects=new ArrayList<DrawObject>();


    /**Zakladni barva, kterou se bude kreslit*/
    protected Color color=null;

    /**Zakladni typ, ktery slouzi pro editaci objekjtu*/
    protected Integer type=0;

    /**Titulek, ktery se priradi objektu*/
    protected String title="";
    /** Vychozi bod souradnicoveho systemu, kam se vztahuji ostatni body a mapove podklady*/
    protected GPSPoint centralPoint=null;
    /**Doplnujici soubor s obrazkem k polygonu*/
    protected Image mediaImage=null;

    protected String mapType=BingMapsStat.TYPE_Aerial;
    
    /** Veci pro zobrazeni ikony letadla */
    protected GPSPoint iconPoint=null;
    protected double iconHeading=0;
    private Image iconImage = null;

    /***************************************************************************
     * Editace jednoho polygonu
     **************************************************************************/
    /**Indikator, jestli je objekt vkladan nebo editovan*/
    protected boolean insertMode=false;
    /**Priznak povoleni editace*/
    protected boolean editEnable=false;
    /**Index editovaneho objektu*/
    protected int editedIndex=-1;
    /**Seznam kontrolnich bodu polygonu*/
    protected ArrayList<Point> points = new ArrayList<Point>();
    /**Obrazky ke kazdemu bodu*/
    protected ArrayList<Image> pointsImages = new ArrayList<Image>();

    /**Ridici bod na obrazovce*/
    protected volatile Point controlPoint;
    /**Posledni bod pred tazenim*/
    protected Point lastPoint=new Point();
    /**Limit maximalniho a minimalniho poctu bodu*/
    protected int minPoints=3;
    /**Limit maximalniho a minimalniho poctu bodu*/
    protected int maxPoints=0;

  



    /**Velikost kontrolniho "bodu"*/
    final static int DIAMETER=10;
    /**Maximalni vzdalenost bodu od usecky, aby se mohl vlozit novy kontrolni bod*/
    final static double MAX_DISTANCE=4.0;
    /**Vykreslovaci stetec*/
    final static float STROKE_STRENGTH=10.0f;
    /**Vykreslovaci stetec*/
    final static BasicStroke line3p = new BasicStroke(3.0f,
                                          BasicStroke.CAP_BUTT,
                                          BasicStroke.JOIN_MITER,
                                          STROKE_STRENGTH);
    /**Sirka nactenych mapovych bloku*/
    final static  int MAP_WIDTH=256;
    /**Vyska nactenych mapovych bloku*/
    final static int MAP_HEIGHT=256;
    /**Maximalni pocet nacitanych bloku do pameti*/
    final static int MAX_TILES_LOADED=64;

    /**Typy polygonu: 0 - plny polygon, 1 - pouze body, 2 - pouze body (sipky), 3 - lines*/
    final static int TYPE_POLYGON=0;
    /**1 - pouze body*/
    final static int TYPE_POINTS=1;
    /**2 - pouze body (sipky)*/
    final static int TYPE_ARROWS=2;
    /** 3 - lines*/
    final static int TYPE_LINES=3;


    /**Aktualni sirka drawPanelu*/
    protected volatile int width;
    /**Aktualni sirka drawPanelu*/
    protected volatile int height;
    /**Priznak, ze je mapa nactena a muze probehnout prekresleni*/
    private volatile boolean  mapReady=false;
    /**
     * Konstruktor
     */
    public DrawPanel()
    {
        super();
        this.setBackground(new Color(255, 255, 255));
        //this.setSize(200, 200);
        content = this;

        try
        {

            //Udalosti objektu
            this.mouseListener = new MainMouseListener();
            this.addMouseListener(mouseListener);
            this.addMouseMotionListener(mouseListener);
            this.addMouseWheelListener(mouseListener);

            this.keyListener=new MainKeyListener();
            this.addKeyListener(keyListener);
            compListener=new MainCompListener();
            this.addComponentListener(compListener);
            this.setFocusable(true);

            //this.geo=Globals.config.getMapCenter();
            //this.zoom=Globals.config.getMapZoom();

            //Config cfg = Application.getInstance(GJAProjectApp.class).getConfiguration();
            //this.geo = cfg.getMapCenter();
            //this.zoom = cfg.getMapZoom();
            this.geo =Globals.config.getMapCenter();
            this.zoom = Globals.config.getMapZoom();

            //Prenastaveni koordinatu na horni levy pixel stredniho obrazku
            GPSPoint p=BingMapsProjection.adjustCoords(geo, -MAP_WIDTH, -MAP_HEIGHT, zoom);
            this.iconImage = ImageIO.read(getClass().getResourceAsStream("resources/model_aero_small.png"));
     


            T=new Timer(200,new ActionListener(){
                public void actionPerformed(ActionEvent evt) {
                 if(mapReady)
                 {
                     repaint();
                     mapReady=false;
                 }
                }
            });
            T.start();
        }
        catch (Exception e)
        {
            
        }


    }

    /**
     * Povoli nebo zakaze vkladani bodu/polygonu
     * @param value true - povoleno
     */
    public void setInsertEnabled(boolean value)
    {
        if(value)
        {
            this.insertMode=true;
            this.editEnable=true;
            this.editedIndex=-1;
            this.resetPoints();
        }
        else
        {
            this.insertMode=false;
            this.editEnable=false;
            this.editedIndex=-1;
            this.resetPoints();
            content.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Nastavi priznaky editovani daneho polygonu
     * @param index index polygonu. Pro zakazani nastavte index na -1
     */
    public void setEditEnabled(int index)
    {
        if(index>=0)
        {
            this.insertMode=false;
            this.editEnable=true;
            this.editedIndex=index;
            this.editPolygon(this.objects.get(editedIndex).polygons);
            this.setCenter(this.objects.get(editedIndex).centralPoint);
            this.setColor(this.objects.get(editedIndex).polygonsColors);
            this.repaint();
        }
        else
        {
            this.resetPoints();
            this.insertMode=false;
            this.editEnable=false;
            this.editedIndex=-1;
            content.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Vykona potrebne operace k prevodu GPS souradnic polygonu na koordinaty
     * @param list Body polygonu
     */
    void editPolygon(ArrayList<GPSPoint> list)
    {
        ArrayList<Point> plist=new ArrayList<Point>();
        for(GPSPoint p : list)
        {
            plist.add(BingMapsProjection.getCoords(geo, p, this.getControlPoint(), zoom));
        }
        this.points=plist;
    }
    
    void setMapType(String type)
    {
      this.mapType=type;
      removeMaps();
      BingMapsStat.clearImageCache();
      translateControlPoint(0,0);
      loadMaps();
      
    }

    /**
     * Nastavi centrum mapy na dany bod
     * @param center
     */
    void setCenter(GPSPoint center)
    {
        if(center==null) return;
        Point p=BingMapsProjection.getCoords(geo,center, controlPoint, zoom);
        int differenceX=p.x-width/2;
        int differenceY=p.y-height/2;
        this.translatePoints(-differenceX,-differenceY);
        this.loadMaps();
    }

    /**
     * Nastavi vlozeny obrazek do polygonu
     * @param im
     */
    void setImage(Image im)
    {
        this.mediaImage=im;
    }

 



    /**
     * Vypocita prusecik dvou usecek
     * @param p0 Prvni bod prvni usecky
     * @param p1 Druhy bod prvni usecky
     * @param p2 Prvni bod druhe usecky
     * @param p3 Druhy bod druhe usecky
     * @return Vysledny bod - prusecik
     * @throws Exception Chyba. pokud prusecik nelezi v pruniku ctvercu ohranicujicich usecky
     */
    protected static Point getCrossPoint(Point p0,Point p1,Point p2, Point p3)
            throws Exception
    {
        Point val=new Point();
        //Parametricka rovnice primky
        double x0=p0.x,x1=p1.x,x2=p2.x,x3=p3.x;
        double y0=p0.y,y1=p1.y,y2=p2.y,y3=p3.y;

        //double x0=50,x1=250,x2=50,x3=250;
        //double y0=50,y1=250,y2=250,y3=50;

        double a1=y1-y0,a2=y3-y2;
        double b1= -(x1-x0),b2= -(x3-x2);
        double c1= -( a1*x0 + b1*y0);
        double c2= -( a2*x2 + b2*y2);

        if(a1/b1==a2/b2)
        {
            throw(new Exception("Rovnoběžné přímky nebo mimo rozsah"));
        }
        val.x=  (int)((b1*c2 - c1*b2)/(a1*b2-a2*b1));
        val.y= -(int)((a1*c2 - a2*c1)/(a1*b2-a2*b1));
        if((y0>y1) && (val.y >y0 || val.y<y1) || (y0<=y1) && (val.y <y0 || val.y>y1) ||
                (y2>y3) && (val.y >y2 || val.y<y3) || (y2<=y3) && (val.y <y2 || val.y>y3) ||
                (x0>x1) && (val.x >x0 || val.x<x1) || (x0<=x1) && (val.x <x0 || val.x>x1) ||
                (x2>x3) && (val.x >x2 || val.x<x3) || (x2<=x3) && (val.x <x2 || val.x>x3))
        {
            throw(new Exception("Mimo rozsah"));
        }
        return val;
    }


    /**
     * Vypocita vzdalenost bodu od usecky
     * @param P Zadany bod
     * @param p1 Prvni bod usecky
     * @param p2 Druhy bod usecky
     * @return Numericka vzdalenost bodu
     * @throws Exception Chyba, pokud prusecik kolmice od bodu nelezi na usecce p1p2
     */
    protected static double getDistance(Point P,Point p1,Point p2)
            throws Exception
    {

        //Parametricka rovnice primky
        double x=P.x,x1=p1.x,x2=p2.x;
        double y=P.y,y1=p1.y,y2=p2.y;

        //double x0=50,x1=250,x2=50,x3=250;
        //double y0=50,y1=250,y2=250,y3=50;

        //Rovnice prvni primky
        double a1=y2-y1;
        double b1= -(x2-x1);
        double c1= -( a1*x1 + b1*y1);

        //Rovnice druhe primky
        double a2=-b1;
        double b2= a1;
        double c2= -( a2*x + b2*y);

        //Prusecik
        double interX =  ((b1*c2 - c1*b2)/(a1*b2-a2*b1));
        double interY =  -((a1*c2 - a2*c1)/(a1*b2-a2*b1));
        if((y1>y2) && (interY >y1 || interY<y2) || (y1<=y2) && (interY <y1 || interY>y2) ||
          (x1>x2) && (interX >x1 || interX<x2) || (x1<=x2) && (interX <x1 || interX>x2) )

        {
            throw(new Exception("Mimo rozsah"));
        }

        return Math.sqrt(Math.pow(interX-x,2) + Math.pow(interY-y,2)  );
    }

    /**
     * Vymaze vsechny body, ktere byly editovatelne
     */
    void resetPoints()
    {
        this.points.clear();
        this.mediaImage=null;
        this.type=DrawPanel.TYPE_POLYGON;
        this.setTitle(null, null);
        this.repaint();

    }

    /*
     * Vymaze kompletni obsah panelu
     */
    void clear()
    {

        objects.clear();
        iconPoint=null;
        resetPoints();

    }

    /**
     * Prida polygon do vnitrni struktury pro vykreslovani
     * @param points Polygon, ktery se bude pridavat
     * @param removeLast Ma se odstranit posledni bod polygonu?
     * @param imgs Pripadne obrazky ke kazdemu bodu polygonu
     */
    int addPolygon(ArrayList<GPSPoint> points,boolean removeLast,ArrayList<Image> imgs)
    {
        //ArrayList<GPSPoint> p=new ArrayList<GPSPoint>(points);
        if(type==DrawPanel.TYPE_POLYGON && points.size()>0 && removeLast==true)
            points.remove(points.size()-1);
        DrawObject o =new DrawObject();
        o.polygons=points;
        Polygon P=new Polygon();
        for(int i = 0;i<points.size();i++)
        {
            GPSPoint pa=points.get(i);
            Point pg=BingMapsProjection.getCoords(geo, pa,this.getControlPoint(),zoom);
            P.addPoint(pg.x, pg.y);
        }

        o.polygonsGraphics = P;
        o.polygonsColors= color;
        o.polygonsTypes = type;
        o.title=title;
        o.centralPoint=centralPoint;
        o.images=imgs;
        this.objects.add(o);
        return objects.size()-1;
    }

    /**
     * Prida polygon do vnitrni struktury pro vykreslovani
     * @param points Polygon, ktery se bude pridavat
     */
    int addPolygon(ArrayList<GPSPoint> points)
    {
        return addPolygon(points,true,null);
    }

    /**
     * Upravi polygon na zadanem indexu
     * @param points Polygon, ktery se bude pridavat
     * @param index Index polygonu ve strukture polygonu
     * @param imgs Pripadne obrazky ke kazdemu bodu polygonu
     */
    void updatePolygon(int index,ArrayList<GPSPoint> points,ArrayList<Image> imgs)
    {
        ArrayList<GPSPoint> p=new ArrayList<GPSPoint>(points);
        DrawObject o = this.objects.get(index);
        o.polygons = p;
        Polygon P=new Polygon();
        for(int i = 0;i<p.size();i++)
        {
            GPSPoint pa=p.get(i);
            Point pg=BingMapsProjection.getCoords(geo, pa, this.getControlPoint(),zoom);
            P.addPoint(pg.x, pg.y);
        }
        o.polygonsGraphics = P;
        o.images=imgs;
    }
    /**
     * Upravi polygon na zadanem indexu
     * @param points Polygon, ktery se bude pridavat
     * @param index Index polygonu ve strukture polygonu
     */
    void updatePolygon(int index,ArrayList<GPSPoint> points)
    {
        updatePolygon(index, points,null);
    }

    /**
     * Smaze polygon na danem indexu
     * @param index
     */
    void deletePolygon(int index)
    {

        if(index>=0 && index < objects.size())
            objects.get(index).deleted=true;
    }

    /**
     * Pridani konzumenta, ktery prebira informace o statusu komponenty
     * @param listener
     */
    public void addStatusListener(InfoEvent listener)
    {
        statusListeners.add(InfoEvent.class, listener);
    }



    /**
     * Smazani konzumenta
     * @param listener
     */
    public void removeStatusListener(InfoEvent listener)
    {
        statusListeners.remove(InfoEvent.class, listener);
    }



    /**
     * Spusteni udalosti o zmene statusu kresliciho panelu
     * @param status Text, ktery je dan jako popisek
     */
    protected void fireStatusEvent(String status, int code)
    {
        Object[] listeners = statusListeners.getListenerList();
        // loop through each listener and pass on the event if needed
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2)
        {
            if (listeners[i] == InfoEvent.class)
            {
                // pass the event to the listeners event dispatch method
                ((InfoEvent) listeners[i + 1]).infoUpdated(status, code);
            }
        }
    }


    /**
     * Vykresleni grafiky do komponenty
     * @param g1 Reference na graficky objekt
     */
    protected synchronized  void paintComponent(Graphics g1)
    {

        super.paintComponent(g1);
        Point d,d2;
        HashMap<Point,Image> pics=new HashMap<Point, Image>();

        final Graphics2D g=(Graphics2D)g1.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(line3p);
        //g.setColor(new Color(0x4671D5));


        this.paintMaps(g);
        //Vykresleni vsech polygonu
        for(int i=0;i<objects.size();i++)
        {
            if(i==editedIndex) continue;
            DrawObject o = objects.get(i);
            if(o.deleted || o.polygons.size()==0) continue;
            Color c=o.polygonsColors;
            Integer type=o.polygonsTypes;
            Polygon p=o.polygonsGraphics;
            Font f = new Font(this.getFont().getFontName(), Font.BOLD, 14);
            g.setFont(f);
            FontMetrics fm1 = g.getFontMetrics();
            if(type==DrawPanel.TYPE_POLYGON)
            {
                g.setColor(new Color(c.getRGB() & 0x00ffffff | 0x70000000,true));
                g.fillPolygon(o.polygonsGraphics);
                g.setColor(c);
                g.drawPolygon(o.polygonsGraphics);


                if(o.centralPoint==null || o.title==null) continue;
                Point titlePoint=BingMapsProjection.getCoords(geo, o.centralPoint, controlPoint, zoom);
                //Vykresli popisek oblasti

                int strWidth=fm1.stringWidth(o.title);
                int strHeight=fm1.getHeight();

                g.setColor(new Color(0xffffffff ,true));
                //g.setColor(new Color(0xaaffffff ,true));
                g.fillRoundRect(titlePoint.x, titlePoint.y-strHeight, strWidth + 10, strHeight + 6,5,5);
                g.setColor(c);
                g.drawRoundRect(titlePoint.x, titlePoint.y-strHeight, strWidth + 10, strHeight + 6,5,5);
                g.setColor(new Color(0xFFA64300,true));
                g.drawString(o.title, titlePoint.x +6, titlePoint.y +2);

            }
            else if(type==DrawPanel.TYPE_POINTS)
            {
                for(int j=0;j<p.npoints;j++)
                {
                    g.setColor(new Color(0xFFFFFF));
                    g.fillRect(p.xpoints[j] - DIAMETER/2  , p.ypoints[j] - DIAMETER/2,DIAMETER, DIAMETER);
                    g.setColor(c);
                    g.drawRect(p.xpoints[j] - DIAMETER/2  , p.ypoints[j] - DIAMETER/2, DIAMETER, DIAMETER);
                }
            }
            else if(type==DrawPanel.TYPE_ARROWS)
            {
                for(int j=0;j<p.npoints;j++)
                {
                    //p.xpoints[j] - DIAMETER/2  , p.ypoints[j] - DIAMETER/2,DIAMETER, DIAMETER
                    int[] xpoints={p.xpoints[j] - DIAMETER/2,p.xpoints[j] + DIAMETER/2,p.xpoints[j]};
                    int[] ypoints={p.ypoints[j] - DIAMETER/2,p.ypoints[j] - DIAMETER/2,p.ypoints[j]};
                    g.setColor(new Color(0xFFFFFF));
                    g.fillPolygon(xpoints,ypoints,3);
                    g.setColor(c);
                    g.drawPolygon(xpoints,ypoints,3);

                    //Kontrola pritomnosti obrazku
                    if(o.images!=null && o.images.size()>j)
                    {
                        pics.put(new Point(p.xpoints[j]+DIAMETER,p.ypoints[j]+DIAMETER), o.images.get(j));

                    }
                }
            }
            else if(type==DrawPanel.TYPE_LINES)
            {
                g.setColor(c);
                g.drawPolyline(p.xpoints, p.ypoints, p.npoints);

                Font f2 = new Font(this.getFont().getFontName(), Font.BOLD, 12);
                g.setFont(f2);
                FontMetrics fm2 = g.getFontMetrics();

               

                if(o.centralPoint==null || o.title==null) continue;
                int strWidth=fm2.stringWidth(o.title);
                int strHeight=fm2.getHeight();
                Point titlePoint=BingMapsProjection.getCoords(geo, o.centralPoint, controlPoint, zoom);
                //Vykresli popisek oblasti
                g.setColor(new Color(0xAAffffff ,true));
                //g.setColor(new Color(0xaaffffff ,true));
                g.fillRoundRect(titlePoint.x, titlePoint.y-strHeight, strWidth + 10, strHeight + 6,5,5);
                //g.setColor(c);
                //g.drawRoundRect(titlePoint.x, titlePoint.y-strHeight, strWidth + 10, strHeight + 6,5,5);
                g.setColor(Globals.COLOR_LINE_TEXT);
                g.drawString(o.title, titlePoint.x +6, titlePoint.y +2);
                g.setFont(f);
            }
        }

        //Kresleni obrazku, ktere jsou ve fronte
        for (Map.Entry<Point, Image> entry : pics.entrySet())
        {
            g.setColor(new Color(0xffffff));
            Point p=entry.getKey();
            int diamX=60;
            int diamY=45;
            if(lastPoint.x<=p.x && lastPoint.x>=p.x-2*DIAMETER && lastPoint.y<=p.y && lastPoint.y>=p.y-2*DIAMETER)
            {
                diamX=400;
                diamY=300;
            }
            g.drawRoundRect(p.x,p.y, diamX, diamY, 5, 5);
            g.drawImage(entry.getValue(), p.x, p.y,diamX,diamY,new Color(0xffffff), content);

        }

        //this.updateMap(g);
        /***********************************************************************
         * Vykreslovani editovaneho polygonu
         **********************************************************************/
        //Spoji prvni a posledni bod
        g.setColor(this.color);
        if(points.size() >2 && insertMode==false && type==DrawPanel.TYPE_POLYGON)
        {
            d=(Point)points.get(0);
            d2=(Point)points.get(points.size()-1);
            g.drawLine(d.x , d.y , d2.x , d2.y );
        }

        for (int i=0;i<points.size();i++)
        {
            d=(Point)points.get(i);

            //Vykresleni usecky
            if(i<points.size()-1 && (type==DrawPanel.TYPE_LINES || type==DrawPanel.TYPE_POLYGON))
            {
                g.setColor(this.color);
                d2=(Point)points.get(i+1);
                g.drawLine(d.x , d.y , d2.x , d2.y );
            }
            //Vykresleni zachytnych krouzku, pokud je povoleno editovani
            if(editEnable)
            {
                if(type==DrawPanel.TYPE_ARROWS)
                {
                    int[] xpoints={d.x - DIAMETER/2,d.x + DIAMETER/2,d.x};
                    int[] ypoints={d.y - DIAMETER/2,d.y - DIAMETER/2,d.y};
                    g.setColor(new Color(0xFFFFFF));
                    g.fillPolygon(xpoints,ypoints,3);
                    g.setColor(this.color);
                    g.drawPolygon(xpoints,ypoints,3);
                    if(pointsImages!=null && pointsImages.size()>i)
                    {
                        g.setColor(new Color(0xffffff));
                        g.drawRoundRect(d.x+DIAMETER,d.y+DIAMETER, 400, 300, 5, 5);
                        g.drawImage(pointsImages.get(i), d.x+DIAMETER,d.y+DIAMETER,400,300,new Color(0xffffff), content);
                    }
                }
                else
                {
                    g.setColor(new Color(0xFFFFFF));
                    g.fillOval(d.x - DIAMETER/2  , d.y - DIAMETER/2,DIAMETER, DIAMETER);
                    g.setColor(this.color);
                    g.drawOval(d.x - DIAMETER/2  , d.y - DIAMETER/2, DIAMETER, DIAMETER);
                }
            }

        }
        if(this.mediaImage!=null)
        {
            g.setColor(new Color(0xffffff));
            g.drawRoundRect(lastPoint.x+DIAMETER,lastPoint.y+DIAMETER, 400, 300, 5, 5);
            g.drawImage(mediaImage, lastPoint.x+DIAMETER, lastPoint.y+DIAMETER,400,300,new Color(0xffffff), content);
        }
        
        /* Vytvori obrazek ikony letadla */
        if(this.iconPoint!=null)
        {
          Point pg=BingMapsProjection.getCoords(geo, iconPoint,this.getControlPoint(),zoom);
          if(iconImage==null)
            g.drawOval(pg.x - DIAMETER/2  , pg.y - DIAMETER/2, DIAMETER, DIAMETER);
          else
          {
            g.rotate(Math.toRadians(iconHeading), pg.x, pg.y);
            g.drawImage(iconImage, pg.x-20,pg.y-14 ,null);
          }
        }
        
        
        
        //statsPanel.revalidate();
        //statsPanel.repaint();
        //if(controlPoint!=null)
        //    g.drawOval(controlPoint.x - DIAMETER/2  , controlPoint.y - DIAMETER/2, DIAMETER, DIAMETER);

    }

    /**
     * Nastavi barvu nove vytvareneho polygonu
     * @param c
     */
    public void setColor(Color c)
    {
        this.color=c;
    }

    /**
     * Nastavi typ polygonu
     * 0 - plny polygon, 1 - pouze body, 2 - pouze body (sipky), 3 - lines
     * @param t
     */
    public void setType(int t)
    {
        this.type=t;
    }

    /**
     * Nastavi titulek a pozici stredoveho bodu
     * @param title
     * @param position
     */
    public void setTitle (String title, GPSPoint position)
    {
        this.centralPoint=position;
        this.title=title;
    }


    /**
     * Limity minimalniho a maximalniho poctu bodu pro editovany polygon
     * @param minimum
     * @param maximum
     */
    public void setLimits(int minimum, int maximum)
    {
        this.minPoints=minimum;
        this.maxPoints=maximum;
    }
    
    /** Nastavi ikonu letadla */
    public void setIconPoint(GPSPoint p,double heading)
    {
      this.iconPoint=p;
      this.iconHeading=heading;
    }
    /**
     * Vrati pole editovanych bodu
     * @return Vysledne pole GPS souradnic
     */
    public ArrayList<GPSPoint> getPoints()
    {
        ArrayList<GPSPoint> gpsList= new ArrayList<GPSPoint>();
        for(int i=0;i<points.size();i++)
        {
            gpsList.add(BingMapsProjection.getGPS(geo, controlPoint, points.get(i), zoom));
        }
        return gpsList;
    }

    /**
     * Vykresli mapy nactene v pameti
     * @param g Odkaz na graficky objekt
     */
    synchronized  void paintMaps(Graphics2D g)
    {
        if(this.controlPoint==null) return;
        Point cp=this.getControlPoint();
        MapPicture map;
        Image im;
        Point p;
        for(int i=0;i<this.maps.size();i++)
        {
            map=maps.get(i);
            p=map.getPoint();
            im=map.getImage();
            if(p.x +cp.x + im.getWidth(null) > 0 && p.x +cp.x < width
                    && p.y +cp.y + im.getHeight(null) > 0 && p.y +cp.y < height)

            g.drawImage(im, p.x+cp.x, p.y+cp.y, content);

        }
        im=null;
        map=null;
    }

    /**
     * Nacte mapy, co jsou ve fronte, v paralelnim procesu
     */
    synchronized public void loadMaps()
    {
        this.width=this.getWidth();
        this.height=this.getHeight();
        if(mapThread==null || !mapThread.isAlive())
        {
            mapThread = new Thread (this);
            mapThread.start();
        }
    }
    /**
     * Odstrani vsechny podklady
     */
    synchronized public void removeMaps()
    {
        this.maps.clear();
        this.queueTile.clear();
    }

    /**
     * Funkce umoznujici spusteni paralelniho behu dlasiho vlakna
     * Nacteni mapovyych podkladu ze stranek MS
     */
    public void run()
    {

        try{
            //GPSPoint gps=new GPSPoint(50.119048, 17.05384);
            //Zacne vykreslovani od prostredku


            BingMapsStat mapsEngine = new BingMapsStat("ArH99tY2gCd-518hK_uCJd32CWXnT2SMie0IQvdI6RpQwTLdzxuqxQayc00g2OSs");
            //mapsEngine.loadMapImage(MAP_WIDTH, MAP_HEIGHT, this.geo, this.zoom, BingMapsStat.TYPE_ROAD);
            //BingMapsProjection projection=mapsEngine.getProjection();
            //projection.adjustMap(MAP_WIDTH, MAP_HEIGHT);

            //while(int x=);

            while(!queueTile.isEmpty())
            {
                Point p=queueTile.pop();
                GPSPoint gps=BingMapsProjection.adjustCoords(geo, p.x+MAP_WIDTH/2, p.y+MAP_HEIGHT/2, this.zoom);
                mapsEngine.loadMapImage(MAP_WIDTH, MAP_HEIGHT, gps, this.zoom, mapType,p.x,p.y);
                Image map = mapsEngine.getImage();
                MapPicture mp=new MapPicture(map,gps,p);
                this.maps.add(mp);
                map=null;
                mp=null;
                this.mapReady=true;
            }
            //Vycisteni pri nacteni prilis velkeho poctu obrazku
            if(maps.size()>MAX_TILES_LOADED)
            {
                Image im;
                for(int i=0;i<maps.size();i++)
                {
                    Point p=maps.get(i).getPoint();
                    if(p.x +controlPoint.x + MAP_WIDTH <= 0 || p.x +controlPoint.x >= width
                      || p.y +controlPoint.y + MAP_HEIGHT <= 0 || p.y +controlPoint.y >= height)
                    {
                        maps.remove(i);
                        i--;
                    }
                }
            }

            //repaint();


        }catch(Exception E){}
    }

    /**
     * Ziska novy kontrolni bod v souradnicich obrazovky
     * @return kontrolni bod
     */
    protected Point getControlPoint()
    {
        if(controlPoint==null)
        {
            controlPoint=new Point(this.width/2 - MAP_WIDTH/2,this.height/2 - MAP_HEIGHT/2);

        }
        return controlPoint;
    }

    /**
     * Aplikuje transformaci posunutim kontrolniho bodu
     * pokud chybi mapovy podklad, prida jej do fronty
     * @param x Index posunuti horizontalne
     * @param y Index posunuti vertikalne
     */
    protected synchronized void translateControlPoint(int x, int y)
    {
        //Presune i kontrolni bod
        Point P=this.getControlPoint();
        P.x+=x;
        P.y+=y;

        //Prvni vyskyty souradnic kontrolnich bodu
        int baseX=P.x,baseY=P.y;
        //Nastavim bazove souradnice
        while(baseX<0)
            baseX+=MAP_WIDTH;
        while(baseX>MAP_WIDTH)
            baseX-=MAP_WIDTH;

        while(baseY<0)
            baseY+=MAP_HEIGHT;
        while(baseY>MAP_HEIGHT)
            baseY-=MAP_HEIGHT;

        for (int i=baseX;i<width+MAP_WIDTH;i+=MAP_WIDTH * 2)
        {
            for (int j=baseY;j<height+MAP_HEIGHT;j+=MAP_HEIGHT*2)
            {
                //Kontroluji ctyrokoli bodu a vkladam do fronty na nacteni obrazku
                boolean presentij=false,presenti2j=false,presentij2=false,presenti2j2=false;
                for (int k=0;k<maps.size();k++)
                {
                    //Body (i,j) (i,j-MAP_HEIGHT) (i-MAP_WIDTH,j) (i-MAP_WIDTH,j-MAP_HEIGHT)
                    //Bod je relativne vzhledem k pocatku
                    Point mPoint=maps.get(k).getPoint();
                    if(mPoint.x==i-P.x && mPoint.y==j-P.y)
                        presentij=true;
                    if(mPoint.x==i-P.x-MAP_WIDTH && mPoint.y==j-P.y)
                        presenti2j=true;
                    if(mPoint.x==i-P.x-MAP_WIDTH && mPoint.y==j-P.y-MAP_HEIGHT)
                        presenti2j2=true;
                    if(mPoint.x==i-P.x && mPoint.y==j-P.y-MAP_HEIGHT)
                        presentij2=true;

                }

                //Prida bod do fronty, pozici ma relativni vzhledem ke kontrolnimu bodu
                if(!presentij && !isIncluded(i-P.x,j-P.y))
                {
                    queueTile.add(new Point(i-P.x,j-P.y));
                }
                if(!presenti2j && !isIncluded(i-P.x-MAP_WIDTH,j-P.y))
                {
                    queueTile.add(new Point(i-P.x-MAP_WIDTH,j-P.y));
                }
                if(!presenti2j2 && !isIncluded(i-P.x-MAP_WIDTH,j-P.y-MAP_HEIGHT))
                {
                    queueTile.add(new Point(i-P.x-MAP_WIDTH,j-P.y-MAP_HEIGHT));
                }
                if(!presentij2 && !isIncluded(i-P.x,j-P.y-MAP_HEIGHT))
                {
                    queueTile.add(new Point(i-P.x,j-P.y-MAP_HEIGHT));
                }
            }
        }
        //System.out.println("Pocet udaju v zasobniku: "+queueTile.size());
    }

    /**
     * Zjisti, jestli je zadany bod jiz zarazen ve fronte ke zpracovani k nacteni obrazku
     * @param x
     * @param y
     * @return
     */
    private boolean isIncluded(int x,int y)
    {
      if(queueTile==null)
        return false;
        for(int i=0;i<queueTile.size();i++)
        {
            Point p = queueTile.get(i);
            if(p.x==x && p.y==y)
                return true;
        }
        return false;
    }

    /**
     * Posune vsechny body o zadany ofset v ose X a Y
     * Tyto pole: polygons, polygonsGraphics,points, maps
     * @param x
     * @param y
     */
    protected void translatePoints(int x, int y)
    {
        for(int i=0;i<objects.size();i++)
        {
            //ArrayList<Point> list = polygons.get(i);
            Polygon p=objects.get(i).polygonsGraphics;
            for(int j=0;j<p.npoints;j++)
            {
                //list.get(j).x+=x;
                //list.get(j).y+=y;
                p.xpoints[j]+=x;
                p.ypoints[j]+=y;
            }
        }

        for(int i=0;i<points.size();i++)
        {
            points.get(i).x+=x;
            points.get(i).y+=y;
        }
         



        translateControlPoint(x,y);

    }

    /**
     * Zazoomoje nebo odzoomuje aktualni zobrazeni
     * @param zoomIn Priznak zvetseni/zmenseni
     * @param x
     * @param y
     */
    protected void zoomPoints(boolean zoomIn, int x, int y)
    {
        float coef=1.0f;
        if(!zoomIn)
            coef=-0.5f;
        for(int i=0;i<objects.size();i++)
        {
            ArrayList<GPSPoint> list = objects.get(i).polygons;
            //Polygon p=polygons.get(i);
            for(int j=0;j<list.size();j++)
            {
                //TODO: Zde je nutny prepocet vzdy z GPS souradnic
                //list.get(j).x*=coef;
                //list.get(j).y*=coef;
                Point point=BingMapsProjection.getCoords(geo,list.get(j), controlPoint, zoom);
                //p.xpoints[j]=(int)(p.xpoints[j] + coef * (p.xpoints[j] - x));
                //p.ypoints[j]=(int)(p.ypoints[j] + coef * (p.ypoints[j] - y));
                objects.get(i).polygonsGraphics.xpoints[j]=point.x;
                objects.get(i).polygonsGraphics.ypoints[j]=point.y;
            }
        }

        for(int i=0;i<points.size();i++)
        {
            points.get(i).x=(int)(points.get(i).x + coef * (points.get(i).x - x));
            points.get(i).y=(int)(points.get(i).y + coef * (points.get(i).y - y));
        }
    }


    /**
     * Zazoomuje na zadane souradnice
     * @param x
     * @param y
     */
    public void zoomIn(int x, int y)
    {
        if(zoom<21)
        {
            zoom++;
            int tmpx=(int) (x - 2 * (x - controlPoint.x));
            int tmpy=(int) (y - 2 * (y - controlPoint.y));
            this.removeMaps();

            translateControlPoint(- (x - controlPoint.x), - (y - controlPoint.y));
            zoomPoints(true,x,y);
            loadMaps();

        }
        this.fireStatusEvent("Úroveň zvětšení: "+zoom,InfoEvent.CODE_INFO);
    }

    /**
     * Odzoomuje vzhledem k zadanym soouradnicim
     * @param x
     * @param y
     */
    public void zoomOut(int x, int y)
    {
        //odzoomovani
        if(zoom>0)
        {
            zoom--;
            int tmpx=x-(x-controlPoint.x)/2;
            int tmpy=y-(y-controlPoint.y)/2;
            this.removeMaps();
            translateControlPoint(x-tmpx, y-tmpy);
            zoomPoints(false,x,y);

            loadMaps();
        }

        this.fireStatusEvent("Úroveň zvětšení: "+zoom,InfoEvent.CODE_INFO);
    }


    /**
     * Vrati, jestli je mozne ukoncit editaci daneho polygonu
     * @return Priznak, ze je mozne polygon uzavrit
     */
    protected boolean canClose()
    {
        if(points.size()<minPoints)
        {
            fireStatusEvent("POZOR: Polygon má příliš málo bodů.",InfoEvent.CODE_ERROR);
            return false;
        }
        if(type!=DrawPanel.TYPE_POLYGON)
            return true;
        Point p1=points.get(0);
        Point p2=points.get(points.size()-1);
        boolean intersection=false;
        for(int i=1;i<points.size()-2;i++)
        {
            //Hledani pruseciku
            try
            {
                Point P=DrawPanel.getCrossPoint((Point)points.get(i), (Point)points.get(i+1), p1,p2);
                intersection=true;
                break;
            }
            catch (Exception E)
            {   /*nema prusecik*/  }

        }
        if(intersection)
            fireStatusEvent("POZOR: Nemožno uzavřít polygon tak, aby nedocházelo ke křížení.",InfoEvent.CODE_ERROR);
        return !intersection;
    }










    /***************************************************************************
     * Pomocna trida pro mapovani obrazku do mapy
     **************************************************************************/
    protected class MapPicture
    {
        private Image image;
        private GPSPoint gps;
        private Point coords;

        public MapPicture(Image image, GPSPoint gps, Point p)
        {
            this.coords=new Point(p);
            this.gps=new GPSPoint(gps);
            this.image=image;
        }

        public Image getImage()
        {
            return image;
        }
        public GPSPoint getGPS()
        {
            return gps;
        }
        public Point getPoint()
        {
            return coords;
        }

    }

    /***************************************************************************
     * Pomocna trida pro vykreslovane objekty
     **************************************************************************/
    public class DrawObject
    {
        /**GPS data o polygonu*/
        public ArrayList<GPSPoint> polygons;

        /**Dulezite pro vykresleni*/
        public Polygon polygonsGraphics;
        /**Barvy prislusnych polygonu*/
        public Color polygonsColors;
        /**Typy polygonu: 0 - plny polygon, 1 - pouze body, 2 - pouze body (sipky), 3 - lines*/
        public Integer polygonsTypes;

        /**Popisek*/
        public String title;
        /**Bod uvnitr polygonu*/
        public GPSPoint centralPoint;

        /**Priznak smazani*/
        public boolean deleted=false;

        /**Doprovodny obrazek*/
        public ArrayList<Image> images=null;

    }




    /***************************************************************************
     * Pomocna trida pro praci s udalostmi
     **************************************************************************/

    public class MainMouseListener extends MouseAdapter
    {

        /**Index presouvaneho bodu v seznamu*/
        private int draggedIndex=-1;


        /**
         * Funkce: Kliknuti na kreslici plochu
         * 1) Vlozeni kontrolniho bodu
         * 2) Zobrazeni statistickeho formulare
         * @param evt
         */
        @Override
        public void mouseClicked(MouseEvent evt)
        {
            content.requestFocusInWindow();
            if(!editEnable)
            {
                 
                return;
            }
            //GPSPoint p=BingMapsProjection.getGPS(geo, controlPoint, evt.getPoint(), zoom);
            //JOptionPane.showMessageDialog(null,BingMapsProjection.getCoords(geo, p, controlPoint, zoom));
            if(maxPoints<=points.size() && maxPoints!=0) return;
            if(evt.getButton()==MouseEvent.BUTTON1 && evt.getClickCount()==1)
            {
                for(int i=0;i<points.size();i++)
                {
                    //Aby se nevkladalo na stejne misto dvakrat
                    Point point=(Point)points.get(i);
                    if(Math.abs(evt.getX() - point.x) < DIAMETER/2 && Math.abs(evt.getY() - point.y) < DIAMETER/2)
                    {
                        return;
                    }
                }
                if(type==DrawPanel.TYPE_POINTS || type==DrawPanel.TYPE_ARROWS)
                {
                    Point point=new Point(evt.getX(),evt.getY());
                    points.add(point);
                    if(mediaImage!=null)
                        pointsImages.add(mediaImage);
                    
                    mediaImage=null;

                    fireStatusEvent("Bod vložen", points.size()==maxPoints ? InfoEvent.CODE_DRAWING_FINISHED : InfoEvent.CODE_INFO);
                    repaint();
                }
                else
                {
                    for(int i=0;i<points.size();i++)
                    {
                        //Vkladani novych bodu do usecek
                        try
                        {
                            double distance;
                            if(i==points.size()-1){
                                distance=getDistance(evt.getPoint(), (Point)points.get(i), (Point)points.get(0));
                            } else {
                                distance=getDistance(evt.getPoint(), (Point)points.get(i), (Point)points.get(i+1));
                            }

                            if(distance<MAX_DISTANCE)
                            {
                                points.add(i+1, evt.getPoint());
                                
                                content.repaint();
                                return;
                            }
                        }
                        catch(Exception E){}
                    }
                

                if(insertMode)
                {
                    //Vlozeni noveho kontrolniho bodu na konec
                    Point point=new Point(evt.getX(),evt.getY());
                    boolean intersection=false;
                    if(type==DrawPanel.TYPE_LINES || type==DrawPanel.TYPE_POLYGON)
                    {
                        for(int i=0;i<points.size()-2;i++)
                        {

                            //Hledani pruseciku
                            try
                            {
                                Point P=DrawPanel.getCrossPoint((Point)points.get(i), (Point)points.get(i+1), (Point)points.get(points.size()-1),point);
                                intersection=true;
                                break;
                            }
                            catch (Exception E)
                            {   /*nema prusecik*/  }
                        }
                    }
                    if(!intersection)
                    {
                        points.add(point);
                        if(mediaImage!=null)
                            pointsImages.add(mediaImage);

                        mediaImage=null;
                        fireStatusEvent("Bod vložen", points.size()==maxPoints ? InfoEvent.CODE_DRAWING_FINISHED : InfoEvent.CODE_INFO);
                        System.out.println("X: " + evt.getX() + ", Y: " + evt.getY() + ", Kliknuto: " + evt.getClickCount());
                    }
                    else
                    {
                        fireStatusEvent("Bod nebyl vložen, neboť se úsečky nesmí křížit.",InfoEvent.CODE_ERROR);
                    }
                    content.repaint();
                }
                }
            }
        }


        /**
         * Udalost pretahnuti mysi
         * 1) edituje aktualni bod
         * 2) pohne celou mapou
         * @param evt
         */
        @Override
        public void mouseDragged(MouseEvent evt)
        {

            content.requestFocusInWindow();

            if(!editEnable)
                draggedIndex=-1;
            if(draggedIndex<0)
            {
                //Posunuti souradnic
                Point P=evt.getPoint();
                translatePoints(P.x-lastPoint.x, P.y-lastPoint.y);
                
                loadMaps();
                lastPoint=P;
            }

            if( draggedIndex<0 || !editEnable)
            {
                content.repaint();
                return;
            }


            Point point=evt.getPoint();
            boolean intersection=false;
            if(type==DrawPanel.TYPE_POLYGON)
            {
                for(int i=0;i<points.size()-1;i++)
                {
                    try
                    {
                        if((i<this.draggedIndex-2 || i>this.draggedIndex)&& !(i>=points.size()-2 && draggedIndex==0))
                        {   DrawPanel.getCrossPoint((Point)points.get(i), (Point)points.get(i+1), (Point)points.get(this.draggedIndex==0 && !insertMode ? points.size()-1 : this.draggedIndex-1),point);
                            intersection=true;
                            break;}
                    }
                    catch (Exception E)
                    {}

                    try
                    {
                       if((i<this.draggedIndex-1 || i>this.draggedIndex+1)&& !(i<1 && draggedIndex==points.size()-1))
                        {   DrawPanel.getCrossPoint((Point)points.get(i), (Point)points.get(i+1), point, (Point)points.get(this.draggedIndex==points.size()-1 && !insertMode ? 0 : this.draggedIndex+1 ));
                            intersection=true;
                            break;  }
                    }
                    catch (Exception E)
                    {  }
                }
                //Posledni kontrola uzavreni polygonu
                if(!insertMode  && type==DrawPanel.TYPE_POLYGON)
                {
                    try {
                        if(this.draggedIndex>1 && this.draggedIndex<points.size()-1) {
                        DrawPanel.getCrossPoint((Point)points.get(points.size()-1), (Point)points.get(0), (Point)points.get(this.draggedIndex-1),point);
                        intersection=true; }
                    }catch(Exception E){}
                    try {
                        if(this.draggedIndex<points.size()-2 && this.draggedIndex>0) {
                        DrawPanel.getCrossPoint((Point)points.get(points.size()-1), (Point)points.get(0), point, (Point)points.get(this.draggedIndex+1));
                        intersection=true; }
                    }catch(Exception E){}
                }
            }
            if(!intersection)
            {
                points.set(this.draggedIndex, point);
            }
            else
            {
                fireStatusEvent("V polygonu se nesmí křížit úsečky.",InfoEvent.CODE_INFO);
            }
            content.repaint();
        }



        /**
         *
         * Zmacknuti mysi nad nejakym bodem (popr. prvni volani pred tahnutim)
         * 1) Namacknuti leveho tlacitka - oznaceni bodu pro tazeni
         * 2) Prave tlacitko - smazani bodu
         * @param evt
         */
        @Override
        public void mousePressed(MouseEvent evt)
        {
            content.requestFocusInWindow();
            lastPoint=evt.getPoint();
            if(!editEnable) return;
            //if(editEnable)
            //    fireStatusEvent("Kliknutím začněte kreslit polygon. Stisknutím klávesy Enter ukončíte editaci.");

            //Stisk tlacitka nad nejakym editovatelnym bodem
            if(evt.getButton()==MouseEvent.BUTTON1)
            {
                //Tahnuti bodem levym tlacitkem
                int index=-1;
                for(int i=points.size()-1;i>=0;i--)
                {
                    Point d=(Point)points.get(i);
                    if(Math.abs(evt.getX() - d.x) < DIAMETER/2 && Math.abs(evt.getY() - d.y) < DIAMETER/2)
                    {
                        index=i;
                        break;
                    }
                }
                this.draggedIndex=index;
            }
            else if(evt.getButton()==MouseEvent.BUTTON3)
            {
                //Smazani bodu pravym tlacitkem
                for(int i=points.size()-1;i>=0;i--)
                {
                    Point d=(Point)points.get(i);
                    if(Math.abs(evt.getX() - d.x) < DIAMETER/2 && Math.abs(evt.getY() - d.y) < DIAMETER/2)
                    {
                        //Hledani pruseciku nove vznikle usecky se stavajicimi
                        boolean intersection=false;
                        if(type==DrawPanel.TYPE_LINES || type==DrawPanel.TYPE_POLYGON)
                        {
                            for(int j=0;j<points.size();j++)
                            {
                                try
                                {
                                    Point d2=(Point)points.get(i==points.size()-1 && !insertMode ? 0 : i+1);
                                    Point d3=(Point)points.get(i==0 && !insertMode ? points.size()-1 : i-1);
                                    if((j>i-3 && j<i+2)  || (i==points.size()-1 && j==0)
                                            || (i==1 && j==points.size()-1) || (i==0 && j>=points.size()-2))
                                    {   continue;   }
                                    if(j==points.size()-1){
                                        if(insertMode ||type==DrawPanel.TYPE_LINES) break;
                                        Point P=DrawPanel.getCrossPoint((Point)points.get(j), (Point)points.get(0), d2,d3);
                                    }else{
                                        Point P=DrawPanel.getCrossPoint((Point)points.get(j), (Point)points.get(j+1), d2,d3);
                                    }
                                    intersection=true;
                                    break;
                                }
                                catch (Exception E)
                                { System.out.println(E.toString());/*nema prusecik*/ }
                            }
                        }
                        if(!intersection)
                        {
                            points.remove(i);
                            try
                            {
                                pointsImages.remove(i);
                            }
                            catch(Exception E)
                            {

                            }
                            content.repaint();
                        }
                        else
                        {
                            fireStatusEvent("Bod nebyl smazán, neboť by výsledný polygon obsahoval zkřížené úsečky.",InfoEvent.CODE_ERROR);
                        }
                        content.repaint();
                        break;
                    }
                }
            }

        }


        /**
         * Zaznamenava pohyb mysi
         * 1) Meni kurzor, pokud prejede nad bodem
         * 2) Zvetsi obrazek pod kurzorem
         * @param evt
         */
        @Override
        public void mouseMoved(MouseEvent evt)
        {

            if(!content.hasFocus()) content.requestFocusInWindow();
            lastPoint=evt.getPoint();

            if(editEnable)
            {
                //Zjisti,jestli se kurzor nachazi nad bodem, ktery je editovatelny
                boolean over=false;
                String statusText=new String();
                for(int i=0;i<points.size();i++)
                {
                    Point d=(Point)points.get(i);
                    if(Math.abs(evt.getX() - d.x) < DIAMETER/2 && Math.abs(evt.getY() - d.y) < DIAMETER/2)
                    {
                        content.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        over=true;
                        statusText="Tažením upravíte pozici bodu. Kliknutím pravým tlačítkem bod smažete.";
                        break;
                    }
                }
                if(!over)
                {
                    content.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    statusText="Kliknutím začněte kreslit polygon. Kliknutím na úsečku vložíte řídicí bod. Stiskem klávesy Enter polygon uzavřete.";//Stisknutím klávesy Enter ukončíte editaci.";
                }
                fireStatusEvent(statusText,InfoEvent.CODE_INFO);
            }
            repaint();


        }




        /**
         * Skrolovani koleckem - zazoomovani nebo odzoomovani
         * @param e
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            int notches = e.getWheelRotation();
            if(notches<0)
            {
                //zazoomovani zvetsenim
                zoomIn(e.getX(),e.getY());
            }
            if(notches>0)
            {
                //odzoomovani
                zoomOut(e.getX(),e.getY());
            }
        }
    }






    /***************************************************************************
     * Pomocna trida pro praci s udalostmi
     **************************************************************************/
    public class MainKeyListener extends KeyAdapter
    {
        /**
         * Stisknuti klavesy
         * 1) Enter - uzavreni polygonu a odpaleni udalosti
         * @param e
         */
        @Override
        public void keyReleased(KeyEvent e)
        {
            //Pri stisku klavesy Enter se snazi uzavrit aktualne editovany polygon
            //a vlozit ho do trvale vykreslovaci struktury
            if(e.getKeyChar()==KeyEvent.VK_ENTER)
            {

                if(editEnable && canClose())
                {
                    //Ulozeni kolekce bodu do seznamu polygonu
                    if(editEnable)
                    {
                        fireStatusEvent("Polygon uzavřen.",InfoEvent.CODE_DRAWING_FINISHED);
                    }
                    insertMode=false;
                    repaint();

                }


            }
            /*
            else if(e.getKeyChar()==KeyEvent.VK_E)
            {
                editEnable=true;
                repaint();
            }

            else if(e.getKeyChar()==KeyEvent.VK_I)
            {
                insertMode=!insertMode;
                repaint();
            }*/
        }
    }

    /***************************************************************************
     * Pomocna trida pro praci s udalostmi
     **************************************************************************/
    public class MainCompListener extends ComponentAdapter
    {
        /**
         * Prekresleni panelu po zmene velikosti okna
         * @param e
         */
        @Override
        public void componentResized(ComponentEvent e)
        {
            Component c = e.getComponent();
            width=c.getWidth();
            height=c.getHeight();
            translatePoints(0,0);
            loadMaps();
        }


        @Override
        public void componentShown(ComponentEvent e)
        {
            Component c = e.getComponent();
            width=c.getWidth();
            height=c.getHeight();

        }

    }

}
