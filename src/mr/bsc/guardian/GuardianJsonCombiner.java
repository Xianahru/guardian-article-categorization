package mr.bsc.guardian;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Dieses Programm kombiniert die Antworten der Guardian-API in eine {@link NewspaperArticleCollection}. Es entstehen so pro Kategorie
 * vier verschiedene Dateien, je eine für BUSINESS, POLITICS, SCIENCE und SPORT. Dabei kann ausgewählt werden, ob die Antworten zum
 * Trainingsdatensatz oder zum Testdatensatz kombiniert werden sollen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class GuardianJsonCombiner {
	
	private final static String training = "E:\\Bachelorarbeit Texte\\TheGuardian\\training\\";
	private final static String test = "E:\\Bachelorarbeit Texte\\TheGuardian\\test\\";
	
	/*
	 * Variablen, um das richtige Verzeichnis zu identifizieren
	 */
	private final static String BUSINESS = "business";
	private final static String POLITICS = "politics";
	private final static String SCIENCE = "science";
	private final static String SPORT = "sport";
	
	
	private final static boolean combineTraining = false; //Legt fest, ob der Trainingsdatensatz erstellt werden soll, oder der Testdatensatz
	

	/**
	 * Hier werden die Antworten in jeder Kategorie zusammengefasst.
	 * 
	 * @param args
	 * 
	 * @throws IOException  bei der Ein- oder Ausgabe ist was schief gegangen. Häufiger Fehler ist das Vorhandsein von einem bereits kombinierten Datensatz,
	 * 						welcher nicht die gleiche Struktur aufweist.
	 */
	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Formatiertes JSON mit 40 000 Zeilen ist schöner als alles in einer Zeile zu schreiben.
		
		String path;
		/*
		 * Training oder Test
		 */
		if(combineTraining) {
			path = training;
		} else {
			path = test;
		}
		
		NewspaperArticleCollection businessArticles = combineJson(path + BUSINESS, gson); 	//Erstellen der NewspaperArticleCollection BUSINESS
		NewspaperArticleCollection politicsArticles = combineJson(path + POLITICS, gson); 	//Erstellen der NewspaperArticleCollection POLITICS
		NewspaperArticleCollection scienceArticles = combineJson(path + SCIENCE, gson); 	//Erstellen der NewspaperArticleCollection SCIENCE
		NewspaperArticleCollection sportArticles = combineJson(path + SPORT, gson); 		//Erstellen der NewspaperArticleCollection SPORT
		
		saveNewspaperArticleCollectionToFile(businessArticles, combineTraining, gson, BUSINESS);	//Serialisierung der NewspaperArticleCollection BUSINESS
		saveNewspaperArticleCollectionToFile(politicsArticles, combineTraining, gson, POLITICS);	//Serialisierung der NewspaperArticleCollection POLITICS
		saveNewspaperArticleCollectionToFile(scienceArticles, combineTraining, gson, SCIENCE);		//Serialisierung der NewspaperArticleCollection SCIENCE
		saveNewspaperArticleCollectionToFile(sportArticles, combineTraining, gson, SPORT);			//Serialisierung der NewspaperArticleCollection SPORT
		
	}
	
	/**
	 * Diese Methode führt die Serialisierung einer NewspaperArticleCollection durch.
	 * 
	 * @param articles 	die {@link NewspaperArticleCollection}, die serialisiert werden soll.
	 * @param training 	handelt es sich um den Trainingsdatensatz oder um den Testdatensatz?
	 * @param gson		zur Serialisierung benötigtes {@link GSON}.
	 * @param category	Kategorie (Business, Politics, Science, Sport).
	 * 
	 * @throws IOException bei der Ein- oder Ausgabe ist was schief gegangen.
	 */
	private static void saveNewspaperArticleCollectionToFile(NewspaperArticleCollection articles, boolean training, Gson gson, String category) throws IOException {
		String purpose;
		if(training) {
			purpose = "Training";
		} else {
			purpose = "Test";
		}
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\Bachelorarbeit Texte\\TheGuardian\\" + category + purpose +".json"), StandardCharsets.UTF_8));
		writer.write(gson.toJson(articles));
		writer.close();
	}
	
	/**
	 * Diese Methode lädt eine Antwort der Guardian-API.
	 * 
	 * @param f		die Datei, die geladen werden soll.
	 * @param gson	zur Deserialisierung benötigtes {@link GSON}.
	 * 
	 * @return Die Antwort der Guardian-API als {@link ResponseWrapper}
	 * 
	 * @throws FileNotFoundException	die angegebene Datei der korrespondierenden API-Antwort konnte nicht gefunden werden.
	 */
	private static ResponseWrapper loadResponseWrapper(File f, Gson gson) throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
		return gson.fromJson(reader, ResponseWrapper.class);
	}
	
	/**
	 * Diese Methode kombiniert alle Dateien im angegebenen Verzeichnis. Deshalb ist es wichtig, dass nur sich solche Dateien in diesem
	 * Verzeichnis befinden, die kombiniert werden sollen, um ein fehlerfreies Kombinieren zu ermöglichen.
	 * 
	 * @param path	das Verzeichnis, in dem die Dateien sich befinden.
	 * @param gson	zur Deserialisierung benötigtes {@link GSON}.
	 * 
	 * @return Die kombinierten Antworten der Guardian-API als {@link NewspaperArticleCollection}
	 * 
	 * @throws FileNotFoundException	die angegebene Datei der korrespondierenden API-Antwort konnte nicht gefunden werden.
	 */
	private static NewspaperArticleCollection combineJson(String path, Gson gson) throws FileNotFoundException {
		NewspaperArticleCollection articles = new NewspaperArticleCollection();
		
		File dir = new File(path + "\\");
		File[] directoryListing = dir.listFiles();
		
		if(directoryListing != null) {
			for (File f : directoryListing) {
				ResponseWrapper re = loadResponseWrapper(f, gson);
				for(Result r : re.response.results) {
					if(r.fields.bodyText.length() > 0) {
						articles.add(new NewspaperArticle(r.webTitle, r.fields.bodyText));	
					} else {
						System.out.println(r.webTitle + " hat keinen Content!");
					}		
				}
			}
		}
		
		return articles;
	}
	
}
