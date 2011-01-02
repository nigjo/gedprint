package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;

import javax.swing.KeyStroke;

import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.core.DocumentManager;
import net.sourceforge.gedprint.ui.GedPainter;
import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.PrintManagerFactory;

public class PrintFamilyBook extends BasicAction
{
  private static final long serialVersionUID = -4539998494437698574L;

  public PrintFamilyBook()
  {
    super(Bundle.getString("PrintFamilyBook.title", PrintFamilyBook.class)); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl P")); //$NON-NLS-1$
    setEnabled(false);
    Lookup.getGlobal().addPropertyChangeListener("activePainter",
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            Object newValue = evt.getNewValue();
            setEnabled(newValue instanceof GedPainter);
          }
        });
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    PrintManagerFactory factory = Lookup.getGlobal().lookup(
        PrintManagerFactory.class);

    GedPainter document = DocumentManager.getActiveDocument();
    PrintManager manager = factory.createPrintManager();

    Record fam = document.getRecord();
    if(!(fam instanceof Family))
      return;

    manager.setTitleFamily((Family)fam);
    manager.addFamily((Family)fam, true, true);
    manager.setOwner(Lookup.getGlobal().lookup(JFrame.class));
    manager.print();
  }
}
