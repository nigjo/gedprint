package net.sourceforge.gedprint.gui.core;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.Action;

import net.sourceforge.gedprint.gui.action.BasicAction;

public class ActionManager
{
  private static final String ACTION_PACKAGE;

  static
  {
    ACTION_PACKAGE = BasicAction.class.getPackage().getName();
  }
  private static ActionManager globalManager;
  Map<String, Action> cache;

  private ActionManager()
  {
    checkValidCreation();
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

  public static <A extends Action> A getAction(Class<A> actionClass)
  {
    ActionManager manager = getManager();
    return manager.get(actionClass);
  }

  public static BasicAction getBasicAction(String actionName)
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

  private <A extends Action> A get(Class<A> actionClass)
  {
    String actionName = actionClass.getName();
    Action action = findAction(actionName);
    if(action == null)
    {
      try
      {
        action = actionClass.newInstance();
        cache.put(actionName, action);
      }
      catch(Exception ex)
      {
        throw new IllegalStateException(ex);
      }
    }
    return actionClass.cast(action);
  }

  private BasicAction get(String actionName)
  {
    String actionClassName = ACTION_PACKAGE + '.' + actionName;
    BasicAction action = (BasicAction)findAction(actionClassName);
    if(action == null)
    {
      try
      {
        Class<?> actionClass = Class.forName(actionClassName);
        action = (BasicAction)actionClass.newInstance();
      }
      catch(ClassNotFoundException e)
      {
        action = new AbstractBasicAction(actionName);
      }
      catch(Exception e)
      {
        throw new IllegalStateException(e);
      }

      cache.put(actionClassName, action);
    }
    return action;
  }

  private Action findAction(String actionClassName)
  {
    if(cache == null)
      cache = new HashMap<String, Action>();

    return cache.get(actionClassName);
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

    @Override
    public void actionPerformed(ActionEvent e)
    {
      // nothing to do in this action (always disabled)
    }
  }


  /*--------------------------------------------------*
   *
   *  D E P R E C A T E D
   *
   *--------------------------------------------------*/
  /** @deprecated use getBasicAction() or getAction(Class)*/
  @Deprecated
  public static BasicAction getAction(String actionName)
  {
    return getBasicAction(actionName);
  }

  /**
   * fuehrt eine Action aus.
   *
   * @param actionClass
   * @param data
   */
  @Deprecated
  public static void performAction(Class<? extends BasicAction> actionClass,
      String key, Object data)
  {
    String actionName = actionClass.getSimpleName();
    BasicAction action = getAction(actionName);
    if(!actionClass.isInstance(action))
      throw new IllegalStateException("action name already used."); //$NON-NLS-1$
    perform(action, key, data);
  }

  @Deprecated
  public static void performAction(String actionName)
  {
    performAction(actionName, null, null);
  }

  @Deprecated
  public static void performAction(String actionName, String key, Object data)
  {
    BasicAction action = getAction(actionName);
    perform(action, key, data);
  }

  @Deprecated
  private static void perform(BasicAction action, String key, Object data)
  {
    ActionEvent evt = new ActionEvent(getManager(),
        ActionEvent.ACTION_PERFORMED, (String)action.getValue(
        BasicAction.ACTION_COMMAND_KEY));
    // Daten uebergeben
    action.setProperty(key, data);
    // Aktion ausfuehren
    action.actionPerformed(evt);
    // Daten wieder loeschen
    //action.putValue(BasicAction.ACTION_DATA, null);
  }
}
