package net.sourceforge.gedprint.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Utility Klasse fuer den ServiceLoader.
 * 
 * Aus "Java ist auch eine Insel", 8.Auflage, Kapitel 7.3.2
 * 
 * @author C. Ullenboom
 */
public class Lookup
{
  public static <T> T lookup(Class<T> clazz)
  {
    Iterator<T> iterator = ServiceLoader.load(clazz).iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  public static <T> Collection<? extends T> lookupAll(Class<T> clazz)
  {
    Collection<T> result = new ArrayList<T>();
    for(T e : ServiceLoader.load(clazz))
      result.add(e);
    return result;
  }
}
