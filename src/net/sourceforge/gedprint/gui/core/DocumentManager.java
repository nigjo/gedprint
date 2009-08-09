package net.sourceforge.gedprint.gui.core;

import java.awt.Dimension;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DocumentManager
{
  private static DocumentManager manager;
  private final JDesktopPane gedDesktop;

  public DocumentManager(JDesktopPane gedDesktop)
  {
    super();
    this.gedDesktop = gedDesktop;
    if(manager != null)
      throw new IllegalStateException("already initialized"); //$NON-NLS-1$
    manager = this;
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
    docFrame.getContentPane().add(doc);
    docFrame.pack();
    docFrame.setLocation(10 * 2, 10 * 2);
    docFrame.setVisible(true);
    gedDesktop.add(docFrame);
    try
    {
      docFrame.setSelected(true);
    }
    catch(PropertyVetoException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
