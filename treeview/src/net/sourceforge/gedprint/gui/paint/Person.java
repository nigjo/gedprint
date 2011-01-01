package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.text.DateFormat;
import java.util.Date;
import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.gedcom.Individual;

/** Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class Person extends BasicObject
{
  private Individual indi;
  String contentString;

  Dimension lastdim;

  public Person(Individual indi)
  {
    this.indi = indi;
    Date d;
    StringBuilder builder = new StringBuilder();

    DateFormat fmt = DateFormat.getDateInstance();
    builder.append(indi.getClearedFullName());
    d = indi.getBirthDate();
    builder.append('\n');
    if(d != null)
    {
      builder.append(Bundle.getString("BIRT.title", getClass())); //$NON-NLS-1$
      builder.append(' ');
      builder.append(fmt.format(d));
    }
    d = indi.getDeathDate();
    builder.append('\n');
    if(d != null)
    {
      builder.append(Bundle.getString("DEAT.title", getClass())); //$NON-NLS-1$
      builder.append(' ');
      builder.append(fmt.format(d));
    }
    contentString = builder.toString();

  }

  @Override
  public void paint(Graphics g)
  {
    Dimension s = getSize(g);

    Point pos = getLocation();
    if(pos == null)
      return;

    Color old = g.getColor();
    g.setColor(Color.BLACK);
    g.drawRect(pos.x, pos.y, s.width, s.height);

    int lineheight = g.getFontMetrics().getHeight();
    String[] lines = contentString.split("[\\n\\r]+"); //$NON-NLS-1$
    int ypos = pos.y;
    for(String line : lines)
    {
      ypos += lineheight;
      g.drawString(line, pos.x + BORDER, ypos);
    }

    g.setColor(old);
  }

  @Override
  public Dimension getSize(Graphics g)
  {
    if(g==null)
      return lastdim;
    FontMetrics fm = g.getFontMetrics();
    int width = 0;
    int height = 0;

    String[] lines = contentString.split("[\\n\\r]+"); //$NON-NLS-1$
    for(String line : lines)
    {
      Rectangle bounds = fm.getStringBounds(line, g).getBounds();
      if(width < bounds.width)
        width = bounds.width;
      height += fm.getHeight();
    }

    lastdim = new Dimension(width + 2 * BORDER, height + BORDER);
    return lastdim;
  }

  public Individual getIndividual()
  {
    return indi;
  }
}
