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
