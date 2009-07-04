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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;

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

    StatusZeile status = new StatusZeile();
    drawPanel.addPropertyChangeListener(GedPainter.PROPERTY_RECORD, status);
    getContentPane().add(status, BorderLayout.SOUTH);
  }

  /**
   * @param gedfile
   */
  public void setGedFile(GedFile gedfile)
  {
    // TODO Automatisch erstellter Methoden-Stub
    this.ged = gedfile;
  }

  /**
   * @param string
   */
  public void setStartID(String string)
  {
    drawPanel.clearAll();
    
    Record r = this.ged.findID(string);
    if(r instanceof Individual)
    {
      Individual indi = (Individual) r;
      Logger.getLogger(getClass().getName()).info(indi.getClearedFullName());
      Logger.getLogger(getClass().getName()).info("Age: " + indi.getAge()); //$NON-NLS-1$

      drawPanel.add(indi);

      // Individual father = indi.getDataFather();
      // Individual mother = indi.getDataMother();
      // Family family = indi.getDataChildFamily();
      // Family[] faminlaw = indi.getDataSpouceFamilies();
    }
    else if(r instanceof Family)
    {
      Family fam = (Family) r;
      Logger.getLogger(getClass().getName()).fine(fam.toString());

      drawPanel.add(fam);
      if(fam.getChildrenCount() > 0)
      {
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
}
