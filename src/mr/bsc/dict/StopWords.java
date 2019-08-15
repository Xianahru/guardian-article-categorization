package mr.bsc.dict;

import java.util.ArrayList;

/**
 * Repräsentation derer Stoppwörter, die beim Training eines Vorhersagemodells entfernt werden sollen.
 * Wird durch GSON deserialisiert.
 * 
 * Die Liste der Stopwörter (default english stopwords list) wurde von unten stehendem Link übernommen und leicht modifiziert.
 * {@link https://www.ranks.nl/stopwords}
 *  
 * @author Marius Rosenbaum
 *
 */
public class StopWords {
	
	private ArrayList<String> stopwords = new ArrayList<String>();
	
	/**
	 * Überprüft, ob ein String ein Stoppwort ist.
	 * 
	 * @param s	Der String, der überprüft werden soll.
	 * 
	 * @return true, wenn der String ein Stoppwort ist.
	 */
	public boolean isStoppwort(String s) {
		return stopwords.contains(s);
	}
	
	@Override
	public String toString() {
		return stopwords.toString();
	}
}
