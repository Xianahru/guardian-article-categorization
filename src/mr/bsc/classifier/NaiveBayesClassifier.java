package mr.bsc.classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import mr.bsc.dict.ArticleAnalyzer;
import mr.bsc.dict.Dictionary;
import mr.bsc.dict.DictionaryCreator;
import mr.bsc.dict.Vocabulary;
import mr.bsc.dict.VocabularyCombiner;
import mr.bsc.guardian.NewspaperArticle;

/**
 * Diese Klasse implementiert einen naiven Bayes-Klassifikator.
 * Dabei wird ein Testset an Zeitungsartikeln in eine der 4 Kategorien BUSINESS, POLITICS, SCIENCE oder SPORT eingeordnet.
 * 
 * @author Marius Rosenbaum
 *
 */
public class NaiveBayesClassifier {
	
	/*
	 * Wert für das Laplace-Smoothing 
	 */
	private final static int SMOOTHING_LAPLACE = 1;
	
	/*
	 * Die verschiedenen Kategorien
	 */
	private final static int TYPE_BUSINESS = 0;
	private final static int TYPE_POLITICS = 1;
	private final static int TYPE_SCIENCE = 2;
	private final static int TYPE_SPORT = 3;
	
	/* ----------------------------------------------------------------------------------------------------------------------------- */
	
	/*
	 * Die Pfade für Modell_1 (ungefiltert)
	 */
	private static final String PATH_DICTIONARY_EVERYTHING_BUSINESS = "cleaned\\businessDictionaryCleaned.json";
	private static final String PATH_DICTIONARY_EVERYTHING_POLITICS = "cleaned\\politicsDictionaryCleaned.json";
	private static final String PATH_DICTIONARY_EVERYTHING_SCIENCE = "cleaned\\scienceDictionaryCleaned.json";		
	private static final String PATH_DICTIONARY_EVERYTHING_SPORT = "cleaned\\sportDictionaryCleaned.json";
	private static final String PATH_VOCABULARY_EVERYTHING = "cleaned\\combinedVocabularyCleaned.json";
	
	/*
	 * Die Pfade für Modell_2 (gefiltert)
	 */
	private static final String PATH_DICTIONARY_FILTERED_BUSINESS = "cleanFiltered\\businessDictionaryCleanFiltered.json";
	private static final String PATH_DICTIONARY_FILTERED_POLITICS = "cleanFiltered\\politicsDictionaryCleanFiltered.json";
	private static final String PATH_DICTIONARY_FILTERED_SCIENCE = "cleanFiltered\\scienceDictionaryCleanFiltered.json";		
	private static final String PATH_DICTIONARY_FILTERED_SPORT = "cleanFiltered\\sportDictionaryCleanFiltered.json";
	private static final String PATH_VOCABULARY_FILTERED = "cleanFiltered\\combinedVocabularyCleanFiltered.json";
	
	/* ----------------------------------------------------------------------------------------------------------------------------- */

	private ArticleAnalyzer analyzer;
	
	private Dictionary businessDictionary;
	private Dictionary politicsDictionary;
	private Dictionary scienceDictionary;
	private Dictionary sportDictionary;
	private Vocabulary vocabulary;
	
	public NaiveBayesClassifier() throws FileNotFoundException {
		this.analyzer = new ArticleAnalyzer();
	}
	
