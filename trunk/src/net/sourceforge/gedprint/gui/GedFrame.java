package net.sourceforge.gedprint.gui;

import java.awt.Dimension;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.paint.DrawPanel;
import net.sourceforge.gedprint.gui.paint.Person;

/** Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class GedFrame extends JFrame
{
  private static final long serialVersionUID = -7892421281873115631L;
  private GedFile ged;
  private DrawPanel drawPanel;

  public GedFrame()
  {
    super(Messages.getString("frame.title"));
    setSize(new Dimension(1024, 768));
    setLocationByPlatform(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    drawPanel = new DrawPanel();
    getContentPane().add(new JScrollPane(drawPanel));
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
    // TODO Automatisch erstellter Methoden-Stub
    Record r = this.ged.findID(string);
    if(r instanceof Individual)
    {
      Individual indi = (Individual)r;
      Logger.getLogger(getClass().getName()).info(indi.getClearedFullName());
      Logger.getLogger(getClass().getName()).info("Age: " + indi.getAge()); //$NON-NLS-1$

      drawPanel.add(new Person(indi));

      Individual father = indi.getDataFather();
      Individual mother = indi.getDataMother();
      Family family = indi.getDataChildFamily();
      Family[] faminlaw = indi.getDataSpouceFamilies();
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
      logger.info("quit application");
      logger.info(new SimpleDateFormat().format(new Date()));
      logger.info("------------------------------");
    }
  }

  private boolean isReadyToExit()
  {
    Logger.getLogger(getClass().getName()).fine("is ready?");

    return true;
  }

}
