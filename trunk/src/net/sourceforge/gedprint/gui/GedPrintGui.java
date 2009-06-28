package net.sourceforge.gedprint.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import net.sourceforge.gedprint.gui.startup.GuiLogger;
import net.sourceforge.gedprint.gui.startup.GuiStartup;
import net.sourceforge.gedprint.gui.startup.GuiThreadGroup;

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
    // Protokollierung initialisieren
    GuiLogger.initLogger();

    // Programmstart in die Protokolldatei mit Zeitstempel
    Logger logger = Logger.getLogger(GedPrintGui.class.getName());
    logger.info("------------------------------"); //$NON-NLS-1$
    logger.info(new SimpleDateFormat().format(new Date()));

    GuiStartup gui = new GuiStartup();
    if(gui.parseCommandline(args))
    {
      // Programm starten
      GuiThreadGroup.execute(gui);
    }
  }

}
