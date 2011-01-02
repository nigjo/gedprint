package net.sourceforge.gedprint.ui;

/**
 * Servicefactory fuer Darstellungsklassen.
 * 
 * Jede Implementierung der Darstellungsklassen muss als Service fuer dieses
 * Interface bekannt gemacht werden. Nach dem Oeffnen einer Datei wird dem
 * Anwender eine Liste
 * 
 * @author nigjo
 */
public interface GedDocumentFactory
{

  String getName();

  GedPainter createDocument();

}
