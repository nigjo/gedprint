package net.sourceforge.gedprint.gui.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.GedPrintGui;
import net.sourceforge.gedprint.gui.action.BasicAction;

/**
 * Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class GedFrame extends JFrame
{
  private static final long serialVersionUID = -7892421281873115631L;
  //private GedFile ged;
  private GedPainter drawPanel;

  public GedFrame(String painterClassName)
  {
    super(Messages.getString("frame.title")); //$NON-NLS-1$
    setSize(new Dimension(1024, 768));
    setLocationByPlatform(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // Icon setzen
    URL iconRes = getClass().getResource("icon32.png"); //$NON-NLS-1$
    Image icon = new ImageIcon(iconRes).getImage();
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(icon, 0);
    try
    {
      mt.waitForAll();
    }
    catch(InterruptedException e)
    {
      e.printStackTrace();
    }
    setIconImage(icon);

    // Anzeigeklasse
    addGedPainter(painterClassName);

    // Menue und Toolbar
    initMenu();

    // Statuszeile initialisieren
    initStatus();
  }

  private void initMenu()
  {
    JMenuBar menubar = MenuGenerator.generate();

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
    
    ActionManager.setActionProperty("painter", drawPanel);
  }

  /**
   * @param gedfile
   * @deprecated
   */
  /*public void setGedFile(GedFile gedfile)
  {
    //setStartID(null);
    //this.ged = gedfile;
    ActionManager.setActionProperty(BasicAction.PROPERTY_FILE, gedfile);
  }*/

  /**
   * @param string
   * @deprecated
   */
  public void setStartID(String string)
  {
    drawPanel.clearAll();

    GedFile ged = (GedFile) ActionManager.getActionProperty(BasicAction.PROPERTY_FILE);
    // Abbrechen, wenn keine GEDCOM Datei oder
    // keine ID angegeben ist.
    if(ged == null || string == null)
    {
      ActionManager.performAction("AddRecordAction", null); //$NON-NLS-1$
      //ActionManager.setActionProperty(BasicAction.PROPERTY_RECORD, null);
      //updatePanel();
      return;
    }

    Record r = ged.findID(string);
    if(r instanceof Individual)
    {
      Individual indi = (Individual) r;
      Logger.getLogger(getClass().getName()).info(indi.getClearedFullName());
      Logger.getLogger(getClass().getName()).info("Age: " + indi.getAge()); //$NON-NLS-1$

      drawPanel.addRecord(indi);
    }
    else if(r instanceof Family)
    {
      Family fam = (Family) r;
      Logger.getLogger(getClass().getName()).fine(fam.toString());

      // Die Familie selbst
      drawPanel.addRecord(fam);

      if(fam.getChildrenCount() > 0)
      {
        // und die Familien der Kinder.
        for(Family family : fam.getChildFamilies())
        {
          drawPanel.addRecord(family);
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

    //ActionManager.setActionProperty(BasicAction.PROPERTY_RECORD, r);
    ActionManager.performAction("AddRecordAction", r); //$NON-NLS-1$
    updatePanel();
  }

  /**
   * @deprecated
   */
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

  /**
   * @deprecated
   */
  public Record getRecord()
  {
    if(drawPanel == null)
      return null;
    return drawPanel.getRecord();
  }
}
