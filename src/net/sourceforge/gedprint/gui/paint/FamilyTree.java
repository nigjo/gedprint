package net.sourceforge.gedprint.gui.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 4:50:03 PM
 * 
 * @todo Hier fehlt die Beschreibung der Klasse.
 * 
 * @author hof
 */
public class FamilyTree extends BasicObject
{
  private static final boolean DEBUG = false;
  private Family fam;
  //
  Hashtable<Individual, Point> relativePos;
  Hashtable<Individual, BasicObject> elements;
  Vector<RelationLine> lines;
  //
  boolean updateLayout;
  private Dimension layoutsize;
  boolean showChildrenFamily;

  public FamilyTree(Family fam)
  {
    this(fam, false);
  }

  public FamilyTree(Family fam, boolean showChildrenFamily)
  {
    this.fam = fam;

    updateLayout = true;
    this.showChildrenFamily = showChildrenFamily;
  }

  @Override
  public Dimension getSize(Graphics g)
  {
    if(g != null && updateLayout)
      updateLayout(g);
    if(layoutsize == null)
      layoutsize = new Dimension(BORDER, BORDER);
    return layoutsize;
  }

  @Override
  public void setLocation(Point location)
  {
    super.setLocation(location);
    if(relativePos == null)
      return;
    for(RelationLine line : lines)
    {
      line.translate(location.x, location.y);
    }
    for(Individual indi : relativePos.keySet())
    {
      Point relpos = relativePos.get(indi);
      BasicObject pers = elements.get(indi);

      Point wo = new Point(relpos);
      wo.translate(location.x, location.y);
      pers.setLocation(wo);
    }
  }

  public void paint(Graphics g)
  {
    if(updateLayout)
      updateLayout(g);

    for(RelationLine line : lines)
    {
      line.paint(g);
    }
    
    for(Individual individual : elements.keySet())
    {
      BasicObject p = elements.get(individual);
      p.paint(g);
    }

    if(DEBUG)
    {
      Color old = g.getColor();
      Point pos = getLocation();
      Dimension s = getSize(g);
      g.setColor(Color.LIGHT_GRAY);
      g.drawRect(pos.x, pos.y, s.width, s.height);
      g.setColor(new Color(128, 128, 128, 32));
      g.fillRect(pos.x, pos.y, s.width, s.height);
      g.setColor(old);
    }

  }

  private BasicObject layoutPerson(Individual indi, BasicObject drawObject,
      Point nextPos, Dimension size, Graphics g)
  {
    if(drawObject == null)
    {
      drawObject = new Person(indi);
      elements.put(indi, drawObject);
    }

    drawObject.setLocation(new Point(nextPos.x + BORDER, nextPos.y));
    Dimension isize = drawObject.getSize(g);
    nextPos.x += isize.width + BORDER;
    Point iPos = drawObject.getLocation();
    if(size.height < isize.height)
      size.height = isize.height;
    if(size.width < nextPos.x)
      size.width = nextPos.x;

    relativePos.put(indi, (Point) nextPos.clone());

    Point fampos = getLocation();
    iPos.translate(fampos.x, fampos.y);

    return drawObject;
  }

  private void translate(Individual indi, int dx, int dy)
  {
    elements.get(indi).getLocation().translate(dx, dy);
    relativePos.get(indi).translate(dx, dy);
  }

  private void updateLayout(Graphics g)
  {
    Logger.getLogger(getClass().getName()).fine("updateLayout"); //$NON-NLS-1$
    Logger.getLogger(getClass().getName()).fine(fam.toString());
    elements = new Hashtable<Individual, BasicObject>();
    relativePos = new Hashtable<Individual, Point>();
    lines = new Vector<RelationLine>();

    int parentGap = 2 * BORDER;

    Point famCenter = null;

    Point relPos = new Point(0, 0);
    relPos.y += BORDER;
    Dimension psize = new Dimension();

    Individual husband = fam.getHusband();
    Individual wife = fam.getWife();

    // Eltern positionieren
    Person er = (Person) layoutPerson(husband, null, relPos, psize, g);
    relPos.x += parentGap;
    Person sie = (Person) layoutPerson(wife, null, relPos, psize, g);

    int childCount = fam.getChildrenCount();
    if(childCount == 0)
    {
      int erx = er.getLocation().x + er.getSize(g).width;
      int siex = sie.getLocation().x;
      famCenter = new Point((erx + siex) / 2, psize.height / 2);
    }
    else
    {
      // Kinder in der Zeile drunter
      int parentChildrenGap = 4 * BORDER;
      relPos.x = 0;
      relPos.y += psize.height + parentChildrenGap;
      Dimension csize = new Dimension();

      Enumeration children = fam.getChildren();
      while(children.hasMoreElements())
      {
        Individual child = (Individual) children.nextElement();
        BasicObject obj = null;
        if(showChildrenFamily)
        {
          Family[] dataSpouceFamilies = child.getDataSpouceFamilies();
          if(dataSpouceFamilies.length > 0)
          {
            // erstmal nur die erste Ehe
            obj = new FamilyTree(dataSpouceFamilies[0]);
            elements.put(child, obj);
          }
        }
        layoutPerson(child, obj, relPos, csize, g);
      }

      if(psize.width < csize.width)
      {
        // Eltern zentrieren
        int left = (csize.width - psize.width - BORDER) / 2;
        translate(husband, left, 0);
        translate(wife, left, 0);

        // Breite speichern
        psize.width = csize.width;
      }
      else
      {
        // Kinder zentrieren
        int left = (psize.width - csize.width - BORDER) / 2;
        left += left%2;
        children = fam.getChildren();
        while(children.hasMoreElements())
        {
          Individual child = (Individual) children.nextElement();
          translate(child, left, 0);
        }
      }
      Point fampos = getLocation();
      famCenter = new Point(psize.width / 2, BORDER+ psize.height + (parentChildrenGap)
          / 2);
      famCenter.translate(fampos.x, fampos.y);

      // Alle Kinder mit dem Familienpunkt verbinden
      children = fam.getChildren();
      while(children.hasMoreElements()){
        Individual child = (Individual) children.nextElement();
        BasicObject bo = elements.get(child);
        if(bo instanceof FamilyTree){
          FamilyTree subtree = (FamilyTree) bo;
          BasicObject parent = subtree.elements.get(child);
          bo = new Person(child);
          bo.getSize(g);
          bo.setLocation(parent.getLocation());
          bo.translate(fampos.x, fampos.y);
        }
        lines.add(new RelationLine((Person) bo, famCenter));
      }
      
      psize.height += csize.height + parentChildrenGap;
    }

    psize.width += BORDER;
    psize.height += 2 * BORDER;
    layoutsize = psize;

    // Linien ziehen
    lines.add(new RelationLine(er, famCenter));
    lines.add(new RelationLine(sie, famCenter));

    Logger.getLogger(getClass().getName()).fine("done " + fam.toString()); //$NON-NLS-1$
  }

  public Family getFamily()
  {
    return fam;
  }
}
