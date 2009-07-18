package net.sourceforge.gedprint.gui.action;

import java.beans.PropertyChangeEvent;

import net.sourceforge.gedprint.core.Messages;

public class FileMenuAction extends BasicAction
{
  private static final long serialVersionUID = 8075406551995372107L;

  public FileMenuAction()
  {
    super(Messages.getString("FileMenuAction.title")); //$NON-NLS-1$
    
  }
  
  public void propertyChange(PropertyChangeEvent evt)
  {
    // nothing to do. FileMenu is always active.
  }
}
