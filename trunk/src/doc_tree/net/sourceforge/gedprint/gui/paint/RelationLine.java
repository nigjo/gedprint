package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 26, 2009, 4:53:21 PM
 * 
 * @todo Hier fehlt die Beschreibung der Klasse.
 * 
 * @author hof
 */
public class RelationLine implements DrawingObject
{
  private int[] xPoints;
  private int[] yPoints;

  public RelationLine(Person origin, Point endpoint)
  {
    super();

    Logger.getLogger(getClass().getName()).fine(
        "new line for " + origin.getIndividual().toString() //$NON-NLS-1$
            + " to " + endpoint.toString()); //$NON-NLS-1$

    Vector<Point> points = new Vector<Point>();

    Point loc = (Point) origin.getLocation().clone();
    Dimension size = origin.getSize(null);
    // ermitteln, wo sich die beiden Personen befinden.
    if(loc.y+size.height < endpoint.y)
    {
      // Ursprung ist oberhalb von Endpunkt
      // also Unterkante Ursprung -> Endpunkt
      loc.translate(size.width / 2, size.height);
      points.add((Point) loc.clone());
      loc.y = endpoint.y-3;
      points.add((Point) loc.clone());
      loc.x = endpoint.x;
      points.add((Point) loc.clone());
    }
    else if(loc.y > endpoint.y)
    {
      // Ursprung ist unterhalb von Endpunkt
      // also Oberkante Ursprung -> Endpunkt
      loc.translate(size.width / 2, 0);
      points.add((Point) loc.clone());
      loc.y = endpoint.y+3;
      points.add((Point) loc.clone());
      loc.x = endpoint.x;
      points.add((Point) loc.clone());
    }
    else if(loc.x+size.width < endpoint.x)
    {
      // Ursprung ist links von Endpunkt
      // also rechte Kante Ursprung -> Endpunkt
      loc.translate(size.width, 0);
      loc.y=endpoint.y;
      points.add((Point) loc.clone());
    }
    else
    {
      // Ursprung ist rechts von Endpunkt
      // also links Kante Ursprung -> Endpunkt
      loc.y=endpoint.y;
      points.add((Point) loc.clone());
    }

    // this.endpoint = endpoint;
    points.add(endpoint);
    xPoints = new int[points.size()];
    yPoints = new int[points.size()];
    for(int i = 0; i < xPoints.length; i++)
    {
      Point p = points.get(i);
      xPoints[i] = p.x;
      yPoints[i] = p.y;
    }
  }

  public void paint(Graphics g)
  {
    Color old = g.getColor();
    g.setColor(Color.GRAY);
    g.drawPolyline(xPoints, yPoints, xPoints.length);
    g.setColor(old);
  }

  public void translate(int dx, int dy)
  {
    for(int i = 0; i < xPoints.length; i++)
    {
      xPoints[i] += dx;
      yPoints[i] += dy;
    }
  }

}
