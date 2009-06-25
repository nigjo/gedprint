package net.sourceforge.gedprint.gui.startup;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Neue Klasse erstellt von hof. Erstellt am Jun 25, 2009, 2:24:49 PM
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author hof
 */
public class GuiThreadGroup extends ThreadGroup
{
  public static void execute(Runnable runnable)
  {
    GuiThreadGroup group =
        new GuiThreadGroup();
    new Thread(group, runnable).start();
  }

  public GuiThreadGroup()
  {
    super("gedprint");
  }

  @Override
  public void uncaughtException(Thread t, Throwable e)
  {
    super.uncaughtException(t, e);

    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.
        getLocalizedMessage(), e);
  }
}
