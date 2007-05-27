package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Font;
import java.awt.Graphics;
import java.text.DateFormat;

import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.print.BasicElement;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

abstract public class Element extends BasicElement
{
  public static final double DEFAULT_LINE_HEIGHT = 1.;

  private static final String FONT_FAMILY = "SansSerif"; //$NON-NLS-1$

  /** Schriftart für normale Dateneinträge. */
  public static final Font FONT_DATA = new Font(FONT_FAMILY, Font.PLAIN,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .40));

  /** Schriftart für die Namenseintäge der Indididuen. */
  public static final Font FONT_NAME = new Font(FONT_FAMILY, Font.BOLD,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .55));

  /** Schriftart für Überschriften. */
  public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .45));

  /** Schriftart für Überschriften in den Datenzellen. */
  public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.PLAIN,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .25));

  /** Schriftart für dickere Überschriften in den Datenzellen. */
  public static final Font FONT_TITLE_BOLD = new Font(FONT_FAMILY, Font.BOLD,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .35));

  /** Schriftart für ID Einträge. */
  public static final Font FONT_ID = new Font(FONT_FAMILY, Font.PLAIN,
      convertCmToPt(DEFAULT_LINE_HEIGHT * .23));

  public static final int TAB_LEFT = 0;
  public static final int TAB_DATA = 1;
  public static final int TAB_PLACE = 2;
  public static final int TAB_SURNAME = 3;
  public static final int TAB_MORE = 4;
  public static final int TAB_RIGHT = 5;

  @Override
  protected double getDefaultLineHeight()
  {
    return DEFAULT_LINE_HEIGHT;
  }

  protected int getDefaultIndent(int res)
  {
    return convertCmToPixel(DEFAULT_LINE_HEIGHT * .15, res);
  }

  protected int getGivnIndent(int res)
  {
    String[] testStrings = new String[] {
        Messages.getString("print.data.given_names"), //$NON-NLS-1$
        Messages.getString("print.data.Father"), //$NON-NLS-1$
        Messages.getString("print.data.Mother"), //$NON-NLS-1$
        Messages.getString("print.data.spouse") //$NON-NLS-1$
    };

    return getMaxWidth(res, testStrings, FONT_TITLE);
  }

  protected int getSurnIndent(int res)
  {
    String[] testStrings = new String[] { Messages
        .getString("print.data.sure_names.short") //$NON-NLS-1$
    };

    return getMaxWidth(res, testStrings, FONT_TITLE);
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
   * @param g
   *          aktueller Grafikkontext
   * @param sub
   *          Record, dessen Inhalt geschrieben werden soll
   * @param leftBorder
   *          Linker Rand der Zelle
   * @param rightBorder
   *          Rechter Rand der Zelle
   * @param fontBaseline
   *          Baseline des Textes
   * @param centered
   *          gibt an, ob der Text in der Zelle zentriert werden soll.
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
}
