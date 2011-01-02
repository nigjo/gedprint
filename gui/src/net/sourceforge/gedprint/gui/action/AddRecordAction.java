package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.ui.GedPainter;

public class AddRecordAction extends BasicAction
{
  private static final long serialVersionUID = 2181353137287185919L;

  @Override
  public void actionPerformed(ActionEvent e)
  {
    Object data = getLookup().getProperty("id");
    if(data == null)
      return;
    Logger.getLogger(getClass().getName()).fine(data.toString());

    if(data instanceof String)
      addId((String)data);
    if(data instanceof Record)
      addRecord((Record)data);
  }

  public void addId(String id)
  {
    GedFile file = lookup(GedFile.class);
    if(file == null)
    {
      Object property = getLookup().getProperty("GedFile");
      if(property instanceof GedFile)
        file = (GedFile)property;
      else
        return;
    }
    addRecord(file.findID(id));
  }

  public void addRecord(Record rec)
  {
    GedPainter painter = getPainter();
    if(painter != null){
      painter.addRecord(rec);
      painter.repaint();
    }
  }
}
