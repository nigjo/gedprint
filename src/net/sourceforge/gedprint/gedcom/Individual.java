package net.sourceforge.gedprint.gedcom;

import java.util.Calendar;

/** Ein Individuum.
 * 
 * @author nigjo
 */
public class Individual extends Record
{
  public static final int MAX_LIVING_AGE = 120;

  public Individual(String id)
  {
    super(Tag.INDI, id);
  }

  /** erstellt ein Individuum anhand eines vorhandenen Records.
   * 
   * @param rec
   */
  Individual(Record rec)
  {
    this(rec.getID());

    if (!rec.isTag(Tag.INDI))
      throw new InvalidDataException("not an Individual"); //$NON-NLS-1$

    setLevel(rec.getLevel());
    setContent(rec.getContent());
    addSubRecords(rec.elements());
  }

  public String getFullName()
  {
    Record name = getSubRecord(Tag.NAME);

    if (name.getContent() != null)
      return name.getContent();

    StringBuffer buf = new StringBuffer();

    if (name.getSubRecord(Tag.NAME_PREFIX) != null)
      buf.append(name.getSubRecord(Tag.NAME_PREFIX));

    if (name.getSubRecord(Tag.GIVEN_NAME) != null)
    {
      if (buf.length() > 0)
        buf.append(' ');
      buf.append(name.getSubRecord(Tag.GIVEN_NAME));
    }
    if (name.getSubRecord(Tag.SURNAME) != null)
    {
      if (buf.length() > 0)
        buf.append(' ');
      buf.append('/' + name.getSubRecord(Tag.SURNAME).toString() + '/');
    }
    if (buf.length() > 0)
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
    if (getSubRecord(Tag.DEAT) != null)
      return true;

    return getAge() > MAX_LIVING_AGE;
  }

  public int getAge()
  {
    int age = -1;
    Record birth = getSubRecord(Tag.BIRTH);

    if (birth != null)
    {
      Calendar bDate = null;
      Calendar dDate = null;
      if (birth.getSubRecord(Tag.DATE) != null)
        bDate = parseDate(birth.getSubRecord(Tag.DATE).getContent());
      if (getSubRecord(Tag.DEAT) != null)
      {
        Record death = getSubRecord(Tag.DEAT);
        if (death.getSubRecord(Tag.DATE) != null)
          dDate = parseDate(death.getSubRecord(Tag.DATE).getContent());
      }

      if (bDate != null)
        age = calcYears(bDate, dDate);
    }

    return age;
  }

  /** prueft, ob es sich um zwei gleiche Individuen handelt.
   *  Die Gleichheit wird nur anhand einer UID geprueft. Ist bei einem der
   *  Personen keine UID vorhanden gelten sie nicht als gleich.
   */
  public boolean equals(Object obj)
  {
    if (obj instanceof Individual)
    {
      Individual i = (Individual) obj;
      if (getUID() != null && i.getUID() != null)
      {
        return getUID().equals(i.getUID());
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
    if (uid != null)
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
    if (dDate == null)
      dDate = Calendar.getInstance();
    //    $age=date("Y",time())-date("Y",$birthday);
    age = dDate.get(Calendar.YEAR) - bDate.get(Calendar.YEAR);
    // Checks to see if birthday has occured this year. If not subtract 1 from age
    //    if(date("z",time())<date("z",$birthday)) 
    if (dDate.get(Calendar.DAY_OF_YEAR) < bDate.get(Calendar.DAY_OF_YEAR))
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
    Record subRecord = getSubRecord(Tag.FAMC);
    subRecord = findID(subRecord.getContent());
    return (Family)subRecord;
    
  }

  public Family[] getDataSpouceFamilies()
  {
    Record[] subRecords = getSubRecords(Tag.FAM_SPOUSE);
    Family[] fams = new Family[subRecords.length];
    for(int i=0;i<subRecords.length;i++){
      fams[i]=(Family) findID(subRecords[i].getContent());
    }
    return fams;
  }
}
