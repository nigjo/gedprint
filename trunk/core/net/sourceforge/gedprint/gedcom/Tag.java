package net.sourceforge.gedprint.gedcom;

public enum Tag
{
  FAMC("FAMC"), //$NON-NLS-1$
  HEAD("HEAD"), //$NON-NLS-1$
  DATE("DATE"), //$NON-NLS-1$
  INDI("INDI"), //$NON-NLS-1$
  NAME("NAME"), //$NON-NLS-1$
  NAME_PREFIX("NPFX"), //$NON-NLS-1$
  GIVEN_NAME("GIVN"), //$NON-NLS-1$
  SURNAME("SURN"), //$NON-NLS-1$
  BIRTH("BIRT"), //$NON-NLS-1$
  DEAT("DEAT"), //$NON-NLS-1$
  UID("_UID"), //$NON-NLS-1$
  FAM_SPOUSE("FAMS"), //$NON-NLS-1$
  FAM_CHILD("FAMC"), //$NON-NLS-1$
  FAM("FAM"), //$NON-NLS-1$
  WIFE("WIFE"), //$NON-NLS-1$
  HUSBAND("HUSB"), //$NON-NLS-1$
  CHILDREN("CHIL"), //$NON-NLS-1$
  TRLR("TRLR"), //$NON-NLS-1$
  MARRIAGE("MARR"); //$NON-NLS-1$

  private final String tag;

  private Tag(String tag)
  {
    this.tag = tag;
  }

  public String toString()
  {
    return tag;
  }
}
