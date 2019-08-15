package mr.bsc.dict;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Respräsentation eines Vokabulars. Wird hauptsächlich bei der Ähnlichkeitsberechnung von Zeitungsartikeln genutzt, entsteht aber
 * auch bei der Erstellung des Trainingsdatensatzes für die Klassifikation von Zeitungsartikeln
 * 
 * @author Marius Rosenbaum
 *
 */
public class Vocabulary {
	
	private LinkedHashSet<String> words = new LinkedHashSet<String>();
	
	/**
	 * Fügt ein Wort in das Vokabular ein.
	 * 
	 * @param s 	Das Wort, das eingefügt werden soll.
	 */
	public void add(String s) {
		this.words.add(s);
	}
	
	/**
	 * Gibt das von diesem Objekt verwaltete LinkedHashSet zurück.
	 * 
	 * @return Das LinkedHashSet dieses Objektes.
	 */
	public LinkedHashSet<String> getWords(){
		return this.words;
	}
	
	/**
	 * Gibt die Größe des Vokabulars zurück.
	 * 
	 * @return Die Größe des Vokabulars als {@link int}.
	 */
	public int size() {
		return this.words.size();
	}
	
	/**
	 * Überprüft, ob der ein Wort bereits im Vokabular enthalten ist.
	 * 
	 * @param s	Der String, dessen Präsenz im Vokabular überprüft werden soll.
	 * 
	 * @return true, wenn das Wort bereits im Vokabular enthalten ist.
	 */
	public boolean contains(String s) {
		return this.words.contains(s);
	}
	
	/**
	 * Fügt mehrere Wörter aus einer HashMap in das Vokabular ein.
	 * Dafür wird das keySet der HashMap genutzt.
	 * 
	 * @param entries 	Die HashMap, deren Wörter ins Vokabular eingefügt werden sollen.
	 */
	public void add(HashMap<String, Integer> entries) {
		words.addAll(entries.keySet());
	}
}

