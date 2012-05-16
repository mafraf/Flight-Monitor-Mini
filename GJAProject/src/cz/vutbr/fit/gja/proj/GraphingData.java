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
    double position = 0.0;
    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    
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
      if(data != null)
      {
        gdata.clear();
        for(int i=0; i<=(int)max; i+=1)
        {
          gdata.add((int)data.getDoubleAt(i));
        } 
        revalidate();
      }
    }
    
    public void acTime(double time)
    {
      position = time;
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

        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();

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

        s = "Čas";
        sy = h - PAD - down + (PAD - sh)/2 + lm.getAscent();
        sw = (float)font.getStringBounds(s, frc).getWidth();
        sx = (w - sw);
        g2.drawString(s, sx, sy);

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
        if(position > 0)
        {
          g2.setPaint(Color.blue);
          g2.setStroke(dashed); 
          g2.draw(new Line2D.Double(position+PAD, PAD - PAD/4, position+PAD, h - PAD/4));
        }     
    }
 
    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for(int i = 0; i < gdata.size(); i++) 
        {
            if((gdata.get(i)) > max)
            {
                max = (gdata.get(i));
            }
        }
        return max;
    }
}
