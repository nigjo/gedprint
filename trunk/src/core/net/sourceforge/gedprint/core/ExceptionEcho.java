/**
 * 
 */
package net.sourceforge.gedprint.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class ExceptionEcho
{
  private ResourceBundle bundle = ResourceBundle.getBundle(
      "net.sourceforge.gedprint.core.ExceptionEcho"); //$NON-NLS-1$
  private String message;

  public ExceptionEcho(Throwable t)
  {
    String name = t.getClass().getSimpleName();
    String pattern;
    try
    {
      pattern = bundle.getString(name);
    }
    catch(MissingResourceException e)
    {
      pattern = bundle.getString("default"); //$NON-NLS-1$
    }
    Object[] args = new Object[2];
    args[0] = t.getLocalizedMessage();
    args[1] = t.toString();

    message = MessageFormat.format(pattern, args);
  }

  @Override
  public String toString()
  {
    return message;
  }

  public static void show(Throwable t)
  {
    ExceptionEcho echo = new ExceptionEcho(t);
    t.printStackTrace();
    JOptionPane.showMessageDialog(null, echo, t.getClass().getSimpleName(),
        JOptionPane.ERROR_MESSAGE);
  }
}