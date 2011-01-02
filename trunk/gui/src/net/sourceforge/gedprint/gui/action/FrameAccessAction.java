package net.sourceforge.gedprint.gui.action;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import net.sourceforge.gedprint.core.lookup.Lookup;
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

  /** @deprecated use Lookup or getOwner() */
  @Deprecated
  protected GedFrame getFrame(Object source)
  {
    return Lookup.getGlobal().lookup(GedFrame.class);
  }

  protected JFrame getOwner(ActionEvent event)
  {
    Object source = event.getSource();
    if(source instanceof JMenuItem)
    {
      while(!(source instanceof JMenuBar))
      {
        while(source instanceof JMenuItem)
        {
          source = ((JMenuItem)source).getParent();
        }
        while(source instanceof JPopupMenu)
        {
          source = ((JPopupMenu)source).getInvoker();
        }
      }
    }
    if(source instanceof JFrame)
      return (JFrame)source;
    if(source instanceof JComponent)
    {
      JRootPane rootPane = ((JComponent)source).getRootPane();
      Container parent = rootPane.getParent();
      if(parent instanceof JFrame)
        return (JFrame)parent;
    }

    // bisher nicht gefunden. Fallback: Global suchen
    return Lookup.getGlobal().lookup(JFrame.class);
  }
}
