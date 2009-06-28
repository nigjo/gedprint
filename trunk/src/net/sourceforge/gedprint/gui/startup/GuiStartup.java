package net.sourceforge.gedprint.gui.startup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.GedFrame;
import net.sourceforge.gedprint.gui.GedPrintGui;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 1:51:32 PM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class GuiStartup implements Runnable
{
  private static final int CMD_OK = 0;
  private static final int CMD_FILE = 1;
  private static final int CMD_INDI = 2;
  private static final int CMD_FAM = 3;
  //File infile;
  private static final String ARG_FILENAME = "filename";
  private static final String ARG_INDIVIDUAL = "individual";
  private static final String ARG_FAMILY = "family";
  Properties arguments;

  public GuiStartup()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception ex)
    {
      // dann halt nicht.
    }
    Properties defaults = new Properties();
    //defaults.setProperty(ARG_FILENAME, "");
    //defaults.setProperty(ARG_INDIVIDUAL, "");
    //defaults.setProperty(ARG_FAMILY, "");

    arguments = new Properties(defaults);
  }

  public void run()
  {
    // DEBUG start
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);
    arguments.list(out);
    Logger.getLogger(getClass().getName()).finer(sw.toString());
    // DEBUG end

    GedFrame frame = new GedFrame();

    String file = arguments.getProperty(ARG_FILENAME);
    if(file != null && file.length() > 0)
    {
      try
      {
        frame.setGedFile(new GedFile(file));
      }
      catch(FileNotFoundException ex)
      {
        illegalArg(Messages.getString("err.filenotfound"), file);
        return;
      }
      catch(IOException ex)
      {
        illegalArg(Messages.getString("err.ioerror"), file);
        return;
      }
      
      // StartID setzen, wenn Datei gelesen wurde
      String startid = arguments.getProperty(ARG_INDIVIDUAL);
      if(startid == null)
	  startid = arguments.getProperty(ARG_FAMILY);
      frame.setStartID('@' + startid + '@');
    }

    frame.setVisible(true);
  }

  public static void exit()
  {
    exit(0);
  }

  public static void exit(int rc)
  {
    System.exit(rc);
  }

  private void illegalArg(String pattern, String arg)
  {
    String title = Messages.getString(GedPrintGui.class, "frame.title");
    String message = MessageFormat.format(pattern, new Object[]
        {
          arg
        });
    JOptionPane.showMessageDialog(null, message, title,
        JOptionPane.ERROR_MESSAGE);
  }

  public boolean parseCommandline(String[] args)
  {
    int status = CMD_OK;
    for(String arg : args)
    {
      if(arg == null || arg.length() == 0)
        continue;
      if(arg.charAt(0) == '-')
      {
        if(status != CMD_OK)
        {
          illegalArg(Messages.getString("err.missingargument"), arg);
          return false;
        }
        else if(arg.length() == 1)
        {
          illegalArg(Messages.getString("err.invalidarg"), arg);
          return false;
        }

        switch(arg.charAt(1))
        {
          case 'i':
            status = CMD_INDI;
            break;
          case 'f':
            status = CMD_FAM;
            break;
          default:
            illegalArg(Messages.getString("err.unknownoption"), arg);
            return false;
        }
      }
      else
      {
        switch(status)
        {
          case CMD_INDI:
            arguments.setProperty(ARG_INDIVIDUAL, arg);
            break;
          case CMD_FAM:
            arguments.setProperty(ARG_FAMILY, arg);
            break;
          case CMD_FILE:
          default:
            String infile = arguments.getProperty(ARG_FILENAME);
            if(infile != null && infile.length() > 0)
            {
              illegalArg(Messages.getString("err.unknownargument"), arg);
              return false;
            }
            arguments.setProperty(ARG_FILENAME, arg);
            break;
        }
        status = CMD_OK;
      }
    }

    return true;
  }

}