	/**
	 * Diese Methode klassifierzt den Testdatensatz.
	 * 
	 * @param businessPath 	Das Verzeichnis mit den Zeitungsartikeln der Kategorie BUSINESS
	 * @param politicsPath 	Das Verzeichnis mit den Zeitungsartikeln der Kategorie POLITICS
	 * @param sciencePath 	Das Verzeichnis mit den Zeitungsartikeln der Kategorie SCIENCE
	 * @param sportPath 	Das Verzeichnis mit den Zeitungsartikeln der Kategorie SPORT
	 * @param filtering		<p>{@code true}, Modell_2 wird zur Vorhersage benutzt</p>
	 * 						<p>{@code false}, Modell_1 wird zur Vorhersage benutzt</p>
	 * 
	 * @throws IOException	Beim Laden des Textkorpus ist etwas schiefgegangen. Sind die Dateien an der richtigen Stelle und korrekt benannt?
	 */
	public void classifyArticles(String businessPath, String politicsPath, String sciencePath, String sportPath, boolean filtering) throws IOException {
		
		this.load(filtering); //Hier wird das korrekte Modell geladen
		
		int matrix[][] = new int[4][4]; //Neue zweidimensionale Matrix erstellen
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				matrix[i][j] = 0;
			}
		}
		
		//Zähler, der lediglich für die Ausgabe der bereits klassifizierten Artikel genutzt wird
		int count = 1;
		
		//Hier findet die eigentlich Klassifizierung statt
		//Alle Artikel werden nacheinander durchlaufen und klassifiziert
		for(NewspaperArticle article : DictionaryCreator.loadNewspaperCollection(businessPath).getArticles()) {
			int classifierOutput = this.classifyArticle(article, filtering); //Der Ergebnis der Klassifikation wird in dieser Variable gespeichert
			/*
			 * Die Artikel, die aus der Kategorie Business stammen, werden in der "Zeile" 0 gespeichert. Das Ergebnis der Klassifikation ist dann
			 * in der entsprechenden "Spalte" vorhanden. Beispiel: Ein Artikel der Klasse BUSINESS (0) wird eingegeben und soll klassifiziert werden. Das
			 * Vorhersagemodell liefert das Ergenis SPORT (3). Dementsprechend wird der Wert in matrix[0][3] um eins erhöht.
			 */
			matrix[0][classifierOutput] = matrix[0][classifierOutput] + 1;
			log(count);
			count++;
		}
		for(NewspaperArticle article : DictionaryCreator.loadNewspaperCollection(politicsPath).getArticles()) {
			int classifierOutput = this.classifyArticle(article, filtering);
			matrix[1][classifierOutput] = matrix[1][classifierOutput] + 1;
			log(count);
			count++;
		}
		for(NewspaperArticle article : DictionaryCreator.loadNewspaperCollection(sciencePath).getArticles()) {
			int classifierOutput = this.classifyArticle(article, filtering);
			matrix[2][classifierOutput] = matrix[2][classifierOutput] + 1;
			log(count);
			count++;
		}
		for(NewspaperArticle article : DictionaryCreator.loadNewspaperCollection(sportPath).getArticles()) {
			int classifierOutput = this.classifyArticle(article, filtering);
			matrix[3][classifierOutput] = matrix[3][classifierOutput] + 1;
			log(count);
			count++;
		}
		
		/**
		 * Hier wird die Matrix ausgegeben. Zahlen getrennt mit einem Leerzeichen.
		 */
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		
		/*
		 * NOTE: Diese Methode ist noch etwas starr. Sie funktioniert nur für genau 4 Kategorien. Generell ist das Laden von Vorhersagemodellen und dem Vokabular
		 * noch etwas unschön. TODO Es wäre denkbar, das Vokabular direkt abhängig von den gewählten Kategorien bzw. den gewählten Dictionary-Objekten zu machen. Also
		 * das Vokabular aus den Dictionary-Objekten extrahieren. Prinzipiell mit momentaner Implementierung relativ simpel, indem man die Keysets der jeweiligen
		 * Dictionary-Objekten zur Laufzeit in ein einzelnes Set zusammenführt.
		 */
		
	}

	/**
	 * Diese Methode klassifiziert genau einen Zeitungsartikel.
	 * 
	 * @param article		Der Zeitungsartikel, der klassifiziert werden soll
	 * @param filtering		<p>{@code true}, Modell_2 wird zur Vorhersage benutzt</p>
	 * 						<p>{@code false}, Modell_1 wird zur Vorhersage benutzt</p>
	 * 
	 * @return	{@link int} zwischen 0 und 3. Mögliche Werte und deren Bedeutung sind: 0 = BUSINESS, 1 = POLITICS, 2 = SCIENCE, 3 = SPORT
	 */
	private int classifyArticle(NewspaperArticle article, boolean filtering) {
		HashMap<String, Integer> words = analyzer.analyze(article, filtering);
		
		return calculateTopic(words);
	}
	
	/**
	 * Der Algorithmus zur Klassifizierung wird durch diese Methode ausgeführt.
	 * 
	 * @param words		Die Wörter des zerlegten Zeitungsartikels
	 * 
	 * @return	{@link int} zwischen 0 und 3. Mögliche Werte und deren Bedeutung sind: 0 = BUSINESS, 1 = POLITICS, 2 = SCIENCE, 3 = SPORT
	 */
	private int calculateTopic(HashMap<String, Integer> words) {
		
		int biggest = NaiveBayesClassifier.TYPE_BUSINESS;
		double comp = 0;
		double business = calculateProbability(words, businessDictionary, (double) 9989/39270);
		comp = business;
		double politics = calculateProbability(words, politicsDictionary, (double) 9954/39270);
		//Wenn die Wahrscheinlichkeit für POLITICS größer ist als die vorherige (BUSINESS), ersetzen!
		if (politics > comp) {
			biggest = NaiveBayesClassifier.TYPE_POLITICS;
			comp = politics;
		}
		//Wenn die Wahrscheinlichkeit für SCIENCE größer ist als die vorherige (POLITICS), ersetzen!
		double science = calculateProbability(words, scienceDictionary, (double) 9443/39270);
		if (science > comp) {
			biggest = NaiveBayesClassifier.TYPE_SCIENCE;
			comp = science;
		}
		//Wenn die Wahrscheinlichkeit für SPORT größer ist als die vorherige (SCIENCE), ersetzen!
		double sport = calculateProbability(words, sportDictionary, (double) 9884/39270);
		if (sport > comp) {
			biggest = NaiveBayesClassifier.TYPE_SPORT;
			comp = sport;
		}
		
		return biggest;
		
		/*
		 * NOTE: Auch diese Methode kann noch verbessert werden. TODO Die A-priori-Wahrscheinlichkeiten sind noch hartkodiert. Prinzipiell entsteht so eine Wahrscheinlich nur durch vorwissen.
		 * Man könnte den einzelnen Dicionary-Objekten bei der Erstellung noch eine Variable hinzufügen, die die Anzahl der Zeitungsartikel festhält, aus denen das Dictionary entstanden ist.
		 * Damit ließe sich eine A-priori-Wahrscheinlichkeit zur Laufzeit ausrechnen.
		 * Auch hier aber besteht das Problem, dass exakt 4 Kategorien vorliegen müssen. Eine Lösung, die unabhängig von der Anzahl der Kategorien eine Klassifikation durchführen kann, wäre
		 * wünschenswert.
		 */
	}
	
	/**
	 * Diese Methode berechnet die Wahrscheinlichkeit bei der Klassifizierung.
	 * 
	 * @param words		Die Wörter des zerlegten Zeitungsartikels
	 * @param dic		Das Wörterbuch der Kategorie, für die eine Wahrscheinlichkeit berechnet werden soll
	 * @param priori	Die A-priori-Wahrscheinlichkeit für die Kategorie, für die eine Wahrscheinlichkeit berechnet werden soll
	 * 
	 * @return	Die Wahrscheinlichkeit der Kategorie anzugehören als {@link double}
	 */
	private double calculateProbability(HashMap<String, Integer> words, Dictionary dic, double priori) {
		
		double probability = Math.log(priori); //Logarithmus der A-priori-Wahrscheinlichkeit
		
		/**
		 * Durch die Annahme der bedingten Unabhängigkeit der Merkmale wird die Liste der Wörter duchlaufen und deren Wahrscheinlichkeiten
		 * miteinander multipliziert
		 */
		for (String e : words.keySet()) {
			double zaehler = dic.findOccurrence(e) + NaiveBayesClassifier.SMOOTHING_LAPLACE; //Der Zähler ist das Vorkommen des Worten im Dictionary + der gewählte Glättungswert
			double nenner = dic.getTotalWords() + this.vocabulary.size(); //Der Nenner ist die größe des Dictionaries (Das Vorkommen jedes Wortes) + die Größe des Vokabulars
			//Natuerlicher Logarthimus (Ln) bei sehr kleinen Zahlen
			double temp = words.get(e) * (Math.log(zaehler/nenner)); //Ein Wort kommt bspw. 5 Mal vor, also muss 5 Mal diese Rechnung ausgeführt werden
			probability += temp;
		}
		
		return probability;
	}
	
	/**
	 * Hier wird das korrekte Modell zur Klassifizierung geladen.
	 * 
	 * @param filtering 	{@code true}, Modell_2 wird zur Vorhersage geladen
	 * 						{@code false}, Modell_1 wird zur Vorhersage geladen
	 * 
	 * @throws IOException 
	 */
	private void load(boolean filtering) throws IOException {
		if(filtering) {
			
			this.businessDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_FILTERED_BUSINESS);
			this.politicsDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_FILTERED_POLITICS);
			this.scienceDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_FILTERED_SCIENCE);
			this.sportDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_FILTERED_SPORT);
			this.vocabulary = VocabularyCombiner.loadVocabulary(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + PATH_VOCABULARY_FILTERED);

		} else {
			
			this.businessDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_EVERYTHING_BUSINESS);
			this.politicsDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_EVERYTHING_POLITICS);
			this.scienceDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_EVERYTHING_SCIENCE);
			this.sportDictionary = DictionaryCreator.loadDictionary(DictionaryCreator.PATH_DIRECTORY_DICTIONARIES + PATH_DICTIONARY_EVERYTHING_SPORT);
			this.vocabulary = VocabularyCombiner.loadVocabulary(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + PATH_VOCABULARY_EVERYTHING);
		}
	}
	
	/**
	 * Methode, um Logging zu erleichtern.
	 * 
	 * @param i Die Zahl, dier ausgegeben werden soll
	 */
	private void log(int i) {
		System.out.println("Classified Article: " + i);
	}
}