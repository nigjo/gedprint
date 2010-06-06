package net.sourceforge.gedprint.core.lookup;

import java.util.EventListener;

public interface LookupListener extends EventListener
{
  public void lookupChanged(LookupEvent e);
}
