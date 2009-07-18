package net.sourceforge.gedprint.gui.core;

import java.util.Hashtable;

import net.sourceforge.gedprint.gui.action.BasicAction;

public class ActionManager
{
  private static final String ACTION_PACKAGE;
  static
  {
    ACTION_PACKAGE = BasicAction.class.getPackage().getName();
  }
  private static ActionManager globalManager;

  Hashtable<String, BasicAction> cache;

  private ActionManager()
  {
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
      cache = new Hashtable<String, BasicAction>();

    action = cache.get(actionName);
    if(action != null)
      return action;

    try
    {
      Class actionClass = Class.forName(ACTION_PACKAGE + '.' + actionName);
      action = (BasicAction) actionClass.newInstance();
    }
    catch(ClassNotFoundException e)
    {
      BasicAction defaultAction = new BasicAction(actionName);
      defaultAction.setEnabled(false);
      action = defaultAction;
    }
    catch(Exception e)
    {
      throw new IllegalStateException(e);
    }

    cache.put(actionName, action);
    return action;
  }

}
