package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.KeyStroke;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.core.GedFrame;
import net.sourceforge.gedprint.print.book.standard.FamilyBook;

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

  @Override
  public void actionPerformed(ActionEvent e)
  {
    GedFrame frame = getFrame(e);
    FamilyBook book = new FamilyBook(frame);

    Record fam = frame.getRecord();
    if(!(fam instanceof Family))
      return;
    book.setTitleFamily((Family) fam);
    book.addFamily((Family) fam, true, true);
    book.print();
  }
}
