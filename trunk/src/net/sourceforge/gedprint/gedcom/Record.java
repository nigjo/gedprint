package net.sourceforge.gedprint.gedcom;

import java.io.PrintStream;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Eine Zeile innhalb einer Gedcom-Datei. Alle Folgezeilen der Gedcom-Datei mit
 * einem hoeheren Level als der Eintrag selber, werden als "entries" im Objekt
 * mitgespeichert.
 * 
 * @author nigjo
 */
public class Record implements Cloneable, Comparable<Record>
{
  private static String[] months;

  private Record parent;

  private Vector<Record> entries;

  private String type;

  private String content;

  private String ID;

  private int level = -1;

  public Record()
  {
    super();
  }

  protected Record(Tag type, String id)
  {
    this(type.toString(), id);
  }

  protected Record(String type, String id)
  {
    this();
    setType(type);
    setID(id);
  }

  public Enumeration elements()
  {
    if(entries != null)
      return entries.elements();
    return new Enumeration() {
      public boolean hasMoreElements()
      {
        return false;
      }

      public Object nextElement()
      {
        throw new NoSuchElementException();
      }
    };
  }

  /**
   * liefert den ersten Untereintrag mit dem gesuchten Typ.
   * 
   * @return <code>null</code>, falls kein Record gefunden wurde.
   */
  public Record getSubRecord(Tag tag)
  {
    return getSubRecord(tag.toString());
  }

  public Record getSubRecord(String type)
  {
    if(type.indexOf('/') > 0)
    {
      String[] strings = type.split("/", 2); //$NON-NLS-1$
      Record sub = getSubRecord(strings[0]);
      if(sub != null)
        return sub.getSubRecord(strings[1]);
    }
    else
    {
      Enumeration e = elements();
      while(e.hasMoreElements())
      {
        Record r = (Record) e.nextElement();
        if(r.getType().equals(type))
          return r;
      }
    }
    return null;
  }

  /**
   * liefert alle Untereintraege mit dem gesuchten Typ.
   * 
   * @return Es wird auf jeden Fall ein Feld zurueckgeliefert. Enthaelt der
   *         Record keine Untereintraege vom gesuchten Typ hat das Feld die
   *         Laenge 0.
   */
  public Record[] getSubRecords(Tag tag)
  {
    return getSubRecords(tag.toString());
  }

  public Record[] getSubRecords(String type)
  {
    Vector<Record> found = new Vector<Record>();
    Enumeration e = elements();
    while(e.hasMoreElements())
    {
      Record r = (Record) e.nextElement();
      if(r.getType().equals(type))
        found.add(r);
    }
    return found.toArray(new Record[found.size()]);
  }

  public int getSubRecordCount(Tag tag)
  {
    return getSubRecordCount(tag.toString());
  }

  public int getSubRecordCount(String string)
  {
    Enumeration e = elements();
    int counter = 0;
    while(e.hasMoreElements())
    {
      Record r = (Record) e.nextElement();
      if(r.getType().equals(string))
        counter++;
    }

    return counter;
  }

  /**
   * fuegt ein Untereintrag dem Record hinzu. Ist der Level des einzutragenden
   * Records kleiner oder gleich des Records zu dem der Untereintrag gemacht
   * werden soll, wird der Untereintrag automatisch an die naechtshoehere Ebene
   * weitergereicht. So wird auf jeden Fall die Struktur der GEDCOM-Datei
   * erhalten.
   * 
   * @throws InvalidDataException falls der Level des Untereintrags um mehr als
   *           ein Level hoeher ist als der Basisrecord. Man kann kein Record
   *           mit dem Level 4 zu einem Record mit dem Level 2 hinzufuegen.
   * @return Eingetragener Record. Dies muss nicht zwangslaeufig das gleiche
   *         Objekt sein, wie <tt>rec</tt>.
   */
  public Record addSubRecord(Record rec)
  {
    if(rec.getLevel() <= getLevel())
      return getParent().addSubRecord(rec);

    if(rec.getLevel() > getLevel() + 1)
      throw new InvalidDataException("Level jump"); //$NON-NLS-1$

    if(entries == null)
      entries = new Vector<Record>();

    if(!entries.contains(rec))
    {
      entries.add(rec);
      rec.setParent(this);
      rec.setLevel(getLevel() + 1);
      if(rec.getID() != null)
        getFile().registerID(rec);
    }

    return rec;
  }

