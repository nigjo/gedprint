package net.sourceforge.gedprint.gedcom;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.gedprint.core.AbstractEnumeration;
import net.sourceforge.gedprint.core.EmptyEnumeration;
import net.sourceforge.gedprint.core.Filter;

/**
 * Neue Klasse erstellt am 16.01.2005.
 * 
 * @author nigjo
 */
public class GedFile implements Cloneable
{
  Record records;

  private File gedfile;

  private int otherCount;

  private int individualCount;

  private int familyCount;

  private Vector<IDData> ids;

  /*****************************************************************************
   * ==================================================
   * 
   * K O N S T R U K T O R E N
   * 
   * ==================================================
   */

  public GedFile()
  {
    super();

    records = new BaseRecord();
  }

  public GedFile(String infilename) throws FileNotFoundException, IOException
  {
    this(infilename, null);
  }

  public GedFile(String infilename, Filter f) throws FileNotFoundException,
      IOException
  {
    this();
    read(infilename, f);
  }

  /*****************************************************************************
   * ==================================================
   * 
   * P U B L I C
   * 
   * ==================================================
   */
  public int getIndividualCount()
  {
    return individualCount;
  }

  public int getFamilyCount()
  {
    return familyCount;
  }

  public int getOtherCount()
  {
    return otherCount;
  }

  public void read(String infile) throws FileNotFoundException, IOException
  {
    read(infile, null);
  }

  public void read(String infile, Filter f) throws FileNotFoundException,
      IOException
  {
    read(new File(infile), f);
  }

  public void read(File file) throws FileNotFoundException, IOException
  {
    read(file, null);
  }

