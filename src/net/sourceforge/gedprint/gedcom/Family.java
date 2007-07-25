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
   * 
   * @return
   */
  public Enumeration getChildren()
  {
    final Record[] children = getSubRecords(Tag.CHILDREN);
    if(children.length > 0)
    {
      return new AbstractEnumeration() {
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
      return new EmptyEnumeration();
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

  @SuppressWarnings("unchecked")
  public Vector<Family> getChildFamilies(boolean deepSearch)
  {
    Vector<Family> harvester = new Vector<Family>();
    Enumeration children = getChildren();
    while(children.hasMoreElements())
    {
      Record child = (Record) children.nextElement();
      Record[] spouseFamilies = child.getSubRecords(Tag.FAM_SPOUSE);
      for(int i = 0; i < spouseFamilies.length; i++)
      {
        harvester.add((Family) findID(spouseFamilies[i].getContent()));
      }
    }

    if(deepSearch)
    {
      Vector<Family> parents = (Vector<Family>) harvester.clone();
      for(Family family : parents)
      {
        Vector<Family> childFamilies = family.getChildFamilies(true);

        // doppelte Elemente suchen und entfernen
        Vector<Family> doubles = (Vector<Family>) childFamilies.clone();
        doubles.retainAll(harvester);
        childFamilies.removeAll(doubles);

        // Rest dem harvester hinzufuegen
        harvester.addAll(childFamilies);
      }
    }

    return harvester;
  }
}
