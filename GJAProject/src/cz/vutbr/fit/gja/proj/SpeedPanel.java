package cz.vutbr.fit.gja.proj;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;

public class SpeedPanel extends JPanel
{
  private static Image img1 = null;
  private static Image img2 = null;
  private static double rotation = 45.0;
          
  public SpeedPanel()
  {
    super();
    try
    {
      img1 = ImageIO.read(getClass().getResourceAsStream("resources/Speed.png"));
      img2 = ImageIO.read(getClass().getResourceAsStream("resources/Pointer.png"));
      Dimension d = new Dimension(200, 200);
      this.setMinimumSize(d);
      this.setMaximumSize(d);
      this.setPreferredSize(d);
      revalidate();
    }
    catch(IOException ex)
    {
      System.out.println("Iamge not load");
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
