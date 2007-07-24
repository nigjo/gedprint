package net.sourceforge.gedprint.gui.paint;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import net.sourceforge.gedprint.gedcom.Individual;

/** Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class Person implements DrawingObject
{
  private Individual indi;

  public Person(Individual indi)
  {
    this.indi = indi;
  }

  public void paint(Graphics g)
  {
  }

  public Dimension getSize(Graphics g)
  {
    String full = indi.getFullName();

    FontMetrics fm = g.getFontMetrics();
    Rectangle bounds = fm.getStringBounds(full, g).getBounds();

    return null;
  }

}
