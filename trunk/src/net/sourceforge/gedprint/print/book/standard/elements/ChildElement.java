package net.sourceforge.gedprint.print.book.standard.elements;

import java.awt.Dimension;
import java.awt.Graphics;
import java.text.MessageFormat;

import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

public class ChildElement extends Element
{
  private static final int UNKNOWN = 0;
  private static final int MALE = 1;
  private static final int FEMALE = 2;

  public ChildElement()
  {
    super();
  }

  public ChildElement(Individual child)
  {
    this();
    setRecord(child);
  }

  private int[] getLines(int top, int res)
  {
    int lines[] = new int[6];
    for(int i = 0; i < lines.length; i++)
      lines[i] = top + convertCmToPixel(i * DEFAULT_LINE_HEIGHT, res);
    return lines;
  }

  public int print(Graphics g, int left, int top, Dimension size, int res)
  {
    int[] lines = getLines(top, res);
    int[] tabs = getTabs(left, size, res);

    if(lines[5] > top + size.height)
      throw new RuntimeException("page size exceeded"); //$NON-NLS-1$

    paintGrid(g, tabs, lines);

    paintText(g, res, lines, tabs);

    if(getRecord() != null)
      paintRecordData(g, res, lines, tabs, getRecord());

    return lines[5] - lines[0];
  }

  private void paintRecordData(Graphics g, int res, int[] lines, int[] tabs,
      Record record)
  {
    int fontline = convertCmToPixel(DEFAULT_LINE_HEIGHT * .8, res);
    int indent = convertCmToPixel(DEFAULT_LINE_HEIGHT * .125, res);

    Record main, sub;

    //
    // I D
    //
    g.setFont(FONT_ID);
    String content = record.getIDCleared();
    int left = center(content, g, tabs, 0);
    g.drawString(content, left, lines[5] - 2 * indent);

    //
    // N A M E
    //    
    g.setFont(FONT_NAME);
    paintName(g, res, (Individual) record, tabs[TAB_LEFT], tabs[TAB_SURNAME],
        tabs[TAB_MORE], lines[0] + fontline, false);

    //
    // G E S C H L E C H T
    //
    int gender = UNKNOWN;
    g.setFont(FONT_DATA);
    sub = record.getSubRecord("SEX"); //$NON-NLS-1$
    if(sub != null)
    {
      content = sub.getContent();
      if("M".equals(content.toUpperCase())) //$NON-NLS-1$
      {
        gender = MALE;
        content = Messages.getString("print.data.male"); //$NON-NLS-1$
      }
      else
      {
        gender = FEMALE;
        content = Messages.getString("print.data.female"); //$NON-NLS-1$
      }
      left = center(content, g, tabs, 0);
      g.drawString(content, left, lines[1] + fontline);
    }

    //
    // G E B U R T
    //
    g.setFont(FONT_DATA);
    main = record.getSubRecord("BIRT"); //$NON-NLS-1$
    if(main != null)
    {
      paintDataEntry(g, main.getSubRecord("DATE"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_PLACE], lines[1] + fontline, true);

      paintDataEntry(g,
          main.getSubRecord("PLAC"), tabs[TAB_PLACE] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_RIGHT], lines[1] + fontline, false);
    }

    //
    // T O D
    //
    g.setFont(FONT_DATA);
    main = record.getSubRecord("DEAT"); //$NON-NLS-1$
    if(main != null)
    {
      paintDataEntry(g, main.getSubRecord("DATE"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_PLACE], lines[2] + fontline, true);

      paintDataEntry(g,
          main.getSubRecord("PLAC"), tabs[TAB_PLACE] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_RIGHT], lines[2] + fontline, false);

