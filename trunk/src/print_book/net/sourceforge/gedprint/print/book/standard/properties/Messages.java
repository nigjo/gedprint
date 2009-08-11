package net.sourceforge.gedprint.print.book.standard.properties;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
  private static final String BUNDLE_NAME = Messages.class.getName().toLowerCase();

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle(BUNDLE_NAME);

  private Messages()
  {
  }

  public static String getString(String key)
  {
    try
    {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e)
    {
      System.err.println(e.toString());
      return '!' + key + '!';
    }
  }
}
