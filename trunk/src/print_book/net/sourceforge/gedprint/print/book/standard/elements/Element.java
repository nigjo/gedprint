package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Font;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.print.BasicElement;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

abstract public class Element extends BasicElement
{
  private static final double DEFAULT_LINE_HEIGHT = 1.;

  private static final String FONT_FAMILY = "SansSerif"; //$NON-NLS-1$

  private static Hashtable<Entry<String, Double>, Font> fonts;

  public static Font getFont(Font f)
  {
    return f;
  }

  public Font getFont(String id)
  {
    return getFont(id, getDefaultLineHeight());
  }

  public static Font getFont(String id, double lineheight)
  {
    FontEntry fe = new FontEntry(id, lineheight);
    if(fonts == null)
      fonts = new Hashtable<Entry<String, Double>, Font>();
    if(fonts.containsKey(fe))
      return fonts.get(fe);

    String[] infos = id.split("="); //$NON-NLS-1$
    if(infos.length != 3)
      throw new IllegalArgumentException(id);
    try
    {
      // Parameter aus ID lesen
      int style = Integer.parseInt(infos[1]);
      double factor = Double.parseDouble(infos[2]);

      // Font erstellen und speichern
      Font f = new Font(FONT_FAMILY, style, convertCmToPt(lineheight * factor));
      fonts.put(fe, f);

      // Ergebnis
      return f;
    }
    catch(NumberFormatException e)
    {
      throw new IllegalArgumentException(id);
    }
  }
  /** Schriftart f&uuml;r normale Dateneintr&auml;ge. */
  public static final String FONT_DATA = "font.data=" + Font.PLAIN + '=' + .4; //$NON-NLS-1$

  /** Schriftart f&uuml;r die Namenseint&auml;ge der Indididuen. */
  public static final String FONT_NAME = "font.name=" + Font.BOLD + '=' + .55; //$NON-NLS-1$

  /** Schriftart f&uuml;r &uuml;berschriften. */
  public static final String FONT_HEADER = "font.header=" + Font.BOLD + '=' + .45; //$NON-NLS-1$

  /** Schriftart f&uuml;r &uuml;berschriften in den Datenzellen. */
  public static final String FONT_TITLE = "font.title=" + Font.PLAIN + '=' + .25; //$NON-NLS-1$

  /** Schriftart f&uuml;r dickere &uuml;berschriften in den Datenzellen. */
  public static final String FONT_TITLE_BOLD = "font.title.bold=" + Font.BOLD + '=' + .35; //$NON-NLS-1$

  /** Schriftart f&uuml;r ID Eintr&auml;ge. */
  public static final String FONT_ID = "font.id=" + Font.PLAIN + '=' + .23; //$NON-NLS-1$

  public static final int TAB_LEFT = 0;
  public static final int TAB_DATA = 1;
  public static final int TAB_PLACE = 2;
  public static final int TAB_SURNAME = 3;
  public static final int TAB_MORE = 4;
  public static final int TAB_RIGHT = 5;

  private double lineHeight = DEFAULT_LINE_HEIGHT;

  @Override
  protected double getDefaultLineHeight()
  {
    return lineHeight;
  }

  public void setLineHeight(double lineHeight)
  {
    this.lineHeight = lineHeight;
  }

  protected int getDefaultIndent(int res)
  {
    return convertCmToPixel(DEFAULT_LINE_HEIGHT * .15, res);
  }

  protected int getGivnIndent(int res)
  {
    String[] testStrings = new String[]
    { Messages.getString("print.data.given_names"), //$NON-NLS-1$
        Messages.getString("print.data.Father"), //$NON-NLS-1$
        Messages.getString("print.data.Mother"), //$NON-NLS-1$
        Messages.getString("print.data.spouse") //$NON-NLS-1$
    };

    return getMaxWidth(res, testStrings, getFont(FONT_TITLE));
  }

  protected int getSurnIndent(int res)
  {
    String[] testStrings = new String[]
    { Messages.getString("print.data.sure_names.short") //$NON-NLS-1$
    };

    return getMaxWidth(res, testStrings, getFont(FONT_TITLE));
  }

