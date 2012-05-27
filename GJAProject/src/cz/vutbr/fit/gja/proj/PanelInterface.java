package cz.vutbr.fit.gja.proj;

import cz.vutbr.fit.gja.proj.utils.*;

/**
 * Rozhrani pro panely ukazatelu a graf
 * @author Ondrej Vagner
 */
public interface PanelInterface 
{
 /**
  * Metoda pro nastaveni aktualni hodnoty
  * @param item aktualni hodnota
  */     
  public void setData(TelemetryData.TelemetryItem item);
 
 /**
  * Metoda pro nastaveni zobrazovaneho ukazatele (budiku)
  * @param max maximalni zobrazovana hodnota 
  */      
  public void changeSpeed(double max);  
  
 /**
  * Metoda pro nahrani vsech dat 
  * @param data strukura obsahujici data
  * @param max pocet zaznamu 
  */    
  public void setAllData(TelemetryData.TelemetryVar data, int max);
  
 /**
  * Metoda pro nastaveni aktualniho casu 
  * @param time aktualni cas
  */   
  public void acTime(double time);
}
