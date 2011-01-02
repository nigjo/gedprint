package net.sourceforge.gedprint.gui.paint;

import net.sourceforge.gedprint.ui.GedDocumentFactory;
import net.sourceforge.gedprint.ui.GedPainter;

public class DocumentFactory implements GedDocumentFactory
{

  @Override
  public String getName()
  {
    return "Nachfahrenbaum"; //$NON-NLS-1$
  }

  @Override
  public GedPainter createDocument()
  {
    return new DrawPanel();
  }
}
