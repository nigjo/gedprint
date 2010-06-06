package net.sourceforge.gedprint.gui.action;


import javax.swing.AbstractAction;
import javax.swing.Icon;

import net.sourceforge.gedprint.gui.core.ActionManager;
import net.sourceforge.gedprint.gui.core.GedPainter;

/**
 * Grundklasse fuer alle Actions in diesem Paket.
 * 
 * @author nigjo
 */
public abstract class BasicAction extends AbstractAction
    //implements PropertyChangeListener
{
  /**
   * Die aktuelle GEDCOM Datei der Anwendung.  
   */
  @Deprecated
  public static final String PROPERTY_FILE = "gedcom.file"; //$NON-NLS-1$
  @Deprecated
  public static final String PROPERTY_RECORD = "gedcom.record"; //$NON-NLS-1$
  @Deprecated
  public static final String PROPERTY_SELECTION = "selection"; //$NON-NLS-1$
  public static final String ACTION_DATA = "action.data"; //$NON-NLS-1$
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

//  @Override
//  public void propertyChange(PropertyChangeEvent evt)
//  {
//    String pattern = "changing property {0} in {1}"; //$NON-NLS-1$
//    Object[] args = new Object[]
//    {
//      evt.getPropertyName(), getClass().getSimpleName()
//    };
//    if(getClass().equals(BasicAction.class))
//      args[1] = getValue(NAME);
//    String message = MessageFormat.format(pattern, args);
//    Logger.getLogger(getClass().getName()).fine(message);
//  }

  protected <L> L lookup(Class<L> type)
  {
    return ActionManager.getLookup().lookup(type);
  }

  protected GedPainter getPainter()
  {
    //TODO: return (GedPainter) getProperty("painter"); //$NON-NLS-1$
    return null;
  }
}
