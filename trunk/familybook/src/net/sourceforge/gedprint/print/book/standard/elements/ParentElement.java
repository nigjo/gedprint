package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Dimension;
import java.awt.Graphics;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gedcom.Tag;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

public class ParentElement extends Element
{
  private final String title;

  public ParentElement(String title)
  {
    this.title = title;
  }

  public ParentElement(String title, Individual parent)
  {
    this(title);
    setRecord(parent);
  }

  private int[] getLines(int top, int res)
  {
    int lines[] = new int[7];
    for(int i = 0; i < lines.length; i++)
      lines[i] = top + convertCmToPixel(i * getDefaultLineHeight(), res);
    return lines;
  }

  public int print(Graphics g, int left, int top, Dimension size, int res)
  {
    int[] lines = getLines(top, res);
    int[] tabs = getTabs(left, size, res);

    if(lines[6] > top + size.height)
      throw new RuntimeException("element does not fit page"); //$NON-NLS-1$

    paintGrid(g, tabs, lines);

    paintText(g, res, lines, tabs);

    if(getRecord() != null)
      paintRecordData(g, res, lines, tabs, getRecord());

    return lines[6] - lines[0];
  }

  private void paintRecordData(Graphics g, int res, int[] lines, int[] tabs,
      Record record)
  {
    int fontline = getFontline(g, getFont(FONT_DATA)) + getFontline(g, getFont(FONT_TITLE_BOLD));
    int indent = getDefaultIndent(res);

    Record main, sub;
    Family childFamily = null;

    // Familie als Kind suchen
    main = record.getSubRecord("FAMC"); //$NON-NLS-1$
    if(main != null)
      childFamily = (Family) main.findID(main.getContent());

    //
    // I D
    //
    g.setFont(getFont(FONT_ID));
    String content = record.getIDCleared();
    int left = center(content, g, tabs, 0);
    g.drawString(content, left, lines[5] + fontline);
    if(childFamily != null)
    {
      content = childFamily.getIDCleared();
      left = center(content, g, tabs, 0);
      int idline = getFontline(g);
      g.drawString(content, left, lines[5] + fontline - idline);
    }

    //
    // N A M E
    //    
    g.setFont(getFont(FONT_NAME));
    paintName(g, res, (Individual) record, tabs[TAB_LEFT], tabs[TAB_SURNAME],
        tabs[TAB_MORE], lines[0] + fontline, false);

    //
    // G E B U R T
    //
    g.setFont(getFont(FONT_DATA));
    main = record.getSubRecord("BIRT"); //$NON-NLS-1$
    if(main != null)
    {
      paintDataEntry(g, main.getSubRecord("DATE"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_PLACE], lines[1] + fontline, true);

      paintDataEntry(g,
          main.getSubRecord("PLAC"), //$NON-NLS-1$
          tabs[TAB_PLACE] + 2 * indent, tabs[TAB_RIGHT], lines[1] + fontline,
          false);
    }

    //
    // T O D
    //
    main = record.getSubRecord("DEAT"); //$NON-NLS-1$
    if(main != null)
    {
      paintDataEntry(g, main.getSubRecord("DATE"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_PLACE], lines[2] + fontline, true);

      paintDataEntry(g,
          main.getSubRecord("PLAC"), //$NON-NLS-1$
          tabs[TAB_PLACE] + 2 * indent, tabs[TAB_RIGHT], lines[2] + fontline,
          false);
      paintDataEntry(g, main.getSubRecord("CAUS"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_RIGHT], lines[3] + fontline, false);
    }

    //
    // E L T E R N
    //
    if(childFamily != null)
    {
      sub = childFamily.getSubRecord("HUSB"); //$NON-NLS-1$
      if(sub != null)
      {
        String id = sub.getContent();

        g.setFont(getFont(FONT_ID));
        content = Record.clearID(id);
        left = center(content, g, tabs, 4);
        g.drawString(content, left, lines[4] + getFontline(g, getFont(FONT_ID)));

        g.setFont(getFont(FONT_DATA));
        main = childFamily.findID(id);
        if(main != null)
        {
          paintName(g, res, (Individual) main, tabs[TAB_DATA],
              tabs[TAB_SURNAME], tabs[TAB_RIGHT], lines[4] + fontline, false);
        }
      }
      sub = childFamily.getSubRecord("WIFE"); //$NON-NLS-1$
      if(sub != null)
      {
        String id = sub.getContent();
        g.setFont(getFont(FONT_ID));
        content = Record.clearID(id);
        left = center(content, g, tabs, 4);
        g.drawString(content, left, lines[5] + getFontline(g, getFont(FONT_ID)));

        g.setFont(getFont(FONT_DATA));
        main = childFamily.findID(id);
        if(main != null)
        {
          paintName(g, res, (Individual) main, tabs[TAB_DATA],
              tabs[TAB_SURNAME], tabs[TAB_RIGHT], lines[5] + fontline, false);
        }
      }
    }

    //
    // weitere Ehen
    //
    if(record.getSubRecordCount(Tag.FAMILY_AS_SPOUSE) > 1)
    {
      g.setFont(getFont(FONT_TITLE));
      fontline = getFontline(g);
      String more = Messages.getString("print.data.further_marriages"); //$NON-NLS-1$
      setChecked(g, indent, more, tabs[TAB_RIGHT] - indent, lines[0], fontline);
    }
  }

