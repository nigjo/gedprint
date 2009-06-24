package net.sourceforge.gedprint.gui;

import java.awt.Dimension;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.paint.DrawPanel;

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
    super("GEDFrame");
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
    if (r instanceof Individual)
    {
      Individual indi = (Individual) r;
      System.out.println(indi.getClearedFullName());
      System.out.println("Age: " + indi.getAge()); //$NON-NLS-1$
      
      indi.getDataFather();
      indi.getDataMother();
      indi.getDataChildFamily();
      indi.getDataSpouceFamilies();
    }
  }

  @Override
  public void dispose()
  {
    if(isReadyToExit())
    {
      super.dispose();
    }
  }

  private boolean isReadyToExit()
  {
    Logger.getLogger(getClass().getName()).fine("is ready?");

    return true;
  }

}
