/*
 * GJAProjectApp.java
 */

package cz.vutbr.fit.gja.proj;

import cz.vutbr.fit.gja.proj.utils.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class GJAProjectApp extends SingleFrameApplication {

    /**
     * Konfigurace aplikace
     */
    private Config configuration;

    /**
     * Data z telemetrickych cidel
     */
    private TelemetryData telemetry=new TelemetryData();
    
    /**
     * Seznam modelu - prozacatku inicializovane,aby nehazelo Null Pointer
     */
    private ArrayList<ModelData> modelParams=new ArrayList<ModelData>();

    /**
     * Pole s parametry spusteni programu
     */
    private String[] args;




    
    /**
     * Vraci instanci objektu s konfiguraci
     * @return Instance tridy Config s aktualni konfiguraci aplikace
     */
    public Config getConfiguration() {
      return configuration;
    }

    public TelemetryData getTelemetry(){
      return telemetry;
    }
    
    /*
     * Vrati seznam platnych modelu
     */
    public ArrayList<ModelData> getModelData() {
      return modelParams;
    }



    /**
     * Volano pred korektnim ukoncenim aplikace - uklid
     */
    @Override
    protected void shutdown() {



      super.shutdown();
    }


    /**
     * Volano pred startup. Ulozeni parametru spusteni
     * @param args Parametry spusteni aplikace
     */
    @Override
    protected void initialize(String[] args) {
        this.args = args;

        super.initialize(args);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
      // nacteni konfigurace
      this.configuration = Config.loadConfig(Config.defaultConfigFile);
      this.modelParams=ModelData.LoadModels(configuration.getModelPath()+"/"+ModelData.MODEL_FILE); 
 

      // zobrazit hlavni formular
      show(new GJAProjectView(this));
    
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root 
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of GJAProjectApp
     */
    public static GJAProjectApp getApplication() {
        return Application.getInstance(GJAProjectApp.class);
    }

    /**
     * Main method launching the application.
     * @param args Paremetry spusteni programu
     */
    public static void main(String[] args) {
        launch(GJAProjectApp.class, args);
    }
}
