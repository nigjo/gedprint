package net.sourceforge.gedprint.gui.action;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.gui.core.ActionManager;
import net.sourceforge.gedprint.ui.GedPainter;

/**
 * Grundklasse fuer alle Actions in diesem Paket.
 * 
 * @author nigjo
 */
public abstract class BasicAction extends AbstractAction
{
  public static final String ACTION_LOOKUP = "action.lookup"; //$NON-NLS-1$
  private static final long serialVersionUID = 51080980824162277L;

  protected BasicAction()
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

  protected synchronized Lookup getLookup()
  {
    Lookup lookup = (Lookup)getValue(ACTION_LOOKUP);
    if(lookup == null)
    {
      lookup = Lookup.create(this);
      putValue(ACTION_LOOKUP, lookup);
    }
    return lookup;
  }

  protected <L> L lookup(Class<L> type)
  {
    Lookup lookup = getLookup();
    return lookup.lookup(type);
  }

  public void addValue(Object o)
  {
    Lookup lookup = getLookup();
    lookup.put(o);
  }

  protected void removeValue(Object o)
  {
    Lookup lookup = getLookup();
    lookup.remove(o);
  }

  public void setProperty(String key, Object data)
  {
    getLookup().setProperty(key, data);
  }

  protected GedPainter getPainter()
  {
    GedPainter painter = lookup(GedPainter.class);
    if(painter != null)
      return painter;

    painter = (GedPainter)getLookup().getProperty("GedPainter"); //$NON-NLS-1$
    if(painter != null)
      return painter;

    return null;
  }
}
