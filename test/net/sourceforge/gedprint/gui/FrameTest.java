package net.sourceforge.gedprint.gui;

import junit.framework.TestCase;
import net.sourceforge.gedprint.gedcom.GedFile;

public class FrameTest extends TestCase
{
  private static final int SEC = 1000;

  public void testCreate() throws Exception
  {
    new GedFrame();
  }

  public void testShowClose() throws Exception
  {
    GedFrame frame = new GedFrame();
    frame.setVisible(true);
    Thread.sleep(1 * SEC);
    frame.dispose();
  }

  public void testFamily() throws Exception
  {
    GedFrame frame = new GedFrame();
    frame.setVisible(true);

    frame.setGedFile(new GedFile("hofschroeer.ged"));
    frame.setStartID("@I58@");

    Thread.sleep(10 * SEC);
    frame.dispose();
  }
}
