package net.sourceforge.gedprint.gedcom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Ein Individuum.
 * 
 * @author nigjo
 */
public class Individual extends Record
{
  public static final int MAX_LIVING_AGE = 120;

  public Individual(String id)
  {
    super(Tag.INDIVIDUAL, id);
  }

  /** erstellt ein Individuum anhand eines vorhandenen Records.
   * 
   * @param rec
   */
  Individual(Record rec)
  {
    this(rec.getID());

    if(!rec.isTag(Tag.INDIVIDUAL))
      throw new InvalidDataException("not an Individual"); //$NON-NLS-1$

    setLevel(rec.getLevel());
    setContent(rec.getContent());
    addSubRecords(rec.asList());
  }

  public String getFullName()
  {
    Record name = getSubRecord(Tag.NAME);

    if(name.getContent() != null)
      return name.getContent();

    StringBuilder buf = new StringBuilder();

    if(name.getSubRecord(Tag.NAME_PREFIX) != null)
      buf.append(name.getSubRecord(Tag.NAME_PREFIX));

    if(name.getSubRecord(Tag.GIVEN_NAME) != null)
    {
      if(buf.length() > 0)
        buf.append(' ');
      buf.append(name.getSubRecord(Tag.GIVEN_NAME));
    }
    if(name.getSubRecord(Tag.SURNAME) != null)
    {
      if(buf.length() > 0)
        buf.append(' ');
      buf.append('/');
      buf.append(name.getSubRecord(Tag.SURNAME).toString());
      buf.append('/');
    }
    if(buf.length() > 0)
      return buf.toString();
    return "?"; //$NON-NLS-1$
  }

  public String getClearedFullName()
  {
    String full = getFullName();
    return full.replaceAll("/", ""); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public boolean isDead()
  {
    if(getSubRecord(Tag.DEATH) != null)
      return true;

    return getAge() > MAX_LIVING_AGE;
  }

  public int getAge()
  {
    int age = -1;
    Record birth = getSubRecord(Tag.BIRTH);

    if(birth != null)
    {
      Calendar bDate = null;
      Calendar dDate = null;
      if(birth.getSubRecord(Tag.DATE) != null)
        bDate = parseDate(birth.getSubRecord(Tag.DATE).getContent());
      if(getSubRecord(Tag.DEATH) != null)
      {
        Record death = getSubRecord(Tag.DEATH);
        if(death.getSubRecord(Tag.DATE) != null)
          dDate = parseDate(death.getSubRecord(Tag.DATE).getContent());
      }

      if(bDate != null)
        age = calcYears(bDate, dDate);
    }

    return age;
  }

  public Date getBirthDate()
  {
    return getDate(Tag.BIRTH);
  }

  public Date getDeathDate()
  {
    return getDate(Tag.DEATH);
  }

  private Date getDate(Tag tag)
  {
    Record birth = getSubRecord(tag);
    if(birth == null)
      return null;
    Calendar bDate = null;
    if(birth.getSubRecord(Tag.DATE) != null)
      bDate = parseDate(birth.getSubRecord(Tag.DATE).getContent());
    if(bDate == null)
      return null;
    return bDate.getTime();
  }

  /** prueft, ob es sich um zwei gleiche Individuen handelt.
   *  Die Gleichheit wird nur anhand einer UID geprueft. Ist bei einem der
   *  Personen keine UID vorhanden gelten sie nicht als gleich.
   */
  @Override
  public boolean equals(Object obj)
  {
    if(super.equals(obj))
      return true;
    if(obj instanceof Individual)
    {
      Individual i = (Individual)obj;
      if(getUID() != null && i.getUID() != null)
      {
        return getUID().equals(i.getUID());
      }
      else if(getID() != null && i.getID() != null)
      {
        return getID().equals(i.getID());
      }
    }

    return false;
  }

  /** liefert die UID des Eintrags.
   * 
   * @return <code>null</code>, falls keine UID fuer das Individuum gesetzt
   *         wurde.
   */
  public String getUID()
  {
    Record uid = getSubRecord(Tag.UID);
    if(uid != null)
      return uid.getContent();
    return null;
  }

  /** Berechnet den Jahresunterschied zwischen zwei Daten.
   * 
   * @param bDate "Geburtsdatum"
   * @param dDate "Sterbedatum". Ist dieser Parameter null, wird das aktuelle
   *        Datum als Referenz verwendet.
   * @return
   */
  private int calcYears(Calendar bDate, Calendar dDate)
  {
    int age = 0;
    //    $birthday=date("U",strtotime($month."/".$day."/".$year));
    if(dDate == null)
      dDate = Calendar.getInstance();
    //    $age=date("Y",time())-date("Y",$birthday);
    age = dDate.get(Calendar.YEAR) - bDate.get(Calendar.YEAR);
    // Checks to see if birthday has occured this year. If not subtract 1 from age
    //    if(date("z",time())<date("z",$birthday)) 
    if(dDate.get(Calendar.DAY_OF_YEAR) < bDate.get(Calendar.DAY_OF_YEAR))
      // $age-=1; 
      age--;
    //    echo $age;
    return age;
  }

  public Individual getDataFather()
  {
    Family famc = getDataChildFamily();
    return famc.getHusband();
  }

  public Individual getDataMother()
  {
    Family famc = getDataChildFamily();
    return famc.getWife();
  }

  /**
   * liefert die Familie als Kind.
   */
  public Family getDataChildFamily()
  {
    Record subRecord = getSubRecord(Tag.FAMILY_AS_CHILD);
    subRecord = findID(subRecord.getContent());
    return (Family)subRecord;

  }

  public List<Family> getDataSpouceFamilies()
  {
    List<Record> subRecords = getSubRecords(Tag.FAMILY_AS_SPOUSE);
    List<Family> fams = new ArrayList<Family>();
    for(Record record : subRecords)
      fams.add(findID(record.getContent(), Family.class));

    return fams;
  }

}
