package net.sourceforge.gedprint.gui.core;

import net.sourceforge.gedprint.ui.GedPainter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.GedPrintGui;

/**
 * Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class GedFrame extends JFrame
{
  private static final long serialVersionUID = -7892421281873115631L;
  // private GedFile ged;
  // private GedPainter drawPanel;
  JDesktopPane desktop;

  public GedFrame(String painterClassName)
  {
    super(Bundle.getString("frame.title", GedFrame.class)); //$NON-NLS-1$
    setSize(new Dimension(1024, 768));
    setLocationByPlatform(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // Anzeigeklasse
    desktop = new JDesktopPane();
    desktop.setBackground(Color.LIGHT_GRAY);
    getContentPane().add(desktop);
    DocumentManager.init(desktop);

    // Icons setzen
    ArrayList<Image> icons = new ArrayList<Image>();
    String[] iconResNames = new String[]
    {
      "icon16.png", //$NON-NLS-1$
      "icon32.png", //$NON-NLS-1$
      "icon48.png", //$NON-NLS-1$
      "icon256.png", //$NON-NLS-1$
    };
    MediaTracker mt = new MediaTracker(this);
    for(String iconResName : iconResNames)
    {
      URL iconRes = getClass().getResource(iconResName);
      if(iconRes == null)
        continue;
      Image icon = new ImageIcon(iconRes).getImage();
      icons.add(icon);
      mt.addImage(icon, 0);
    }
    try
    {
      mt.waitForAll();
    }
    catch(InterruptedException e)
    {
      Logger.getLogger(getClass().getName()).log(Level.FINE, e.toString(), e);
    }
    setIconImages(icons);

    // addGedPainter(painterClassName);

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
    // drawPanel.addPropertyChangeListener(GedPainter.PROPERTY_RECORD, status);
    getContentPane().add(status, BorderLayout.SOUTH);
  }

  @Override
  public void dispose()
  {
    if(isReadyToExit())
    {
      Frame[] frames = getFrames();
      for(Frame frame : frames)
      {
        if(frame != this && frame.isVisible())
        {
          return;
        }
      }
      super.dispose();

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

      initListener();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
      String cmd = evt.getPropertyName();
      if(cmd.equals(GedPainter.PROPERTY_RECORD))
        doRecord(evt);
    }

    private void doRecord(PropertyChangeEvent evt)
    {
      Record rec = (Record)evt.getNewValue();
      if(rec == null)
        text.setText(DEFAULT);
      else if(rec instanceof Individual)
        text.setText(rec.toString());
      else
        text.setText(DEFAULT);
    }

    private void initListener()
    {
      Lookup lookup = Lookup.getGlobal();
      lookup.addPropertyChangeListener("status", this);
      lookup.addPropertyChangeListener(GedPainter.PROPERTY_RECORD, this);
    }
  }

  public void close()
  {
    dispose();
  }

}
