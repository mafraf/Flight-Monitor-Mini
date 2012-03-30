/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.gja.proj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * Formular pro zobrazeni napovedy.
 * 
 * @author Martin Falticko
 *
 */
public class HelpForm
{

    final Component parent;

    /**
     * Konstruktor
     *
     * @param parent Rodicovske okno
     */
    public HelpForm(final Component parent) {
        this.parent=parent;
        SwingUtilities.invokeLater(new Runnable()
        {

            public void run() {
                // create jeditorpane
                JEditorPane jEditorPane = new JEditorPane();

                // make it read-only
                jEditorPane.setEditable(false);

                // create a scrollpane; modify its attributes as desired
                JScrollPane scrollPane = new JScrollPane(jEditorPane);

                // add an html editor kit
                HTMLEditorKit kit = new HTMLEditorKit();
                jEditorPane.setEditorKit(kit);
jEditorPane.setBackground(new Color(245,245,245));
                // add some styles to the html
                StyleSheet styleSheet = kit.getStyleSheet();
                styleSheet.addRule("body {background-color: #f5f5f5: ;color:#000; font-family:times; font-size: 11px; padding: 5px; }");
                styleSheet.addRule("h1 {color: blue;}");
                styleSheet.addRule("h2 {color: #ff0000;}");
                styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
                styleSheet.addRule("table { font-size: 9px; background-color: white;}");

                // create some simple html as a string
                String htmlString;
                try
                {
                    htmlString = HelpForm.readFileAsString("help.html");
                }catch(Exception e)
                {
                    htmlString="<html><body><h1>Nelze načíst soubor s nápovědou</h1></body></html>";
                }

                // create a document, set it on the jeditorpane, then add the html
                Document doc = kit.createDefaultDocument();
                jEditorPane.setDocument(doc);
                jEditorPane.setText(htmlString);
                jEditorPane.setCaretPosition(0);
                // now add it all to a frame
                JFrame j = new JFrame("Nápověda");
                j.getContentPane().add(scrollPane, BorderLayout.CENTER);

                // make it easy to close the application
                j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // display the frame
                j.setSize(new Dimension(750,700));

                // pack it, if you prefer
                //j.pack();

                // center the jframe, then make it visible
                j.setLocationRelativeTo(parent);
                j.setVisible(true);
            }
        });
    }

    /**
     * Nacte zadany soubor jako retezec
     * @param filePath
     * @return
     * @throws java.io.IOException
     */
    private static String readFileAsString(String filePath)
    throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
