package mr.bsc.dict;

import java.util.HashMap;

/**
 * Respräsentation eines Dictionaries. Stellt Teil des Vorhersagemodells dar.
 * Ein Dictionary entählt alle Wörter einer Kategorie in Kombination mit der Anzahl des Auftretens.
 * 
 * @author Marius Rosenbaum
 *
 */
public class Dictionary {
	HashMap<String, Integer> entries = new HashMap<String, Integer>();

	private int words = 0;

	/**
	 * Gibt die Größe des Wörterbuches zurück. Entspricht der Größe des Vokabulars derselben Kategorie.
	 * 
	 * @return Die Anzahl der Einträge dieses {@link Dictionary}.
	 */
	public int getDistinctWords() {
		return this.entries.size();
	}
	
	/**
	 * Gibt die Anzahl an Wörtern zurück, die zum kompilieren dieses Wörterbuchs beigetragen haben.
	 * Wenn ein Wörterbuch die zwei Einträge a und b hat, und a und b jeweils 2 mal im Wörterbuch vorkommen,
	 * würde diese Methode den Wert 4 zurückgeben.
	 * 
	 * @return Die Gesamtzahl der Wörter, als {@link int}.
	 */
	public int getTotalWords() {
		return words;
	}
	
	/**
	 * Gibt die von diesem Objekt verwaltete HashMap zurück.
	 * 
	 * @return Die von diesem Objekt verwaltete HashMap.
	 */
	public HashMap<String, Integer> getEntries(){
		return this.entries;
	}
	
	/**
	 * Überprüft, wie häufig das Wort <code> word </code> in diesem Wörterbuch vorkommt.
	 * 
	 * @param word	Der String, dessen Häufigkeit ermittelt werden soll.
	 * 
	 * @return 	Die Häufigkeit des Wortes oder 0, wenn das Wort nicht im Wörterbuch vorkommt.	
	 */
	public int findOccurrence(String word) {
		return this.entries.getOrDefault(word, 0);
	}
	
	/**
	 * Fügt diesem Wörterbuch die Wörter und Häufigkeiten aus einer HashMap hinzu.
	 * 
	 * @param map Die HashMap, deren Wörter in das Wörterbuch hinzugefügt werden sollen.
	 */
	public void add(HashMap<String, Integer> map) {
		for(String key : map.keySet()) {
			//Falls das Wort noch nicht enthalten ist, wird es hinzugefügt und die Häufigkeit zu dem Default 0 hinzugefügt.
			this.entries.put(key, this.entries.getOrDefault(key, 0) + map.get(key));
			this.words += map.get(key); //Anzahl der enthaltenen Wörter aktualisieren! Lieber O(1) als O(n)!
		}
	}
	
}
