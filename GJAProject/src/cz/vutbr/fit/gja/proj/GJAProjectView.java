/*
 * GJAProjectView.java
 */
package cz.vutbr.fit.gja.proj;


import cz.vutbr.fit.gja.proj.utils.*;
import cz.vutbr.fit.gja.proj.utils.TelemetryData.TelemetrySensor;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.TreeSet;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
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
    private double time=0.0;
   

    
    
    private TelemetryData data;
    private Timer animationTimer;
    /** Priznak zablokovani zmeny stavu timeSlideru **/
    private boolean sliderDisable=false;
    
    


    /**
     * Konstruktor vytvori potrebne objekty a navaze je na udalosti
     * @param app
     */
    public GJAProjectView(SingleFrameApplication app)
    {
        super(app);

        initComponents();
        data = Application.getInstance(GJAProjectApp.class).getTelemetry();
        animationTimer=new Timer(50,new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                time+=50.0/1000.0 * (double)animationSpinner.getValue();
                if(time>data.getMaxTimestamp())
                {
                  animationTimer.stop();
                  stopBtn.setEnabled(false);
                  playBtn.setEnabled(true);
                }
                else
                {
                  //Aktualizuje polozky
                  sliderDisable=true;
                  timeSlider.setValue((int)time);
                  sliderDisable=false;
                  updateDisplayData();
                }
            }
        });
        animationTimer.setRepeats(true);

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
        ImageIcon i = resourceMap.getImageIcon("Application.icon"); //NOI18N
        if (i != null)
        {
            this.getFrame().setIconImage(i.getImage());
        }
        //Minimalni velikost okna
        this.getFrame().setMinimumSize(new Dimension(650, 400));

        //Zmena popisku statuspanelu podle udalosti v DrawPanelu
        this.drawPanel1.addStatusListener(new StatusEvent());


        this.getFrame().pack();

        Config cfg=Application.getInstance(GJAProjectApp.class).getConfiguration();
        drawPanel1.setMapType(cfg.getMapType());
        jComboBox1.setSelectedIndex(cfg.getMapType().equals(BingMapsStat.TYPE_Aerial) ? 0 : 1);

        
        
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
        jComboBox1 = new javax.swing.JComboBox();
        stopBtn = new javax.swing.JButton();
        playBtn = new javax.swing.JButton();
        animationSpinner = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        leftPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        altComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        altPanel1 = new cz.vutbr.fit.gja.proj.AltPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        speedComboBox = new javax.swing.JComboBox();
        speedPanel1 = new cz.vutbr.fit.gja.proj.SpeedPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        drawPanel1 = new cz.vutbr.fit.gja.proj.DrawPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
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
        timeSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                timeSliderPropertyChange(evt);
            }
        });

        timeLabel.setText(resourceMap.getString("timeLabel.text")); // NOI18N
        timeLabel.setName("timeLabel"); // NOI18N
        timeLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Letecká", "Standardní" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        stopBtn.setAction(actionMap.get("stopClicked")); // NOI18N
        stopBtn.setIcon(resourceMap.getIcon("stopBtn.icon")); // NOI18N
        stopBtn.setText(resourceMap.getString("stopBtn.text")); // NOI18N
        stopBtn.setName("stopBtn"); // NOI18N

        playBtn.setAction(actionMap.get("PlayClicked")); // NOI18N
        playBtn.setIcon(resourceMap.getIcon("playBtn.icon")); // NOI18N
        playBtn.setText(resourceMap.getString("playBtn.text")); // NOI18N
        playBtn.setName("playBtn"); // NOI18N

        animationSpinner.setModel(new SpinnerNumberModel(1.0, 0.0, 100.0, 0.1));
        animationSpinner.setName("animationSpinner"); // NOI18N
        animationSpinner.setValue(1.0);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(225, 225, 225)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addGap(101, 101, 101)
                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(animationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(playBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(26, 26, 26))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timeSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(playBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(stopBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(animationSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .addComponent(jLabel5)))
                .addGap(18, 18, 18))
        );

        playBtn.getAccessibleContext().setAccessibleName(resourceMap.getString("playBtn.AccessibleContext.accessibleName")); // NOI18N
        playBtn.getAccessibleContext().setAccessibleDescription(resourceMap.getString("playBtn.AccessibleContext.accessibleDescription")); // NOI18N

        leftPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        leftPanel.setName("leftPanel"); // NOI18N
        leftPanel.setPreferredSize(new java.awt.Dimension(280, 436));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(250, 550));

        jPanel2.setName("jPanel2"); // NOI18N

        altComboBox.setName("comboAltDataType"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(altPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(altComboBox, 0, 210, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(altComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(altPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        speedComboBox.setName("comboSpeedDataType"); // NOI18N

        speedPanel1.setName("speedPanel1"); // NOI18N

        javax.swing.GroupLayout speedPanel1Layout = new javax.swing.GroupLayout(speedPanel1);
        speedPanel1.setLayout(speedPanel1Layout);
        speedPanel1Layout.setHorizontalGroup(
            speedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        speedPanel1Layout.setVerticalGroup(
            speedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(speedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(speedComboBox, 0, 201, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(speedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, 0, 265, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel5);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        drawPanel1.setName("drawPanel1"); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.setName("jComboBox2"); // NOI18N

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setName("jComboBox3"); // NOI18N

        javax.swing.GroupLayout drawPanel1Layout = new javax.swing.GroupLayout(drawPanel1);
        drawPanel1.setLayout(drawPanel1Layout);
        drawPanel1Layout.setHorizontalGroup(
            drawPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(drawPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(528, Short.MAX_VALUE))
        );
        drawPanel1Layout.setVerticalGroup(
            drawPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(drawPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(drawPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(288, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(drawPanel1);

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 673, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jSplitPane1.setRightComponent(jTabbedPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        jMenuItem1.setAction(actionMap.get("modelyClicked")); // NOI18N
        jMenuItem1.setLabel(resourceMap.getString("jMenuItem1.label")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);
        jMenuItem1.getAccessibleContext().setAccessibleName(resourceMap.getString("jMenuItem1.AccessibleContext.accessibleName")); // NOI18N

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
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 800, Short.MAX_VALUE)
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

      if(!sliderDisable)
      {
        time=(int)timeSlider.getValue();
        updateDisplayData();
      }
    }//GEN-LAST:event_timeSliderStateChanged

    /** 
     * Zmena typu mapy
     */
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
      String str=jComboBox1.getSelectedIndex()==0 ? BingMapsStat.TYPE_Aerial : BingMapsStat.TYPE_ROAD;
      drawPanel1.setMapType(str);
      try{
      Config cfg=Application.getInstance(GJAProjectApp.class).getConfiguration();
      cfg.setMapType(str);
      cfg.saveConfig(Config.defaultConfigFile);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Chyba", JOptionPane.ERROR_MESSAGE);
      }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void timeSliderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_timeSliderPropertyChange
      // TODO add your handling code here:
      
      
    }//GEN-LAST:event_timeSliderPropertyChange

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
    private javax.swing.JComboBox altComboBox;
    private cz.vutbr.fit.gja.proj.AltPanel altPanel1;
    private javax.swing.JSpinner animationSpinner;
    private cz.vutbr.fit.gja.proj.DrawPanel drawPanel1;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton playBtn;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem settingMenuItem;
    private javax.swing.JComboBox speedComboBox;
    private cz.vutbr.fit.gja.proj.SpeedPanel speedPanel1;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopBtn;
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
    
    Config cfg=Application.getInstance(GJAProjectApp.class).getConfiguration();

    String wd = cfg.getModelPath();
    JFileChooser fc = new JFileChooser(wd);
    fc.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".log") || f.getName().toLowerCase().endsWith(".jml");
      }

      @Override
      public String getDescription() {
        return "Datové typy (*.log, *.jml)";
      }
    });
    int rc = fc.showDialog(null, "Vyberte datový soubor");
    if (rc == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String filename = file.getAbsolutePath();
      altComboBox.removeAllItems();
      speedComboBox.removeAllItems();
      playBtn.setEnabled(false);
      stopBtn.setEnabled(false);
      sliderDisable=true;
      if(data.loadData(filename))
      {
        //Doslo k nahrani dat
        timeSlider.setMaximum((int)Math.round(data.getMaxTimestamp()));
        timeSlider.setMinimum(0);
        timeSlider.setMajorTickSpacing(10);
        timeSlider.setMinorTickSpacing(1);
        timeSlider.setEnabled(true);
        timeSlider.setValue(0);
        playBtn.setEnabled(true);
        time=0.0;
        TreeSet<TelemetrySensor> data1 = data.getData();
        for(TelemetrySensor s:data1)
        {
          for(TelemetryData.TelemetryVar v:s.getVariables())
          {
            altComboBox.addItem(v);
            if(v.getUnit().equals("m"))
              altComboBox.setSelectedItem(v);
            
            speedComboBox.addItem(v);
            if(v.getUnit().equals("m/s"))
              speedComboBox.setSelectedItem(v);
            
          }
        }
        updateDisplayData();
      }
      else
      {
        //Nemam zadna data
        timeSlider.setMaximum(0);
        timeSlider.setMinimum(0);
        timeSlider.setMajorTickSpacing(10);
        timeSlider.setMinorTickSpacing(1);
        timeSlider.setEnabled(false);
      }
      sliderDisable=false;
    }
    return;
  }
  
  /**
   * Aktualizuje zobrazena data podle aktualni konfigurace
   */
  void updateDisplayData()
  {
    //Alt
    TelemetryData.TelemetryVar var = (TelemetryData.TelemetryVar)altComboBox.getSelectedItem();
    if(var!=null)
    {
      double val=var.getDoubleAt(time);
      altPanel1.setNumber(val);
    }
    //Speed
    TelemetryData.TelemetryVar var2 = (TelemetryData.TelemetryVar)speedComboBox.getSelectedItem();
    if(var2!=null)
    {
      double val=var2.getDoubleAt(time);
      speedPanel1.setNumber(val);
      speedPanel1.changeSpeed(var2.getMax()<120.0);
    }
    mainPanel.revalidate();
    this.mainPanel.repaint();
  }

  @Action
  public void PlayClicked() 
  {
    playBtn.setEnabled(false);
    if(!data.isEmpty())
    {
      stopBtn.setEnabled(true);
      animationTimer.start();
    }
  }

  @Action
  public void stopClicked() 
  {
    stopBtn.setEnabled(false);
    animationTimer.stop();
    if(!data.isEmpty())
    {
      playBtn.setEnabled(true);
    }
  }

  
}
