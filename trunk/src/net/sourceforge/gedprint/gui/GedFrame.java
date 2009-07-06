package net.sourceforge.gedprint.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.action.Exit;
import net.sourceforge.gedprint.gui.action.FileMenuAction;
import net.sourceforge.gedprint.gui.action.OpenGedcom;
import net.sourceforge.gedprint.gui.action.PrintFamilyBook;

/**
 * Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class GedFrame extends JFrame
{
  private static final long serialVersionUID = -7892421281873115631L;
  private GedFile ged;
  private GedPainter drawPanel;

  public GedFrame(String painterClassName)
  {
    super(Messages.getString("frame.title")); //$NON-NLS-1$
    setSize(new Dimension(1024, 768));
    setLocationByPlatform(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // Anzeigeklasse
    addGedPainter(painterClassName);

    // Menue und Toolbar
    initMenu();

    // Statuszeile initialisieren
    initStatus();
  }

  private void initMenu()
  {
    JMenuBar menubar = new JMenuBar();
    JMenu menu;

    menu = new JMenu(new FileMenuAction());
    menu.add(new OpenGedcom());
    menu.addSeparator();
    menu.add(new PrintFamilyBook());
    menu.addSeparator();
    menu.add(new Exit());
    menubar.add(menu);

    setJMenuBar(menubar);
  }

  private void initStatus()
  {
    StatusZeile status = new StatusZeile();
    drawPanel.addPropertyChangeListener(GedPainter.PROPERTY_RECORD, status);
    getContentPane().add(status, BorderLayout.SOUTH);
  }

  private void addGedPainter(String painterClassName)
  {
    try
    {
      Class cl = Class.forName(painterClassName);
      drawPanel = (GedPainter) cl.newInstance();
    }
    catch(Exception e)
    {
      throw new IllegalStateException(e);
    }
    if(drawPanel.isScrollable())
      getContentPane().add(new JScrollPane(drawPanel));
    else
      getContentPane().add(drawPanel);
  }

  /**
   * @param gedfile
   */
  public void setGedFile(GedFile gedfile)
  {
    this.ged = gedfile;
    setStartID(null);
  }

  /**
   * @param string
   */
  public void setStartID(String string)
  {
    drawPanel.clearAll();

    // Abbrechen, wenn keine GEDCOM Datei oder
    // keine ID angegeben ist.
    if(this.ged == null || string == null)
    {
      updatePanel();
      return;
    }

    Record r = this.ged.findID(string);
    if(r instanceof Individual)
    {
      Individual indi = (Individual) r;
      Logger.getLogger(getClass().getName()).info(indi.getClearedFullName());
      Logger.getLogger(getClass().getName()).info("Age: " + indi.getAge()); //$NON-NLS-1$

      drawPanel.add(indi);
    }
    else if(r instanceof Family)
    {
      Family fam = (Family) r;
      Logger.getLogger(getClass().getName()).fine(fam.toString());

      // Die Familie selbst
      drawPanel.add(fam);

      if(fam.getChildrenCount() > 0)
      {
        // und die Familien der Kinder.
        for(Family family : fam.getChildFamilies())
        {
          drawPanel.add(family);
        }
      }
    }
    else if(r == null)
    {
      Logger.getLogger(getClass().getName()).fine("no record found"); //$NON-NLS-1$
    }
    else
    {
      Logger.getLogger(getClass().getName()).fine(r.toString());
    }

    updatePanel();
  }

  private void updatePanel()
  {
    if(isVisible())
    {
      repaint();
      validate();
    }
  }

  @Override
  public void dispose()
  {
    if(isReadyToExit())
    {
      super.dispose();
      Frame[] frames = getFrames();
      for(Frame frame : frames)
      {
        if(frame.isVisible())
        {
          return;
        }
      }

      // wenn ich hier angekommen bin, ist kein Fenster mehr sichtbar.
      Logger logger = Logger.getLogger(GedPrintGui.class.getName());
      logger.info("quit application"); //$NON-NLS-1$
      logger.info(new SimpleDateFormat().format(new Date()));
      logger.info("------------------------------"); //$NON-NLS-1$
    }
  }

  private boolean isReadyToExit()
  {
    Logger.getLogger(getClass().getName()).fine("is ready?"); //$NON-NLS-1$

    return true;
  }

  private static class StatusZeile extends JPanel implements
      PropertyChangeListener
  {
    private static final String DEFAULT = "none"; //$NON-NLS-1$
    private static final long serialVersionUID = 4859664657296219900L;
    JLabel text;

    public StatusZeile()
    {
      super(new FlowLayout(FlowLayout.LEFT, 2, 0));
      text = new JLabel(DEFAULT);
      add(text);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
      String cmd = evt.getPropertyName();
      if(cmd.equals(GedPainter.PROPERTY_RECORD))
        doRecord(evt);
    }

    private void doRecord(PropertyChangeEvent evt)
    {
      Record rec = (Record) evt.getNewValue();
      if(rec == null)
        text.setText(DEFAULT);
      else if(rec instanceof Individual)
        text.setText(rec.toString());
      else
        text.setText(DEFAULT);
    }
  }

  public void close()
  {
    dispose();
  }

  public Record getRecord()
  {
    if(drawPanel == null)
      return null;
    return drawPanel.getRecord();
  }
}
