package net.sourceforge.gedprint.print.book.standard;

import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.PrintManagerFactory;

public class PrinterFactory implements PrintManagerFactory
{

  @Override
  public PrintManager createPrintManager()
  {
    return new FamilyBook();
  }

}
