/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Vlastni renderer pro sloupec komponenty JTable, ktery vykresluje barvu na pozadi
 *
 * @author Martin Falticko
 */
public class ColorColumnRenderer extends JLabel implements TableCellRenderer {


  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {


    setText("");
    //if (value != null)
    setOpaque(true);
    setBackground((Color)value);

    return this;
  }

  // The following methods override the defaults for performance reasons
  @Override public void validate() {}
  @Override public void revalidate() {}
  @Override protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
  @Override public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}


}
