package cz.vutbr.fit.gja.proj;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import cz.vutbr.fit.gja.proj.utils.*;

public class SpeedPanel extends JPanel implements PanelInterface
{
  private Image img1 = null;
  private Image img2 = null;
  private double rotation = 0.0;
  private double max = 120.0;
          
  public SpeedPanel()
  {
    super();
    try
    {
      img1 = ImageIO.read(getClass().getResourceAsStream("resources/Speed2.png"));
      img2 = ImageIO.read(getClass().getResourceAsStream("resources/Pointer.png"));
      Dimension d = new Dimension(210, 210);
      this.setMinimumSize(d);
      this.setMaximumSize(d);
      this.setPreferredSize(d);
      this.setNumber(65);
    }
    catch(IOException ex)
    {
      System.out.println("Iamge not load");
    }
  }

   public void setNumber(int start)
  {
    if(this.max == 120.0)
    {
      if(start > 120)
      {
        start = 120;
      }
      this.rotation = (360.0/(max/(double)start));
    }
    else
    {
      if(start > 260)
      {
        start = 260;
      }
      this.rotation = 360.0/(max/(double)(start)) - 28.0 + ((((double)start - 20.0)/60.0)*7.0);    
    }
    revalidate();   
  }
  
  public void setData(TelemetryData.TelemetryItem item)
  {
    this.setNumber((int)item.getDouble());
  }
  
  public void changeSpeed(boolean slow)
  {
    try
    {
      if(slow)
      {
        img1 = ImageIO.read(getClass().getResourceAsStream("resources/Speed2.png"));  
        this.max = 120.0;
      }
      else
      {
        img1 = ImageIO.read(getClass().getResourceAsStream("resources/Speed.png")); 
        this.max = 260.0;
      }
      revalidate();
    }
    catch(IOException ex)
    {
      System.out.println("Image not load");
    }
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    if (img1 != null) 
    {
      g2d.drawImage(img1, 0, 0, null);
      g2d.rotate(Math.toRadians(rotation), 100, 100);
      g2d.drawImage(img2, 90, 28, null);
    }
    else
    {
      g2d.setPaint(Color.WHITE);
      g2d.fill(new Rectangle(0, 0, 200, 200));
    }
  }
}
