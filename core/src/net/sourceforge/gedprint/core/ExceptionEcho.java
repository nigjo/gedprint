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
  private Throwable cause;

  public ExceptionEcho(Throwable t)
  {
    super();
    setThrowable(t);

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
    setMessagedata(pattern, 0);
  }

  public ExceptionEcho(Throwable t, String pattern, int tracecount)
  {
    super();
    setThrowable(t);
    setMessagedata(pattern, tracecount);
  }

  @Override
  public String toString()
  {
    return message;
  }

  public static void show(Throwable t)
  {
    ExceptionEcho echo = new ExceptionEcho(t);
    echo.show();
  }

  public static void show(Throwable cause, String pattern, int tracecount)
  {
    ExceptionEcho echo = new ExceptionEcho(cause, pattern, tracecount);
    echo.show();
  }

  private void setMessagedata(String pattern, int tracecount)
  {
    Object[] args = new Object[3];
    args[0] = cause.getLocalizedMessage();
    args[1] = cause.toString();
    if(tracecount <= 0)
      args[2] = "";
    else
    {
      StringBuilder builder = null;
      boolean firstfound = false;
      for(StackTraceElement element : cause.getStackTrace())
      {
        if(!firstfound)
        {
          String className = element.getClassName();
          if(className.startsWith("net.sourceforge.gedprint"))
            firstfound = true;
          else
            continue;
        }
        if(tracecount-- <= 0)
          break;
        if(builder == null)
          builder = new StringBuilder();
        else
          builder.append('\n');

        builder.append(element.toString());
      }
      args[2] = builder.toString();
    }

    message = MessageFormat.format(pattern, args);
  }

  private void setThrowable(Throwable cause)
  {
    this.cause = cause;
  }

  private void show()
  {
    cause.printStackTrace();
    JOptionPane.showMessageDialog(null, this, cause.getClass().getSimpleName(),
        JOptionPane.ERROR_MESSAGE);
  }

}