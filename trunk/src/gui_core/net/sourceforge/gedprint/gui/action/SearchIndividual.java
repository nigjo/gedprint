package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class SearchIndividual extends BasicAction
{
  private static final long serialVersionUID = 4316704368424658584L;

  public void actionPerformed(ActionEvent e)
  {
    ActionDialog dlg = new ActionDialog(this);
    
    dlg.setVisible(true);
    Action selected = dlg.getSelectedAction();
  }

}
