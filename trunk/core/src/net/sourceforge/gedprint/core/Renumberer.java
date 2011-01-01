package net.sourceforge.gedprint.core;

import java.util.List;
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
    List<Record> individuals = data.getIndividuals();
    for(Record record : individuals)
    {
      Individual in = (Individual) record;
      String old = in.getID();
      String neu = indiNum.getProperty(old);
      in.setID(neu);

      // als Kind
      changeIDs(in.getSubRecords(Tag.FAMILY_AS_CHILD), famNum);
      changeIDs(in.getSubRecords(Tag.FAMILY_AS_SPOUSE), famNum);
    }

    //
    // Familien
    //
    for(Record record : data.getFamilies())
    {
      Family fam = (Family) record;
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

  private void changeIDs(List<Record> subRecords, Properties numbers)
  {
    String old;
    String neu;
    for(Record record : subRecords)
    {
      old = record.getContent();
      neu = numbers.getProperty(old);
      record.setContent(neu);
    }
  }

  private Properties findIds(List<Record> records, int startnum)
  {
    String prefix = null;
    Properties numbers = new Properties();
    for(Record rec : records)
    {
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
