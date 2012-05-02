/*
 * GJAProjectView.java
 */
package cz.vutbr.fit.gja.proj;


import cz.vutbr.fit.gja.proj.utils.BingMapsStat;
import cz.vutbr.fit.gja.proj.utils.Globals;
import cz.vutbr.fit.gja.proj.utils.GPSPoint;
import cz.vutbr.fit.gja.proj.utils.ModelData;
import java.awt.Image;
import java.awt.Toolkit;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.application.Application;



/**
 * The application's main frame.
 *
 */
public class GJAProjectView extends FrameView
{
    /**Datovy objekt s rezervacemi*/
    
    /**Priznak aktivni komponenty*/
    private int activeComponent=0;
    private static final int COMPONENT_RESERVE=1;
    private static final int COMPONENT_SPECIES=2;
    private static final int COMPONENT_POI=4;

    /**Odkaz na vyhledavaci formular*/
    //private SearchForm searchForm;
    /**Ukazatel na casovy slider*/
    private int timeIndex=1;
    /**ID daneho druhu*/
    private int speciesId=0;
    /**Index do vybrane historie */
    private int historyIndex=0;
    /**
     * Datum zacatku intervalu platnosti
     */
    private ArrayList<Calendar> dateList=new ArrayList<Calendar>();
    /**
     * Datum konce intervalu platnosti
     */
    private ArrayList<Calendar> dateToList=new ArrayList<Calendar>();
    
    


    /**
     * Konstruktor vytvori potrebne objekty a navaze je na udalosti
     * @param app
     */
    public GJAProjectView(SingleFrameApplication app)
    {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        //int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
/*        for (int i = 0; i < busyIcons.length; i++)
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });*/
        //idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        //statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
       /* TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {

            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName))
                {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName))
                {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName))
                {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });*/

        // nastaveni ikony hlavnimu oknu
        ImageIcon i = resourceMap.getImageIcon("Application.icon");
        if (i != null)
        {
            this.getFrame().setIconImage(i.getImage());
        }
        //Minimalni velikost okna
        this.getFrame().setMinimumSize(new Dimension(650, 400));

        //Zmena popisku statuspanelu podle udalosti v DrawPanelu
        this.drawPanel1.addStatusListener(new StatusEvent());


        this.getFrame().pack();


        
        
        /*this.searchForm = new SearchForm(this.getFrame());
        if (i != null)
        {
            this.searchForm.setIconImage(i.getImage());
        }*/



    }

