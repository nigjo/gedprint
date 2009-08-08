package net.sourceforge.gedprint.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Record;

public class AddRecordAction extends BasicAction
{
  private static final long serialVersionUID = 2181353137287185919L;

  public void actionPerformed(ActionEvent e)
  {
    Object data = getValue(ACTION_DATA);
    if(data == null)
      return;
    Logger.getLogger(getClass().getName()).fine(data.toString());

    GedFile file = (GedFile) getProperty(PROPERTY_FILE);
    if(file == null)
      return;
    if(data instanceof String)
      data = file.findID((String) data);
    if(data instanceof Record)
      getPainter().addRecord((Record) data);
  }
}
