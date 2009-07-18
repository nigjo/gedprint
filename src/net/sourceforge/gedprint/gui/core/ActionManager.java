package net.sourceforge.gedprint.gui.core;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
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
  PropertyChangeSupport pcSupport;

  private final Object propMutex = new Object();
  private HashMap<String, Object> properties;

  private ActionManager()
  {
    pcSupport = new PropertyChangeSupport(this);
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
    pcSupport.addPropertyChangeListener(action);

    return action;
  }

  public static Object getActionProperty(String property)
  {
    ActionManager manager = getManager();
    return manager.getProperty(property);
  }
  
  public static void setActionProperty(String property, Object newValue)
  {
    ActionManager manager = getManager();
    manager.firePropertyChange(property, newValue);
  }

  public Object getProperty(String key)
  {
    synchronized(propMutex)
    {
      if(properties == null)
        return null;
      return properties.get(key);
    }
  }

  public Object setProperty(String key, Object value)
  {
    synchronized(propMutex)
    {
      if(properties == null)
        properties = new HashMap<String, Object>();
      return properties.put(key, value);
    }
  }

  private void firePropertyChange(String property, Object newValue)
  {
    Object oldValue = setProperty(property, newValue);
    pcSupport.firePropertyChange(property, oldValue, newValue);
  }
}
