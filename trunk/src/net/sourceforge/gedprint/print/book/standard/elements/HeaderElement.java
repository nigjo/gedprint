package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Eine neue Klasse von hof. Erstellt am 10.05.2007, 15:22:31
 * 
 * @todo Hier fehlt die Beschreibung der Klasse
 * 
 * @author hof
 */
public class HeaderElement extends Element
{
  String title;

  public HeaderElement(String title)
  {
    this.title = title;
  }

  public int print(Graphics g, int left, int top, Dimension size, int res)
  {
    int height = convertCmToPixel(getDefaultLineHeight() * .6, res);

    // Hintergrund
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(left, top, size.width, height);
    // Kasten drum herum
    g.setColor(Color.BLACK);
    g.drawRect(left, top, size.width, height);
    // Oben 'ne Dicke linie;
    g.fillRect(left, top - 1, size.width, 2);

    if(title != null)
    {
      g.setFont(getFont(FONT_HEADER));
      int fontline = getFontline(g);
      int indent = getDefaultIndent(res);
      g.drawString(title, left + indent, top + fontline);
    }

    return height;
  }

}
