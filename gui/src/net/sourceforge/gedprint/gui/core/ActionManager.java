package net.sourceforge.gedprint.gui.core;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.sourceforge.gedprint.core.lookup.Lookup;

import net.sourceforge.gedprint.gui.action.BasicAction;

public class ActionManager
{
  private static final String ACTION_PACKAGE;
  static
  {
    ACTION_PACKAGE = BasicAction.class.getPackage().getName();
  }

  private static ActionManager globalManager;
  private Lookup actionLookup;

  private static synchronized Lookup getLookup()
  {
    return getManager().actionLookup;
  }

  Map<String, BasicAction> cache;
//  PropertyChangeSupport pcSupport;

//  private final Object propMutex = new Object();
//  private HashMap<String, Object> properties;

  private ActionManager()
  {
    checkValidCreation();
    actionLookup = Lookup.create(this);
//    pcSupport = new PropertyChangeSupport(this);
  }

  private void checkValidCreation()
  {
    StackTraceElement[] trace = new Throwable().getStackTrace();
    String searchedClass = ActionManager.class.getName();
    // Das dritte Element in der Liste muss "getManager" aus dieser Klasse sein
    if(trace.length < 3)
      throw new IllegalStateException("ActionManager not created by itself"); //$NON-NLS-1$
    if(!searchedClass.equals(trace[2].getClassName()))
      throw new IllegalStateException("ActionManager not created by itself"); //$NON-NLS-1$
    if(!trace[2].getMethodName().equals("getManager")) //$NON-NLS-1$
      throw new IllegalStateException("ActionManager not created by itself"); //$NON-NLS-1$
  }

  public static BasicAction getAction(String actionName)
  {
    ActionManager manager = getManager();
    return manager.get(actionName);
  }

  private static ActionManager getManager()
  {
    if(globalManager == null)
      globalManager = new ActionManager();
    return globalManager;
  }

  private BasicAction get(String actionName)
  {
    BasicAction action;

    if(cache == null)
      cache = new HashMap<String, BasicAction>();

    action = cache.get(actionName);
    if(action != null)
      return action;

    try
    {
      Class<?> actionClass = Class.forName(ACTION_PACKAGE + '.' + actionName);
      action = (BasicAction) actionClass.newInstance();
    }
    catch(ClassNotFoundException e)
    {
      action = new AbstractBasicAction(actionName);
    }
    catch(Exception e)
    {
      throw new IllegalStateException(e);
    }

    cache.put(actionName, action);

    return action;
  }

  /**
   * fuehrt eine Action aus.
   * 
   * @param actionClass
   * @param data
   */
  public static void performAction(Class<? extends BasicAction> actionClass,
     String key, Object data)
  {
    String actionName = actionClass.getSimpleName();
    BasicAction action = getAction(actionName);
    if(!actionClass.isInstance(action))
      throw new IllegalStateException("action name already used."); //$NON-NLS-1$
    perform(action, key, data);
  }

  public static void performAction(String actionName)
  {
    performAction(actionName, null, null);
  }

  public static void performAction(String actionName, String key, Object data)
  {
    BasicAction action = getAction(actionName);
    perform(action, key, data);
  }

  private static void perform(BasicAction action, String key, Object data)
  {
    ActionEvent evt = new ActionEvent(getManager(),
        ActionEvent.ACTION_PERFORMED, (String) action
            .getValue(BasicAction.ACTION_COMMAND_KEY));
    // Daten uebergeben
    action.setProperty(key, data);
    // Aktion ausfuehren
    action.actionPerformed(evt);
    // Daten wieder loeschen
    //action.putValue(BasicAction.ACTION_DATA, null);
  }
  
  private static class AbstractBasicAction extends BasicAction
  {
    private static final long serialVersionUID = -6203670363521467316L;

    public AbstractBasicAction(String actionName)
    {
      super(actionName);
      setEnabled(false);
      Logger.getLogger(getClass().getName()).fine(actionName);
    }

    public void actionPerformed(ActionEvent e)
    {
      // nothing to do in this action (always disabled)
    }

  }
}
