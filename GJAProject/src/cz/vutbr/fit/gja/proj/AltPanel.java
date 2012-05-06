package cz.vutbr.fit.gja.proj;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import cz.vutbr.fit.gja.proj.utils.*;

public class AltPanel extends JPanel implements PanelInterface
{
  private Image img1 = null;
  private Image img2 = null;
  private Image img3 = null;
  private double rotation1 = 0.0; 
  private double rotation2 = 0.0; 
  private Font f = null;
  private String speed = null;
  private Integer num;
  private double speednum;
          
  public AltPanel()
  {
    super();
    this.f = new Font("Arial", Font.BOLD, 24);
    this.speed = "125";
    try
    {
      this.img1 = ImageIO.read(getClass().getResourceAsStream("resources/Alt.png"));
      this.img2 = ImageIO.read(getClass().getResourceAsStream("resources/Pointer.png"));
      this.img3 = ImageIO.read(getClass().getResourceAsStream("resources/PointerS.png"));
      Dimension d = new Dimension(200, 200);
      this.setMinimumSize(d);
      this.setMaximumSize(d);
      this.setPreferredSize(d);
      this.setNumber(128);  //Pak volat s 0 !!!!!
    }
    catch(IOException ex)
    {
      System.out.println("Image not load");
    }
  }
  
  public void changeSpeed(boolean slow)
  {
      
  }
   
  public void setNumber(double start)
  {
    this.speednum = start;
    double big = (double)this.speednum/100.0;
    double small = this.speednum - ((int)big)*100; 
    this.rotation2 = 360.0/(10.0/big);
    this.rotation1 = 360.0/(100.0/small); 
    revalidate();   
  }
  
  
  public void setData(TelemetryData.TelemetryItem item)
  {
    this.setNumber(item.getDouble());
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Disable antialiasing for text
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    if (img1 != null) 
    {
      g2d.drawImage(img1, 0, 0, null);
      g2d.setFont(f);
      if(this.speednum < 10)
      {
        this.speed = Integer.toString((int)speednum);    
        g2d.drawString(speed, 158, 107);        
      }
      else
      {
        if(this.speednum < 100)
        {
          this.speed = Integer.toString((int)speednum);    
          g2d.drawString(speed, 145, 107);      
        }
        else
        {       
          this.speed = Integer.toString((int)speednum);    
          g2d.drawString(speed, 132, 107);
        }
      }
      g2d.rotate(Math.toRadians(rotation2), 100, 100);
      g2d.drawImage(img3, 90, 52, null);
      g2d.rotate(Math.toRadians(-rotation2+rotation1), 100, 100);
      g2d.drawImage(img2, 90, 28, null);
      g2d.rotate(Math.toRadians(-rotation1), 100, 100);
    }
    else
    {
      g2d.setPaint(Color.WHITE);
      g2d.fill(new Rectangle(0, 0, 200, 200));
    }
  }
}
