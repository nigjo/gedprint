package net.sourceforge.gedprint.gui.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.action.BasicAction;

public abstract class GedPainter extends JPanel implements
    PropertyChangeListener
{
  private static final long serialVersionUID = 2282097895141356155L;

  public static final String PROPERTY_RECORD = "painter.record"; //$NON-NLS-1$

  public GedPainter()
  {
    ActionManager.addPropertyChangeListener(this);
  }

  public void addRecord(Record rec)
  {
    if(rec instanceof Individual)
      addIndividual((Individual) rec);
    else if(rec instanceof Family)
      addFamily((Family) rec);

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

  public void propertyChange(PropertyChangeEvent evt)
  {
    String prop = evt.getPropertyName();
    if(BasicAction.PROPERTY_FILE.equals(prop))
    {
      // Eine neue Datei loescht erstmal alles bisherige
      clearAll();
    }
  }
}
