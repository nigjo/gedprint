package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gui.GedFrame;

public class Exit extends BasicAction
{
  private static final long serialVersionUID = 3133439925045511079L;

  public Exit()
  {
    super(Messages.getString("exit")); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F4")); //$NON-NLS-1$
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    GedFrame frame = getFrame(e);
    if(frame == null)
      System.exit(1);
    else
      frame.close();
  }
}
