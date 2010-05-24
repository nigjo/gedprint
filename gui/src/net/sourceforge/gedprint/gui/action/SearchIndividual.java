package net.sourceforge.gedprint.gui.action;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SearchIndividual extends DialogAction
{
  private static final long serialVersionUID = 4316704368424658584L;

  @Override
  public JComponent getContentPane()
  {
    return new JLabel("Hallo Welt");
  }

  @Override
  protected void load()
  {
  }

  @Override
  protected void store()
  {
  }
}
