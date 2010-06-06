package net.sourceforge.gedprint.core.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LookupChangeSupport
{
  HashMap<Class<?>, List<LookupListener>> lookupListener;
  private final Lookup lookup;

  public LookupChangeSupport(Lookup lookup)
  {
    this.lookup = lookup;
  }

  public void addLookupListener(LookupListener l)
  {
    addLookupListener(Object.class, l);
  }

  public void addLookupListener(String lookupClassName, LookupListener l)
      throws ClassNotFoundException
  {
    Class<?> lookupClass;
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    lookupClass = cl.loadClass(lookupClassName);
    addLookupListener(lookupClass, l);
  }

  public synchronized void addLookupListener(Class<?> lookupClass, LookupListener l)
  {
    if(lookupClass == null || l == null)
      return;
    if(lookupListener == null)
      lookupListener = new HashMap<Class<?>, List<LookupListener>>();
    List<LookupListener> list = lookupListener.get(lookupClass);
    if(list == null)
    {
      list = new ArrayList<LookupListener>();
      list.add(l);
    }
    else if(!list.contains(l))
    {
      list.add(l);
    }
  }

  public synchronized boolean removeLookupListener(LookupListener l)
  {
    if(l == null)
      return false;
    if(lookupListener == null)
      return false;
    boolean removed = false;
    for(Class<?> lClass : lookupListener.keySet())
    {
      List<LookupListener> list = lookupListener.get(lClass);
      if(list.contains(l))
        removed |= list.remove(l);
    }
    return removed;
  }

  public boolean removeLookupListener(String lookupClassName, LookupListener l)
      throws ClassNotFoundException
  {
    Class<?> lookupClass;
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    lookupClass = cl.loadClass(lookupClassName);
    return removeLookupListener(lookupClass, l);
  }

  public synchronized boolean removeLookupListener(Class<?> lookupClass, LookupListener l)
  {
    if(lookupClass == null || l == null)
      return false;
    if(lookupListener == null)
      return false;
    List<LookupListener> list = lookupListener.get(lookupClass);
    if(list == null)
      return false;
    if(list.contains(l))
    {
      return list.remove(l);
    }
    return false;
  }

  public void fireElementAddedEvent(Object element)
  {
    LookupEvent event = new LookupEvent(lookup, LookupEvent.ELEMENT_ADDED, element);
    fireLookupEvent(event);
  }

  public void fireElementRemovedEvent(Object element)
  {
    LookupEvent event = new LookupEvent(lookup, LookupEvent.ELEMENT_REMOVED, element);
    fireLookupEvent(event);
  }

  private synchronized void fireLookupEvent(LookupEvent event)
  {
    if(lookupListener == null)
      return;
    Object element = event.getElement();
    for(Class<?> lClass : lookupListener.keySet())
    {
      if(!lClass.isInstance(element))
        continue;
      List<LookupListener> list = lookupListener.get(lClass);
      for(LookupListener l : list)
      {
        l.lookupChanged(event);
      }
    }
  }
}
