package net.sourceforge.gedprint.core;

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

  public Lookup()
  {
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

  public boolean put(Object o)
  {
    if(storage == null)
      storage = new ArrayList<Object>();
    if(!storage.contains(o))
    {
      return storage.add(o);
      // change event
    }
    return false;
  }

  public boolean remove(Object o)
  {
    if(storage == null)
      return false;
    if(storage.contains(o))
    {
      return storage.remove(o);
      // change event
    }
    return false;
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
    if(storage == null)
      return null;
    ArrayList<T> result = new ArrayList<T>();
    for(Object object : storage)
    {
      if(clazz.isInstance(object))
        result.add(clazz.cast(object));
    }
    return result;
  }

  private static class GlobalLookup extends Lookup
  {
    HashMap<Class<?>, ServiceLoader<?>> loaderCache;

    @Override
    public <T> T lookup(Class<T> clazz)
    {
      ServiceLoader<?> loader = getLoader(clazz);
      Iterator<?> iterator = loader.iterator();
      return iterator.hasNext() ? clazz.cast(iterator.next()) : super.lookup(clazz);
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
      if(result.size() == 0)
        return super.lookupAll(clazz);
      return result;
    }
  }
}
