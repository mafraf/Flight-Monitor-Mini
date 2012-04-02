package cz.vutbr.fit.gja.proj;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;

public class AltPanel extends JPanel implements PanelInterface
{
  private static Image img1 = null;
  private static Image img2 = null;
  private static Image img3 = null;
  private static double rotation1 = 50.0;
  private static double rotation2 = 20.0;
  private static Font f = null;
  private String speed = null;
          
  public AltPanel()
  {
    super();
    f = new Font("Arial", Font.BOLD, 24);
    speed = new String("125");
    try
    {
      img1 = ImageIO.read(getClass().getResourceAsStream("resources/Alt.png"));
      img2 = ImageIO.read(getClass().getResourceAsStream("resources/Pointer.png"));
      img3 = ImageIO.read(getClass().getResourceAsStream("resources/PointerS.png"));
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
  
  public void loadData()
  {
  
  }
  
  public void setData()
  {
  
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    if (img1 != null) 
    {
      g2d.drawImage(img1, 0, 0, null);
      g2d.rotate(Math.toRadians(rotation2), 100, 100);
      g2d.drawImage(img3, 90, 52, null);
      g2d.rotate(Math.toRadians(-rotation2+rotation1), 100, 100);
      g2d.drawImage(img2, 90, 28, null);
      g2d.rotate(Math.toRadians(-rotation1), 100, 100);
      g2d.setFont(f);
      g2d.drawString(speed, 132, 107);
    }
    else
    {
      g2d.setPaint(Color.WHITE);
      g2d.fill(new Rectangle(0, 0, 200, 200));
    }
  }
}