    /**
     * Okno O aplikaci
     */
    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = GJAProjectApp.getApplication().getMainFrame();
            aboutBox = new GJAProjectAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        GJAProjectApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        timeSlider = new javax.swing.JSlider();
        timeLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        leftPanel = new javax.swing.JPanel();
        speedPanel1 = new cz.vutbr.fit.gja.proj.SpeedPanel();
        altPanel1 = new cz.vutbr.fit.gja.proj.AltPanel();
        drawPanel1 = new cz.vutbr.fit.gja.proj.DrawPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        settingMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        helpMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setMinimumSize(new java.awt.Dimension(600, 400));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(600, 400));

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel1.setAlignmentX(0.0F);
        jPanel1.setAlignmentY(0.0F);
        jPanel1.setMaximumSize(new java.awt.Dimension(681, 36));
        jPanel1.setMinimumSize(new java.awt.Dimension(681, 36));
        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(cz.vutbr.fit.gja.proj.GJAProjectApp.class).getContext().getActionMap(GJAProjectView.class, this);
        jButton2.setAction(actionMap.get("btnZoomIn")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cz.vutbr.fit.gja.proj.GJAProjectApp.class).getContext().getResourceMap(GJAProjectView.class);
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setToolTipText(resourceMap.getString("jButton2.toolTipText")); // NOI18N
        jButton2.setFocusPainted(false);
        jButton2.setFocusTraversalPolicyProvider(true);
        jButton2.setName("jButton2"); // NOI18N

        jButton3.setAction(actionMap.get("btnZoomOut")); // NOI18N
        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setToolTipText(resourceMap.getString("jButton3.toolTipText")); // NOI18N
        jButton3.setAlignmentY(0.0F);
        jButton3.setName("jButton3"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        timeSlider.setMajorTickSpacing(5);
        timeSlider.setMaximum(20);
        timeSlider.setPaintTicks(true);
        timeSlider.setSnapToTicks(true);
        timeSlider.setEnabled(false);
        timeSlider.setName("timeSlider"); // NOI18N
        timeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeSliderStateChanged(evt);
            }
        });

        timeLabel.setText(resourceMap.getString("timeLabel.text")); // NOI18N
        timeLabel.setName("timeLabel"); // NOI18N
        timeLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Letecká", "Standardní" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(194, 194, 194)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(127, 127, 127)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(timeSlider, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)))
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        leftPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        leftPanel.setName("leftPanel"); // NOI18N
        leftPanel.setPreferredSize(new java.awt.Dimension(280, 436));
        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.PAGE_AXIS));

        speedPanel1.setName("speedPanel1"); // NOI18N

        javax.swing.GroupLayout speedPanel1Layout = new javax.swing.GroupLayout(speedPanel1);
        speedPanel1.setLayout(speedPanel1Layout);
        speedPanel1Layout.setHorizontalGroup(
            speedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        speedPanel1Layout.setVerticalGroup(
            speedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        leftPanel.add(speedPanel1);

        altPanel1.setName("altPanel1"); // NOI18N

        javax.swing.GroupLayout altPanel1Layout = new javax.swing.GroupLayout(altPanel1);
        altPanel1.setLayout(altPanel1Layout);
        altPanel1Layout.setHorizontalGroup(
            altPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        altPanel1Layout.setVerticalGroup(
            altPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        leftPanel.add(altPanel1);

        drawPanel1.setName("drawPanel1"); // NOI18N

        javax.swing.GroupLayout drawPanel1Layout = new javax.swing.GroupLayout(drawPanel1);
        drawPanel1.setLayout(drawPanel1Layout);
        drawPanel1Layout.setHorizontalGroup(
            drawPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 877, Short.MAX_VALUE)
        );
        drawPanel1Layout.setVerticalGroup(
            drawPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(drawPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(drawPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        jMenuItem1.setAction(actionMap.get("modelyClicked")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        settingMenuItem.setAction(actionMap.get("settingClicked")); // NOI18N
        settingMenuItem.setText(resourceMap.getString("settingMenuItem.text")); // NOI18N
        settingMenuItem.setName("settingMenuItem"); // NOI18N
        fileMenu.add(settingMenuItem);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        helpMenuItem.setAction(actionMap.get("showHelpClickeed")); // NOI18N
        helpMenuItem.setText(resourceMap.getString("helpMenuItem.text")); // NOI18N
        helpMenuItem.setName("helpMenuItem"); // NOI18N
        helpMenu.add(helpMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1194, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1024, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Kliknuti na tlacitko Vlozit novou chranenou oblast
     * @param evt
     */
    /**
     * Reakce na zmenu temporalniho ukazatele
     * @param evt
     */
    private void timeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeSliderStateChanged
      System.out.println(timeSlider.getValue());
        if(timeSlider.getValue() !=timeIndex)
        {
           /* timeIndex=timeSlider.getValue();
            if(timeIndex<1 || Globals.species==null) return;
            Calendar c=dateList.get(timeIndex-1);
            Calendar dateTo = dateToList.get(timeIndex-1);
            SpecAreas area=Globals.species.getAreas();
            if(timeIndex==dateList.size())
            {
                setDate(c.getTime(), null);
                area.selectAtTime();
            }
            else
            {
                setDate(c.getTime(), dateTo.getTime());
                area.selectAtTime(c);
            }
            area.next();
            
            Globals.fireSpeciesChangedEvent("",0);*/
            

        }
    }//GEN-LAST:event_timeSliderStateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
      // TODO add your handling code here:
      drawPanel1.setMapType(jComboBox1.getSelectedIndex()==0 ? BingMapsStat.TYPE_Aerial : BingMapsStat.TYPE_ROAD);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * Testovaci akce - vymazani vsech mapovych podkladu
     */
    @Action
    public void Btn1click()
    {

        this.drawPanel1.removeMaps();

    }

    /**
     * Zobrazeni formulare pro vyhledavani
     */
    @Action
    public void btnSearchClick()
    {
        //searchForm.showSearch();
    }

    /**
     * Prepnuti na zalozku Novy druh
     */
    

    
    /**
     * Zpracovani zmen statusu DrawPanelu
     */
    protected class StatusEvent implements InfoEvent
    {
        public void infoUpdated(String status, int code)
        {
            printStatus(status, code);
        }
    }

    /**
     * Zpracovani zmen statusu datovych panelu
     */
    protected class DataChangedEvent implements InfoEvent
    {
        //Flag =true - bude se palit udalost
        private boolean flag=false;
        public DataChangedEvent(boolean flag)
        {
            this.flag=flag;
        }
        public void infoUpdated(String status, int code)
        {
           /*printStatus(status, code);
           drawPanel1.clear();
           setReservations();
           setLines();
           setSpeciesView(null);
           setTimeView();
           if(Globals.species!=null)
           {
               editMenuItem.setEnabled(true);
               deleteMenuItem.setEnabled(true);
               timeSlider.setEnabled(true);
           }
           else
           {
               timeSlider.setEnabled(false);
               editMenuItem.setEnabled(false);
               deleteMenuItem.setEnabled(false);
           }
           if(flag)
           {
               //Zavolano z prvku Component
               Globals.fireSpeciesChangedEvent("", InfoEvent.CODE_INFO);
           }
           else
           {
               //Zavolano zmetody fireSpecieseee...
               
           }*/
        }
    }

    /**
     * Vypise status do spodni listy
     * @param status Status
     * @param code Kod statusu (Info, Chyba, Dokonceno
     */
    protected void printStatus(String status, int code)
    {
            Color c;
            if(code==InfoEvent.CODE_ERROR)
            {
                c=new Color(0xff0000);
            }
            else if(code==InfoEvent.CODE_DRAWING_FINISHED)
            {
                c=new Color(0x8888ff);
            }else
            {
                c=new Color(0x000000);
            }
            statusMessageLabel.setForeground(c);
            statusMessageLabel.setText(status);
    }



    /**
     * Otevreni noveho formulare s nastavenim
     */
    @Action
    public void settingClicked()
    {
        JFrame mainFrame = GJAProjectApp.getApplication().getMainFrame();
        JDialog configForm = new ConfigForm(mainFrame);
        configForm.setLocationRelativeTo(mainFrame);
        configForm.setVisible(true);
    }

    /**
     * Zazoomovani drawPanelu
     */
    @Action
    public void btnZoomIn() {
        drawPanel1.zoomIn(drawPanel1.getWidth()/2, drawPanel1.getHeight()/2);
        
    }

    /**
     * Odzoomovani drawPanelu
     */
    @Action
    public void btnZoomOut() {
        drawPanel1.zoomOut(drawPanel1.getWidth()/2, drawPanel1.getHeight()/2);
    }

    

    /**
     * Nic
     */
    @Action
    public void reservationsShowClicked()
    {
        
        //if(this.showResMenuItem.)
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private cz.vutbr.fit.gja.proj.AltPanel altPanel1;
    private cz.vutbr.fit.gja.proj.DrawPanel drawPanel1;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem settingMenuItem;
    private cz.vutbr.fit.gja.proj.SpeedPanel speedPanel1;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JSlider timeSlider;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
//    private final Timer busyIconTimer;
//    private final Icon idleIcon;
//    private final Icon[] busyIcons = new Icon[15];
//    private int busyIconIndex = 0;
    private JDialog aboutBox;


  

   



    

    /**
     *
     * Nastavi casovy posuvnik podle dodanych dat
     */
    private void setTimeView()
    {
        /*if(Globals.species==null)
        {
            timeSlider.setMaximum(1);
            timeSlider.setMinimum(1);
            timeSlider.setValue(1);
            timeSlider.setEnabled(false);
            dateList.clear();
            dateToList.clear();
            timeIndex=1;
            setDate(null, null);
        }
        else
        {
            
            
            SpecAreas area=Globals.species.getAreas();
            area.selectHistory();
            int cnt=0;
            dateList.clear();
            dateToList.clear();
            while(area.next())
            {
                dateList.add(area.valid_from);
                dateToList.add(area.valid_to);
                cnt++;
            }


            //dateList.add(Calendar.getInstance());
            timeSlider.setMaximum(cnt);
            timeSlider.setMinimum(1);
            timeSlider.setMajorTickSpacing(1);
            timeSlider.setMinorTickSpacing(1);
            if(Globals.species.id!=speciesId || historyIndex!=cnt)
            {
                historyIndex=cnt;
                speciesId=Globals.species.id;
                timeSlider.setValue(cnt);
                timeSlider.setEnabled(true);
                timeIndex=cnt;

                if (cnt > 0)
                  setDate(area.valid_from.getTime(), null);
                else
                  setDate(null, null);
            }
        }*/
    }
    
    /**
     * Nastavi datum do pripraveneho labelu
     * @param date Datum, ktere se ma zobrazit
     */
    public void setDate(Date date, Date dateTo)
    {
        //if(date==null)
        //    date=new Date();
        //String dateOut;
        //DateFormat dateFormatter;
      SimpleDateFormat formatter = new SimpleDateFormat("d. M. yyyy",
				 Locale.getDefault());
      if (date != null) {

        if (dateTo == null)
          dateTo = new Date();

        timeLabel.setText(formatter.format(date) + " - " + formatter.format(dateTo));
      }
      else
        timeLabel.setText("");
    }

    


    /**
     * Zobrazeni napovedy.
     */
    @Action
    public void showHelpClickeed()
    {
        JFrame mainFrame = GJAProjectApp.getApplication().getMainFrame();
        HelpForm jd=new HelpForm(mainFrame);
        //JDialog jd = new JDialog(jf);
        //jd.setModal(true);
        //jd.setLocationRelativeTo(mainFrame);
        //jd.setVisible(true);
    }

  @Action
  public void modelyClicked() {
    ModelFrame panel=new ModelFrame();
    panel.setVisible(true);
  }

  
}
