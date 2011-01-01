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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Neue Klasse erstellt am 16.01.2005.
 * 
 * @author nigjo
 */
public final class GedFile implements Cloneable
{
  private Record root;
  private File gedfile;
  private int otherCount;
  private int individualCount;
  private int familyCount;
  private List<IDData> ids;

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

    root = new BaseRecord();
  }

  public GedFile(String infilename) throws FileNotFoundException, IOException
  {
    this(infilename, null);
  }

  public GedFile(String infilename, GedFileFilter f) throws
      FileNotFoundException,
      IOException
  {
    this(new File(infilename), f);
  }

  public GedFile(File infile) throws FileNotFoundException, IOException
  {
    this(infile, null);
  }

  public GedFile(File infile, GedFileFilter f) throws FileNotFoundException,
      IOException
  {
    this();
    read(infile, f);
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

  public void read(String infile, GedFileFilter f) throws FileNotFoundException,
      IOException
  {
    read(new File(infile), f);
  }

  public void read(File file) throws FileNotFoundException, IOException
  {
    read(file, null);
  }

  public final void read(File file, GedFileFilter filter)
      throws FileNotFoundException, IOException
  {
    setFile(file);

    // alte Daten loeschen
    root.clear();
    Record lastRecord = root;

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
        // WORKAROUND fuer defekte "UTF-8 mit BOM" Erkennung
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
    if(!lastRecord.isTag(Tag.END_OF_FILE_MARK))
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

      for(Record r : getIndividuals())
        r.print(out);

      for(Record r : getFamilies())
        r.print(out);

      Tag[] exclude =
      {
        Tag.HEAD, Tag.INDIVIDUAL, Tag.FAMILY, Tag.END_OF_FILE_MARK
      };
      for(Record r : getRecordsExcluded(exclude))
        r.print(out);

      out.println("0 " + Tag.END_OF_FILE_MARK); //$NON-NLS-1$

      out.close();
    }
    catch(IOException e)
    {
      Logger.getLogger(getClass().getName()).log(Level.FINE,
          e.toString(), e);
    }
  }

  public Record findRecord(Tag tag)
  {
    return findRecord(tag.toString());
  }

  public Record findRecord(String type)
  {
    if(root == null)
      return null;

    for(Record r : root)
    {
      if(type.equals(r.getType()))
        return r;
    }
    return null;
  }

  public Record findID(String id)
  {
    if(root == null)
      return null;

    for(Record r : root)
    {
      if(id.equals(r.getID()))
        return r;
    }
    return null;
  }

  public Individual findUID(String uid)
  {
    for(Record rec : getIndividuals())
    {
      Individual i = (Individual)rec;
      if(uid.equals(i.getUID()))
        return i;
    }
    return null;
  }

  public File getFile()
  {
    return this.gedfile;
  }

  public List<Record> getIndividuals()
  {
    return getRecords(Tag.INDIVIDUAL);
  }

  public List<Record> getFamilies()
  {
    return getRecords(Tag.FAMILY);
  }

  @Override
  public GedFile clone()
  {
    try
    {
      GedFile copy = (GedFile)super.clone();
      copy.gedfile = null;
      copy.otherCount = 0;
      copy.individualCount = 0;
      copy.familyCount = 0;

      // Beim Erstellen der Kopie werden die Zaehler aktualisiert
      copy.cloneBase(root);

      copy.ids = new ArrayList<IDData>();
      for(IDData nextElement : ids)
      {
        IDData clone = (IDData)nextElement.clone();
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
    root = new BaseRecord();
    for(Record rec : original)
      root.addSubRecord(rec.clone());
  }

  /**
   * prueft, ob ein Individuum in der Datei vorhanden ist.
   * 
   * @see Individual#equals(Object)
   */
  public boolean contains(Individual indi)
  {
    List<Record> indis = root.getSubRecords(Tag.INDIVIDUAL);
    for(Record record : indis)
    {
      if(indi.equals(record))
        return true;
    }
    return false;
  }

  public void sort()
  {
    root.sort();
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
  private List<Record> getRecords(Tag tag)
  {
    return getRecords(tag.toString());
  }

  private List<Record> getRecords(String type)
  {
    if(root == null)
      return Collections.emptyList();

    return root.getSubRecords(type);
  }

  /**
   * Lieferte alle Records der Datei exclusive der angegebenen.
   * 
   * @param exclude Feld mit Record-Typen
   */
  private List<Record> getRecordsExcluded(Tag... exclude)
  {
    String[] types = new String[exclude.length];
    for(int i = 0; i < exclude.length; i++)
    {
      types[i] = exclude[i].toString();
    }
    return getRecordsExcluded(types);
  }

  private List<Record> getRecordsExcluded(String... exclude)
  {
    if(root == null)
      return Collections.emptyList();
    List<String> tags = Arrays.asList(exclude);
    List<Record> harvester = new ArrayList<Record>();
    for(Record record : root)
    {
      if(!tags.contains(record.getType()))
        harvester.add(record);
    }
    return harvester;
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
  private class BaseRecord extends Record
  {
    @Override
    public Record clone()
    {
      BaseRecord kopie = (BaseRecord)super.clone();
      // kopie.BaseRecord.this = BaseRecord.this;
      return kopie;
    }

    @Override
    public GedFile getFile()
    {
      // Die eignetliche Rekursion der Record-Klasse darf hier nicht angewendet
      // werden und erfordert zwingend diese implementation, da sie die
      // Abbruchbedingung der Rekursion ist.
      return GedFile.this;
    }

    @Override
    public int getLevel()
    {
      // Record ist nicht sichtbar im eigentlichen Sinne
      return -1;
    }

    @Override
    public Record addSubRecord(Record rec)
    {
      Tag tag = rec.getTag();
      if(tag == null)
      {
        // Tag ist nicht definiert. Macht hier aber nichts.
        otherCount++;
      }
      else
      {
        switch(tag)
        {
          case FAMILY:
            familyCount++;
            rec = new Family(rec);
            break;

          case INDIVIDUAL:
            individualCount++;
            rec = new Individual(rec);
            break;

          default:
            otherCount++;
        }
      }
      return super.addSubRecord(rec);
    }

    @Override
    public void clear()
    {
      familyCount = 0;
      individualCount = 0;
      otherCount = 0;
      super.clear();
    }
  }

  /*
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

    @Override
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
      ids = new ArrayList<IDData>();
    for(IDData data : ids)
    {
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
    IDData data = findIdData(Tag.INDIVIDUAL);
    return data.next - 1;
  }

  public int getMaxFamilyId()
  {
    IDData data = findIdData(Tag.FAMILY);
    return data.next - 1;
  }

  public GedFileFilter updateFilter(GedFileFilter f)
  {
    f.learn(root);
    return f;
  }

  public void apply(GedFileFilter f)
  {
    f.apply(root);
  }

  /*
   * ==================================================
   * 
   * D E P R E C A T E D
   * 
   * ==================================================
   */
}
