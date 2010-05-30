package net.sourceforge.gedprint.core;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/** Implementation einer Enumeration ohne Elemente.
 * 
 * @author nigjo
 */
public class EmptyEnumeration<E> implements Enumeration<E>
{
  public boolean hasMoreElements()
  {
    return false;
  }

  public E nextElement()
  {
    throw new NoSuchElementException("empty enumeration"); //$NON-NLS-1$
  }

}
