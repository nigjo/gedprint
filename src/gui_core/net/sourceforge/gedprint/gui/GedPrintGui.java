package net.sourceforge.gedprint.gui;

import net.sourceforge.gedprint.gui.core.GuiStartup;
import net.sourceforge.gedprint.gui.core.GuiThreadGroup;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 24, 2009, 2:45:51 PM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class GedPrintGui
{
  public static final void main(String[] args)
  {
    GuiStartup gui = new GuiStartup();
    if(gui.parseCommandline(args))
    {
      // Programm starten
      GuiThreadGroup.execute(gui);
    }
  }

}
