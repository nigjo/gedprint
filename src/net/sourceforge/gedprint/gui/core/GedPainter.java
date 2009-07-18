package net.sourceforge.gedprint.gui.core;

import javax.swing.JPanel;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;

public abstract class GedPainter extends JPanel
{
  private static final long serialVersionUID = 2282097895141356155L;

  public static final String PROPERTY_RECORD = "painter.record"; //$NON-NLS-1$

  public void addRecord(Record rec){
    if(rec instanceof Individual)
      addIndividual((Individual)rec);
    else if(rec instanceof Family)
      addFamily((Family)rec);

    firePropertyChange(PROPERTY_RECORD, null, rec);
  }
  
  protected abstract void addIndividual(Individual indi);

  protected abstract void addFamily(Family fam);

  public boolean isScrollable()
  {
    return true;
  }

  public abstract void clearAll();

  public abstract Record getRecord();

}