  public void read(File file, Filter filter) throws FileNotFoundException,
      IOException
  {
    setFile(file);

    // alte Daten loeschen
    records.clear();
    Record lastRecord = records;

    // Datei als UTF-8 Datei oeffnen...
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(getFile()), "UTF-8")); //$NON-NLS-1$

    // ... und einlesen
    String zeile;
    boolean firstline = true;
    while(null != (zeile = in.readLine()))
    {
      if(firstline)
      {
        firstline = false;
        // WORKAROUND fuer defekte UTF-8 mit BOM Erkennung
        // Siehe Bug ID 4508058
        char firstchar = zeile.charAt(0);
        if(firstchar == 0xFEFF)
          zeile = zeile.substring(1);
      }

      zeile = zeile.trim();
      if(zeile.length() > 0)
      {
        // Einfache Syntax-Pruefungen
        Record r = new Record();
        if(zeile.charAt(1) != ' ')
          throw new InvalidSyntaxException("level wrong"); //$NON-NLS-1$
        r.setLevel(zeile.charAt(0) - '0');

        // ID filtern
        int pos = 2;
        if(zeile.charAt(pos) == '@')
        {
          int wo = zeile.indexOf(' ', pos);
          r.setID(zeile.substring(2, wo));
          pos = wo + 1;
        }

        int wo = zeile.indexOf(' ', pos);
        if(wo < 0)
        {
          r.setType(zeile.substring(pos));
        }
        else
        {
          r.setType(zeile.substring(pos, wo));
          r.setContent(zeile.substring(wo + 1));
        }

        // Zeile eintragen
        lastRecord = lastRecord.addSubRecord(r);
      }
    }

    in.close();

    // Dateiende pruefen
    if(!Tag.TRLR.equals(lastRecord.getType()))
    {
      throw new InvalidSyntaxException("missing trailer"); //$NON-NLS-1$
    }
  }

  public void write(String outfilename)
  {
    write(outfilename, false);
  }

  public void write(String outfilename, boolean utf8)
  {
    write(new File(outfilename), utf8);
  }

  public void write(File outfile)
  {
    write(outfile, false);
  }

  public void write(File outfile, boolean utf8)
  {
    try
    {
      PrintStream out;
      if(utf8)
      {
        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(
            outfile)), false, "UTF-8"); //$NON-NLS-1$
      }
      else
      {
        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(
            outfile)));
      }

      findRecord(Tag.HEAD).print(out);

      Enumeration e;
      e = getIndividuals();
      while(e.hasMoreElements())
      {
        Record r = (Record) e.nextElement();
        r.print(out);
      }

      e = getFamilies();
      while(e.hasMoreElements())
      {
        Record r = (Record) e.nextElement();
        r.print(out);
      }

      Tag[] exclude =
      { Tag.HEAD, Tag.INDI, Tag.FAM, Tag.TRLR
      };
      e = getRecordsExcluded(exclude);
      while(e.hasMoreElements())
      {
        Record r = (Record) e.nextElement();
        r.print(out);
      }

      out.println("0 " + Tag.TRLR); //$NON-NLS-1$

      out.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public Record findRecord(Tag tag)
  {
    return findRecord(tag.toString());
  }

  public Record findRecord(String type)
  {
    if(records == null)
      return null;

    Enumeration e = records.elements();
    while(e.hasMoreElements())
    {
      Record r = (Record) e.nextElement();
      if(type.equals(r.getType()))
        return r;
    }
    return null;
  }

  public Record findID(String id)
  {
    if(records == null)
      return null;

    Enumeration e = records.elements();
    while(e.hasMoreElements())
    {
      Record r = (Record) e.nextElement();
      if(id.equals(r.getID()))
        return r;
    }
    return null;
  }

  public Individual findUID(String uid)
  {
    Enumeration e = getIndividuals();
    while(e.hasMoreElements())
    {
      Individual i = (Individual) e.nextElement();
      if(uid.equals(i.getUID()))
        return i;
    }
    return null;
  }

  public File getFile()
  {
    return this.gedfile;
  }

  public Enumeration getIndividuals()
  {
    return getRecords(Tag.INDI);
  }

  public Enumeration getFamilies()
  {
    return getRecords(Tag.FAM);
  }

  public Object clone()
  {
    try
    {
      GedFile copy = (GedFile) super.clone();
      copy.gedfile = null;
      copy.otherCount = 0;
      copy.individualCount = 0;
      copy.familyCount = 0;

      // Beim Erstellen der Kopie werden die Zaehler aktualisiert
      copy.cloneBase(records);

      copy.ids = new Vector<IDData>();
      Enumeration<IDData> iddata = ids.elements();
      while(iddata.hasMoreElements())
      {
        IDData nextElement = iddata.nextElement();
        IDData clone = (IDData) nextElement.clone();
        copy.ids.add(clone);
      }

      return copy;
    }
    catch(CloneNotSupportedException e)
    {
    }
    return null;
  }

  private void cloneBase(Record original)
  {
    // records = (Record) original.clone();
    records = new BaseRecord();
    Enumeration recs = original.elements();
    while(recs.hasMoreElements())
    {
      Record rec = (Record) recs.nextElement();
      records.addSubRecord((Record) rec.clone());
    }
  }

  /**
   * prueft, ob ein Individuum in der Datei vorhanden ist.
   * 
   * @see Individual#equals(Object)
   */
  public boolean contains(Individual indi)
  {
    Record[] indis = records.getSubRecords(Tag.INDI);
    for(int i = 0; i < indis.length; i++)
    {
      if(indi.equals(indis[i]))
        return true;
    }
    return false;
  };

  public void sort()
  {
    records.sort();
  }

  /*****************************************************************************
   * ==================================================
   * 
   * P R O T E C T E D
   * 
   * ==================================================
   */
  protected void setFile(File file)
  {
    this.gedfile = file;
  }

  /*****************************************************************************
   * ==================================================
   * 
   * P R I V A T E
   * 
   * ==================================================
   */
  /**
   * liefert alle Records der Datei mit dem angegeben Typ.
   */
  private Enumeration getRecords(Tag tag)
  {
    return getRecords(tag.toString());
  }

  private Enumeration getRecords(final String type)
  {

    if(records == null)
      return new EmptyEnumeration();
    return new AbstractEnumeration() {
      String searchType = type.toUpperCase();
      Enumeration e = records.elements();

      protected void findNextElement()
      {
        index = RUNNING;
        while(index == RUNNING && e.hasMoreElements())
        {
          Record r = (Record) e.nextElement();
          if(searchType.equals(r.getType()))
          {
            nextElement = r;
            return;
          }
        }
        nextElement = null;
        index = STOP;
      }
    };
  }

  /**
   * Lieferte alle Records der Datei exclusive der angegebenen.
   * 
   * @param exclude Feld mit Record-Typen
   */
  private Enumeration getRecordsExcluded(Tag[] exclude)
  {
    String[] types = new String[exclude.length];
    for(int i=0;i<exclude.length;i++){
      types[i]=exclude[i].toString();
    }
    return getRecordsExcluded(types);
  }
  private Enumeration getRecordsExcluded(final String[] exclude)
  {
    if(records == null)
      return new EmptyEnumeration();
    return new AbstractEnumeration() {
      Enumeration e = records.elements();

      protected void findNextElement()
      {
        index = RUNNING;
        while(index == RUNNING && e.hasMoreElements())
        {
          Record r = (Record) e.nextElement();
          for(int i = 0; i < exclude.length; i++)
          {
            if(exclude[i].equals(r.getType()))
            {
              // Schleife abbrechen
              r = null;
              break;
            }
          }
          if(r != null)
          {
            // keiner der Typen passte.
            nextElement = r;
            return;
          }
        }
        nextElement = null;
        index = STOP;
      }
    };
  }

  /*****************************************************************************
   * ==================================================
   * 
   * I N N E R E K L A S S E N
   * 
   * ==================================================
   */
  /**
   * Spezial-Record, der die Wurzel der ganzen Record repraesentiert. Dieser
   * Record ist dringend notwendig, da er einige Daten des eigentlichen GedFile
   * steuert.
   */
  class BaseRecord extends Record
  {
    public Object clone()
    {
      BaseRecord kopie = (BaseRecord) super.clone();
      // kopie.BaseRecord.this = BaseRecord.this;
      return kopie;
    }

    public GedFile getFile()
    {
      // Die eignetliche Rekursion der Record-Klasse darf hier nicht angewendet
      // werden und erfordert zwingend diese implementation, da sie die
      // Abbruchbedingung der Rekursion ist.
      return GedFile.this;
    }

    public int getLevel()
    {
      // Record ist nicht sichtbar im eigentlichen Sinne
      return -1;
    }

    public Record addSubRecord(Record rec)
    {
      switch(rec.getTag())
      {
      case FAM:
        familyCount++;
        rec = new Family(rec);
        break;

      case INDI:
        individualCount++;
        rec = new Individual(rec);
        break;

      default:
        otherCount++;
      }
      return super.addSubRecord(rec);
    }

    public void clear()
    {
      familyCount = 0;
      individualCount = 0;
      otherCount = 0;
      super.clear();
    }

  }

  /*****************************************************************************
   * ==================================================
   * 
   * IDs
   * 
   * ==================================================
   */
  private class IDData implements Cloneable
  {
    String type;
    String prefix;
    int next;

    protected Object clone()
    {
      try
      {
        return super.clone();
      }
      catch(CloneNotSupportedException e)
      {
      }
      return null;
    }
  }

  private IDData findIdData(Tag tag)
  {
    return findIdData(tag.toString());
  }

  private IDData findIdData(String type)
  {
    if(ids == null)
      ids = new Vector<IDData>();
    Enumeration e = ids.elements();
    while(e.hasMoreElements())
    {
      IDData data = (IDData) e.nextElement();
      if(data.type.equals(type))
        return data;
    }

    IDData data = new IDData();
    data.type = type;
    data.prefix = null;
    data.next = 1;
    ids.add(data);
    return data;
  }

  // static String id_prefix = null;
  // static int nextID = 1;
  public void registerID(Record record)
  {
    IDData data = findIdData(record.getType());

    String id = record.getIDCleared();

    if(data.prefix == null)
    {
      String[] id2 = id.split("\\d"); //$NON-NLS-1$
      data.prefix = id2[0];
    }

    if(!id.startsWith(data.prefix))
      throw new InvalidSyntaxException("Invalid Prefix for " + id); //$NON-NLS-1$

    String num = id.substring(data.prefix.length());
    int id_num = Integer.parseInt(num);
    if(id_num >= data.next)
      data.next = id_num + 1;
  }

  public String getFreeID(String type)
  {
    IDData data = findIdData(type);
    return '@' + data.prefix + (data.next++) + '@';
  }

  public int getMaxIndividualId()
  {
    IDData data = findIdData(Tag.INDI);
    return data.next - 1;
  }

  public int getMaxFamilyId()
  {
    IDData data = findIdData(Tag.FAM);
    return data.next - 1;
  }

  public Filter updateFilter(Filter f)
  {
    f.learn(records);
    return f;
  }

  public void apply(Filter f)
  {
    f.apply(records);
  }

  /*****************************************************************************
   * ==================================================
   * 
   * D E P R E C A T E D
   * 
   * ==================================================
   */
}

//
// CVS-Protokoll
//
// $Log$
//