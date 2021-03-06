package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.KeyStroke;

import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.gui.core.GedFrame;

public class Exit extends FrameAccessAction
{
  private static final long serialVersionUID = 3133439925045511079L;

  public Exit()
  {
    super(Bundle.getString("Exit.title", Exit.class)); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F4")); //$NON-NLS-1$
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    GedFrame frame = Lookup.getGlobal().lookup(GedFrame.class);
    if(frame == null)
      System.exit(1);
    else
      frame.close();
  }
  
  public void propertyChange(PropertyChangeEvent evt)
  {
    // nothing to do. Exit is always active.
  }
}
