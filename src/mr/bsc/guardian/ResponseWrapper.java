package mr.bsc.guardian;

/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API
 * Diese Klasse enthält die gesamte Antwort der API in der Variable {@link ResponseWrapper#response}.
 * Wird von GSON benötigt, um die Antwort zu deserialisieren.
 * 
 * @author Marius Rosenbaum
 *
 */
public class ResponseWrapper {
	Response response;
}
