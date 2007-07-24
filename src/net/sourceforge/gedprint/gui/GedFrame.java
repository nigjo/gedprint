package net.sourceforge.gedprint.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
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

  public GedFrame()
  {
    super("GEDFrame");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel drawPanel = new DrawPanel();
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
      System.out.println("Age: " + indi.getAge());
    }
  }

  /**
   * 
   */
  public void center()
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frame = getSize();

    this.setLocation(
      (screen.width - frame.width) / 2,
      (screen.height - frame.height) / 2);
  }
}
