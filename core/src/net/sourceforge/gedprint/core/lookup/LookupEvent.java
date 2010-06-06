package net.sourceforge.gedprint.core.lookup;

import java.util.EventObject;

public class LookupEvent extends EventObject
{
  private static final long serialVersionUID = -8464777328413063334L;
  public static final int ELEMENT_ADDED = 1;
  public static final int ELEMENT_REMOVED = 1;
  private final int type;
  private final Object element;

  public LookupEvent(Lookup lookup, int type, Object element)
  {
    super(lookup);
    this.type = type;
    this.element = element;
  }

  public Lookup getLookup()
  {
    return (Lookup)getSource();
  }

  public int getType()
  {
    return type;
  }

  public Object getElement()
  {
    return element;
  }
}
