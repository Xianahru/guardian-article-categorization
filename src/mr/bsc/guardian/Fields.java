package mr.bsc.guardian;

/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API
 * Der Text eines Zeitungsartikels ist in der Variable {@link #bodyText} enthalten
 * Wird von GSON benötigt, um die Antwort zu deserialisieren.
 * Hat sonst keinen Nutzen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Fields {

	String bodyText;
}
