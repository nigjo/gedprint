package net.sourceforge.gedprint.core;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/** Implementation einer Enumeration ohne Elemente.
 * 
 * @author nigjo
 */
public class EmptyEnumeration implements Enumeration
{
  public boolean hasMoreElements()
  {
    return false;
  }

  public Object nextElement()
  {
    throw new NoSuchElementException("empty enumeration"); //$NON-NLS-1$
  }

}
