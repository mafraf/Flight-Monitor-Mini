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
