package net.sourceforge.gedprint.gedcom.tools;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.gedprint.gedcom.GedFile;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.gedcom.Record;
import net.sourceforge.gedprint.gedcom.Tag;

/** Neue Klasse erstellt am 06.02.2005.
 * 
 * @author nigjo
 */
public class GedFileMerger
{
  //private GedFile master;
  private GedFile result;
  //LinkedList check;
  LinkedList<Individual> unchecked;

  private GedFileMerger()
  {
    //check = new LinkedList();
    unchecked = new LinkedList<Individual>();
  }

  private void add(GedFile addon)
  {
    Renumberer renum = new Renumberer(addon.clone());
    renum.renumber(result.getMaxIndividualId() + 1, result.getMaxFamilyId() + 1);

    GedFile kopie = renum.getFile();

    kopie.findRecord(Tag.HEAD);

    Record copyRoot = getRoot(kopie);
    Record destRoot = getRoot(result);

    for(Record rec : copyRoot)
    {
      if(rec.isTag(Tag.HEAD) || rec.isTag(Tag.END_OF_FILE_MARK))
        continue;

      destRoot.addSubRecord(rec);
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
    //this.master = master;
    //result = (GedFile) master.clone();
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

  private Record getRoot(GedFile file)
  {
    try
    {
      Field rootField = GedFile.class.getDeclaredField("root");
      rootField.setAccessible(true);
      return (Record)rootField.get(file);
    }
    catch(Exception ex)
    {
      Logger.getLogger(GedFileMerger.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  class MergeRecord
  {
    int grad;
    Record master;
    Record add;
  }
}
