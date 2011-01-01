package net.sourceforge.gedprint.gui.action;

import java.beans.PropertyChangeEvent;

import net.sourceforge.gedprint.core.Bundle;

public class FileMenuAction extends BasicMenu
{
  private static final long serialVersionUID = 8075406551995372107L;

  public FileMenuAction()
  {
    super(Bundle.getString("FileMenuAction.title", FileMenuAction.class)); //$NON-NLS-1$
    
  }
  
  public void propertyChange(PropertyChangeEvent evt)
  {
    // nothing to do. FileMenu is always active.
  }
}
