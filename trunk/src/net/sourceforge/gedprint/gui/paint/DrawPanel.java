package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Vector;

import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class DrawPanel extends JPanel
{
  private static final long serialVersionUID = 1601760105575908398L;
  Vector<DrawingObject> objects;
  BufferedImage buffer;

  public DrawPanel()
  {
    setPreferredSize(new Dimension(800, 600));

    setDoubleBuffered(true);

    setBackground(Color.WHITE);

    buffer = null;
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if(buffer == null)
    {
      Logger.getLogger(getClass().getName()).fine("create buffer"); //$NON-NLS-1$
      // Objekte ausrichten
      Dimension bufsize = arrangeObjects(g, objects);
      if(bufsize == null)
      {
        // Mini-Dummy-Bild erzeugen.
        bufsize = new Dimension(10, 10);
      }

      // Groesse des Panel neu definieren
      setPreferredSize(bufsize);
      // und ggf. die Scrollbalken erzwingen
      invalidate();
      getParent().validate();

      // Puffer erstellen mit aktueller Groesse.
      // Es wird nur in Graustufen gezeichnet, deswegen sollte hier nichts
      // anderes verwendet werden.
      buffer = new BufferedImage(bufsize.width, bufsize.height,
          BufferedImage.TYPE_BYTE_GRAY);

      Graphics bg = buffer.getGraphics();
      bg.setColor(getBackground());
      bg.fillRect(0, 0, bufsize.width, bufsize.height);
      bg.setFont(g.getFont());

      // alle Objekte zeichnen
      if(objects != null)
      {
        Enumeration e = objects.elements();
        while(e.hasMoreElements())
        {
          ((DrawingObject) e.nextElement()).paint(bg);
        }
      }

    }

    g.drawImage(buffer, 0, 0, this);
  }

  public void add(DrawingObject obj)
  {
    if(objects == null)
      objects = new Vector<DrawingObject>();
    objects.add(obj);
    buffer = null;
  }

  private Dimension arrangeObjects(Graphics g, Iterable<DrawingObject> dobjects)
  {
    if(dobjects == null)
      return null;
    Point frameborder = new Point(BasicObject.BORDER * 4,
        BasicObject.BORDER * 3);

    int nextx = frameborder.x;
    int nexty = frameborder.y;
    Dimension psize = new Dimension();
    for(DrawingObject object : dobjects)
    {
      if(object instanceof BasicObject)
      {
        BasicObject bo = (BasicObject) object;
        bo.setLocation(new Point(nextx, nexty));
        Dimension size = bo.getSize(g);
        if(size == null)
        {
          Logger.getLogger(DrawPanel.class.getName()).info(
              "no size for " + bo.toString()); //$NON-NLS-1$
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
    psize.width += 2 * frameborder.x;
    psize.height += frameborder.y;
    return psize;
  }

}
