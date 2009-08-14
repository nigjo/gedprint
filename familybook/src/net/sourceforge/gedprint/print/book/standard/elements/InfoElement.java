package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Eine neue Klasse von hof. Erstellt am 11.05.2007, 13:53:56
 * 
 * @todo Hier fehlt die Beschreibung der Klasse
 * 
 * @author hof
 */
public class InfoElement extends Element
{
  String description;

  public InfoElement()
  {
    super();
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public int print(Graphics g, int left, int top, Dimension size, int res)
  {
    int height = convertCmToPixel(getDefaultLineHeight() * 4.4, res);

    // Kasten drum herum
    g.drawRect(left, top, size.width, height);

    if(description != null)
    {
      int fontline = getFontline(g, getFont(FONT_TITLE));
      int indent = getDefaultIndent(res);

      g.setFont(getFont(FONT_TITLE));
      g.drawString(description, left + indent, top + fontline);
    }

    return height;
  }
}
