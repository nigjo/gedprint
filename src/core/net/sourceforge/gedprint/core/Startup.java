package net.sourceforge.gedprint.core;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Startup
{
  private static final String DEFAULT_MAIN = "net.sourceforge.gedprint.gui.core.GuiStartup"; //$NON-NLS-1$

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    String starterClassName = DEFAULT_MAIN;

    // eventuellen Starter suchen
    for(int i = 0; i < args.length; i++)
    {
      if(args[i].equals("--starter")) //$NON-NLS-1$
      {
        if(args.length >= i + 1)
        {
          starterClassName = args[i + 1];
          String[] args2 = new String[args.length - 2];
          if(i > 0)
            System.arraycopy(args, 0, args2, 0, i);
          if(i + 2 < args.length)
            System.arraycopy(args, i + 2, args2, i, args2.length - i);

          args = args2;
          break;
        }
      }
    }

    try
    {
      Class starterClass = Class.forName(starterClassName);
      Object starterObject = starterClass.newInstance();
      GedPrintStarter starter = (GedPrintStarter) starterObject;

      if(starter.parseCommandline(args))
        GedPrintThreadGroup.execute(starter);
    }
    catch(Exception e)
    {
      ExceptionEcho.show(e);
    }
  }

  public static class GedPrintThreadGroup extends ThreadGroup
  {
    public static void execute(Runnable runnable)
    {
      GedPrintThreadGroup group = new GedPrintThreadGroup();
      new Thread(group, runnable).start();
    }

    public GedPrintThreadGroup()
    {
      super("gedprint"); //$NON-NLS-1$
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
      super.uncaughtException(t, e);

      Logger.getLogger(getClass().getName()).log(Level.SEVERE,
          e.getLocalizedMessage(), e);
    }
  }
}
