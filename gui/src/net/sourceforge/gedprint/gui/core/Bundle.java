package net.sourceforge.gedprint.gui.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Einfache Zugriffsklasse fuer ResourceBundles.
 *
 * @author nigjo
 */
public final class Bundle
{
  private static Map<Package, ResourceBundle> cache;
  private static final Object mutex = new Object();

  private Bundle()
  {
    // Utility class only
  }

  public static String getString(String key, Class<?> context)
  {
    ResourceBundle bundle = getBundle(context);
    return bundle.getString(key);
  }

  private static ResourceBundle getBundle(Class<?> context)
      throws MissingResourceException
  {
    Package pack = context.getPackage();
    synchronized(mutex)
    {
      if(cache == null)
        cache = new HashMap<Package, ResourceBundle>();
      if(cache.containsKey(pack))
        return cache.get(pack);
      ClassLoader cl = context.getClassLoader();
      String bundleName = pack.getName() + ".Bundle";
      ResourceBundle bundle =
          ResourceBundle.getBundle(bundleName, Locale.getDefault(), cl);
      cache.put(pack, bundle);
      return bundle;
    }
  }
}
