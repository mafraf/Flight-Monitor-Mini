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

import cz.vutbr.fit.gja.proj.utils.*;

/**
 * Rozhrani pro panely ukazatelu a graf
 * @author Ondrej Vagner
 */
public interface PanelInterface 
{
 /**
  * Metoda pro nastaveni aktualni hodnoty
  * @param item aktualni hodnota
  */     
  public void setData(TelemetryData.TelemetryItem item);
 
 /**
  * Metoda pro nastaveni zobrazovaneho ukazatele (budiku)
  * @param max maximalni zobrazovana hodnota 
  */      
  public void changeSpeed(double max);  
  
 /**
  * Metoda pro nahrani vsech dat 
  * @param data strukura obsahujici data
  * @param max pocet zaznamu 
  */    
  public void setAllData(TelemetryData.TelemetryVar data, int max);
  
 /**
  * Metoda pro nastaveni aktualniho casu 
  * @param time aktualni cas
  */   
  public void acTime(double time);
}
