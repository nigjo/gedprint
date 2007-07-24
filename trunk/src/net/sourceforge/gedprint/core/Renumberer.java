package net.sourceforge.gedprint.core;

import java.util.Enumeration;
import java.util.Properties;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gedcom.Tag;

public class Renumberer
{
  private final GedFile data;

  public Renumberer(GedFile data)
  {
    super();
    this.data = data;
  }

  public void renumber(int indiStart, int famStart)
  {
    Properties indiNum = findIds(data.getIndividuals(), indiStart);
    Properties famNum = findIds(data.getFamilies(), famStart);

    //
    // Individuen
    //
    Enumeration individuals = data.getIndividuals();
    while(individuals.hasMoreElements())
    {
      Individual in = (Individual) individuals.nextElement();
      String old = in.getID();
      String neu = indiNum.getProperty(old);
      in.setID(neu);

      // als Kind
      changeIDs(in.getSubRecords(Tag.FAM_CHILD), famNum);
      changeIDs(in.getSubRecords(Tag.FAM_SPOUSE), famNum);
    }

    //
    // Familien
    //
    Enumeration families = data.getFamilies();
    while(families.hasMoreElements())
    {
      Family fam = (Family) families.nextElement();
      String old = fam.getID();
      String neu = famNum.getProperty(old);
      fam.setID(neu);

      // als Kind
      changeIDs(fam.getSubRecords(Tag.HUSBAND), indiNum);
      changeIDs(fam.getSubRecords(Tag.WIFE), indiNum);
      changeIDs(fam.getSubRecords(Tag.CHILDREN), indiNum);
    }

    //
    // Familien
    //
}

  private void changeIDs(Record[] subRecords, Properties numbers)
  {
    String old;
    String neu;
    for(int i = 0; i < subRecords.length; i++)
    {
      old = subRecords[i].getContent();
      neu = numbers.getProperty(old);
      subRecords[i].setContent(neu);
    }
  }

  private Properties findIds(Enumeration records, int startnum)
  {
    String prefix = null;
    Properties numbers = new Properties();
    while(records.hasMoreElements())
    {
      Record rec = (Record) records.nextElement();
      String key = rec.getID();
      if (prefix == null)
      {
        String[] id2 = key.split("\\d"); //$NON-NLS-1$
        prefix = id2[0];
      }
      String value = prefix + String.valueOf(startnum++) + '@';
      numbers.setProperty(key, value);
    }

    return numbers;
  }

  public GedFile getFile()
  {
    return data;
  }
}
