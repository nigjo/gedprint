package net.sourceforge.gedprint.print.book.standard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.print.BasicElement;
import net.sourceforge.gedprint.print.BasicPage;
import net.sourceforge.gedprint.print.book.standard.elements.Element;
import net.sourceforge.gedprint.print.book.standard.elements.ParentElement;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

public class Page extends BasicPage
{
  public static final String DEFAULT_FONT = "SansSerif"; //$NON-NLS-1$

  public Page()
  {
    super();
  }

  public Page(Family fam)
  {
    this();
    setFamily(fam);
  }

  protected void paintPageContent(Graphics g, int res, int left, int top,
      Dimension printSize)
  {
    if(getElements() == null)
      return;
    Enumeration<BasicElement> list = getElements();
    while(list.hasMoreElements())
    {
      BasicElement element = list.nextElement();

      int height = element.print(g, left, top, printSize, res);
      top += height;
    }
  }

  protected void paintPageHeader(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    Font titleFont = new Font(DEFAULT_FONT, Font.BOLD, Element
        .convertCmToPt(.75));
    g.setFont(titleFont);
    String title = spread(Messages.getString("print.page.header_title")); //$NON-NLS-1$
    int textPos = left + Element.center(title, g, printSize.width);
    int baseline = Element.convertCmToPixel(top * 2 / 3, res);
    g.drawString(title, textPos, baseline);

    Font countFont = new Font(DEFAULT_FONT, Font.PLAIN, Element
        .convertCmToPt(.25));
    g.setFont(countFont);
    title = Messages.getString("print.page.sheet_number"); //$NON-NLS-1$
    if(getFamily() != null)
    {
      title = MessageFormat.format(title, new Object[] { getFamily()
          .getIDCleared() });
    }
    else
    {
      title = MessageFormat.format(title, new Object[] { "______" }); //$NON-NLS-1$
    }

    boolean isOdd = getPageNumber() % 2 != 0;
    if(isOdd)
      textPos = left + Element.alignRight(title, g, printSize.width);
    else
      textPos = left;
    g.drawString(title, textPos, baseline);
  }

  protected void paintPageFooter(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    Font countFont = new Font(DEFAULT_FONT, Font.PLAIN, Element
        .convertCmToPt(.5));
    g.setFont(countFont);

    String text = Messages.getString("print.page.page_number"); //$NON-NLS-1$
    text = MessageFormat.format(text, new Object[] { new Integer(
        getPageNumber()) });

    int textPos = left + Element.center(text, g, printSize.width);

    int footerHeight = size.height - top - printSize.height;

    int baseline = top + printSize.height + footerHeight * 1 / 3;
    g.drawString(text, textPos, baseline);

    g.setFont(Element.getFont(Element.FONT_ID, 1.));
    String title = DateFormat.getDateInstance(DateFormat.MEDIUM).format(
        new Date());
    boolean isOdd = getPageNumber() % 2 != 0;
    if(isOdd)
      textPos = left;
    else
      textPos = left + Element.alignRight(title, g, printSize.width);
    g.drawString(title, textPos, baseline);
  }

  public void createParent(String title)
  {
    add(new ParentElement(title));
  }
}
