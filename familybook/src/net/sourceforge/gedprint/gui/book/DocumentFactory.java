package net.sourceforge.gedprint.gui.book;

import net.sourceforge.gedprint.ui.GedDocumentFactory;
import net.sourceforge.gedprint.ui.GedPainter;

public class DocumentFactory implements GedDocumentFactory
{

  @Override
  public GedPainter createDocument()
  {
    return new BookPainter();
  }

  @Override
  public String getName()
  {
    return "Familienbuch"; //$NON-NLS-1$
  }
  
}
