package net.sourceforge.gedprint.core;

import net.sourceforge.gedprint.core.lookup.Lookup;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Startklasse der Anwendung.
 * 
 * @author Hofschr&ouml;er, Jens
 */
public class Startup
{
  private static final String DEFAULT_MAIN =
      "net.sourceforge.gedprint.gui.core.GuiStartup"; //$NON-NLS-1$
  private static Logger appglobal;

  private Startup()
  {
  }

  /**
   * Startet die Initialisierung.
   * @param args Kommandozeilenargumente
   */
  public static void main(String[] args)
  {
    initGlobalLogger();
    StartupRunner.startup(args);
  }

  private static void initGlobalLogger() throws SecurityException
  {
    ConsoleHandler console = new ConsoleHandler();
    console.setLevel(Level.ALL);
    AppLogHelper logHelper = new AppLogHelper();
    console.setFormatter(logHelper);
    console.setFilter(logHelper);

    appglobal = Logger.getLogger("net.sourceforge.gedprint");
    appglobal.setUseParentHandlers(false);
    appglobal.addHandler(console);
    appglobal.setLevel(Level.ALL);

    Lookup.getGlobal().put(appglobal);
  }

  /**
   * Initialisierungsklasse der Anwendung. Die Hilfsklasse preueft die
   * Uebergebenen Parameter der Kommandozeile und startet die Anwendung
   * in einem eigenen Thread. Dieser Thread verwendet einen
   * {@link ServiceClassLoader} als Kontextlader um eventuelle externe
   * .jar Dateien mit Serviceklassen dynamisch zu laden.
   */
  private static class StartupRunner implements Runnable
  {
    private String starterClassName;
    private String[] args;

    public StartupRunner(String starterClassName, String[] args)
    {
      this.starterClassName = starterClassName;
      this.args = args;
    }

    public void run()
    {
      try
      {
        Class<?> starterClass = Class.forName(starterClassName);
        Object starterObject = starterClass.newInstance();
        GedPrintStarter starter = (GedPrintStarter)starterObject;

        if(starter.parseCommandline(args))
          starter.run();
      }
      catch(Exception e)
      {
        // Jede Exception waehrend der Initialisierung abfangen.
        ExceptionEcho.show(e);
      }
    }

    /**
     * Initialisierung der Anwendung. Test auf Startklasse und Generiung der
     * eigenen ThreadGroup und Klassenlader.
     *
     * @param Vollstaendige Kommandozeile. Die Kommandozeile wird nach einem
     *        Parameter "<code>--starter</code>" durchsucht und der nachfolgende
     *        Parameter als Startklasse interpretiert. Diese beiden Argumente
     *        werden im Folgenden nicht mehr an die Startklasse weitergereicht.
     */
    private static void startup(String[] args)
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

            // gefilterte Kommandozeile weiterreichen
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
  }

  /**
   * Hilfsklasse um unerwartet auftretende Exceptions zu verarbeiten.
   */
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

  private static class AppLogHelper extends Formatter
      implements java.util.logging.Filter
  {
    public AppLogHelper()
    {
    }

    public boolean isLoggable(LogRecord record)
    {
      if(record.getLoggerName().startsWith("java"))
        return false;
      if(record.getLoggerName().startsWith("sun.awt"))
        return false;
      return true;
    }

    @Override
    public String format(LogRecord record)
    {
      StringBuilder builder = new StringBuilder();
      String source = record.getSourceClassName();
      builder.append(source.substring(source.lastIndexOf('.') + 1));
      builder.append(": ");
      builder.append(getMessage(record));
      builder.append("\n");
      return builder.toString();
    }

    private String getMessage(LogRecord record)
    {
      String message = record.getMessage();

      ResourceBundle resourceBundle = record.getResourceBundle();
      if(record.getResourceBundleName() != null)
        resourceBundle = ResourceBundle.getBundle(record.getResourceBundleName());
      if(resourceBundle != null)
        message = resourceBundle.getString(message);

      Object[] params = record.getParameters();
      if(params != null)
        message = MessageFormat.format(message, params);

      return message;
    }
  }
}
