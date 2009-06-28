package net.sourceforge.gedprint.gui.startup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
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
  public static void initLogger()
  {
    String packName = GedPrintGui.class.getPackage().getName();
    String baselogger = packName.substring(0, packName.lastIndexOf('.'));
    Logger logger = Logger.getLogger(baselogger);
    logger.setLevel(Level.ALL);
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

        String message = record.getMessage();
        if(record.getResourceBundle() != null)
        {
          message = record.getResourceBundle().getString(message);
        }
        else if(record.getResourceBundleName() != null)
        {
          message = ResourceBundle.getBundle(record.getResourceBundleName()).
              getString(message);
        }
        Object[] params = record.getParameters();
        if(params != null)
          message = MessageFormat.format(message, params);
        builder.append(message);

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
    logger.addHandler(chandler);

    // Protokollausgabe auch in Datei
    try
    {
      File logfile = new File("GedPrint.log"); //$NON-NLS-1$

      ensureMaxSize(logfile, 1024 * 1024l);

      FileHandler handler = new FileHandler(logfile.getName(), true);
      handler.setFormatter(formatter);
      logger.addHandler(handler);
    }
    catch(Exception ex)
    {
      Logger.getLogger(GedPrintGui.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static void ensureMaxSize(File logfile, long maxlength)
      throws IOException
  {
    if(!logfile.exists() || logfile.length() < maxlength)
      return;

    File tempFile = File.createTempFile("~prune", ".log"); //$NON-NLS-1$ //$NON-NLS-2$
    BufferedReader in = new BufferedReader(new FileReader(logfile));
    try
    {
      BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
      try
      {
        in.skip(logfile.length() - maxlength);
        String zeile;
        while(null != (zeile = in.readLine()))
        {
          out.write(zeile);
          out.newLine();
        }
      }
      finally
      {
        out.close();
      }
    }
    finally
    {
      in.close();
    }
    if(!logfile.delete())
      return;
    tempFile.renameTo(logfile);
  }

}
