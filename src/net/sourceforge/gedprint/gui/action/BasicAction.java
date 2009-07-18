package net.sourceforge.gedprint.gui.action;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import net.sourceforge.gedprint.gui.core.ActionManager;
import net.sourceforge.gedprint.gui.core.GedFrame;

/**
 * Grundklasse fuer alle Actions in diesem Paket.
 * 
 * @author nigjo
 */
public class BasicAction extends AbstractAction implements
    PropertyChangeListener
{
  private static final long serialVersionUID = 51080980824162277L;

  protected BasicAction(GedFrame owner)
  {
    super();
    checkValidCreation();
  }

  protected BasicAction(String name, Icon icon)
  {
    super(name, icon);
    findMnemonic(name);
    checkValidCreation();
  }

  public BasicAction(String name)
  {
    super(name);
    findMnemonic(name);
    checkValidCreation();
  }

  private void checkValidCreation()
  {
    StackTraceElement[] trace = new Throwable().getStackTrace();
    String searchedClass = ActionManager.class.getName();
    for(StackTraceElement element : trace)
    {
      if(searchedClass.equals(element.getClassName()))
      {
        if(element.getMethodName().equals("get")) //$NON-NLS-1$
          return;
        break;
      }
    }
    throw new IllegalStateException("action not created by ActionManager"); //$NON-NLS-1$
  }

  private void findMnemonic(String name)
  {
    int wo = name.indexOf('&');
    // wenn das & nicht vorhanden oder das letzte Zeichen ist,
    // dann passiert hier nichts.
    if(wo < 0 || wo + 1 == name.length())
      return;
    int mnemonic = Character.toUpperCase(name.charAt(wo + 1));
    putValue(MNEMONIC_KEY, mnemonic);
    if(wo == 0)
      name = name.substring(1);
    else
      name = name.substring(0, wo) + name.substring(wo + 1);
    putValue(NAME, name);
  }

  public void actionPerformed(ActionEvent e)
  {
  }

  public void propertyChange(PropertyChangeEvent evt)
  {
    Logger.getLogger(getClass().getName()).fine(evt.getPropertyName());
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
