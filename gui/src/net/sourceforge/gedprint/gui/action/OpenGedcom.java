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
import net.sourceforge.gedprint.core.lookup.Lookup;
import net.sourceforge.gedprint.core.Bundle;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gui.core.DocumentManager;
import net.sourceforge.gedprint.ui.GedDocumentFactory;
import net.sourceforge.gedprint.ui.GedPainter;

public class OpenGedcom extends FrameAccessAction
{
  private static final long serialVersionUID = -4059235264496416456L;
  private static final String EXT = ".ged"; //$NON-NLS-1$
  private File lastdir;

  public OpenGedcom()
  {
    super(Bundle.getString("OpenGedcom.title", OpenGedcom.class)); //$NON-NLS-1$
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl O")); //$NON-NLS-1$

    lastdir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
  }

  @Override
  public void actionPerformed(ActionEvent ae)
  {
    File selected = null;

    Object data = getLookup().getProperty("openfile");
    if(data instanceof GedFile)
    {
      Lookup l = Lookup.create(data);
      GedPainter doc = createDocument(l);
      if(doc != null)
        DocumentManager.addDocument(doc);
      return;
    }

    Object source = ae.getSource();
    Component owner;
    if(!(source instanceof Component))
      owner = getOwner(ae);
    else
      owner = (Component)source;

    if(data instanceof File)
    {
      selected = (File)data;
    }
    else
    {
      JFileChooser chooser = new JFileChooser(lastdir);
      FileFilter gedfilter = new GedFileFilter();
      chooser.addChoosableFileFilter(gedfilter);

      chooser.setAcceptAllFileFilterUsed(true);
      chooser.setFileFilter(gedfilter);

      int erg = chooser.showOpenDialog(owner);
      lastdir = chooser.getCurrentDirectory();
      if(erg == JFileChooser.APPROVE_OPTION)
      {
        selected = chooser.getSelectedFile();
        if(chooser.getFileFilter() == gedfilter)
        {
          // pruefen, ob der Dateiname die korrekte Dateiendung hat.
          String name = selected.getName();
          if(!name.toLowerCase().endsWith(EXT))
          {
            selected = new File(selected.getParentFile(), name + EXT);
          }
        }
      }
      else
        return;
    }
    try
    {
      GedPainter doc = createDocument(selected);
      if(doc != null)
        DocumentManager.addDocument(doc);
    }
    catch(FileNotFoundException fnfe)
    {
      String msg = Bundle.getString("action.err.file_not_found",
          OpenGedcom.class); //$NON-NLS-1$
      JOptionPane.showMessageDialog(owner, msg, ae.getActionCommand(),
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    catch(IOException ioe)
    {
      String msg =
          Bundle.getString("action.err.io-error.read", OpenGedcom.class); //$NON-NLS-1$
      JOptionPane.showMessageDialog(owner, msg, ae.getActionCommand(),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    catch(Exception unexpected)
    {
      String pattern = Bundle.getString("OpenGedcom.err.unexpected",
          OpenGedcom.class); //$NON-NLS-1$
      ExceptionEcho.show(unexpected, pattern, 3);
    }
    finally
    {
    }
  }

  private GedPainter createDocument(File selected)
      throws FileNotFoundException, IOException
  {
    GedFile gedFile = new GedFile(selected.getAbsolutePath());
    // setProperty(PROPERTY_FILE, gedFile);
    return createDocument(Lookup.create(gedFile));
  }

  private GedPainter createDocument(Lookup lookup)
  {
    GedFile gedFile = lookup.lookup(GedFile.class);

    GedDocumentFactory factory;
    factory = lookup.lookup(GedDocumentFactory.class);
    if(factory == null)
    {
      factory = queryFactory();
      if(factory == null)
        return null;
    }
    GedPainter doc = factory.createDocument();

    doc.setGedFile(gedFile);
    return doc;
  }

  private GedDocumentFactory queryFactory()
  {
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
      wrapper.add(new JLabel(Bundle.getString("OpenGedcom.select_painter",
          OpenGedcom.class))); //$NON-NLS-1$
      wrapper.add(box);
      String title = Bundle.getString("OpenGedcom.select_painter.title",
          OpenGedcom.class); //$NON-NLS-1$
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
    return factory;
  }

  public void openFile(GedFile gedfile, GedDocumentFactory factory)
  {
    Lookup l = Lookup.create(gedfile);
    if(factory != null)
      l.put(factory);
    GedPainter doc = createDocument(l);
    if(doc != null)
      DocumentManager.addDocument(doc);
  }

  public void openFile(GedFile gedfile, boolean autoselect)
  {
    GedDocumentFactory painter = null;
    if(autoselect)
      painter = Lookup.getGlobal().lookup(GedDocumentFactory.class);
    openFile(gedfile, painter);
  }

  public void openFile(GedFile gedfile)
  {
    openFile(gedfile, false);
  }

  private static class GedFileFilter extends FileFilter
  {
    @Override
    public String getDescription()
    {
      String desc = Bundle.getString("OpenGedcom.gedcom.description",
          OpenGedcom.class); //$NON-NLS-1$
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
