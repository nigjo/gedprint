package net.sourceforge.gedprint.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.gedcom.GedFile;

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

  @Override
  public void actionPerformed(ActionEvent ae)
  {
    JFileChooser chooser = new JFileChooser(lastdir);
    FileFilter gedfilter = new FileFilter() {

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
    };
    chooser.addChoosableFileFilter(gedfilter);

    chooser.setAcceptAllFileFilterUsed(true);
    chooser.setFileFilter(gedfilter);

    Object source = ae.getSource();
    Component owner = (Component) source;
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
        GedFile gedFile = new GedFile(selected.getAbsolutePath());
        setProperty(PROPERTY_FILE, gedFile);
      }
      catch(FileNotFoundException fnfe)
      {
        String msg = Messages.getString("action.err.file_not_found"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(owner, msg, owner.getName(),
            JOptionPane.WARNING_MESSAGE);
        return;
      }
      catch(IOException e1)
      {
        String msg = Messages.getString("action.err.io-error.read"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(owner, msg, owner.getName(),
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      finally
      {
        lastdir = chooser.getCurrentDirectory();
      }

    }

  }
}
