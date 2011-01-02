package net.sourceforge.gedprint.gui.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.gedprint.core.Bundle;

/**
 * Neue Klasse erstellt von nigjo. Am Jan 2, 2011, 10:35:43 PM.
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author nigjo
 */
public class Utilities
{
  public static String getString(String key)
  {
    StackTraceElement callingContext = new Throwable().getStackTrace()[1];
    String callingClass = callingContext.getClassName();
    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      Class<?> context = null;
      context = contextLoader.loadClass(callingClass);
      return Bundle.getString(key, context);
    }
    catch(ClassNotFoundException ex)
    {
      // Duerfte eigentlich nie auftreten, da die Klasse ja diese Methode
      // hier aufgerufen hat.
      Logger.getLogger(Utilities.class.getName()).log(
          Level.SEVERE, ex.toString(), ex);
      return key;
    }
  }

  private Utilities()
  {
  }
}
