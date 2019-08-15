package mr.bsc.dict;

import java.util.ArrayList;

/**
 * Repräsentation derer Symbole, die beim Training eines Vorhersagemodells entfernt werden sollen.
 * Wird durch GSON deserialisiert. 
 *  
 * @author Marius Rosenbaum
 *
 */
public class Symbols {
	
	private ArrayList<String> symbols = new ArrayList<String>();
	
	/**
	 * Überprüft, ob ein String ein Symbol ist.
	 * 
	 * @param s	Der String, der überprüft werden soll.
	 * 
	 * @return true, wenn der String ein Symbol ist.
	 */
	public boolean isSymbol(String s) {
		return symbols.contains(s);
	}
}
