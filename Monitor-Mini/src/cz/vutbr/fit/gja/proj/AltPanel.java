/*
Copyright (c) 2012, Martin Faltičko, Ondřej Vagner
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Jetimodel s.r.o. nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Martin Faltičko, Ondřej Vagner BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cz.vutbr.fit.gja.proj;

import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import cz.vutbr.fit.gja.proj.utils.*;

/**
 * Trida pro vykresleni a obsluhu panelu vyskomeru
 * @author Ondrej Vagner
 */
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

 /**
  * Konstruktor tridy
  */
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
      this.setNumber(0);
    }
    catch(Exception ex)
    {
      System.out.println("Image not load");
    }
  }
 
  
 /**
  * Metoda rozhrani pro nastaveni zobrazovaneho ukazatele (budiku)
  * @param max maximalni zobrazovana hodnota 
  */      
  public void changeSpeed(double max)
  {
      
  }

  
 /**
  * Metoda rozhrani pro nahrani vsech dat 
  * @param data strukura obsahujici data
  * @param max pocet zaznamu 
  */    
  public void setAllData(TelemetryData.TelemetryVar data, int max)
  {
  
  }

  
 /**
  * Metoda rozhrani pro nastaveni aktualniho casu 
  * @param time aktualni cas
  */   
  public void acTime(double time)
  {
  
  }

 /**
  * Metoda pro nastaveni natoceni obou rucicek
  * @param start vsupni hodnota
  */  
  public void setNumber(double start)
  {
    this.speednum = start;
    if(this.speednum < 0)
    {
      this.speednum = 0;
    }
    double big = (double)this.speednum/100.0;
    double small = this.speednum - ((int)big)*100; 
    this.rotation2 = 360.0/(10.0/big);
    this.rotation1 = 360.0/(100.0/small); 
    revalidate();   
  }
  

 /**
  * Metoda rozhrani pro nastaveni aktualni hodnoty
  * @param item aktualni hodnota
  */   
  public void setData(TelemetryData.TelemetryItem item)
  {
    this.setNumber(item.getDouble());
  }

  
 /**
  * Metoda pro prekresleni panelu
  * @param g trida grafickeho kontextu
  */     
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
