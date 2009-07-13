package net.sourceforge.gedprint.gui.action;

import java.util.Hashtable;

public class ActionManager
{
  private static final String ACTION_PACKAGE = "net.sourceforge.gedprint.gui.action"; //$NON-NLS-1$
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
