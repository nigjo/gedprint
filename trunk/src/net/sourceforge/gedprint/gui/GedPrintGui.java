package net.sourceforge.gedprint.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 24, 2009, 2:45:51 PM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class GedPrintGui implements Runnable
{
  public GedPrintGui()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception ex)
    {
      // dann halt nicht.
    }
  }

  public static final void main(String[] args)
  {
    // Protokollierung initialisieren
    initLogger();

    // Programmstart in die Protokolldatei mit Zeitstempel
    Logger logger = Logger.getLogger(GedPrintGui.class.getName());
    logger.info("------------------------------");
    logger.info(new SimpleDateFormat().format(new Date()));

    GedPrintGui gui = new GedPrintGui();
    //if(gui.parseCommandline(args))
    {
      // Programm starten
      gui.run();
    }
  }

  private static void initLogger()
  {
    String packName = GedPrintGui.class.getPackage().getName();
    String baselogger = packName.substring(0, packName.lastIndexOf('.'));
    Logger logger = Logger.getLogger(baselogger);
    logger.setLevel(Level.ALL);
    Formatter formatter = new Formatter()
    {
      String NL = System.getProperty("line.separator");

      @Override
      public String format(LogRecord record)
      {
        StringBuilder builder = new StringBuilder();
        String clname = record.getSourceClassName();
        builder.append(clname.substring(clname.lastIndexOf('.') + 1));
        builder.append(": ");
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

        return builder.toString() + NL;
      }

    };
    ConsoleHandler chandler = new ConsoleHandler();
    chandler.setLevel(Level.ALL);
    chandler.setFormatter(formatter);
    logger.addHandler(chandler);

    // Protokollausgabe auch in Datei
    try
    {
      File logfile = new File("GedPrint.log");

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

    File tempFile = File.createTempFile("~prune", ".log");
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

  public void run()
  {
    GedFrame frame = new GedFrame();

    frame.setVisible(true);
  }

  public static void exit()
  {
    exit(0);
  }

  public static void exit(int rc)
  {
    Logger logger = Logger.getLogger(GedPrintGui.class.getName());
    logger.info("quit application");
    logger.info(new SimpleDateFormat().format(new Date()));
    logger.info("------------------------------");

    System.exit(rc);
  }

}
