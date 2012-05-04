/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Mafio
 */
public class TelemetryData {
  /** Typ dat - 8 bitu */
  public static final int T_DATA8 = 0;
  /** Typ dat - 16 bitu */
  public static final int T_DATA16 = 1;
  /** Typ dat - 24 bitu */
  public static final int T_DATA24 = 4;
  /** Typ dat - Cas */
  public static final int T_TIME = 5;
  /** Typ dat - 32 bitu */
  public static final int T_DATA32 = 8;
  /** Typ dat - GPS */
  public static final int T_GPS = 9;
  /** Typ dat - 37 bitu */
  public static final int T_DATA37 = 12;

  /** Info o pouzitem senzoru */
  public static class TelemetrySensor implements Comparable<TelemetrySensor>{
    long id;
    String name;
    TreeSet<TelemetryVar> variables=new TreeSet<TelemetryVar>();

    String getName(){return this.name;}

    public TelemetrySensor(long _id, String _name)
    {
      id=_id;
      name=_name;
    }

    void addVariable(TelemetryVar v)
    {
      variables.add(new TelemetryVar(v));
    }


    /**
     * Ziska odkaz ze seznamu letovych hodnot
     */
    public TelemetryVar getVar(int param)
    {
      for(TelemetryVar v:variables)
      { if(v.param==param) return v;}
      return null;
    }

    public int compareTo(TelemetrySensor o) {
      if(this.id>o.id)
        return 1;
      else if (this.id==o.id)
        return 0;
      else return -1;
    }
  };






  /** Info o zobrazene promenne */
  public static class TelemetryVar implements Comparable<TelemetryVar>{
    int param;
    String name;
    String unit;
    ArrayList<TelemetryItem> data ;

    TelemetryVar(int _param,String _name,String _unit)
    {
      param=_param;
      name=_name;
      unit=_unit;
      data=new ArrayList<TelemetryItem>();
    }

    public TelemetryVar(TelemetryVar e) {
      param=e.param;
      name=e.name;
      unit=e.unit;
      data=new ArrayList<TelemetryItem>(e.data);
    }
    public void addItem(TelemetryItem i)
    {
      data.add(new TelemetryItem(i));
    }

    public int compareTo(TelemetryVar o) {
      return this.param - o.param;
    }
  }





  /** Polozka zaznamu telemetrie */
  public static class TelemetryItem implements Comparable<TelemetryItem>{
    private int dataType;
    private int decimals;
    private int value;
    private long timestamp;

    TelemetryItem(int type,int dec,int _value,long _timestamp)
    {
      dataType=type;
      decimals=dec;
      value=_value;
      timestamp=_timestamp;
    }

    TelemetryItem(TelemetryItem i)
    {
      dataType=i.dataType;
      decimals=i.decimals;
      value=i.value;
      timestamp=i.timestamp;
    }

    int getType(){
      return dataType;
    }

    /**
     * Vrati udaj typu Double
     * @return
     */
    double getDouble()
    {
      switch(dataType)
      {
        case T_DATA8:
        case T_DATA16:
        case T_DATA24:
        case T_DATA32:
        case T_DATA37:
          return value * Math.pow(10, -decimals);
        default: return 0.0;
      }
    }

    /**
     * Vrati udaj typu Int
     */
    int getInt()
    {
      return value;
    }

    public int compareTo(TelemetryItem o) {
      if(this.timestamp>o.timestamp)
        return 1;
      else if (this.timestamp==o.timestamp)
        return 0;
      else return -1;
    }
  }

  /**
   * Struktura s telemetrickymi udaji
   */
  private TreeSet<TelemetrySensor> data=new TreeSet<TelemetrySensor>();





  /**
   * Vrati seznam s nactenymi telemetrickymi daty
   * @return
   */
  public TreeSet<TelemetrySensor> getData(){return data;}

  /**
   * Vrati senzor podle zadaneho ID
   */
  public TelemetrySensor getSensor(long id)
  {
    for(TelemetrySensor s: data)
    {
      if(s.id==id)
        return s;
    }
    return null;
  }


