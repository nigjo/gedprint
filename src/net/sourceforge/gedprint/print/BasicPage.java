package net.sourceforge.gedprint.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.gedprint.gedcom.Family;

abstract public class BasicPage
{
  public static final String DEFAULT_FONT = "SansSerif"; //$NON-NLS-1$

  Vector<BasicElement> elements;

  private Border border;

  int pageNumber;

  private Family family;

  public BasicPage()
  {
    super();
  }

  public BasicPage(Family fam)
  {
    this();
    setFamily(fam);
  }

  public Family getFamily()
  {
    return family;
  }

  protected Enumeration<BasicElement> getElements()
  {
    if(elements == null)
      return null;
    return elements.elements();
  }

  public void paintPage(Graphics g, Dimension size, int res)
  {
    //
    // Druckrand bestimmen
    //
    if(border == null)
      border = new Border();

    Rectangle printArea = border.getPrintArea(size, res);

    int left = printArea.x;
    int top = printArea.y;

    Dimension printSize = printArea.getSize();

    //
    // Seite drucken
    //
    if(Boolean.getBoolean("debug")) //$NON-NLS-1$
    {
      // zu Debug-Zwecken: Gitternetz im Hintergrund
      paintCross(g, size, left, top, printSize);
    }

    paintPageContent(g, res, left, top, printSize);

    paintPageHeader(g, left, printArea.y, size, printSize, res);
    paintPageFooter(g, left, printArea.y, size, printSize, res);
  }

  abstract protected void paintPageContent(Graphics g, int res, int left,
      int top, Dimension printSize);

  abstract protected void paintPageHeader(Graphics g, int left, int top,
      Dimension size, Dimension printSize, int res);

  protected String spread(String string)
  {
    char[] chars = string.toCharArray();
    char[] spreaded = new char[string.length() * 2 - 1];
    Arrays.fill(spreaded, ' ');
    for(int i = 0; i < chars.length; i++)
      spreaded[i * 2] = chars[i];

    return new String(spreaded);
  }

  abstract protected void paintPageFooter(Graphics g, int left, int top,
      Dimension size, Dimension printSize, int res);

  public void setFamily(Family family)
  {
    this.family = family;
  }

  protected void paintCross(Graphics g, Dimension size, int left, int top,
      Dimension printSize)
  {
    Color old = g.getColor();

    g.setColor(Color.LIGHT_GRAY);
    // Kasten um Seite drum herum
    g.drawRect(0, 0, size.width - 1, size.height - 1);
    // Kreuz
    g.drawLine(0, 0, size.width - 1, size.height - 1);
    g.drawLine(0, size.height - 1, size.width - 1, 0);
    // "Lineal" am oberen Rand
    for(int pos = 0; pos <= 21; pos++)
    {
      int cm = BasicElement.convertCmToPixel(pos, 72);
      g.drawLine(cm, 10, cm, 40);
    }
    // Gitternetz im Druckbereich
    for(int i = 0; i <= 16; i++)
    {
      int posX = left + i * printSize.width / 16;
      int posY = top + i * printSize.height / 16;
      if(i % 2 == 0)
        g.setColor(new Color(0xCCCCFF));
      else
        g.setColor(new Color(0xCCFFFF));
      g.drawLine(posX, top, posX, top + printSize.height);
      g.drawLine(left, posY, left + printSize.width, posY);
    }

    g.setColor(old);
  }

  public void add(BasicElement element)
  {
    if(elements == null)
      elements = new Vector<BasicElement>();
    elements.add(element);
  }

  /**
   * Setzt die Druckgrenzen der Seite. Alle Angaben sind in cm.
   */
  public void setBorder(double left, double top, double right, double bottom)
  {
    border = new Border(left, top, right, bottom);
  }

  static private class Border
  {
    private final double left;
    private final double top;
    private final double right;
    private final double bottom;

    public Border()
    {
      this(0, 0, 0, 0);
    }

    public Border(double left, double top, double right, double bottom)
    {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
    }

    Rectangle getPrintArea(Dimension pageSize, int pageResolution)
    {
      int iTop = BasicElement.convertCmToPixel(top, pageResolution);
      int iBottom = BasicElement.convertCmToPixel(bottom, pageResolution);
      int iLeft = BasicElement.convertCmToPixel(left, pageResolution);
      int iRight = BasicElement.convertCmToPixel(right, pageResolution);

      return new Rectangle(iLeft, iTop, pageSize.width - iRight - iLeft,
          pageSize.height - iBottom - iTop);
    }
  }

  public void setPageNumber(int number)
  {
    pageNumber = number;
  }

  public int getPageNumber()
  {
    return pageNumber;
  }
}
