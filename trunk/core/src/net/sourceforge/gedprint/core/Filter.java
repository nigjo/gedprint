package net.sourceforge.gedprint.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import net.sourceforge.gedprint.gedcom.Record;


/** Neue Klasse erstellt am 05.04.2005.
 * 
 * @author nigjo
 */
public class Filter
{
  private Filter parent;
  int level;
  String recType;

  List<Filter> subfilter;

  String[] deny;

  public Filter(String rec)
  {
    level = -1;
    this.recType = rec;
  }

  public Filter add(String subrec)
  {
    return add(new Filter(subrec));
  }
  public Filter add(Filter sub)
  {
    if (subfilter == null)
      subfilter = new ArrayList<Filter>();

    if (!subfilter.contains(sub))
    {
      sub.level = level + 1;
      sub.parent = this;
      if (subfilter.add(sub))
        return sub;
    }

    return null;
  }

  @Override
  public boolean equals(Object obj)
  {
    String rec = getType();
    if (obj instanceof String)
      return rec.equals(obj);
    else if (obj instanceof Filter)
      return rec.equals(((Filter) obj).getType());

    return super.equals(obj);
  }

  String getType()
  {
    return recType;
  }

  public int size()
  {
    if (subfilter != null)
      return subfilter.size();
    return 0;
  }

  public void store(PrintStream out)
  {
    if (level >= 0)
    {
      String pattern = "{0,number,integer} {1}"; //$NON-NLS-1$
      Object[] args = { new Integer(level), getType()};
      out.println(MessageFormat.format(pattern, args));
    }

    if (subfilter != null)
    {
      for(Filter element : subfilter)
      {
        element.store(out);
      }
    }
  }

  public void storeDeny(String filename)
  {
    try
    {
      PrintStream out =
        new PrintStream(
          new BufferedOutputStream(new FileOutputStream(filename)));

      storeDeny(out, ""); //$NON-NLS-1$

      out.close();
    }
    catch (FileNotFoundException e)
    {
    }
  }

  public void storeDeny(PrintStream out, String prefix)
  {
    if (deny != null)
    {
      String pattern = "{0,number,integer} {1} {2}"; //$NON-NLS-1$
      Object[] args = { new Integer(level + 1), null, null };
      //out.println();
      for (int i = 0; i < deny.length; i++)
      {
        if (deny[i].length() < 14)
        {
          char[] buffer = new char[14 - deny[i].length()];
          Arrays.fill(buffer, ' ');
          args[1] = deny[i].concat(new String(buffer));
        }
        else
          args[1] = deny[i];
        args[2] = prefix + recType + '/' + deny[i];
        out.println(MessageFormat.format(pattern, args));
      }
    }
    if (subfilter != null)
    {
      for(Filter filter : subfilter)
      {
        filter.storeDeny(out, prefix + recType + '/');
      }
    }
  }

  public static Filter load(String filename)
  {
    Filter basic = new Filter(""); //$NON-NLS-1$
    basic.level = -1;
    basic.parent = null;

    try
    {
      Filter current = basic;
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String zeile;
      while (null != (zeile = in.readLine()))
      {
        if (zeile.trim().charAt(0) != ';')
        {
          String[] parts = zeile.split(" "); //$NON-NLS-1$
          int l = Integer.parseInt(parts[0]);
          while (current.level >= l)
            current = current.parent;
          current = current.add(parts[1]);
        }
      }

      in.close();
      return basic;
    }
    catch (FileNotFoundException e)
    {
      //e.printStackTrace();
    }
    catch (IOException e)
    {
      //e.printStackTrace();
    }

    return null;
  }

  public boolean accept(Record subrec)
  {
    // Das Level des Filters muss um einen niedriger sein, als der 
    if (this.level != subrec.getLevel() - 1)
      return false;

    return getSubFilter(subrec.getType()) != null;
  }

  /** Durchsucht den Record nach unbekannten Eintraegen.
   * 
   * @param current
   */
  public void learn(Record current)
  {
    if (level >= 0 && current.getLevel() < this.level)
    {
      parent.learn(current);
      return;
    }
    for(Record sub : current)
    {
      if (accept(sub))
      {
        getSubFilter(sub.getType()).learn(sub);
      }
      else
      {
        if (deny == null)
        {
          deny = new String[] { sub.getType()};
        }
        else
        {
          List<String> list = new ArrayList<String>(Arrays.asList(deny));
          String t = sub.getType();
          if (!list.contains(t))
          {
            list.add(t);
            deny = list.toArray(new String[list.size()]);
          }
        }
      }
    }
  }

  public void apply(Record current)
  {
    if (level >= 0 && current.getLevel() < this.level)
    {
      parent.apply(current);
      return;
    }
    List<Record> del=new ArrayList<Record>();

    for(Record sub : current)
    {
      if (accept(sub))
      {
        getSubFilter(sub.getType()).apply(sub);
      }
      else
      {
        // erstmal nur merken
        del.add(sub);
      }
    }
    // Daten tatsaechlich loeschen
    for(Record delRec : del)
    {
      current.delSubRecord(delRec);
    }
  }
  
  private Filter getSubFilter(String string)
  {
    if (subfilter != null)
    {
      for(Filter f : subfilter)
      {
        if (f.getType().equals(string))
          return f;
      }
    }
    return null;
  }
}
