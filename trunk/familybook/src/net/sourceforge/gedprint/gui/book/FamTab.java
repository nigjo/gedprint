package net.sourceforge.gedprint.gui.book;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.print.book.standard.elements.ChildElement;
import net.sourceforge.gedprint.print.book.standard.elements.Element;
import net.sourceforge.gedprint.print.book.standard.elements.HeaderElement;
import net.sourceforge.gedprint.print.book.standard.elements.ParentElement;
import net.sourceforge.gedprint.print.book.standard.elements.WeddingElement;
import net.sourceforge.gedprint.core.Bundle;

public class FamTab extends JPanel
{
  private static final long serialVersionUID = 6035893905021035607L;

  // private final Family fam;
  private boolean firstpaint = true;

  List<Element> elements;

  public FamTab(Family fam)
  {
    super();

    setDoubleBuffered(true);

    setBackground(Color.WHITE);

    // this.fam = fam;
    elements = new ArrayList<Element>();

    elements.add(new HeaderElement(Bundle.getString("print.data.parents", getClass()))); //$NON-NLS-1$
    elements.add(new ParentElement(
        Bundle.getString("print.data.husband", getClass()), fam.getHusband())); //$NON-NLS-1$
    elements.add(new ParentElement(
        Bundle.getString("print.data.wife", getClass()), fam.getWife())); //$NON-NLS-1$

    // Hochzeit
    elements.add(new HeaderElement(Bundle.getString("print.data.marriage", getClass()))); //$NON-NLS-1$
    elements.add(new WeddingElement(fam));

    // Kinder
    elements.add(new HeaderElement(Bundle.getString("print.data.children", getClass()))); //$NON-NLS-1$
    List<Individual> children = fam.getChildren();
    for(Individual child : children)
      elements.add(new ChildElement(child));
    
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
