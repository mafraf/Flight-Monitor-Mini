/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Vlastni renderer pro sloupec komponenty JTable, ktery zobrazuje obrazek
 *
 * @author Martin Falticko
 */
public class ImageColumnRenderer extends JLabel implements TableCellRenderer {

  /**
   * instance obrazku
   */
  private Image imgValue = null;

  /**
   * barva pozadi bunky
   */
  private Color backgroundColor = null;


  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

    if (isSelected) {
      backgroundColor = table.getSelectionBackground();
    }
    else
      backgroundColor = table.getBackground();

    setText("");

    imgValue = (Image)value;

    return this;
  }

  // The following methods override the defaults for performance reasons
  @Override public void validate() {}
  @Override public void revalidate() {}
  @Override protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
  @Override public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (backgroundColor != null) {
      g.setColor(backgroundColor);
      g.fillRect(0, 0, 50, 50);
    }
    if (imgValue != null) {
      int w = imgValue.getWidth(null);
      int h = imgValue.getHeight(null);
      g.drawImage(imgValue, (50 - w) / 2, (50 - h) / 2, null);
    }


  }

}
