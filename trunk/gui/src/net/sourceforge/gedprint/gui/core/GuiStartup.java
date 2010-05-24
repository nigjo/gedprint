package net.sourceforge.gedprint.gui.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.sourceforge.gedprint.core.GedPrintStarter;
import net.sourceforge.gedprint.core.Lookup;
import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.GedPrintGui;
import net.sourceforge.gedprint.gui.action.BasicAction;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 1:51:32 PM
 * 
 * @todo Hier fehlt die Beschreibung der Klasse.
 * 
 * @author hof
 */
public class GuiStartup implements GedPrintStarter
{
  //<editor-fold defaultstate="collapsed" desc="implementation details">
  // File infile;
  private static final String ARG_FILENAME = "filename"; //$NON-NLS-1$
  private static final String ARG_INDIVIDUAL = "individual"; //$NON-NLS-1$
  private static final String ARG_FAMILY = "family"; //$NON-NLS-1$
  Properties arguments;

  static
  {
    // Protokollierung initialisieren
    GuiLogger.initLogger();

    // Programmstart in die Protokolldatei mit Zeitstempel
    Logger logger = Logger.getLogger(GedPrintGui.class.getName());
    logger.info("------------------------------"); //$NON-NLS-1$
    logger.info(new SimpleDateFormat().format(new Date()));
  }

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

    Collection<? extends GedDocumentFactory> factories =
        Lookup.getGlobal().lookupAll(GedDocumentFactory.class);
    if(factories == null || factories.size() == 0)
    {
      String msg = Messages.getString("err.no_painter"); //$NON-NLS-1$
      Logger.getLogger(getClass().getName()).warning(msg);
      String title = Messages.getString("frame.title"); //$NON-NLS-1$
      JOptionPane.showMessageDialog(null, msg, title,
          JOptionPane.WARNING_MESSAGE);
      exit(1);
    }

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

    frame.setVisible(true);

    initParameters();
  }

  private void initParameters()
  {
    String file = arguments.getProperty(ARG_FILENAME);
    if(file != null && file.length() > 0)
    {
      try
      {
        GedFile gedfile = new GedFile(file);
        ActionManager.setActionProperty(BasicAction.PROPERTY_FILE, gedfile);
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

      // TODO: frame.setStartID('@' + startid + '@');
      ActionManager.performAction("AddRecordAction", '@' + startid + '@'); //$NON-NLS-1$
    }
  }

  public void exit()
  {
    exit(0);
  }

  public void exit(int rc)
  {
    System.exit(rc);
  }

  private void illegalArg(String pattern, String arg)
  {
    String title = Messages.getString(GedFrame.class, "frame.title"); //$NON-NLS-1$
    String message = MessageFormat.format(pattern, new Object[]
        {
          arg
        });
    JOptionPane.showMessageDialog(null, message, title,
        JOptionPane.ERROR_MESSAGE);
  }
  //</editor-fold>

  enum CommandlineArgument
  {
    indi('i'), family('f'), debug;
    //<editor-fold defaultstate="collapsed" desc="implementation details">
    private Character shortSymbol;

    private CommandlineArgument()
    {
      this(null);
    }

    private CommandlineArgument(Character shortSymbol)
    {
      this.shortSymbol = shortSymbol;
    }

    public Character getShortOption()
    {
      return shortSymbol;
    }
    //</editor-fold>
  }

  public boolean parseCommandline(String[] args)
  {
    //<editor-fold defaultstate="collapsed" desc="implementation details">
    CommandlineArgument lastOption = null;
    for(String arg : args)
    {
      if(arg.charAt(0) == '-')
      {
        if(lastOption != null)
        {
          illegalArg(Messages.getString("err.missingargument"), arg); //$NON-NLS-1$
          return false;
        }
        CommandlineArgument option = null;
        if(arg.charAt(1) == '-')
        {
          try
          {
            // Langversion
            option = CommandlineArgument.valueOf(arg.substring(2));
          }
          catch(IllegalArgumentException e)
          {
            illegalArg(Messages.getString("err.invalidarg"), arg); //$NON-NLS-1$
            return false;
          }
        }
        else
        {
          // Kurzversion
          char optionChar = arg.charAt(1);
          for(CommandlineArgument commandlineArg :
              CommandlineArgument.values())
          {
            Character shortOption =
                commandlineArg.getShortOption();
            if(shortOption != null
                && shortOption == optionChar)
            {
              option = commandlineArg;
              break;
            }
          }
          if(option == null)
          {
            illegalArg(Messages.getString("err.unknownoption"), arg); //$NON-NLS-1$
            return false;
          }
        }

        lastOption = null;
        switch(option)
        {
          //</editor-fold>
          //
          // OPTIONS
          //
          case debug:
            arguments.setProperty("debug", Boolean.TRUE.toString());
            break;
          //<editor-fold defaultstate="collapsed" desc="implementation details">
          default:
            lastOption = option;
            break;
        }
      }
      else
      {
        if(lastOption == null)
        {
          //</editor-fold>
          //
          // Parameter ohne Option.
          //
          String infile = arguments.getProperty(ARG_FILENAME);
          if(infile != null && infile.length() > 0)
          {
            illegalArg(Messages.getString("err.unknownargument"), arg); //$NON-NLS-1$
            return false;
          }
          arguments.setProperty(ARG_FILENAME, arg);
          //<editor-fold defaultstate="collapsed" desc="implementation details">
          break;
        }
        switch(lastOption)
        {
          //</editor-fold>
          //
          // PARAMETERS
          //
          case indi:
            arguments.setProperty(ARG_INDIVIDUAL, arg);
            break;
          case family:
            arguments.setProperty(ARG_FAMILY, arg);
            break;
          //<editor-fold defaultstate="collapsed" desc="implementation details">
        }
        lastOption = null;
      }
    }
    if(lastOption != null)
    {
      illegalArg(Messages.getString("err.missingargument"), args[args.length - 1]); //$NON-NLS-1$
      return false;
    }

    return true;
    //</editor-fold>
  }
}
