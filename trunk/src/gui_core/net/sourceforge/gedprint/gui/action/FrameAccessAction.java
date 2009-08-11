package net.sourceforge.gedprint.gui.action;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import net.sourceforge.gedprint.gui.core.GedFrame;

public abstract class FrameAccessAction extends BasicAction
{
  private static final long serialVersionUID = -2587452212647260627L;

  public FrameAccessAction()
  {
    super();
  }

  public FrameAccessAction(String name, Icon icon)
  {
    super(name, icon);
  }

  public FrameAccessAction(String name)
  {
    super(name);
  }


  protected GedFrame getFrame(Object source)
  {
    if(source instanceof GedFrame)
      return (GedFrame) source;
    if(source instanceof ActionEvent)
      source = ((ActionEvent) source).getSource();
    if(source instanceof JMenuItem)
    {
      while(!(source instanceof JMenuBar))
      {
        while(source instanceof JMenuItem)
          source = ((JMenuItem) source).getParent();
        while(source instanceof JPopupMenu)
          source = ((JPopupMenu) source).getInvoker();
      }
    }
    if(source instanceof JComponent)
    {
      JRootPane rootPane = ((JComponent) source).getRootPane();
      Container parent = rootPane.getParent();
      if(parent instanceof GedFrame)
        return (GedFrame) parent;
    }
    // bisher nicht gefunden. Fallback: Global suchen
    Frame[] frames = GedFrame.getFrames();
    for(Frame frame : frames)
    {
      if(frame instanceof GedFrame)
        return (GedFrame) frame;
    }

    // nichts gefunden.
    return null;
  }
}
