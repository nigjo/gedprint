package net.sourceforge.gedprint.gui.book;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.print.book.standard.elements.ChildElement;
import net.sourceforge.gedprint.print.book.standard.elements.Element;
import net.sourceforge.gedprint.print.book.standard.elements.HeaderElement;
import net.sourceforge.gedprint.print.book.standard.elements.ParentElement;
import net.sourceforge.gedprint.print.book.standard.elements.WeddingElement;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

public class FamTab extends JPanel
{
  private static final long serialVersionUID = 6035893905021035607L;

  // private final Family fam;
  private boolean firstpaint = true;

  Vector<Element> elements;

  public FamTab(Family fam)
  {
    super();

    setDoubleBuffered(true);

    setBackground(Color.WHITE);

    // this.fam = fam;
    elements = new Vector<Element>();

    elements.add(new HeaderElement(Messages.getString("print.data.parents"))); //$NON-NLS-1$
    elements.add(new ParentElement(
        Messages.getString("print.data.husband"), fam.getHusband())); //$NON-NLS-1$
    elements.add(new ParentElement(
        Messages.getString("print.data.wife"), fam.getWife())); //$NON-NLS-1$

    // Hochzeit
    elements.add(new HeaderElement(Messages.getString("print.data.marriage"))); //$NON-NLS-1$
    elements.add(new WeddingElement(fam));

    // Kinder
    elements.add(new HeaderElement(Messages.getString("print.data.children"))); //$NON-NLS-1$
    Enumeration children = fam.getChildren();
    while(children.hasMoreElements())
    {
      elements.add(new ChildElement((Individual) children.nextElement()));
    }
    
    for(Element e : elements)
    {
      e.setLineHeight(1.5);
    }
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    int left, top;
    Dimension size=getSize();

    top = 0;
    if(firstpaint)
    {
      left = (int) (size.width * .1);
      size.width *= .8;
    }else{
      Dimension psize = getPreferredSize();
      left = (size.width-psize.width)/2;
      size = psize;
    }

    for(Element element : elements)
    {
      top += element.print(g, left, top, size, 72);
    }

    if(firstpaint)
    {
      firstpaint = false;

      // size = getSize();
      size.height = top+1;
      //size.width+=20;

      // Groesse des Panel neu definieren
      setPreferredSize(size);
      // und ggf. die Scrollbalken erzwingen
      invalidate();
      getParent().validate();
    }
  }
}
