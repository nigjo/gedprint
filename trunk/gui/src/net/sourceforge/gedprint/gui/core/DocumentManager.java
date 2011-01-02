package net.sourceforge.gedprint.gui.core;

import java.awt.Component;
import javax.swing.event.InternalFrameEvent;
import net.sourceforge.gedprint.ui.GedPainter;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import net.sourceforge.gedprint.core.lookup.Lookup;

public class DocumentManager
{
  private static DocumentManager manager;
  private final JDesktopPane gedDesktop;
  private static final Object activeMutex = new Object();

  private DocumentManager(JDesktopPane gedDesktop)
  {
    super();
    this.gedDesktop = gedDesktop;
  }

  public static void init(JDesktopPane gedDesktop)
  {
    if(manager != null)
      throw new IllegalStateException("already initialized"); //$NON-NLS-1$
    manager = new DocumentManager(gedDesktop);
  }

  public static DocumentManager getManager()
  {
    if(manager == null)
      throw new IllegalStateException("manager not initialized"); //$NON-NLS-1$
    return manager;
  }

  public static void addDocument(GedPainter doc)
  {
    getManager().add(doc);
  }

  private void add(GedPainter doc)
  {
    doc.setPreferredSize(new Dimension(640, 480));

    String title = doc.getGedFile().getFile().getName();
    JInternalFrame docFrame = new JInternalFrame(title, true, true, true, true);
    if(doc.isScrollable())
    {
      JScrollPane scroll = new JScrollPane(doc);
      docFrame.getContentPane().add(scroll);
    }
    else
      docFrame.getContentPane().add(doc);
    docFrame.pack();
    docFrame.setLocation(10 * 2, 10 * 2);
    docFrame.setVisible(true);
    docFrame.addInternalFrameListener(new InternalFrameAdapter()
    {
      @Override
      public void internalFrameActivated(InternalFrameEvent e)
      {
        GedPainter painter = getPainter(e);
        synchronized(activeMutex)
        {
          Lookup.getGlobal().setProperty("activePainter", painter);
        }
      }

      @Override
      public void internalFrameDeactivated(InternalFrameEvent e)
      {
        GedPainter painter = getPainter(e);
        synchronized(activeMutex)
        {
          Lookup global = Lookup.getGlobal();
          if(global.getProperty("activePainter") == painter)
            global.setProperty("activePainter", null);
        }
      }

      private GedPainter getPainter(InternalFrameEvent e)
      {
        JInternalFrame internalFrame = e.getInternalFrame();
        Component component = internalFrame.getContentPane().getComponent(0);
        if(component instanceof JScrollPane)
          component =
              ((JScrollPane)component).getViewport().getView();
        GedPainter painter = (GedPainter)component;
        return painter;
      }
    });
    gedDesktop.add(docFrame);
    if(gedDesktop.getComponentCount() == 1)
    {
      try
      {
        docFrame.setMaximum(true);
      }
      catch(PropertyVetoException ex)
      {
        Logger.getLogger(DocumentManager.class.getName()).log(
            Level.WARNING, ex.toString(), ex);
      }
    }
    try
    {
      docFrame.setSelected(true);
    }
    catch(PropertyVetoException e)
    {
      // TODO Auto-generated catch block
      Logger.getLogger(DocumentManager.class.getName()).log(
          Level.WARNING, e.toString(), e);
    }

  }

  public static GedPainter getActiveDocument()
  {
    // TODO mehr tests, ob das auch wirklich passt.
    JInternalFrame iFrame = getManager().gedDesktop.getSelectedFrame();
    Component component = iFrame.getContentPane().getComponent(0);
    if(component instanceof JScrollPane)
      component = ((JScrollPane)component).getViewport().getView();
    return (GedPainter)component;
  }
}
