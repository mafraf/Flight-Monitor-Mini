/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.vutbr.fit.gja.proj;

import java.util.*;
/**
 *
 * @author Martin Falticko
 * Rozhrani pro uzivatelske udalosti
 */


public interface InfoEvent extends java.util.EventListener
{
    public static final int CODE_INFO=0;
    public static final int CODE_DRAWING_FINISHED=1;
    public static final int CODE_ERROR=2;
    public void infoUpdated(String status, int code);
}