  /**
   * @param main
   * @param g
   * @param fontBaseline
   * 
   */
  protected void paintName(Graphics g, int res, Individual main, int tabLeft,
      int tabCenter, int tabRight, int fontBaseline, boolean centered)
  {
    String content = null;
    int left;

    Record sub = main.getSubRecord("NAME"); //$NON-NLS-1$
    String[] parts = sub.getContent().split("/"); //$NON-NLS-1$

    sub = main.getSubRecord("NAME/GIVN"); //$NON-NLS-1$
    if(sub != null)
      content = sub.getContent();
    else if(parts.length < 1 || parts[0].length() == 0)
      content = null;
    else
      content = parts[0];
    if(content != null)
    {
      left = tabLeft + getGivnIndent(res);
      content = shrink(content, g, tabCenter - left);
      if(centered)
        left += center(content, g, tabCenter - left);
      g.drawString(content, left, fontBaseline);
    }
    sub = main.getSubRecord("NAME/SURN"); //$NON-NLS-1$
    content = null;
    if(sub != null)
      content = sub.getContent();
    else if(parts.length < 2 || parts[1].length() == 0)
      content = null;
    else
      content = parts[1];
    if(content != null)
    {
      left = tabCenter + getSurnIndent(res);
      content = shrink(content, g, tabRight - left);
      if(centered)
        left += center(content, g, tabRight - left);
      g.drawString(content, left, fontBaseline);
    }
  }

  /**
   * Schreibt einen Wert in eine Zelle.
   * 
   * @param g aktueller Grafikkontext
   * @param sub Record, dessen Inhalt geschrieben werden soll
   * @param leftBorder Linker Rand der Zelle
   * @param rightBorder Rechter Rand der Zelle
   * @param fontBaseline Baseline des Textes
   * @param centered gibt an, ob der Text in der Zelle zentriert werden soll.
   */
  protected void paintDataEntry(Graphics g, Record sub, int leftBorder,
      int rightBorder, int fontBaseline, boolean centered)
  {
    if(sub != null)
    {
      String content = sub.getContent();
      if(sub.getType().equals("DATE")) //$NON-NLS-1$
      {
        DateFormat fmt = DateFormat.getDateInstance(DateFormat.LONG);
        content = fmt.format(Record.parseDate(content).getTime());
      }
      if(content != null)
      {
        content = shrink(content, g, rightBorder - leftBorder);
        if(centered)
          leftBorder += center(content, g, rightBorder - leftBorder);
        g.drawString(content, leftBorder, fontBaseline);
      }
    }
  }

  protected void drawCheckboxString(Graphics g, String more, int right,
      int top, int fontline, int indent)
  {
    int left = alignRight(more, g, right);
    g.drawString(more, left, top + fontline);

    // "Kasten" malen
    int size = (int) (fontline * .75);
    left -= indent + size;
    top += fontline - size;
    g.drawRect(left, top, size, size);
  }

  protected void setChecked(Graphics g, int indent, String more, int right,
      int top, int fontline)
  {
    int left = alignRight(more, g, right);

    int size = (int) (fontline * .75);
    left -= indent + size;
    top += fontline - size;

    g.drawLine(left, top, left + size, top + size);
    g.drawLine(left, top + size, left + size, top);
  }

  public static int center(String content, Graphics g, int[] tabs, int i)
  {
    return tabs[i] + center(content, g, tabs[i + 1] - tabs[i]);
  }

  private static class FontEntry implements Entry<String, Double>
  {
    String key;
    Double value;

    public FontEntry(String name, Double lineheight)
    {
      key = name;
      value = lineheight;
    }

    public String getKey()
    {
      return key;
    }

    public Double getValue()
    {
      return value;
    }

    public Double setValue(Double value)
    {
      Double old = this.value;
      this.value = value;
      return old;
    }

    @Override
    public int hashCode()
    {
      return (int) (key.hashCode() * value.doubleValue());
    }

    @Override
    public boolean equals(Object obj)
    {
      if(super.equals(obj))
        return true;
      if(!(obj instanceof FontEntry))
        return false;
      FontEntry other = (FontEntry) obj;
      return other.getKey().equals(this.getKey())
          && other.getValue().equals(this.getValue());
    }
  }
}
