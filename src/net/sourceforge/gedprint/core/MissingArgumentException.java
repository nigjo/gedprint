package net.sourceforge.gedprint.core;

public class MissingArgumentException extends RuntimeException
{
  private static final long serialVersionUID = -1979209515901137733L;

  public MissingArgumentException(String message)
  {
    super(message);
  }
}
