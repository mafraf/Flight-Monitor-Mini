/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj.utils;

import cz.vutbr.fit.gja.proj.InfoEvent;
import java.awt.Color;
import javax.swing.event.EventListenerList;

/**
 * Gloabalni konstanty a objekty pouzivane v aplikaci.
 *
 * @author Martin Falticko
 */
public class Globals
{
  /**
   * Instance Objektu s konfiguraci
   */
    public static Config config=Config.loadConfig(Config.defaultConfigFile);
    /**
     * Barva pro vykresleni plochy CHKO na mape
     */
    public static Color COLOR_RESERVE=new Color(0x4671D5);
    /**
     * Barva pro vykresleni plochy oblasti na mape
     */
    public static Color COLOR_SPREAD=new Color(0xff3333);
    /**
     * Barva pro vykresleni plochy editovane oblasti na mape
     */
    public static Color COLOR_SPREAD_EDIT=new Color(0x883333);

    /**
     * Barva lines
     */
    public static Color COLOR_LINE=new Color(0x508D00);
    /**
     * Barva popisu lines
     */
    public static Color COLOR_LINE_TEXT=new Color(0x345c00);




 

    
    //Objekty cekajici na udalost zmenu stavu
    private static EventListenerList statusListeners = new EventListenerList();

    /**
     * Pridani konzumenta, ktery prebira informace o statusu komponenty
     * @param listener
     */
    public static void addSpeciesChangedListener(InfoEvent listener)
    {
        statusListeners.add(InfoEvent.class, listener);
    }

    /**
     * Smazani konzumenta
     * @param listener
     */
    public static void removeSpeciesChangedListener(InfoEvent listener)
    {
        statusListeners.remove(InfoEvent.class, listener);
    }

    /**
     * Spusteni udalosti o zmene statusu kresliciho panelu
     * @param status Text, ktery je dan jako popisek
     */
    public static void fireSpeciesChangedEvent(String status, int code)
    {
        Object[] listeners = statusListeners.getListenerList();
        // loop through each listener and pass on the event if needed
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2)
        {
            if (listeners[i] == InfoEvent.class)
            {
                // pass the event to the listeners event dispatch method
                ((InfoEvent) listeners[i + 1]).infoUpdated(status,code);
            }
        }
    }
}
