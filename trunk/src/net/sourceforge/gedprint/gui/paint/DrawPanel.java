package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

/** Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class DrawPanel extends JPanel
{
  private static final long serialVersionUID = 1601760105575908398L;
  private boolean hasArrangedObjects;
  Vector<DrawingObject> objects;

  public DrawPanel()
  {
    setPreferredSize(new Dimension(800, 600));

    setDoubleBuffered(true);

    setBackground(Color.WHITE);

    hasArrangedObjects = true;
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if(!hasArrangedObjects)
      arrangeObjects(g);

    Dimension d = getSize();

    g.setColor(Color.LIGHT_GRAY);
    g.drawLine(0, 0, d.width, d.height);
    g.drawLine(0, d.height, d.width, 0);

    if(objects != null)
    {
      Enumeration e = objects.elements();
      while(e.hasMoreElements())
        ((DrawingObject)e.nextElement()).paint(g);
    }
  }

  public void add(DrawingObject obj)
  {
    if(objects == null)
      objects = new Vector<DrawingObject>();
    objects.add(obj);
    hasArrangedObjects = false;
  }

  private void arrangeObjects(Graphics g)
  {
    int nextx = BasicObject.BORDER;
    for(DrawingObject object : objects)
    {
      if(object instanceof BasicObject)
      {
        BasicObject bo = (BasicObject)object;
        bo.setLocation(new Point(nextx, BasicObject.BORDER));
        Dimension size = bo.getSize(g);
        nextx += BasicObject.BORDER + size.width;
      }
    }
    hasArrangedObjects = true;
  }

}
