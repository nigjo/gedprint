package net.sourceforge.gedprint.gui.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.GedFile;

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
  // File infile;
  private static final String ARG_FILENAME = "filename"; //$NON-NLS-1$
  private static final String ARG_INDIVIDUAL = "individual"; //$NON-NLS-1$
  private static final String ARG_FAMILY = "family"; //$NON-NLS-1$
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
    // defaults.setProperty(ARG_FILENAME, "");
    // defaults.setProperty(ARG_INDIVIDUAL, "");
    // defaults.setProperty(ARG_FAMILY, "");

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

    String painterClassName = "net.sourceforge.gedprint.gui.paint.DrawPanel"; //$NON-NLS-1$
    //String painterClassName = "net.sourceforge.gedprint.gui.book.BookPainter"; //$NON-NLS-1$
    GedFrame frame = null;
    try
    {
      frame = new GedFrame(painterClassName);
    }
    catch(IllegalStateException e)
    {
      illegalArg(Messages.getString("err.painterclass"), //$NON-NLS-1$ 
          e.getCause().toString());
      return;
    }

    String file = arguments.getProperty(ARG_FILENAME);
    if(file != null && file.length() > 0)
    {
      try
      {
        frame.setGedFile(new GedFile(file));
      }
      catch(FileNotFoundException ex)
      {
        illegalArg(Messages.getString("err.filenotfound"), file); //$NON-NLS-1$
        return;
      }
      catch(IOException ex)
      {
        illegalArg(Messages.getString("err.ioerror"), file); //$NON-NLS-1$
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
    String title = Messages.getString(GedFrame.class, "frame.title"); //$NON-NLS-1$
    String message = MessageFormat.format(pattern, new Object[]
    { arg
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
          illegalArg(Messages.getString("err.missingargument"), arg); //$NON-NLS-1$
          return false;
        }
        else if(arg.length() == 1)
        {
          illegalArg(Messages.getString("err.invalidarg"), arg); //$NON-NLS-1$
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
          illegalArg(Messages.getString("err.unknownoption"), arg); //$NON-NLS-1$
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
            illegalArg(Messages.getString("err.unknownargument"), arg); //$NON-NLS-1$
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
