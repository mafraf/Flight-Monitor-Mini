package cz.vutbr.fit.gja.proj;

import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import cz.vutbr.fit.gja.proj.utils.*;

public class GraphingData extends JPanel implements PanelInterface
{
    ArrayList<Integer> gdata;
    final int PAD = 20;
    final int down = 80;

    public GraphingData()
    {
      super();
      gdata = new ArrayList<Integer>();
      Dimension d = new Dimension(1000, 200);
      this.setMinimumSize(d);
      this.setMaximumSize(d);
      this.setPreferredSize(d);
    }

    public void setData(TelemetryData.TelemetryItem item)
    {

    }

    public void changeSpeed(double max)
    {

    }

    public void setAllData(TelemetryData.TelemetryVar data, int max)
    {
      gdata.clear();
      for(int i=0; i<=(int)max; i+=10)
      {
        gdata.add((int)data.getIntAt(i));
      }
      revalidate();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        g2.draw(new Line2D.Double(PAD, h-PAD-down, w-PAD, h-PAD-down));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        //String s = Integer.toString(getMax());
        String s = "0";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (PAD - sw)/2;
        g2.drawString(s, sx, sy);
        int max = getMax();
        if(!gdata.isEmpty())
        {
          s = Integer.toString(max);
          sy = PAD - PAD/4;
          sx = (PAD - sw)/2;
          g2.drawString(s, sx, sy);
          s = Integer.toString(-1*max);
          sy = h - PAD/4;
          g2.drawString(s, sx, sy);
        }
        // Abcissa label.
        s = "Čas";
        sy = h - PAD - down + (PAD - sh)/2 + lm.getAscent();
        sw = (float)font.getStringBounds(s, frc).getWidth();
        sx = (w - sw);
        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/((double)gdata.size()-1);
        double scale = (double)(h - down - 2*PAD)/max;
        g2.setPaint(Color.green.darker());
        for(int i = 0; i < gdata.size()-1; i++)
        {
            double x1 = PAD + i*xInc;
            double y1 = h - down - PAD - scale*((double)gdata.get(i));
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - down - PAD - scale*((double)gdata.get(i+1));
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Mark data points.
       /* g2.setPaint(Color.red);
        for(int i = 0; i < gdata.size(); i++)
        {
            double x = PAD + i*xInc;
            double y = h - down - PAD - scale*((double)gdata.get(i));
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }*/
    }

    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for(int i = 0; i < gdata.size(); i++) {
            if((gdata.get(i)) > max)
                max = (gdata.get(i));
        }
        return max;
    }
}
