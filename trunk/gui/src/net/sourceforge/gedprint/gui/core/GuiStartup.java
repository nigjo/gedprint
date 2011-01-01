package net.sourceforge.gedprint.gui.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.sourceforge.gedprint.core.GedPrintStarter;
import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.GedPrintGui;
import net.sourceforge.gedprint.gui.action.OpenGedcom;
import net.sourceforge.gedprint.gui.cmdline.CommandlineArgument;
import static net.sourceforge.gedprint.gui.cmdline.CommandlineArgument.*;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 1:51:32 PM
 * 
 * @todo Hier fehlt die Beschreibung der Klasse.
 * 
 * @author hof
 */
public class GuiStartup implements GedPrintStarter
{
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
    //Properties defaults = new Properties();
//    arguments = new Properties(defaults);
  }

  @Override
  public void run()
  {
    // DEBUG start
//    if(arguments.size() > 0)
//    {
//      StringWriter sw = new StringWriter();
//      PrintWriter out = new PrintWriter(sw);
//      arguments.list(out);
//      Logger.getLogger(getClass().getName()).finer(sw.toString());
//    }
//    else
//      Logger.getLogger(getClass().getName()).fine("no arguments");
    // DEBUG end

    Collection<? extends GedDocumentFactory> factories =
        Lookup.getGlobal().lookupAll(GedDocumentFactory.class);
    if(factories == null || factories.isEmpty())
    {
      String msg = Bundle.getString("err.no_painter", getClass()); //$NON-NLS-1$
      Logger.getLogger(getClass().getName()).warning(msg);
      String title = Bundle.getString("frame.title", getClass()); //$NON-NLS-1$
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
      illegalArg(Bundle.getString("err.painterclass", getClass()), //$NON-NLS-1$
          e.getCause().toString());
      return;
    }

    frame.setVisible(true);

    initParameters();
  }

  public static void illegalArg(String pattern, Object... arg)
  {
    String title = Bundle.getString("frame.title", GedFrame.class); //$NON-NLS-1$
    String message = MessageFormat.format(pattern, arg);
    JOptionPane.showMessageDialog(null, message, title,
        JOptionPane.ERROR_MESSAGE);
  }

  private void initParameters()
  {
    if(_DEFAULT.isDefined())
    {
      String file = _DEFAULT.getArgument();
      try
      {
        GedFile gedfile = new GedFile(file);
        //Lookup l = Lookup.create(gedfile);

        //ActionManager.setActionProperty(BasicAction.PROPERTY_FILE, gedfile);
        if(type.isDefined())
        {
          //TODO: add factory to lookup
        }
        ActionManager.performAction(OpenGedcom.class, "openfile", gedfile);
      }
      catch(FileNotFoundException ex)
      {
        illegalArg(Bundle.getString("err.filenotfound", getClass()), file); //$NON-NLS-1$
        return;
      }
      catch(IOException ex)
      {
        illegalArg(Bundle.getString("err.ioerror", getClass()), file); //$NON-NLS-1$
        return;
      }

      // StartID setzen, wenn Datei gelesen wurde
      String startid = null;
      if(indi.isDefined())
        startid = indi.getArgument();
      else if(family.isDefined())
        startid = family.getArgument();

      if(startid != null)
      {
        // TODO: frame.setStartID('@' + startid + '@');
        ActionManager.performAction("AddRecordAction", "id", '@' + startid + '@'); //$NON-NLS-1$
      }
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

  @Override
  public boolean parseCommandline(String[] args)
  {
    try
    {
      CommandlineArgument.parseCommandline(args);
    }
    catch(CommandlineArgumentException e)
    {
      illegalArg(e.getLocalizedMessage());
      return false;
    }
    return true;
  }
}
