package net.sourceforge.gedprint.gui;

import net.sourceforge.gedprint.gui.core.DocumentManager;
import net.sourceforge.gedprint.ui.GedDocumentFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.gedprint.core.GedPrintStarter;
import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.action.AddRecordAction;
import net.sourceforge.gedprint.gui.action.OpenGedcom;
import net.sourceforge.gedprint.gui.cmdline.CommandlineArgument;
import net.sourceforge.gedprint.gui.core.ActionManager;
import net.sourceforge.gedprint.gui.core.GedFrame;
import static net.sourceforge.gedprint.gui.cmdline.CommandlineArgument.*;
import net.sourceforge.gedprint.ui.GedPainter;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 1:51:32 PM
 * 
 * @todo Hier fehlt die Beschreibung der Klasse.
 * 
 * @author hof
 */
public class GuiStartup implements GedPrintStarter
{
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
      Lookup.getGlobal().put(frame);
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
      GedFile gedfile = null;
      try
      {
        gedfile = new GedFile(file);
        //Lookup l = Lookup.create(gedfile);

        OpenGedcom action = ActionManager.getAction(OpenGedcom.class);
        if(type.isDefined())
        {
          String argument = type.getArgument();
          int dot = argument.lastIndexOf('.');
          Collection<? extends GedDocumentFactory> factories =
              Lookup.getGlobal().lookupAll(GedDocumentFactory.class);
          GedDocumentFactory typeFactory = null;
          for(GedDocumentFactory factory : factories)
          {
            if(dot > 0)
            {
              if(argument.equals(factory.getClass().getName()))
              {
                typeFactory = factory;
                break;
              }
            }
            else
            {
              if(argument.equals(factory.getName()))
              {
                typeFactory = factory;
                break;
              }
            }
          }
          if(typeFactory == null)
            action.openFile(gedfile, false);
          else
            action.openFile(gedfile, typeFactory);
        }
        else
          action.openFile(gedfile, true);
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

      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          GedPainter painter = DocumentManager.getActiveDocument();
          if(painter == null)
            return;
          GedFile activeFile = painter.getGedFile();
          if(activeFile == null)
            return;
          // StartID setzen, wenn Datei gelesen wurde
          String startid = null;
          if(indi.isDefined())
            startid = indi.getArgument();
          else if(family.isDefined())
            startid = family.getArgument();

          if(startid != null)
          {
            // TODO: frame.setStartID('@' + startid + '@');
            AddRecordAction action = ActionManager.getAction(
                AddRecordAction.class);
            action.setProperty("GedFile", activeFile);
            action.setProperty("GedPainter", painter);
            action.addId('@' + startid + '@');
            //ActionManager.performAction("AddRecordAction", "id", '@' + startid + '@'); //$NON-NLS-1$
          }
        }
      });
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
