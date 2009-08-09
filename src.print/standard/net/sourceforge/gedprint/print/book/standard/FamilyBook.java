package net.sourceforge.gedprint.print.book.standard;

import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.gedprint.gedcom.Family;
import net.sourceforge.gedprint.gedcom.Individual;
import net.sourceforge.gedprint.print.BasicPage;
import net.sourceforge.gedprint.print.PrintManager;
import net.sourceforge.gedprint.print.book.standard.elements.ChildElement;
import net.sourceforge.gedprint.print.book.standard.elements.HeaderElement;
import net.sourceforge.gedprint.print.book.standard.elements.InfoElement;
import net.sourceforge.gedprint.print.book.standard.elements.ParentElement;
import net.sourceforge.gedprint.print.book.standard.elements.WeddingElement;
import net.sourceforge.gedprint.print.book.standard.properties.Messages;

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
    Vector<BasicPage> pageHarvester = new Vector<BasicPage>();

    // Eltern
    currentPage
        .add(new HeaderElement(Messages.getString("print.data.parents"))); //$NON-NLS-1$
    currentPage.add(new ParentElement(
        Messages.getString("print.data.husband"), fam.getHusband())); //$NON-NLS-1$
    currentPage.add(new ParentElement(
        Messages.getString("print.data.wife"), fam.getWife())); //$NON-NLS-1$

    // Hochzeit
    currentPage
        .add(new HeaderElement(Messages.getString("print.data.marriage"))); //$NON-NLS-1$
    currentPage.add(new WeddingElement(fam));

    // Kinder
    currentPage
        .add(new HeaderElement(Messages.getString("print.data.children"))); //$NON-NLS-1$
    int count = 3;
    Enumeration children = fam.getChildren();
    while(children.hasMoreElements())
    {
      if(count % 5 == 0)
      {
        // Neue Seite anfangen
        pageHarvester.add(currentPage);
        currentPage = new Page(fam);
        count = 0;
      }
      currentPage.add(new ChildElement((Individual) children.nextElement()));
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
      currentPage.add(new HeaderElement(Messages
          .getString("print.data.further_marriages"))); //$NON-NLS-1$
      InfoElement info = new InfoElement();
      info.setDescription(Messages
          .getString("print.data.further_marriages.description")); //$NON-NLS-1$
      currentPage.add(info);
    }
    if(count < 4)
    {
      currentPage.add(new HeaderElement(Messages
          .getString("print.data.additional_information"))); //$NON-NLS-1$
      InfoElement info = new InfoElement();
      info.setDescription(Messages
          .getString("print.data.additional_information.description")); //$NON-NLS-1$
      currentPage.add(info);
    }

    pageHarvester.add(currentPage);

    return pageHarvester.toArray(new BasicPage[pageHarvester.size()]);
  }

}
