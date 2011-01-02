package net.sourceforge.gedprint.gui.action;

import net.sourceforge.gedprint.gedcom.Record;

public class SearchIndividual extends DialogAction<SearchIndividualPanel>
{
  private static final long serialVersionUID = 4316704368424658584L;

  @Override
  public SearchIndividualPanel getContentPane()
  {
    return new SearchIndividualPanel();
  }

  @Override
  protected void load(SearchIndividualPanel content)
  {
    //nothing special
  }

  @Override
  protected void store(SearchIndividualPanel content)
  {
    Record record = content.getSelectedRecord();
  }
}
