package mr.bsc.guardian;
import java.util.ArrayList;

/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API.
 * Wird von GSON benötigt, um die Antwort zu deserialisieren. Hat sonst keinen Nutzen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Section {

	String id;
	String webTitle;
	String webUrl;
	String apiUrl;
	ArrayList<Edition> editions;
}
