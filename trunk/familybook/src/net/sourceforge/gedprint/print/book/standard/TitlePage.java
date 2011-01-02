package net.sourceforge.gedprint.print.book.standard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.book.standard.elements.Element;
import net.sourceforge.gedprint.core.Bundle;

public class TitlePage extends Page
{
  private String[] subTitles;
  private String title;
  private Font titleFont;

  public TitlePage()
  {
    super();
    setTitle(Bundle.getString("print.page.header_title", TitlePage.class)); //$NON-NLS-1$
  }

  @Override
  public void setFamily(Family family)
  {
    super.setFamily(family);

    setTitle(Bundle.getString("print.page.title", getClass())); //$NON-NLS-1$

    Object[] arguments = { family.getHusband().getClearedFullName(),
        family.getWife().getClearedFullName() };

    String subtitle = Bundle.getString("print.page.couple", getClass()); //$NON-NLS-1$
    subtitle = MessageFormat.format(subtitle, arguments);

    String[] lines = subtitle.split("\r\n|\n|\r"); //$NON-NLS-1$
    for(int i = 0; i < lines.length; i++)
      addSubTitle(lines[i]);
  }

  @Override
  protected void paintPageContent(Graphics g, int res, int left, int top,
      Dimension printSize)
  {
    String fontFamily = DEFAULT_FONT;

    int width = printSize.width;
    int height = printSize.height;

    double cmX = Element.convertPixelToCm(width, res);
    double cmY = Element.convertPixelToCm(height, res);

    int pt = Element.convertCmToPt(cmY / 8);
    Font font = getTitleFont();
    font = font.deriveFont(Font.BOLD, pt);

    int x;
    int fontline = Element.convertCmToPt(cmY / 8);

    String text = getTitle();
    font = Element.fitFont(font, text, cmX, cmY / 8);
    g.setFont(font);
    x = left + Element.center(text, g, width);
    g.drawString(text, x, top + 1 * fontline);

    pt = font.getSize() / 2;
    if(pt > fontline / 3)
      pt = fontline / 3;
    Font subtitle = font.deriveFont(Font.PLAIN, pt);
    pt /= 3;
    Font comment = new Font(fontFamily, Font.PLAIN, pt);

    String[] titles = getSubTitles();
    if(titles != null && titles.length > 0)
    {
      int maxlen = 0;
      int maxindex = -1;
      for(int i = 0; i < titles.length; i++)
      {
        if(titles[i].length() > maxlen)
        {
          maxlen = titles[i].length();
          maxindex = i;
        }
      }
      g.setFont(Element.fitFont(subtitle, titles[maxindex], cmX, cmY / 16));
      int center = titles.length / 2;
      int firstline = top + (int) ((4.0 - center * .5) * fontline);
      for(int i = 0; i < titles.length; i++)
      {
        x = left + Element.center(titles[i], g, width);
        g.drawString(titles[i], x, firstline + (i * (fontline / 2)));
      }
    }

    String pattern = Bundle.getString("print.page.state", getClass()); //$NON-NLS-1$
    Object[] arguments = new Object[] { new Date() };

    text = MessageFormat.format(pattern, arguments);
    g.setFont(Element.fitFont(comment, text, cmX, cmY / 32));
    x = left + Element.center(text, g, width);
    g.drawString(text, x, top + (int) (7.5 * fontline));
  }

  private Font getTitleFont()
  {
    if(titleFont == null)
    {
      Class<? extends TitlePage> pageClass = getClass();
      //InputStream stream = pageClass.getResourceAsStream("fonts/oldengl.ttf"); //$NON-NLS-1$
      InputStream stream = pageClass.getResourceAsStream("fonts/OldeEnglish.ttf"); //$NON-NLS-1$
      if(stream != null)
      {
        try
        {
          titleFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        }
        catch (FontFormatException e)
        {
          titleFont = null;
        }
        catch (IOException e)
        {
          titleFont = null;
        }
      }

      if(titleFont == null)
      {
        // create Default-Font
        titleFont = new Font(DEFAULT_FONT, Font.PLAIN, 10);
      }
    }
    return titleFont;
  }

  private String[] getSubTitles()
  {
    return subTitles;
  }

  public void addSubTitle(String subTitle)
  {
    if(subTitles == null)
      subTitles = new String[] { subTitle };
    else
    {
      String[] neu = new String[subTitles.length + 1];
      System.arraycopy(subTitles, 0, neu, 0, subTitles.length);
      neu[subTitles.length] = subTitle;
      subTitles = neu;
    }
  }

  @Override
  protected void paintPageHeader(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    // keine Kopfzeile
  }

  @Override
  protected void paintPageFooter(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    double cmX = Element.convertPixelToCm(printSize.width, res);
    int footerTop = top + printSize.height;
    int footerHeight = size.height - footerTop;
    double footerCm = Element.convertCmToPixel(footerHeight, res);
    Font f = new Font(DEFAULT_FONT, Font.PLAIN, 8);

    String text = getPoweredString();
    g.setFont(Element.fitFont(f, text, cmX, footerCm / 3.));
    int fontline = Element.getFontline(g);
    int x = left + Element.alignRight(text, g, printSize.width);
    g.drawString(text, x, footerTop + fontline);
  }

  public static String getPoweredString()
  {
    String text = Bundle.getString("print.page.powered_by", TitlePage.class); //$NON-NLS-1$
    Object[] args = { PrintManager.VERSION_STRING };
    text = MessageFormat.format(text, args);
    return text;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  private String getTitle()
  {
    return title;
  }
}
