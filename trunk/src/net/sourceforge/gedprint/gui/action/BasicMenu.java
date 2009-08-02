package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;

public abstract class BasicMenu extends BasicAction
{
  private static final long serialVersionUID = 1603983201620045045L;

  public BasicMenu(String title)
  {
    super(title);
  }

  public void actionPerformed(ActionEvent e)
  {
    // nothing to do in an menu
  }

}
