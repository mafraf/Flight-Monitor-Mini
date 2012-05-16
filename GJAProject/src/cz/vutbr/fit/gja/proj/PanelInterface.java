package cz.vutbr.fit.gja.proj;

import cz.vutbr.fit.gja.proj.utils.*;

public interface PanelInterface 
{
  public void setData(TelemetryData.TelemetryItem item);
  public void changeSpeed(double max);  
  public void setAllData(TelemetryData.TelemetryVar data, int max);
  public void acTime(double time);
}
