package net.sourceforge.gedprint.print;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFrame;

import net.sourceforge.gedprint.core.ExceptionEcho;
import net.sourceforge.gedprint.core.GedPrintStarter;
import net.sourceforge.gedprint.core.Lookup;
import net.sourceforge.gedprint.core.Messages;
import net.sourceforge.gedprint.core.MissingArgumentException;
import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Record;

/**
 * Hilfsklasse um ein Familienbuch direkt von der Kommandozeile aus zu drucken.
 * 
 * @author nigjo
 */
public class CommandLinePrinter implements GedPrintStarter
{

  private static final int NONE = 0;
  private static final int INFILE = 1;
  private static final int RECORD = 2;
  private File infile;
  private String record;

  @Override
  public boolean parseCommandline(String[] args)
  {
    int state = NONE;
    for(String arg : args)
    {
      if(arg.charAt(0) == '-')
      {
        switch(arg.charAt(1))
        {
        case 'f':
          state = INFILE;
          break;
        case 'r':
          state = RECORD;
          break;
        default:
          throw new IllegalArgumentException(arg);
        }
      }
      else
      {
        switch(state)
        {
        case RECORD:
          if(record != null)
            throw new IllegalArgumentException(arg);
          record = arg;
          break;
        case INFILE:
          if(infile != null)
            throw new IllegalArgumentException(arg);
          infile = new File(arg);
          break;
        case NONE:
        default:
          throw new IllegalArgumentException(arg);
        }
      }
    }

    if(infile == null)
      throw new MissingArgumentException("-f <infile>"); //$NON-NLS-1$
    if(record == null)
      throw new MissingArgumentException("-r <recordid>"); //$NON-NLS-1$

    if(record.charAt(0) != '@')
      record = '@' + record;
    if(!record.endsWith("@")) //$NON-NLS-1$
      record += '@';

    return true;
  }

  @Override
  public void run()
  {
    GedFile file;
    try
    {
      file = new GedFile(infile);
      Record id = file.findID(record);
      if(id == null)
        throw new IllegalArgumentException("record_not_found"); //$NON-NLS-1$
      if(!(id instanceof Family))
        throw new IllegalArgumentException("record_is_no_family"); //$NON-NLS-1$

      Family family = (Family) id;
      PrintManagerFactory factory = Lookup.getGlobal().lookup(PrintManagerFactory.class);
      PrintManager manager = factory.createPrintManager();

      manager.setTitleFamily(family);
      manager.addFamily(family, true, false);

      JFrame dummy = new JFrame();
      manager.setOwner(dummy);
      manager.print();
    }
    catch(IllegalArgumentException e)
    {
      String pattern = Messages.getString(e.getMessage());
      Object[] arguments = new Object[1];
      arguments[0] = record;
      Throwable t = new IllegalArgumentException(MessageFormat.format(pattern,
          arguments), e);
      ExceptionEcho.show(t);
    }
    catch(Exception e)
    {
      ExceptionEcho.show(e);
    }
  }
}
