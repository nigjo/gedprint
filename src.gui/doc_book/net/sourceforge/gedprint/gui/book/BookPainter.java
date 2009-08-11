package net.sourceforge.gedprint.gui.book;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gui.core.GedPainter;

public class BookPainter extends GedPainter
{
  private static final long serialVersionUID = -5599769299072911707L;

  JTabbedPane tabs;

  public BookPainter()
  {
    super();
    setLayout(new GridLayout(1, 1));
    tabs = new JTabbedPane();
    add(tabs);
  }

  @Override
  public boolean isScrollable()
  {
    return false;
  }

  @Override
  public void addIndividual(Individual indi)
  {
    tabs.addTab(indi.toString(), new JPanel());
  }

  @Override
  public void addFamily(Family fam)
  {
    String title = fam.getIDCleared();
    title += " - "; //$NON-NLS-1$
    title += fam.getHusband().getClearedFullName();
    title += "+"; //$NON-NLS-1$
    title += fam.getWife().getClearedFullName();

    FamTab tab = new FamTab(fam);
    tabs.addTab(title, new JScrollPane(tab));
  }
  
  @Override
  public Record getRecord()
  {
    return null;
  }

  @Override
  public void clearAll()
  {
    if(tabs != null)
      tabs.removeAll();
  }
}
