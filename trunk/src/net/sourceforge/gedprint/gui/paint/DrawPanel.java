package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import java.util.logging.Logger;
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
    Point frameborder = new Point(BasicObject.BORDER * 6,
        BasicObject.BORDER * 3);

    int nextx = frameborder.x;
    int nexty = frameborder.y;
    Dimension psize = new Dimension();
    for(DrawingObject object : objects)
    {
      if(object instanceof BasicObject)
      {
        BasicObject bo = (BasicObject)object;
        bo.setLocation(new Point(nextx, nexty));
        Dimension size = bo.getSize(g);
        if(size == null)
        {
          Logger.getLogger(getClass().getName()).info(
              "no size for " + bo.toString());
        }
        else
        {
          nexty += BasicObject.BORDER + size.height;

          if(psize.height < nexty)
            psize.height = nexty;
          if(psize.width < size.width)
            psize.width = size.width;
        }
      }
    }
    psize.width+= 2*frameborder.x;
    psize.height += frameborder.y;
    setPreferredSize(psize);
    hasArrangedObjects = true;
  }

}
