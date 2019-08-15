package mr.bsc.guardian;

/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API
 * Wird von GSON benötigt, um die Antwort zu deserialisieren.
 * Hat sonst keinen Nutzen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Edition {

	String id;
	String webTitle;
	String webUrl;
	String apiUrl;
	String code;
}
