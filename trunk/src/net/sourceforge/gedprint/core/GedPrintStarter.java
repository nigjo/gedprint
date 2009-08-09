package net.sourceforge.gedprint.core;

public interface GedPrintStarter extends Runnable
{
  public boolean parseCommandline(String[] args);
  
}
