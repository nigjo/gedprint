package net.sourceforge.gedprint.print;

import java.awt.Dimension;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import net.sourceforge.gedprint.gedcom.Family;

abstract public class PrintManager
{
  public static final int ODD = 1;
  public static final int EVEN = 0;
  public static final String VERSION_STRING = "GEDPrint v0.1 alpha"; //$NON-NLS-1$

  private JFrame owner;

  private Vector<BasicPage> pages;

  private JProgressBar progressBar;
  private Family family;
  
  public PrintManager()
  {
    super();
  }
  
  public PrintManager(JFrame owner)
  {
    setOwner(owner);
  }

  public void setOwner(JFrame owner)
  {
    this.owner = owner;
  }

  private PrintJob getJob(String string)
  {
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    return defaultToolkit.getPrintJob(owner, string, null);
  }

  public void add(BasicPage page)
  {
    if(pages == null)
      pages = new Vector<BasicPage>();
    pages.add(page);
  }

  public boolean print()
  {
    if(pages == null)
      return false;

    int pageCounter = 0;

    PrintJob job = getJob(VERSION_STRING);
    if(job == null)
      return false;

    if(progressBar != null)
    {
      progressBar.setMinimum(0);
      progressBar.setMaximum(pages.size());
      progressBar.setValue(0);
    }

    if(job.lastPageFirst())
    {
      // umgekehrte Reihenfolge
      BasicPage[] pageArray = pages.toArray(new BasicPage[pages.size()]);
      for(int i = pageArray.length - 1; i >= 0; i--)
      {
        printPage(pageArray[i], job, pageArray.length - (++pageCounter));
        if(progressBar != null)
          progressBar.setValue(pageCounter);
      }
    }
    else
    {
      for(BasicPage page : pages)
      {
        printPage(page, job, ++pageCounter);
        if(progressBar != null)
          progressBar.setValue(pageCounter);
      }
    }
    job.end();

    return true;
  }

  private void printPage(BasicPage page, PrintJob job, int number)
  {
    Dimension pageDimension = job.getPageDimension();
    int pageResolution = job.getPageResolution();

    page.setPageNumber(number);

    if(pageDimension.height > pageDimension.width)
    {
      // Hochformat
      if(number % 2 == 0)
        // gerade
        page.setBorder(1.0, 2.5, 2.5, 1.8);
      else
        // ungerade
        page.setBorder(2.5, 2.5, 1.0, 1.8);
    }
    else
    {
      // Querformat
      if(number % 2 == 0)
        // gerade
        page.setBorder(1.8, 1.0, 1.8, 2.5);
      else
        // ungerade
        page.setBorder(1.8, 2.5, 1.8, 1.0);
    }

    page.paintPage(job.getGraphics(), pageDimension, pageResolution);
  }

  public BasicPage getPage(int index)
  {
    return pages.get(index);
  }

  public void add(BasicPage[] pages)
  {
    if(pages == null)
      return;
    for(int i = 0; i < pages.length; i++)
      add(pages[i]);
  }

  public void setProgress(JProgressBar progress)
  {
    this.progressBar = progress;
  }

  public void fill(int evenodd)
  {
    while(getPageCount() % 2 != evenodd)
      add(new FillPage());
  }

  public int getPageCount()
  {
    if(pages == null)
      return 0;
    return pages.size();
  }

  /**
   * fuegt eine Familien inclusive Nachfahren zum Manager hinzu.
   * 
   * @param record
   *          Familieneintrag
   * @param addChildren
   *          fuegt die direkten Kinder der Familie hinzu, falls sie ebenfalls
   *          verheiratet sind.
   * @param fullTree
   *          Fuegt alle Nachfahren der Familie hinzu. Dieser Parameter hat
   *          keine Auswirkung, wenn <code>addChildren</code> mit dem Wert
   *          <code>false</code> angegeben wurde.
   */
  public void addFamily(Family record, boolean addChildren, boolean fullTree)
  {
    // Familien beginnen auf ungeraden Seiten
    fill(PrintManager.EVEN);

    // Elternfamilie
    add(createFamilyPages(record));

    if(addChildren)
    {
      // Familien der Nachfahren ausgeben

      // Familien suchen
      Vector<Family> fams = record.getChildFamilies(fullTree);
      // ... und sortieren nach Bogennummer
      Collections.sort(fams, new Comparator<Family>() {
        public int compare(Family o1, Family o2)
        {
          int i1=o1.getIDNumber();
          int i2=o2.getIDNumber();
          if(i1==i2)
            return 0;
          if(i1<i2)
            return -1;
          return 1;
        }
      });
      // Alle Familien eintragen
      for(Family family : fams)
      {
        fill(PrintManager.EVEN);
        add(createFamilyPages(family));
      }
    }
  }

  abstract protected BasicPage[] createFamilyPages(Family record);

  public void setTitleFamily(Family family)
  {
    this.family = family;
  }
  public Family getTitleFamily()
  {
    return family;
  }
}
