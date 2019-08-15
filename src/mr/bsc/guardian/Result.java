package mr.bsc.guardian;
import java.util.Date;


/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API.
 * Diese Klasse stellt ein Zeitungsartikel dar. Wird von GSON benötigt, um die Antwort zu deserialisieren.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Result {

	String id;
	String type;
	String sectionId;
	String sectionName;
	Date webPublicationDate;
	String webTitle;
	String webUrl;
	String apiUrl;
	
	Fields fields;
	
	boolean isHosted;
	String pillarId;
	String pillarName;
	
}
