package net.sourceforge.gedprint.gedcom;

import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.gedprint.core.AbstractEnumeration;
import net.sourceforge.gedprint.core.EmptyEnumeration;

/**
 * Eine Familie.
 * 
 * @author nigjo
 */
public class Family extends Record
{
  public Family(String id)
  {
    super(Tag.FAM, id);
  }

  /**
   * erstellt eine Familie anhand eines vorhandenen Records.
   * 
   * @param rec
   */
  Family(Record rec)
  {
    this(rec.getID());

    if(!rec.isTag(Tag.FAM))
      throw new InvalidDataException("not a Family"); //$NON-NLS-1$

    setLevel(rec.getLevel());
    setContent(rec.getContent());
    addSubRecords(rec.elements());
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
  public Enumeration<Record> getChildren()
  {
    final Record[] children = getSubRecords(Tag.CHILDREN);
    if(children.length > 0)
    {
      return new AbstractEnumeration<Record>() {
        protected void findNextElement()
        {
          if(index == START)
            index = 0;
          if(index >= children.length)
            index = STOP;
          else
          {
            String childID = children[index++].getContent();
            nextElement = findID(childID);
          }
        }
      };
    }
    else
      return new EmptyEnumeration<Record>();
  }

  /**
   * liefert die Anzahl der Kinder der Familie
   */
  public int getChildrenCount()
  {
    return getSubRecordCount(Tag.CHILDREN);
  }

  public Vector<Family> getChildFamilies()
  {
    return getChildFamilies(false);
  }

  public Vector<Family> getChildFamilies(boolean deepSearch)
  {
    Vector<Family> harvester = new Vector<Family>();
    Enumeration<Record> children = getChildren();
    while(children.hasMoreElements())
    {
      Record child = children.nextElement();
      Record[] spouseFamilies = child.getSubRecords(Tag.FAM_SPOUSE);
      for(int i = 0; i < spouseFamilies.length; i++)
      {
        harvester.add((Family) findID(spouseFamilies[i].getContent()));
      }
    }

    if(deepSearch)
    {
      Object clone = harvester.clone();
      @SuppressWarnings("unchecked")
      Vector<Family> parents = (Vector<Family>) clone;
      for(Family family : parents)
      {
        Vector<Family> childFamilies = family.getChildFamilies(true);

        // doppelte Elemente suchen und entfernen
        Object childClone = childFamilies.clone();
        @SuppressWarnings("unchecked")
        Vector<Family> doubles = (Vector<Family>) clone;
        doubles.retainAll(harvester);
        childFamilies.removeAll(doubles);

        // Rest dem harvester hinzufuegen
        harvester.addAll(childFamilies);
      }
    }

    return harvester;
  }
}
