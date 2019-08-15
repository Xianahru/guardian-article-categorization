package mr.bsc.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mr.bsc.guardian.NewspaperArticle;
import mr.bsc.guardian.NewspaperArticleCollection;

/**
 * Diese Klasse erstellt die {@link Dictionary}-Objekte, die als Vorhersagemodelle dienen. Nebenbei werden {@link Vocabulary}-Objekte erstellt.
 * Diese Klasse implementiert {@link Runnable}, um das gleichzeitige Erstellen von {@link Dictionary}-Objekten zu ermöglichen.
 * 
 * @author Marius Rosenbaum
 *
 */
public class DictionaryCreator implements Runnable {

	public final static String PATH_DIRECTORY_DICTIONARIES = "E:\\Bachelorarbeit Texte\\TheGuardian\\dictionaries\\";
	public final static String PATH_DIRECTORY_VOCABULARY = "E:\\Bachelorarbeit Texte\\TheGuardian\\vocabulary\\";
	public final static String PATH_STOPWORDS = "E:\\Bachelorarbeit Texte\\TheGuardian\\stopwords.json";
	public final static String PATH_SYMBOLS = "E:\\Bachelorarbeit Texte\\TheGuardian\\symbols.json";
	
	private static Gson gson =  new GsonBuilder().setPrettyPrinting().create(); //Formatierte JSON-Dateien sind wichtig!
	
	private String topic;
	private String articleCollection;
	private boolean filtering;	//Werden Symbole und Stoppwörter in diesem Thread rausgefiltert und Eigennamen erkannt oder nicht?
	
	public DictionaryCreator(String topic, String articleCollection, boolean filtering) {
		this.topic = topic;
		this.articleCollection = articleCollection;
		this.filtering = filtering;
	}
	
	/**
	 * Liest eine {@link NewspaperArticleCollection} aus einer JSON-Datei ein.
	 * 
	 * @param collectionPath	Der Pfad zur JSON-Datei.
	 * 
	 * @return Eine {@link NewspaperArticleCollection}
	 * 
	 * @throws IOException
	 */
	public static NewspaperArticleCollection loadNewspaperCollection(String collectionPath) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(collectionPath), StandardCharsets.UTF_8));
		NewspaperArticleCollection collection = gson.fromJson(reader, NewspaperArticleCollection.class);
		reader.close();
		return collection;
	}
	
	/**
	 * Liest ein {@link Dictionary} aus einer JSON-Datei ein.
	 * 
	 * @param dictionaryPath	Der Pfad zur JSON-Datei.
	 * 
	 * @return Ein {@link Dictionary}
	 * 
	 * @throws IOException
	 */
	public static Dictionary loadDictionary(String dictionaryPath) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryPath), StandardCharsets.UTF_8));
		Dictionary dic = gson.fromJson(reader, Dictionary.class);
		reader.close();
		return dic;
	}
	
	@Override
	public void run() {
		try {
			createDictionary(this.articleCollection, this.topic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Diese Methode erstellt für eine Kategorie ein {@link Dictionary}-Objekt und ein {@link Vocabulary}-Objekt her.
	 * 
	 * @param articleCollection	Die Textsammlung, für die ein {@link Dictionary}-Objekt und ein {@link Vocabulary}-Objekt erstellt werden soll.
	 * @param category	Die Kategorie der Textsammlung. Wird nur für den Namen der am Ende erstellten JSON-Dateien genutzt.
	 * 
	 * @throws IOException
	 */
	private void createDictionary(String articleCollection, String category) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Dictionary dic = new Dictionary();
		Vocabulary voc = new Vocabulary();

		NewspaperArticleCollection articles = DictionaryCreator.loadNewspaperCollection(articleCollection);
		
		int i = 1;
		ArticleAnalyzer analyzer = new ArticleAnalyzer();
		for(NewspaperArticle n : articles.getArticles()) {
			HashMap<String, Integer> entries = analyzer.analyze(n, this.filtering);
			dic.add(entries);
			voc.add(entries);
			
			System.out.println(i + ". Artikel verarbeitet!");
			i++;
		}
		
		String dictionaryOutput;
		String vocabularyOutput;
		
		if(filtering) {
			dictionaryOutput = PATH_DIRECTORY_DICTIONARIES + category + "DictionaryCleanFiltered.json";
			vocabularyOutput = PATH_DIRECTORY_VOCABULARY + category + "VocabularyCleanFiltered.json";
		} else {
			dictionaryOutput = PATH_DIRECTORY_DICTIONARIES + category + "DictionaryCleaned.json";
			vocabularyOutput = PATH_DIRECTORY_VOCABULARY + category + "VocabularyCleaned.json";
		}
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionaryOutput), StandardCharsets.UTF_8));
		writer.write(gson.toJson(dic));
		writer.close();
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vocabularyOutput), StandardCharsets.UTF_8));
		writer.write(gson.toJson(voc));
		writer.close();
		
	}

}
