package net.sourceforge.gedprint.core;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 10:32:37 AM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class Messages
{
  private Messages()
  {
    // no instance. only factory.
  }

  public static String getString(String key)
  {
    Throwable t = new Throwable();
    StackTraceElement[] trace = t.getStackTrace();
    return getString(trace[1].getClassName(), key);
  }

  public static String getString(Object owner, String key)
  {
    return getString(owner.getClass(), key);
  }

  public static String getString(Class owner, String key)
  {
    String classname = owner.getName();
    String packname = classname.substring(0, classname.lastIndexOf('.'));
    String lastpart = packname.substring(packname.lastIndexOf('.') + 1);
    String bundlename = packname + '.' + lastpart;

    Package.getPackage(packname);

    ClassLoader loader = owner.getClassLoader();

    ResourceBundle bundle = ResourceBundle.getBundle(bundlename,
        Locale.getDefault(), loader);

    return bundle.getString(key);
  }

  private static String getString(String classname, String key)
  {
    try
    {
      Class owner = Class.forName(classname);
      return getString(owner, key);
    }
    catch(ClassNotFoundException e)
    {
      throw new MissingResourceException(e.toString(), classname, key);
    }

  }

}
