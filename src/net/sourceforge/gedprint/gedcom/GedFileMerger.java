package net.sourceforge.gedprint.gedcom;

import java.util.Enumeration;
import java.util.LinkedList;

import net.sourceforge.gedprint.core.Renumberer;

/** Neue Klasse erstellt am 06.02.2005.
 * 
 * @author nigjo
 */
public class GedFileMerger
{
  private GedFile master;
  private GedFile result;

  LinkedList check;
  LinkedList<Individual> unchecked;

  private GedFileMerger()
  {
    check = new LinkedList();
    unchecked = new LinkedList<Individual>();
  }

  private void add(GedFile addon)
  {
    
    Renumberer renum = new Renumberer((GedFile) addon.clone());
    renum.renumber(result.getMaxIndividualId() + 1, result
        .getMaxFamilyId() + 1);
    
    GedFile kopie = renum.getFile();
    
    Enumeration recs = kopie.records.elements();
    while(recs.hasMoreElements())
    {
      Record rec = (Record) recs.nextElement();
      if(!"HEAD".equals(rec.getType()) &&
          !"TRLR".equals(rec.getType()))
      {
        result.records.addSubRecord(rec);
      }
    }
    
    if(true)
      return;
    //++++++++++++++++++++++++++++++++++++++++++++++++++
    //ERSTER SCHRITT:
    // Suchen nach Individuen mit gleicher _UID. Diese
    // sind am einfachsten zu finden und sollten keine
    // weitere Vergleiche norwendig machen.
    //++++++++++++++++++++++++++++++++++++++++++++++++++
//    Enumeration i = addon.getIndividuals();
//    while (i.hasMoreElements())
//    {
//      Individual indi = (Individual) i.nextElement();
//      if (master.contains(indi))
//      {
//        MergeRecord r = new MergeRecord();
//        r.add = (Record) indi.clone();
//        r.master = master.findUID(indi.getUID());
//        r.grad = FULL_MATCH;
//      }
//      else
//      {
//        unchecked.add((Individual) indi.clone());
//      }
//    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++
    //ZWEITER SCHRITT:
    // Alle Familien der gefundenen Individuen auf den
    // Suchstack legen um die direkten Verwandten zu
    // finden und evtl. zu identifizieren. 
    //++++++++++++++++++++++++++++++++++++++++++++++++++

    //++++++++++++++++++++++++++++++++++++++++++++++++++
    //DRITTER SCHRITT:
    // Detailiertere Suche der verbliebenen Individuen
    // mit den verbliebenen Individuen in der master-
    // Datei
    // finden und evtl. zu identifizieren. 
    //++++++++++++++++++++++++++++++++++++++++++++++++++

  }

  /** liefert die zusammengefuegte GEDCOM-Datei.
   */
  private GedFile getMergedFile()
  {
    return result;
  }

  private void setMasterFile(GedFile master)
  {
    this.master = master;
    result = (GedFile) master.clone();
  }

  public static GedFile merge(GedFile master, GedFile addon)
  {
    GedFileMerger merger = new GedFileMerger();

    merger.setMasterFile(master);
    merger.add(addon);

    return merger.getMergedFile();
  }

  /** UIDs Stimmen ueberein. */
  public static int FULL_MATCH = 10;
  /** Lebensspannne (Geburt bis Tot) stimmen ueberein. */
  public static int LIFE_MATCH = 9;
  /** gleiches Geburtsdatum. (+Ort?) */
  public static int BIRTH_MATCH = 8;
  /** keine Uebereinstimmung gefunden. */
  public static int NO_MATCH = 0;
  class MergeRecord
  {
    int grad;
    Record master;
    Record add;
  }
}
