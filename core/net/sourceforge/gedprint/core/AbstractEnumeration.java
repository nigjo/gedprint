package net.sourceforge.gedprint.core;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/** Basisklasse fuer eine eigene Implementation einer Enumeration.
 * 
 * @author nigjo
 */
public abstract class AbstractEnumeration implements Enumeration
{
  protected static final int START = -1;
  protected static final int STOP = -2;
  protected static final int RUNNING = 0;
  
  protected Object nextElement;
  protected int index = START;

  public boolean hasMoreElements()
  {
    if (index == STOP)
      return false;
    if (index == START)
      findNextElement();
    return nextElement != null;
  }

  public Object nextElement()
  {
    if (index == START)
      findNextElement();
    if (index == STOP)
      throw new NoSuchElementException();

    Object rc = nextElement;
    findNextElement();
    return rc;
  }

  /** sucht das naechste Element in der Liste.
   *
   *  Das gefundene Element muss in der Variable <tt>nextElement</tt>
   *  gespeichert werden. Die Variable <tt>start</tt> muss auf ein nicht
   *  negativen Wert oder <tt>RUNNING</tt> gesetzt werden. Wird kein Element
   *  mehr gefunden (suche nach dem letzten Element) muss <tt>index</tt> auf
   *  den Wert <tt>STOP</tt> gesetzt werden. Vor dem ersten Aufruf dieser
   *  Methode hat <tt>index</tt> den Wert <tt>START</tt>.
   */
  abstract protected void findNextElement(); 
}

//
// CVS-Protokoll
//
// $Log$
//