/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.gja.proj.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Martin Falticko
 */
public class ModelData 
{
  /*
   * Jmeno souboru s modely
   */
  public static final String MODEL_FILE="Models.xml";
  /**
   * Jmeno modelu
   */
  private String name;
  //Adresar s daty modelu
  private String directory;
  private String description;
  /**
   * Binarni obrazek
   */
  private Image picture;
  /**
   * Seznam souboru s daty
   */
  private ArrayList<String> dataFiles;
  
  
  ModelData()
  {
    name="";
    description="";
    picture=null;
    dataFiles=new ArrayList<String>();
  }
  
  void setName(String nm)
  {
    this.name=nm;
  }
  
  String getName()
  {
    return name;
  }
  
  void setDescription(String nm)
  {
    this.description=nm;
  }
  
  String getDescription()
  {
    return description;
  }
  
  /**
   * Prida soubor s daty se zadanou cestou do databaze a zkopiruje jej do zadane slozky
   * @param path 
   */
  void addDataFile(String path)
  {
    
  }
  
  void save()
  {
    
  }
  void load()
  {
    
  }
  
  /**
   * Nacte modely ze zadane cesty ve filesystemu
   * @param path cesta k modelu
   * @return arraylist
   */
  public static ArrayList<ModelData> LoadModels(String path)
  {
    ArrayList<ModelData> list = new ArrayList<ModelData>();
    
    return list;
  }
  
  public static void SaveModels(String path,ArrayList<ModelData> data)
  {
    
  }
  
}