      paintDataEntry(g, main.getSubRecord("CAUS"), tabs[TAB_DATA] + 2 * indent, //$NON-NLS-1$
          tabs[TAB_RIGHT], lines[3] + fontline, false);
    }

    //
    // E H E P A R T N E R
    //
    if(gender != UNKNOWN)
    {
      main = record.getSubRecord("FAMS"); //$NON-NLS-1$
      if(main != null)
      {
        main = main.findID(main.getContent());
        if(main != null)
        {
          if(gender == MALE)
            sub = main.getSubRecord("WIFE"); //$NON-NLS-1$
          else
            sub = main.getSubRecord("HUSB"); //$NON-NLS-1$
          if(sub != null)
          {
            Record fam = main;
            String id = sub.getContent();

            g.setFont(FONT_ID);
            content = "{0} / {1}"; //$NON-NLS-1$
            Object[] args = { main.getIDCleared(), Record.clearID(id) };
            content = MessageFormat.format(content, args);
            left = center(content, g, tabs, TAB_MORE);
            g.drawString(content, left, lines[4] + convertCmToPixel(.3, res));

            g.setFont(FONT_DATA);
            paintName(g, res, (Individual) fam.findID(id), tabs[TAB_DATA],
                tabs[TAB_SURNAME], tabs[TAB_RIGHT], lines[4] + fontline, false);

            main = fam;
          }
        }
      }
    }
  }

  private void paintText(Graphics g, int res, int[] lines, int[] tabs)
  {
    // int fontline = convertCmToPixel(DEFAULT_LINE_HEIGHT * .36, res);
    int indent = convertCmToPixel(DEFAULT_LINE_HEIGHT * .15, res);

    g.setFont(FONT_TITLE);
    int fontline = getFontline(g);

    String text = Messages.getString("print.data.given_names.short"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_LEFT] + indent, lines[0], fontline);
    text = Messages.getString("print.data.sure_names.short");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_SURNAME] + indent, lines[0], fontline);

    text = Messages.getString("print.data.date_of_birth");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[1], fontline);
    text = Messages.getString("print.data.place"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_PLACE] + indent, lines[1], fontline);
    text = Messages.getString("print.data.date_of_death"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[2], fontline);
    text = Messages.getString("print.data.place"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_PLACE] + indent, lines[2], fontline);
    text = Messages.getString("print.data.cause_of_death"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[3], fontline);

    text = Messages.getString("print.data.spouse"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[4], fontline);
    text = Messages.getString("print.data.given_names"); //$NON-NLS-1$
    drawString(g, text, tabs[TAB_DATA] + indent, lines[4] + fontline, fontline);
    text = Messages.getString("print.data.sure_names");//$NON-NLS-1$
    drawString(g, text, tabs[TAB_SURNAME] + indent, lines[4], fontline);

    text = Messages.getString("print.data.sex"); //$NON-NLS-1$
    drawString(g, text, center(text, g, tabs, 0), lines[1], fontline);
  }

  private void paintGrid(Graphics g, int[] tabs, int[] lines)
  {
    // Kasten drum herum
    g.drawRect(tabs[TAB_LEFT], lines[0], tabs[TAB_RIGHT] - tabs[TAB_LEFT],
        lines[5] - lines[0]);
    // Oben 'ne Dicke linie;
    g.fillRect(tabs[TAB_LEFT], lines[0] - 1, tabs[TAB_RIGHT] - tabs[TAB_LEFT],
        2);

    g.drawLine(tabs[TAB_LEFT], lines[1], tabs[TAB_RIGHT], lines[1]);
    for(int i = 2; i < 6; i++)
      g.drawLine(tabs[TAB_DATA], lines[i], tabs[TAB_RIGHT], lines[i]);

    g.drawLine(tabs[TAB_DATA], lines[1], tabs[TAB_DATA], lines[5]);
    g.drawLine(tabs[TAB_PLACE], lines[1], tabs[TAB_PLACE], lines[3]);
    g.drawLine(tabs[TAB_SURNAME], lines[0], tabs[TAB_SURNAME], lines[1]);
    g.drawLine(tabs[TAB_SURNAME], lines[4], tabs[TAB_SURNAME], lines[5]);
  }
}
