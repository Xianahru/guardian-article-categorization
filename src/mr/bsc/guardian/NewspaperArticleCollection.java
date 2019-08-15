package mr.bsc.guardian;
import java.util.ArrayList;

/**
 * Diese Klasse entsteht aus der Antwort der Guardian-API, nachdem die Zeitungsartikel extrahiert wurden
 * Sie enthält eine Liste von Zeitungsartikeln in {@link #articles}
 * 
 * 
 * @author Marius Rosenbaum
 *
 */
public class NewspaperArticleCollection {

	private ArrayList<NewspaperArticle> articles = new ArrayList<NewspaperArticle>();
	
	/**
	 * Gibt die von diesem Objekt verwalteten Zeitungsartikel zurück
	 * 
	 * @return Die verwaltete Liste {@link #articles}
	 */
	public ArrayList<NewspaperArticle> getArticles() {
		return this.articles;
	}
	
	/**
	 * Fügt einen Zeitungsartikel in die Liste der von diesem Objekt verwalteten Zeitungsartikel ein
	 * 
	 * @param a ein {@link NewspaperArticle}
	 */
	public void add(NewspaperArticle a) {
		this.articles.add(a);
	}
	
	/**
	 * Gibt die Anzahl von diesem Objekt verwalteten Zeitungsartikel zurück.
	 * 
	 * @return die Anzahl an Zeitungsartikeln als {@link int}
	 */
	public int size() {
		return this.articles.size();
	}
}