  /**
   * Nacte data ze souboru. Pozna, jestli se jedna o *.log nebo *.xml
   * @param file
   */
  public void loadData(String file)
  {
    int mid = file.lastIndexOf(".");
    String ext = file.substring(mid + 1, file.length());

    if(ext.equalsIgnoreCase("log"))
    {
      this.data.clear();
      loadCSV(file);
    }
    else
    {
      JOptionPane.showMessageDialog(null, "Error: " + "Neznámá koncovka souboru", "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  /**
   * Nahraje soubor CSV
   */
  void loadCSV(String file) {
    int line=0;
    try {
      // Open the file that is the first
      // command line parameter
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader in = new InputStreamReader(fis, "windows-1250");

      //FileInputStream fstream = new FileInputStream(file);
      // Get the object of DataInputStream
      //DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(in);

      String strLine;
      //Read File Line By Line
      while ((strLine = br.readLine()) != null) {
        line++;
        strLine=strLine.trim();
        //Prvni znak - komentar?
        if(strLine.startsWith("#"))
          continue;

        String arr[]=strLine.split(";");
        if(arr!=null && arr.length>0)
          parseLineParams(arr);
      }
      //Close the input stream
      in.close();
    } catch (Exception e) {//Catch exception if any
      JOptionPane.showMessageDialog(null, "Chyba na řádku " + String.valueOf(line)+", " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Projede zadany seznam retezcu - ziskany radek
   */
  void parseLineParams(String params[])
  {
    final int ST_TIME=0;
    final int ST_DEVICE_ID=1;
    final int ST_PARAM_NUM=2;
    final int ST_DATATYPE=3;
    final int ST_DECIMALS=4;
    final int ST_VALUE=5;
    final int ST_LABEL=6;
    final int ST_UNIT=7;
    
    int state=ST_TIME;
    long timestamp=0;
    long deviceId=0;
    int paramId=0;
    int dataType=0;
    int decimals=0;
    String label="";
    String unit="";
    if(params==null)
      return;
    for( String param: params)
    { 
      switch(state)
      {
        case ST_TIME:
          timestamp=Integer.parseInt(param);
          state=ST_DEVICE_ID;
          break;
        case ST_DEVICE_ID:
          deviceId=Integer.parseInt(param);
          state=ST_PARAM_NUM;
          break;
        case ST_PARAM_NUM:
          paramId=Integer.parseInt(param);
          if(timestamp==0)
            state=ST_LABEL;
          else 
            state=ST_DATATYPE;
          break;
        case ST_LABEL:
          label=param;
          //Vlozeni noveho cidla a ukonceni nacitani radku
          if(timestamp==0 && paramId==0)
          {
            TelemetrySensor sensor=new TelemetrySensor(deviceId, label);
            this.data.add(sensor);
            return;
          }
          else
          {
            //Nyni nacitam popisek parametru
            state=ST_UNIT;
          }
          break;
        case ST_UNIT:
          unit=param;
          TelemetryVar var=new TelemetryVar(paramId, label, unit);
          TelemetrySensor s=this.getSensor(deviceId);
          if(s!=null)
          {
            s.addVariable(var);
          }
          //Vypadne z funkce
          return;
        case ST_DATATYPE:
          dataType=Integer.parseInt(param);
          state=ST_DECIMALS;
          break;
        case ST_DECIMALS:
          decimals=Integer.parseInt(param);
          state=ST_VALUE;
          break;
        case ST_VALUE:
          long val=Long.parseLong(param);
          //Pokusi se vlozit novy zaznam
          TelemetryItem item=new TelemetryItem(dataType,decimals,(int)val,timestamp);
          TelemetrySensor sen=this.getSensor(deviceId);
          if(sen!=null)
          {
            TelemetryVar par = sen.getVar(paramId);
            if(par!=null)
            {
              par.addItem(item);
            }
          }
          state=ST_PARAM_NUM;
          break;
      }
    }
  }

}
