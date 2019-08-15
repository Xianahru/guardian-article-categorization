package mr.bsc.guardian;
import java.util.ArrayList;


/**
 * Hilfsklasse zum Einlesen von Antworten der Guardian-API
 * Diese Klasse enthält die Informationen zu den enthaltenen Zeitungsartikeln. Die dazugehörigen Zeitungsartikel
 * sind in der Variable {@link Response#results} enthalten. Wird von GSON benötigt, um die Antwort zu deserialisieren.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Response {

	String status;
	String userTier;
	int total;
	int startIndex;
	int pageSize;
	int currentPage;
	int pages;

	Edition edition;
	Section section;

	public ArrayList<Result> results = new ArrayList<Result>();
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Result r : results) {
			builder.append(r.fields.bodyText);
		}
		return builder.toString();
	}
}
