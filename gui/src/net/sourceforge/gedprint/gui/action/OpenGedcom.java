package net.sourceforge.gedprint.gui.action;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.gedprint.core.ExceptionEcho;
import net.sourceforge.gedprint.core.Lookup;
import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.core.DocumentManager;
import net.sourceforge.gedprint.gui.core.GedDocumentFactory;
import net.sourceforge.gedprint.gui.core.GedPainter;

public class OpenGedcom extends FrameAccessAction
{
  private static final long serialVersionUID = -4059235264496416456L;
  private static final String EXT = ".ged"; //$NON-NLS-1$
  private File lastdir;

  public OpenGedcom()
  {
    super(Messages.getString("OpenGedcom.title")); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl O")); //$NON-NLS-1$

    lastdir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
  }

  public void actionPerformed(ActionEvent ae)
  {
    JFileChooser chooser = new JFileChooser(lastdir);
    FileFilter gedfilter = new GedFileFilter();
    chooser.addChoosableFileFilter(gedfilter);

    chooser.setAcceptAllFileFilterUsed(true);
    chooser.setFileFilter(gedfilter);

    Object source = ae.getSource();
    Component owner = (Component)source;
    int erg = chooser.showOpenDialog(owner);
    if(erg == JFileChooser.APPROVE_OPTION)
    {
      File selected = chooser.getSelectedFile();
      if(chooser.getFileFilter() == gedfilter)
      {
        // pruefen, ob der Dateiname die korrekte Dateiendung hat.
        String name = selected.getName();
        if(!name.toLowerCase().endsWith(EXT))
        {
          selected = new File(selected.getParentFile(), name + EXT);
        }
      }

      try
      {
        GedPainter doc = createDocument(selected);
        if(doc != null)
          DocumentManager.addDocument(doc);
      }
      catch(FileNotFoundException fnfe)
      {
        String msg = Messages.getString("action.err.file_not_found"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(owner, msg, ae.getActionCommand(),
            JOptionPane.WARNING_MESSAGE);
        return;
      }
      catch(IOException ioe)
      {
        String msg = Messages.getString("action.err.io-error.read"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(owner, msg, ae.getActionCommand(),
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      catch(Exception unexpected)
      {
        String pattern = Messages.getString("OpenGedcom.err.unexpected"); //$NON-NLS-1$
        ExceptionEcho.show(unexpected, pattern, 3);
      }
      finally
      {
        lastdir = chooser.getCurrentDirectory();
      }
    }
  }

  private GedPainter createDocument(File selected)
      throws FileNotFoundException, IOException
  {
    GedFile gedFile = new GedFile(selected.getAbsolutePath());
    // setProperty(PROPERTY_FILE, gedFile);
    Collection<? extends GedDocumentFactory> factories =
        Lookup.getGlobal().lookupAll(GedDocumentFactory.class);
    GedDocumentFactory factory = null;
    if(factories.size() == 1)
    {
      factory = factories.iterator().next();
    }
    else
    {
      JComboBox box = new JComboBox();
      for(GedDocumentFactory factory2 : factories)
      {
        String name = factory2.getName();
        box.addItem(name);
      }
      JPanel wrapper = new JPanel(new GridLayout(2, 1));
      wrapper.add(new JLabel(Messages.getString("OpenGedcom.select_painter"))); //$NON-NLS-1$
      wrapper.add(box);
      String title = Messages.getString("OpenGedcom.select_painter.title"); //$NON-NLS-1$
      int erg1 = JOptionPane.showConfirmDialog(null, wrapper, title,
          JOptionPane.OK_CANCEL_OPTION);
      if(erg1 != JOptionPane.YES_OPTION)
        return null;

      for(GedDocumentFactory factory2 : factories)
      {
        if(box.getSelectedItem().equals(factory2.getName()))
        {
          factory = factory2;
          break;
        }
      }
    }
    GedPainter doc = factory.createDocument();
    doc.setGedFile(gedFile);
    return doc;
  }

  private static class GedFileFilter extends FileFilter
  {
    @Override
    public String getDescription()
    {
      String desc = Messages.getString("OpenGedcom.gedcom.description"); //$NON-NLS-1$
      desc += " (*" + EXT + ')'; //$NON-NLS-1$
      return desc;
    }

    @Override
    public boolean accept(File f)
    {
      if(f.isDirectory())
        return true;
      String name = f.getName();
      return name.toLowerCase().endsWith(EXT);
    }

  }
}