  public void addSubRecords(Enumeration recs)
  {
    while(recs.hasMoreElements())
      addSubRecord((Record) recs.nextElement());
  }

  /**
   * sucht eine bestimmte ID in der zugehoerigen Gedcom-Datei.
   * 
   * @param id gesuchte ID. Es wird nicht nach Individuum-, Familien- oder
   *          sonstiger ID unterschieden. Der Parameter kann mit und auch ohne
   *          die umschliessenden "@"-Zeichen uebergeben werden. Sie werden bei
   *          bedarf hinzugefuegt.
   * @return Record mit der gesuchten ID oder <code>null</code>, falls die ID
   *         nicht gefunden werden konnte.
   */
  public Record findID(String id)
  {
    if(getFile() != null)
    {
      String AT = "@"; //$NON-NLS-1$
      if(!id.startsWith(AT))
        id = AT + id;
      if(!id.endsWith(AT))
        id = id + AT;
      return getFile().findID(id);
    }
    return null;
  }

  /**
   * liefert das Dateiobjekt dieses Eintrags.
   */
  public GedFile getFile()
  {
    Record rec = getParent();
    if(rec == null)
      return null;
    return rec.getFile();
  }

  public Record getParent()
  {
    if(parent != null)
      return parent;
    return null;
  }

  public String getContent()
  {
    return content;
  }

  public String getID()
  {
    return ID;
  }

  public String getIDCleared()
  {
    return clearID(getID());
  }

  public static String clearID(String id)
  {
    return id.replaceAll("@", ""); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public int getIDNumber()
  {
    String id = getID();
    if(id == null)
      return -1;
    String[] strings = id.split("\\D+"); //$NON-NLS-1$
    return Integer.parseInt(strings[1]);
  }

  public String getIDPrefix()
  {
    String id = getID();
    if(id == null)
      return null;
    String[] strings = id.split("[@\\d]+"); //$NON-NLS-1$
    return strings[1];
  }

  public int getLevel()
  {
    return level;
  }

  public String getType()
  {
    return type;
  }

  public void setContent(String string)
  {
    content = string;
  }

  public void setID(String string)
  {
    ID = string;
  }

  public void setLevel(int i)
  {
    level = i;
  }

  public void setParent(Record record)
  {
    parent = record;
  }

  public void setType(String string)
  {
    type = string;
  }

  public void clear()
  {
    if(entries != null)
    {
      Enumeration e = entries.elements();
      while(e.hasMoreElements())
      {
        ((Record) e.nextElement()).clear();
      }
      entries.removeAllElements();
    }
  }

  /** liefert den Typ und die ID des Records */
  @Override
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append(getType());
    if(getID() != null)
      buf.append(" (" + getID() + ')'); //$NON-NLS-1$
    return buf.toString();
  }

  /**
   * schreibt den Record und alle seine Untereintraege in eine GEDCOM-Datei.
   * 
   * @param out Ausgabestrom der GEDCOM-Datei.
   */
  public void print(PrintStream out)
  {
    String space = String.valueOf(' ');
    out.print(level + space);
    if(level == 0 && ID != null)
      out.print(ID + space);

    out.print(type);

    if(content != null)
      out.println(space + content);
    else
      out.println();

    if(entries != null)
    {
      Enumeration e = entries.elements();
      while(e.hasMoreElements())
        ((Record) e.nextElement()).print(out);
    }
  }

