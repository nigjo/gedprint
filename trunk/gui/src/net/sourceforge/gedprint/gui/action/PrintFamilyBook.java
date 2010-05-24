package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.KeyStroke;

import net.sourceforge.gedprint.core.Lookup;
import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.core.DocumentManager;
import net.sourceforge.gedprint.gui.core.GedPainter;
import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.PrintManagerFactory;

public class PrintFamilyBook extends FrameAccessAction
{
  private static final long serialVersionUID = -4539998494437698574L;

  public PrintFamilyBook()
  {
    super(Messages.getString("PrintFamilyBook.title")); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl P")); //$NON-NLS-1$
    setEnabled(false);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    String name = evt.getPropertyName();
    if(name.equals(PROPERTY_FILE))
    {
      setEnabled(evt.getNewValue() != null);
    }
    else
    {
      super.propertyChange(evt);
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    PrintManagerFactory factory = Lookup.getGlobal().lookup(PrintManagerFactory.class);
    
    GedPainter document = DocumentManager.getActiveDocument();
    PrintManager manager = factory.createPrintManager();

    Record fam = document.getRecord();
    if(!(fam instanceof Family))
      return;
    
    manager.setTitleFamily((Family) fam);
    manager.addFamily((Family) fam, true, true);
    manager.print();
  }
}
