package net.sourceforge.gedprint.print.book.standard;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.print.BasicPage;
import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.book.standard.elements.ChildElement;
import net.sourceforge.gedprint.print.book.standard.elements.HeaderElement;
import net.sourceforge.gedprint.print.book.standard.elements.InfoElement;
import net.sourceforge.gedprint.print.book.standard.elements.ParentElement;
import net.sourceforge.gedprint.print.book.standard.elements.WeddingElement;
import net.sourceforge.gedprint.core.Bundle;

public class FamilyBook extends PrintManager
{
  private TitlePage titlePage;

  public FamilyBook()
  {
    super();

    titlePage = new TitlePage();
    add(titlePage);
  }

  @Override
  public void setTitleFamily(Family family)
  {
    super.setTitleFamily(family);
    titlePage.setFamily(family);
  }

  protected BasicPage[] createFamilyPages(Family fam)
  {
    BasicPage currentPage = new Page(fam);
    List<BasicPage> pageHarvester = new ArrayList<BasicPage>();

    // Eltern
    currentPage
        .add(new HeaderElement(Bundle.getString("print.data.parents", getClass()))); //$NON-NLS-1$
    currentPage.add(new ParentElement(
        Bundle.getString("print.data.husband", getClass()), fam.getHusband())); //$NON-NLS-1$
    currentPage.add(new ParentElement(
        Bundle.getString("print.data.wife", getClass()), fam.getWife())); //$NON-NLS-1$

    // Hochzeit
    currentPage
        .add(new HeaderElement(Bundle.getString("print.data.marriage", getClass()))); //$NON-NLS-1$
    currentPage.add(new WeddingElement(fam));

    // Kinder
    currentPage
        .add(new HeaderElement(Bundle.getString("print.data.children", getClass()))); //$NON-NLS-1$
    int count = 3;
    for(Individual individual : fam.getChildren())
    {
      if(count % 5 == 0)
      {
        // Neue Seite anfangen
        pageHarvester.add(currentPage);
        currentPage = new Page(fam);
        count = 0;
      }
      currentPage.add(new ChildElement(individual));
      count++;
    }
    // Auffuellen
    while(count < 3)
    {
      currentPage.add(new ChildElement());
      count++;
    }

    // Infobloecke
    if(count < 5)
    {
      currentPage.add(new HeaderElement(Bundle
          .getString("print.data.further_marriages", getClass()))); //$NON-NLS-1$
      InfoElement info = new InfoElement();
      info.setDescription(Bundle
          .getString("print.data.further_marriages.description", getClass())); //$NON-NLS-1$
      currentPage.add(info);
    }
    if(count < 4)
    {
      currentPage.add(new HeaderElement(Bundle
          .getString("print.data.additional_information", getClass()))); //$NON-NLS-1$
      InfoElement info = new InfoElement();
      info.setDescription(Bundle
          .getString("print.data.additional_information.description", getClass())); //$NON-NLS-1$
      currentPage.add(info);
    }

    pageHarvester.add(currentPage);

    return pageHarvester.toArray(new BasicPage[pageHarvester.size()]);
  }

}
