package net.sourceforge.gedprint.gui.core;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gui.GedPrintGui;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 2:04:12 PM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class GuiLogger
{
  private static final Logger appLogger =
      Logger.getLogger("net.sourceforge.gedprint");

  private GuiLogger()
  {
  }

  public static void initLogger()
  {
    appLogger.setLevel(Level.ALL);
    Formatter formatter = new Formatter()
    {
      String NL = System.getProperty("line.separator"); //$NON-NLS-1$

      @Override
      public String format(LogRecord record)
      {
        StringBuilder builder = new StringBuilder();
        String clname = record.getSourceClassName();
        builder.append(clname.substring(clname.lastIndexOf('.') + 1));
        builder.append(": "); //$NON-NLS-1$
        String prefix = builder.toString();
        // bisheriges loeschen
        builder.setLength(0);

        builder.append(formatMessage(record));

        String logtext = builder.toString();
        String[] lines = logtext.split(NL);
        builder = new StringBuilder();
        for(String line : lines)
        {
          builder.append(prefix);
          builder.append(line);
          builder.append(NL);
        }
        return builder.toString();
      }
    };
    ConsoleHandler chandler = new ConsoleHandler();
    chandler.setLevel(Level.ALL);
    chandler.setFormatter(formatter);
    appLogger.addHandler(chandler);
    appLogger.setUseParentHandlers(false);

    // Protokollausgabe auch in Datei
    try
    {
      File logfile = new File("GedPrint.log"); //$NON-NLS-1$

      FileHandler handler = new FileHandler(logfile.getName(), 1<<20, 1, true);
      handler.setFormatter(formatter);
      appLogger.addHandler(handler);
    }
    catch(Exception ex)
    {
      Logger.getLogger(GedPrintGui.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
