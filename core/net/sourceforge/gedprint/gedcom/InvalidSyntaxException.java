package net.sourceforge.gedprint.gedcom;

public class InvalidSyntaxException extends RuntimeException
{
  private static final long serialVersionUID = -1560683919098637038L;

  public InvalidSyntaxException()
  {
    super();
  }

  public InvalidSyntaxException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public InvalidSyntaxException(String message)
  {
    super(message);
  }

  public InvalidSyntaxException(Throwable cause)
  {
    super(cause);
  }
}
