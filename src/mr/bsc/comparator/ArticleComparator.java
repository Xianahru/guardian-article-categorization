package mr.bsc.comparator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import mr.bsc.dict.ArticleAnalyzer;
import mr.bsc.dict.DictionaryCreator;
import mr.bsc.dict.Vocabulary;
import mr.bsc.guardian.NewspaperArticle;
import mr.bsc.guardian.NewspaperArticleCollection;

/**
 * Diese Klasse vergleicht mehrere Artikel über die Kosinus-Ähnlichkeit. Dazu werden die Artikel in einem ersten Schritt in
 * Dokumentenvektoren umgewandelt. Mit diesen Vektoren kann dann eine Berechnung der Kosinus-Ähnlichkeit erfolgen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class ArticleComparator {
	
	/**
	 * Diese Methode führt die gesamte Berechnung der Kosinus-Ähnlichkeit durch.
	 * 
	 * @param newspaperPath	Die Zeitungsartikel, die verglichen werden sollen
	 * @param filtered		Der Boolean legt fest, ob eine Filterung des Textes stattfinden soll (Symbole, Stoppwörter, Komposita).
	 * 
	 * @throws IOException
	 */
	public void measureSimilartiy(String newspaperPath, boolean filtered) throws IOException {
		
		NewspaperArticleCollection articles = DictionaryCreator.loadNewspaperCollection(newspaperPath);
		
		ArticleAnalyzer analyze = new ArticleAnalyzer();
		Vocabulary voc = new Vocabulary();

		ArrayList<HashMap<String, Integer>> analyzedArticles = new ArrayList<HashMap<String, Integer>>();
		
		//Alle Artikel werden durchlaufen und zerlegt. Dabei entsteht ein Vokabulary-Objekt für alle Zeitungsartikel
		for(NewspaperArticle article : articles.getArticles()) {
			HashMap<String, Integer> entries = analyze.analyze(article, filtered);
			analyzedArticles.add(entries);
			voc.add(entries);
		}
		
		//Erstellen einer zweidimensionalen Matrix mit der korrekten Größe
		double matrix[][] = new double[analyzedArticles.size()][analyzedArticles.size()];
		
		//Befüllen der Matrix mit dem Ergebnis der Berechnung
		//Beim Vergleich derselben Artikel wird keine Berechnung durchgeführt, sondern der Wert direkt auf 1.0 gesetzt
		for(int i = 0; i < analyzedArticles.size(); i++) {
			for(int j = 0; j < analyzedArticles.size(); j++) {
				if(i == j) {
					matrix[i][j] = 1.0;
				} else {
					//Hier werden die beiden Artikel in Dokumentenvektoren konvertiert
					double[] firstArticle = convertTextToVector(analyzedArticles.get(i), voc);
					double[] secondArticle = convertTextToVector(analyzedArticles.get(j), voc);
					//Hier wird die Kosinus-Ähnlichkeit beider Artikel berechnet und anschließend gleich auf 2 Stellen hinter dem Komma gerundet
					matrix[i][j] = round(calculateCosineSimilarity(firstArticle, secondArticle), 2);
				}
			}
		}
		
		/*
		 * Hier wird das Ergebnis ausgegeben
		 * Momentan als Latex-Tabelle, die fast direkt kopiert ausgegeben werden kann.
		 */
		for(int i = 0; i < analyzedArticles.size(); i++) {
			System.out.print((i + 1));
			for(int j = 0; j < analyzedArticles.size(); j++) {
				if(j > i) {
					System.out.print(" &  ");
				} else {
					if(matrix[i][j]==1) {
						System.out.print( " & \\cellcolor{blue!}\\color{white}" + matrix[i][j]);	
					} else {
						System.out.print( " & \\cellcolor{blue!" + round(matrix[i][j] * 100, 2) + "}" + matrix[i][j]);					
					}
				}
			}
			System.out.print(" \\\\\n");
		}
	}
	
	/**
	 * Diese Methode konvertiert einen Zeitungsartikel in einen Dokumentenvektor. Der Zeitungsartikel muss vorher bereits 
	 * durch den {@link ArticleAnalyzer} in ein HashMap umgewandelt worden sein und ein {@link Vocabulary}-Objekt, mit
	 * allen Wörtern der zu vergleichenden Artikeln, muss existieren.
	 * 
	 * @param entries 	Der zerlegte Zeitungsartikel
	 * @param voc		Das Vokabular, auf dem der Dokumentenvektor basiert
	 * 
	 * @return Den Zeitungsartikel als Dokumentenvektor als {@link int}-Array
	 */
	private double[] convertTextToVector(HashMap<String, Integer> entries, Vocabulary voc) {
		double[] vector = new double[voc.size()];
		
		ArrayList<String> vocab = new ArrayList<String>(voc.getWords());
		for(int i = 0; i < voc.size(); i++) {
			vector[i] = entries.getOrDefault(vocab.get(i), 0);
		}
			
		return vector;
	}
	
	/**
	 * Diese Methode berechnet die Kosinus-Ähnlichkeit zweier Dokumentenvektoren
	 * 
	 * @param first		Der erste Dokumentenvektor
	 * @param second	Der zweite Dokumentenvektor
	 * 
	 * @return	Die Kosinus-Ähnlichkeit zweier Dokumentenvektoren als {@link double}
	 */
	private double calculateCosineSimilarity(double[] first, double[] second) {
		
		double zaehler = calculateScalarProduct(first, second);
		double nenner = calculateEuclideanNorm(first) * calculateEuclideanNorm(second);
		
		return zaehler / nenner;
	}
	
	/**
	 * Diese Methode berechnet das Skalarprodukt zweier Vektoren (Auch "inneres Produkt).
	 * 
	 * @param first		Der erste Vektor
	 * @param second	Der zweite Vektor
	 * 
	 * @return	Das Skalarprodukt der beiden Vektoren als {@link double}
	 */
	private double calculateScalarProduct(double[] first, double[] second) {
		double ret = 0.0;
		
		if(first.length != second.length) {
			throw new RuntimeException("Vektoren haben unterschiedliche Dimensionen!");
		}
		
		for(int i = 0; i < first.length; i++) {
			ret += first[i] * second[i];
		}
		
		return ret;
	}
	
	/**
	 * Berechnet die euklidische Norm eines Vektors. Auch geläufig unter den Begriffen "Standardnorm" oder "2-Norm".
	 * 
	 * @param vector Der Vektor, für den die euklidische Norm ausgerechnet werden soll
	 * 
	 * @return Die euklidische Norm des Vektors als {@link double}
	 */
	private double calculateEuclideanNorm(double[] vector) {
		double ret = 0.0;
		
		for(int i = 0; i < vector.length; i++) {
			ret += Math.pow(vector[i], 2);
		}
		
		return Math.sqrt(ret);
	}
	
	/**
	 * Diese Methode rundet einen {@link double} auf die gewünschte Genauigkeit.
	 * 
	 * @param value		Der {@link double}, der gerundet werden soll
	 * @param places	Die Anzahl der Dezimalstellen, auf die gerundet werden soll
	 * 
	 * @return Den auf die gewünschte Dezimalstelle gerundeten {@link double}
	 */
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
