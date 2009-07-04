package net.sourceforge.gedprint.gui;

import javax.swing.JPanel;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;

public abstract class GedPainter extends JPanel
{
  private static final long serialVersionUID = 2282097895141356155L;

  public static final String PROPERTY_RECORD = "Record"; //$NON-NLS-1$

  abstract public void add(Individual indi);

  abstract public void add(Family fam);

  public boolean isScrollable()
  {
    return true;
  }

  public abstract void clearAll();

}
