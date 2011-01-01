package net.sourceforge.gedprint.gedcom;

public enum Tag
{
  HEAD("HEAD"), //$NON-NLS-1$
  DATE("DATE"), //$NON-NLS-1$
  INDIVIDUAL("INDI"), //$NON-NLS-1$
  NAME("NAME"), //$NON-NLS-1$
  NAME_PREFIX("NPFX"), //$NON-NLS-1$
  GIVEN_NAME("GIVN"), //$NON-NLS-1$
  SURNAME("SURN"), //$NON-NLS-1$
  BIRTH("BIRT"), //$NON-NLS-1$
  DEATH("DEAT"), //$NON-NLS-1$
  UID("_UID"), //$NON-NLS-1$
  FAMILY_AS_SPOUSE("FAMS"), //$NON-NLS-1$
  FAMILY_AS_CHILD("FAMC"), //$NON-NLS-1$
  FAMILY("FAM"), //$NON-NLS-1$
  WIFE("WIFE"), //$NON-NLS-1$
  HUSBAND("HUSB"), //$NON-NLS-1$
  CHILDREN("CHIL"), //$NON-NLS-1$
  MARRIAGE("MARR"), //$NON-NLS-1$
  END_OF_FILE_MARK("TRLR"); //$NON-NLS-1$
  private final String tag;

  private Tag(String tag)
  {
    this.tag = tag;
  }

  @Override
  public String toString()
  {
    return tag;
  }
}
