package net.sourceforge.gedprint.core.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 *
 */
public class Lookup
{
  private static Lookup global;
  private List<Object> storage;
  private final LookupChangeSupport changeSupport;

  private Lookup()
  {
    changeSupport = new LookupChangeSupport(this);
  }

  /**
   * Liefert das globale Lookup. Im globalen Lookup werden zunaechst die
   * ServiceLoader nach einem geeigneten Ergebnis gefragt, bevor die
   * individuell hinzugefuegten Elemente geprueft werden.
   *
   * @return Das globale Lookup
   * @see ServiceLoader
   */
  public static Lookup getGlobal()
  {
    if(global == null)
      global = new GlobalLookup();
    return global;
  }

  public static Lookup create(Object... data)
  {
    Lookup l = new Lookup();
    for(Object object : data)
      l.put(object);
    return l;
  }

  public static Lookup duplicate(Lookup original)
  {
    if(original instanceof GlobalLookup)
      throw new IllegalArgumentException();
    Lookup copy = new Lookup();
    for(Object object : original.storage)
      copy.put(object);
    return copy;
  }

  public boolean put(Object o)
  {
    if(storage == null)
      storage = new ArrayList<Object>();
    if(!storage.contains(o))
    {
      boolean added = storage.add(o);
      if(added)
        // change event
        changeSupport.fireElementAddedEvent(o);
      return added;
    }
    return false;
  }

  public boolean remove(Object o)
  {
    if(storage == null)
      return false;
    if(storage.contains(o))
    {
      boolean removed = storage.remove(o);
      if(removed)
        changeSupport.fireElementRemovedEvent(o);
      return removed;
      // change event
    }
    return false;
  }

  public boolean removeLookupListener(Class<?> lookupClass, LookupListener l)
  {
    return changeSupport.removeLookupListener(lookupClass, l);
  }

  public boolean removeLookupListener(LookupListener l)
  {
    return changeSupport.removeLookupListener(l);
  }

  public void addLookupListener(Class<?> lookupClass, LookupListener l)
  {
    changeSupport.addLookupListener(lookupClass, l);
  }

  public void addLookupListener(LookupListener l)
  {
    changeSupport.addLookupListener(l);
  }

  public <T> T lookup(Class<T> clazz)
  {
    if(storage == null)
      return null;
    for(Object object : storage)
    {
      if(clazz.isInstance(object))
        return clazz.cast(object);
    }
    return null;
  }

  public <T> Collection<? extends T> lookupAll(Class<T> clazz)
  {
    ArrayList<T> result = new ArrayList<T>();
    if(storage == null)
      return result;
    for(Object object : storage)
    {
      if(clazz.isInstance(object))
        result.add(clazz.cast(object));
    }
    return result;
  }

  public void setProperty(String key, Object value)
  {
    LookupProperty p = getLookupProperty(key, true);
    p.setValue(value);
  }

  public Object getProperty(String key)
  {
    LookupProperty p = getLookupProperty(key, false);
    return p == null ? null : p.getValue();
  }

  protected LookupProperty getLookupProperty(String key, boolean autoCreate)
  {
    Collection<? extends LookupProperty> properties =
        lookupAll(LookupProperty.class);
    for(LookupProperty property : properties)
    {
      if(key.equals(property.getName()))
        return property;
    }
    if(autoCreate)
    {
      LookupProperty property = new LookupProperty(key);
      put(property);
      return property;
    }
    return null;
  }

  public static class LookupProperty
  {
    private final String name;
    private Object value;

    public LookupProperty(String name)
    {
      this.name = name;
    }

    private void setValue(Object value)
    {
      this.value = value;
    }

    public String getName()
    {
      return name;
    }

    public Object getValue()
    {
      return value;
    }
  }

  private static class GlobalLookup extends Lookup
  {
    HashMap<Class<?>, ServiceLoader<?>> loaderCache;

    @Override
    public <T> T lookup(Class<T> clazz)
    {
      ServiceLoader<?> loader = getLoader(clazz);
      Iterator<?> iterator = loader.iterator();
      return iterator.hasNext()
          ? clazz.cast(iterator.next())
          : super.lookup(clazz);
    }

    private synchronized ServiceLoader<?> getLoader(Class<?> clazz)
    {
      if(loaderCache == null)
        loaderCache = new HashMap<Class<?>, ServiceLoader<?>>();
      ServiceLoader<?> loader;
      if(!loaderCache.containsKey(clazz))
      {
        loader = ServiceLoader.load(clazz);
        loaderCache.put(clazz, loader);
      }
      else
      {
        loader = loaderCache.get(clazz);
      }
      return loader;
    }

    @Override
    public <T> Collection<? extends T> lookupAll(Class<T> clazz)
    {
      Collection<T> result = new ArrayList<T>();
      for(Object e : getLoader(clazz))
        result.add(clazz.cast(e));
      if(result.isEmpty())
        return super.lookupAll(clazz);
      return result;
    }
  }
}
