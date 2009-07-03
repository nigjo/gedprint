package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.GedPainter;

/**
 * Neue Klasse erstellt am 07.02.2005.
 * 
 * @author nigjo
 */
public class DrawPanel extends GedPainter
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

    addMouseMotionListener(new MouseMotionAdapter() {

      Record lastRecord = null;

      @Override
      public void mouseMoved(MouseEvent e)
      {
        Record rec = getRecord(e.getPoint());
        if(rec != lastRecord)
        {
          firePropertyChange(PROPERTY_RECORD, lastRecord, rec);
          lastRecord = rec;
        }
      }
    });
  }
  
  @Override
  public void add(Individual indi)
  {
    add(new Person(indi));
  }
  
  @Override
  public void add(Family fam)
  {
    add(new FamilyTree(fam, true));
  }

  protected Record getRecord(Point point)
  {
    if(objects == null)
      return null;
    for(DrawingObject obj : objects)
    {
      if(!(obj instanceof BasicObject))
        continue;
      BasicObject bobj = (BasicObject) obj;
      Record record = checkObject(bobj, point);
      if(record != null)
        return record;
    }
    return null;
  }

  private Record checkObject(BasicObject bobj, Point point)
  {
    Point location = bobj.getLocation();
    Dimension size = bobj.getSize(null);
    if(location == null || size == null)
      return null;
    Rectangle boundingbox = new Rectangle(location, size);
    if(boundingbox.contains(point))
    {
      if(bobj instanceof Person)
        return ((Person) bobj).getIndividual();
      else if(bobj instanceof FamilyTree)
      {
        FamilyTree famtree = (FamilyTree) bobj;
        Record rec = null;
        for(Individual indi : famtree.getIndividuals())
        {
          rec = checkObject(famtree.getObject(indi), point);
          if(rec != null)
            break;
        }
        if(rec == null)
          rec = famtree.getFamily();
        return rec;
      }
      else
        throw new IllegalStateException("unknown object type"); //$NON-NLS-1$
    }
    return null;
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
