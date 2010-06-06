package net.sourceforge.gedprint.core;

/**
 * Schnittstelle fuer eine Startklasse.
 * 
 * @author nigjo
 */
public interface GedPrintStarter extends Runnable
{
  public boolean parseCommandline(String[] args);
  
}
