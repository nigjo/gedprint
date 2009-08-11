package net.sourceforge.gedprint.gedcom;

/** Neue Klasse erstellt am 06.02.2005.
 * 
 * @author nigjo
 */
public class InvalidDataException extends RuntimeException
{
  private static final long serialVersionUID = 7784432178495547852L;

  public InvalidDataException()
  {
    super();
  }

  public InvalidDataException(Throwable cause)
  {
    super(cause);
  }

  public InvalidDataException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public InvalidDataException(String message)
  {
    super(message);
  }
}

//
// CVS-Protokoll
//
// $Log$
//