  private void paintText(Graphics g, int res, int[] lines, int[] tabs)
  {
    int fontline = getFontline(g, getFont(FONT_TITLE_BOLD));
    int indent = getDefaultIndent(res);

    g.setFont(getFont(FONT_TITLE_BOLD));
    g.drawString(title, tabs[TAB_LEFT] + indent, lines[0] + fontline);

    g.setFont(getFont(FONT_TITLE));
    String text = Messages.getString("print.data.given_names"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_LEFT] + indent, lines[0] + fontline, fontline);
    text = Messages.getString("print.data.sure_names.short");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_SURNAME] + indent, lines[0], fontline);

    fontline = getFontline(g);
    text = Messages.getString("print.data.date_of_birth");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[1], fontline);
    text = Messages.getString("print.data.date_of_death"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[2], fontline);
    text = Messages.getString("print.data.place"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_PLACE] + indent, lines[1], fontline);
    drawString(g, text, tabs[TAB_PLACE] + indent, lines[2], fontline);
    text = Messages.getString("print.data.cause_of_death"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[3], fontline);

    text = Messages.getString("print.data.Father"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[4], fontline);
    text = Messages.getString("print.data.Mother"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[5], fontline);

    text = Messages.getString("print.data.given_names"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[4] + fontline, fontline);
    drawString(g, text, tabs[TAB_DATA] + indent, lines[5] + fontline, fontline);
    text = Messages.getString("print.data.sure_names");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_SURNAME] + indent, lines[4], fontline);
    drawString(g, text, tabs[TAB_SURNAME] + indent, lines[5], fontline);

    String more = Messages.getString("print.data.further_marriages"); //$NON-NLS-1$
    drawCheckboxString(g, more, tabs[TAB_RIGHT] - indent, lines[0], fontline,
        indent);
  }

  private void paintGrid(Graphics g, int[] tabs, int[] lines)
  {
    // Kasten drum herum
    g.drawRect(tabs[TAB_LEFT], lines[0], tabs[TAB_RIGHT] - tabs[TAB_LEFT],
        lines[6] - lines[0]);
    // Oben 'ne Dicke linie;
    // g.drawLine(tabs[TAB_LEFT], lines[0]-1, tabs[TAB_RIGHT], lines[0]-1);
    g.fillRect(tabs[TAB_LEFT], lines[0] - 1, tabs[TAB_RIGHT] - tabs[TAB_LEFT],
        2);

    // int lineheight = convertCmToPixel(1.25, res);
    g.drawLine(tabs[TAB_LEFT], lines[1], tabs[TAB_RIGHT], lines[1]);
    for(int i = 2; i < 6; i++)
      g.drawLine(tabs[TAB_DATA], lines[i], tabs[TAB_RIGHT], lines[i]);

    g.drawLine(tabs[TAB_DATA], lines[1], tabs[TAB_DATA], lines[6]);
    g.drawLine(tabs[TAB_PLACE], lines[1], tabs[TAB_PLACE], lines[3]);
    g.drawLine(tabs[TAB_SURNAME], lines[0], tabs[TAB_SURNAME], lines[1]);
    g.drawLine(tabs[TAB_SURNAME], lines[4], tabs[TAB_SURNAME], lines[6]);
  }

}
