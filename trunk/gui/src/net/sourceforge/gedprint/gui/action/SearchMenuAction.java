package net.sourceforge.gedprint.gui.action;

import net.sourceforge.gedprint.core.Bundle;

public class SearchMenuAction extends BasicMenu
{
  private static final long serialVersionUID = 4409284122252800770L;

  public SearchMenuAction()
  {
    super(Bundle.getString("SearchMenuAction.title", SearchMenuAction.class)); //$NON-NLS-1$
  }
}
