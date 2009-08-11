package net.sourceforge.gedprint.print;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Leere Seite ohne Inhalt.
 */
public class FillPage extends BasicPage
{
  @Override
  protected void paintPageHeader(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    // kein Inhalt
  }

  @Override
  protected void paintPageContent(Graphics g, int res, int left, int top,
      Dimension printSize)
  {
    // kein Inhalt
  }

  @Override
  protected void paintPageFooter(Graphics g, int left, int top, Dimension size,
      Dimension printSize, int res)
  {
    // kein Inhalt
  }
}
