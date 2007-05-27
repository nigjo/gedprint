package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Dimension;
import java.awt.Graphics;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

/**
 * Eine neue Klasse von hof. Erstellt am 10.05.2007, 15:49:41
 * 
 * @todo Hier fehlt die Beschreibung der Klasse
 * 
 * @author hof
 */
public class WeddingElement extends Element
{

  public WeddingElement()
  {
    super();
  }

  public WeddingElement(Family fam)
  {
    this();
    setRecord(fam);
  }

  public int print(Graphics g, int left, int top, Dimension size, int res)
  {
    int[] lines = getLines(top, res, 1);
    int[] tabs = getTabs(left, size, res);

    if(lines[1] > top + size.height)
      throw new RuntimeException("page size exceeded"); //$NON-NLS-1$

    paintGrid(g, lines, tabs);

    paintText(g, res, lines, tabs);

    if(getRecord() != null)
      paintRecordData(g, res, lines, tabs, getRecord());
    
    return lines[1] - lines[0];
  }

  private void paintRecordData(Graphics g, int res, int[] lines, int[] tabs, Record record)
  {
    int fontline = convertCmToPixel(DEFAULT_LINE_HEIGHT * .8, res);
    int indent = convertCmToPixel(DEFAULT_LINE_HEIGHT * .2, res);

    String content;
    int left;
    Record sub;
    
    g.setFont(FONT_DATA);

    Record main = record.getSubRecord("MARR"); //$NON-NLS-1$
    if(main != null)
    {
      sub = main.getSubRecord(Record.RECORD_DATE);
      if(sub != null)
      {
        content = sub.getContent();
        left = center(content, g, tabs, 1);
        g.drawString(content, left, lines[0] + fontline);
      }
      sub = main.getSubRecord("PLAC"); //$NON-NLS-1$
      if(sub != null)
      {
        content = sub.getContent();
        left = tabs[TAB_PLACE] + indent;
        content = shrink(content, g, tabs[TAB_MORE] - left);
        g.drawString(content, left, lines[0] + fontline);
      }
    }
  }

  private void paintGrid(Graphics g, int[] lines, int[] tabs)
  {
    // Kasten drum herum
    g.drawRect(tabs[TAB_LEFT], lines[0], tabs[TAB_RIGHT] - tabs[TAB_LEFT], lines[1] - lines[0]);
    // Oben 'ne Dicke linie;
    // g.fillRect(tabs[0], lines[0]-1, tabs[4]-tabs[0], 2);

    g.drawLine(tabs[TAB_DATA], lines[0], tabs[TAB_DATA], lines[1]);
    g.drawLine(tabs[TAB_PLACE], lines[0], tabs[TAB_PLACE], lines[1]);
    g.drawLine(tabs[TAB_MORE], lines[0], tabs[TAB_MORE], lines[1]);
  }

  private void paintText(Graphics g, int res, int[] lines, int[] tabs)
  {
    g.setFont(FONT_TITLE);
    int fontline = getFontline(g);
    int indent = getDefaultIndent(res);
    
    String text;

    text = Messages.getString("print.data.date"); //$NON-NLS-1$
    g.drawString(text, tabs[TAB_DATA] + indent, lines[0] + fontline);
    text = Messages.getString("print.data.place"); //$NON-NLS-1$
    g.drawString(text, tabs[TAB_PLACE] + indent, lines[0] + fontline);
  }
}
