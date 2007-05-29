package net.sourceforge.gedprint.print;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import net.sourceforge.gedprint.gedcom.Record;

abstract public class BasicElement
{
  private static FontRenderContext frc;

  private Record data;

  abstract public int print(Graphics g, int left, int top, Dimension size,
      int res);
  
  protected int[] getTabs(int left, Dimension size, int res)
  {
    int width = size.width;
    double cmWidth = convertPixelToCm(width, res);

    int tabs[] = new int[] { left, left + convertCmToPixel(1, res),
        left + convertCmToPixel(cmWidth / 3., res),
        left + convertCmToPixel(cmWidth / 2., res),
        left + convertCmToPixel(cmWidth * .9, res), left + width };
    return tabs;
  }

  /**
   * liefert die Trennlinien zwischen den Datenzeilen
   * 
   * @param top
   *          Position der obersten Linie
   * @param res
   *          Aufloesung des Grafikkontexts
   * @param count
   *          Anzahl der Datenzeilen
   * @return Feld mit Positionsangaben der Linien. Das Feld ist um ein Element
   *         groesser als count
   */
  protected int[] getLines(int top, int res, int count)
  {
    int lines[] = new int[count + 1];
    lines[0] = top;
    double defaultLineHeight=getDefaultLineHeight();
    for(int i = 1; i < lines.length; i++)
      lines[i] = top + convertCmToPixel(i * defaultLineHeight, res);
    return lines;
  }

  abstract protected double getDefaultLineHeight();

  public static int getFontline(Graphics g)
  {
    return getFontline(g, g.getFont());
  }

  public static int getFontline(Graphics g, Font f)
  {
    int baseline = (int) (f.getSize2D() * 1.1);
    if(baseline < f.getSize() + 1)
      baseline = f.getSize() + 1;
    return baseline;
  }

  public static int convertCmToPt(double cm)
  {
    return convertCmToPixel(cm, 72);
  }

  public static int convertInchToPt(double inch)
  {
    return convertInchToPixel(inch, 72);
  }

  public static int convertCmToPixel(double cm, int res)
  {
    return convertInchToPixel(cm / 2.54, res);
  }

  public static int convertInchToPixel(double inch, int res)
  {
    return (int) (inch * res);
  }

  public static double convertPixelToCm(int pixel, int res)
  {
    return convertPixelToInch(pixel, res) * 2.54;
  }

  public static double convertPixelToInch(int pixel, int res)
  {
    return pixel / (double) res;
  }

  public void setRecord(Record data)
  {
    this.data = data;
  }

  public Record getRecord()
  {
    return data;
  }

  abstract protected int getDefaultIndent(int res);

  protected int getMaxWidth(int res, String[] testStrings, Font font)
  {
    int width = 0;
    Rectangle2D stringBounds;
    for(int i = 0; i < testStrings.length; i++)
    {
      String[] lines = getLines(testStrings[i]);
      for(int l = 0; l < lines.length; l++)
      {
        stringBounds = getStringBounds(lines[l], font);
        if(width < stringBounds.getWidth())
          width = (int) stringBounds.getWidth();
      }
    }

    return width + 3 * getDefaultIndent(res);
  }

  protected void drawString(Graphics g, String text, int left, int top,
      int fontline)
  {
    String[] lines = getLines(text);
    for(int i = 0; i < lines.length; i++)
      g.drawString(lines[i], left, top + (i + 1) * fontline);
  }

  private String[] getLines(String text)
  {
    return text.split("\r\n|\r|\n"); //$NON-NLS-1$
  }

  public static String shrink(String content, Graphics g, int width)
  {
    Rectangle2D bounds = getStringBounds(content, g);
    if(bounds.getWidth() < width)
      return content;

    // Verkuerzen bis es passt.
    do
    {
      if(content.length() <= 4)
        return "-"; //$NON-NLS-1$
      String substring = content.substring(0, content.length() - 4);
      content = substring + "..."; //$NON-NLS-1$

      bounds = getStringBounds(content, g);
    }
    while(bounds.getWidth() > width);

    return content;
  }

  /**
   * liefert den Unterscheid, den der Text verschoben werden muss um auf der
   * Breite zentriert zu werden.
   * 
   * @param content
   *          Text zum Zentrieren
   * @param g
   *          aktueller Grafikkontext
   * @param width
   *          Breite der Zelle
   */
  public static int center(String content, Graphics g, int width)
  {
    Rectangle2D bounds = getStringBounds(content, g);
    int left = (width - (int) bounds.getWidth()) / 2;

    return left;
  }

  protected static Rectangle2D getStringBounds(String content, Graphics g)
  {
    return getStringBounds(content, g.getFont());
  }

  protected static Rectangle2D getStringBounds(String content, Font font)
  {
    // Eigenen Context erstellen, da PrintGraphics keinen eigenen hat
    if(frc == null)
      frc = new FontRenderContext(null, false, true);

    Rectangle2D bounds = font.getStringBounds(content, frc);
    return bounds;
  }

  public static int alignRight(String content, Graphics g, int width)
  {
    Rectangle2D bounds = getStringBounds(content, g);

    return width - (int) bounds.getWidth();
  }

  public static Font fitFont(Font current, String text, double maxX, double maxY)
  {
    if(maxX <= 0 || maxY <= 0)
      throw new IllegalArgumentException();

    boolean fit = false;
    int maxXpt = convertCmToPt(maxX);
    int maxYpt = convertCmToPt(maxY);
    float faktor = .9f;

    // Kopie erstellen
    do
    {
      Rectangle2D stringBounds = getStringBounds(text, current);
      if(stringBounds.getWidth() < maxXpt && stringBounds.getHeight() < maxYpt)
      {
        if((1f - faktor) * .1f < 1e-3f)
          fit = true;
        // urspruegliche groesse
        current = current.deriveFont(current.getSize2D() / faktor);
        faktor += (1f - faktor) * .1f;
        // wieder kleiner
        current = current.deriveFont(current.getSize2D() * faktor);
      }
      else
      {
        // Schrift verkleinern
        current = current.deriveFont(current.getSize2D() * faktor);
      }
    }
    while(!fit);

    return current;
  }
}
