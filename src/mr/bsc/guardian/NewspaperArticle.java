package mr.bsc.guardian;

/**
 * Diese Klasse entsteht aus der Antwort der Guardian-API, nachdem die Zeitungsartikel extrahiert wurden
 * Sie respräsentiert genau einen Zeitungsartikel mit einer Überschrift {@link #headline} und dem
 * Textkörper selbst {@link #content}.
 * 
 * 
 * @author Marius Rosenbaum
 *
 */
public class NewspaperArticle {

	private String headline;
	private String content;
	
	
	public NewspaperArticle(String headline, String content) {
		this.headline = headline;
		this.content = content;
	}
	
	/**
	 * Diese Methode gibt die Überschrift des Zeitungsartikels zurück.
	 * 
	 * @return Die Überschrift des Artikels als String
	 */
	public String getHeadline() {
		return this.headline;
	}
	
	/**
	 * Diese Methode gibt den Textkörper des Zeitungsartikels zurück.
	 * 
	 * @return Den Textkörper des Artikels als String
	 */
	public String getContent() {
		return this.content;
	}
	
}
