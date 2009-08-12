package net.sourceforge.gedprint.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Startup
{
  private static final String DEFAULT_MAIN =
      "net.sourceforge.gedprint.gui.core.GuiStartup"; //$NON-NLS-1$

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

    // Klassenlader fuer externe Services
    ClassLoader serviceLoader = new ServiceClassLoader(Thread.currentThread().
        getContextClassLoader());

    // Neuen Thread mit diesem Klassenlader initialisieren
    GedPrintThreadGroup group = new GedPrintThreadGroup();
    Runnable startup = new StartupRunner(starterClassName, args);
    Thread gedthread = new Thread(group, startup, "startup");
    gedthread.setContextClassLoader(serviceLoader);

    // ... und starten
    gedthread.start();
  }

  private static class StartupRunner implements Runnable
  {
    private String realStarterClassName;
    private String[] realArgs;

    public StartupRunner(String realStarterClassName, String[] realArgs)
    {
      this.realStarterClassName = realStarterClassName;
      this.realArgs = realArgs;
    }

    public void run()
    {
      try
      {
        Class starterClass = Class.forName(realStarterClassName);
        Object starterObject = starterClass.newInstance();
        GedPrintStarter starter = (GedPrintStarter)starterObject;

        if(starter.parseCommandline(realArgs))
          starter.run();
      }
      catch(Exception e)
      {
        ExceptionEcho.show(e);
      }
    }

  }

  private static class GedPrintThreadGroup extends ThreadGroup
  {
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