  /**
   * erstellt eine Kopie des Records und aller seiner Untereintraege.
   */
  @Override
  public Object clone()
  {
    try
    {
      Record r = (Record) super.clone();

      r.parent = null;

      if(this.type != null)
        r.type = new String(this.type);
      if(this.content != null)
        r.content = new String(this.content);
      if(this.ID != null)
        r.ID = new String(this.ID);
      r.level = this.level;

      if(entries != null)
      {
        r.entries = new Vector<Record>();

        Enumeration<Record> e = entries.elements();
        while(e.hasMoreElements())
          r.entries.add((Record) e.nextElement().clone());
      }

      return r;
    }
    catch(CloneNotSupportedException e)
    {
      return null;
    }
  }

  public int compareTo(Record o)
  {
    if(level == o.level)
    {
      if(type.equals(o.type))
      {
        try
        {
          int i1 = getIDNumber();
          int i2 = o.getIDNumber();

          return i1 - i2;
        }
        catch(Exception e)
        {
          String id1 = getID();
          String id2 = o.getID();
          if(id1 != null && id2 != null)
            return id1.compareTo(id2);
        }

        if(getContent() != null && o.getContent() != null)
          return getContent().compareTo(o.getContent());

        return 0;
      }

      return type.compareTo(o.type);
    }

    return level < o.level ? -1 : 1;
  }

  public static Calendar parseDate(String geddate)
  {
    Calendar c = Calendar.getInstance(Locale.ENGLISH);

    if(geddate.matches("(((\\d{1,2})\\.?)? ([A-Za-z]{3})? (\\d{3,4}))")) //$NON-NLS-1$
    {
      String[] date = geddate.split(" "); //$NON-NLS-1$
      if(months == null)
      {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.ENGLISH);
        String[] shortMonths = symbols.getShortMonths();

        String mString = Arrays.toString(shortMonths);
        String upper = mString.toUpperCase();
        months = upper.substring(1).split("[], ]+"); //$NON-NLS-1$
      }

      boolean found = false;
      switch(date.length)
      {
      case 1:
        c.set(Calendar.YEAR, Integer.parseInt(date[0]));
        found = true;
        break;
      case 2:
        for(int i = 0; i < months.length; i++)
        {
          if(months[i].equals(date[0].toUpperCase()))
          {
            c.set(Calendar.MONTH, i);
            c.set(Calendar.YEAR, Integer.parseInt(date[1]));
            found = true;
            break;
          }
        }
        break;
      case 3:
        for(int i = 0; i < months.length; i++)
        {
          if(months[i].equals(date[1].toUpperCase()))
          {
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
            c.set(Calendar.MONTH, i);
            c.set(Calendar.YEAR, Integer.parseInt(date[2]));
            found = true;
            break;
          }
        }
        break;
      }

      if(!found)
        System.out.println(geddate);
    }

    return c;
  }

  public boolean delSubRecord(Record record)
  {
    if(entries == null)
      return false;
    if(!entries.contains(record))
      return false;
    return entries.remove(record);
  }

  public void sort()
  {
    if(entries == null)
      return;

    Collections.sort(entries);
  }

  /** @deprecated */
  public String getClearedID()
  {
    return getIDCleared();
  }

  /**
   * liefert den Tag des Records.
   * 
   * @return liefert <code>null</code>, falls der Tag nicht definiert ist.
   *         Das bedeutet aber nicht, dass der Record keinen Typ hat. Es sollte
   *         dann {@link #getType()} verwendet werden um den Record zu
   *         identifizieren.
   * @see #getType()
   */
  public Tag getTag()
  {
    String rtype = getType();
    for(Tag tag : Tag.values())
    {
      if(rtype.equals(tag.toString()))
        return tag;
    }
    return null;
  }

  public boolean isTag(Tag tag)
  {
    String rtype = getType();
    if(rtype == null || tag == null)
      return false;
    return tag.toString().equals(rtype);
  }

}
