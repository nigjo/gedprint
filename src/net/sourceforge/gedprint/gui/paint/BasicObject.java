package net.sourceforge.gedprint.gui.paint;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 10:23:39 AM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public abstract class BasicObject implements DrawingObject
{
  private Point location;

  public static final int BORDER = 5;

  public BasicObject()
  {
  }

  public void setLocation(Point location)
  {
    this.location = location;
  }

  public Point getLocation()
  {
    return location;
  }

  abstract public Dimension getSize(Graphics g);
}
