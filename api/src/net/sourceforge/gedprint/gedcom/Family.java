package net.sourceforge.gedprint.gedcom;

import java.util.List;
import java.util.ArrayList;

/**
 * Eine Familie.
 * 
 * @author nigjo
 */
public class Family extends Record
{
  public Family(String id)
  {
    super(Tag.FAMILY, id);
  }

  /**
   * erstellt eine Familie anhand eines vorhandenen Records.
   * 
   * @param rec
   */
  Family(Record rec)
  {
    this(rec.getID());

    if(!rec.isTag(Tag.FAMILY))
      throw new InvalidDataException("not a Family"); //$NON-NLS-1$

    setLevel(rec.getLevel());
    setContent(rec.getContent());
    addSubRecords(rec.asList());
  }

  public Individual getHusband()
  {
    Record husb = getSubRecord(Tag.HUSBAND);
    if(husb != null)
      return (Individual) findID(husb.getContent());
    return null;
  }

  public Individual getWife()
  {
    Record husb = getSubRecord(Tag.WIFE);
    if(husb != null)
      return (Individual) findID(husb.getContent());
    return null;
  }

  /**
   * liefert die Records der Kinder.
   */
  public List<Individual> getChildren()
  {
    List<Record> childRefs = getSubRecords(Tag.CHILDREN);
    List<Individual> children = new ArrayList<Individual>();
    for(Record record : childRefs)
      children.add(findID(record.getContent(), Individual.class));
    return children;
  }

  /**
   * liefert die Anzahl der Kinder der Familie
   */
  public int getChildrenCount()
  {
    return getSubRecordCount(Tag.CHILDREN);
  }

  public List<Family> getChildFamilies()
  {
    return getChildFamilies(false);
  }

  public List<Family> getChildFamilies(boolean deepSearch)
  {
    List<Family> harvester = new ArrayList<Family>();
    List<Individual> children = getChildren();
    for(Individual child : children)
    {
      List<Record> spouseFamilies = child.getSubRecords(Tag.FAMILY_AS_SPOUSE);
      for(Record record : spouseFamilies)
      {
        harvester.add(findID(record.getContent(), Family.class));
      }
    }

    if(deepSearch)
    {
      List<Family> parents = new ArrayList<Family>(harvester);
      for(Family family : parents)
      {
        List<Family> childFamilies = family.getChildFamilies(true);

        // doppelte Elemente suchen und entfernen
        List<Family> doubles = new ArrayList<Family>(childFamilies);
        doubles.retainAll(harvester);
        childFamilies.removeAll(doubles);

        // Rest dem harvester hinzufuegen
        harvester.addAll(childFamilies);
      }
    }

    return harvester;
  }
}
