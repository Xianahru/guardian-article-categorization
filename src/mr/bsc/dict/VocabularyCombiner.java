package mr.bsc.dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Diese Klasse kombiniert 4 Vocabulary-Objekte von 4 Kategorien.
 * 
 * @author Marius Rosenbaum
 *
 */
public class VocabularyCombiner {
	
	public static final String outputDirectory = "E:\\Bachelorarbeit Texte\\TheGuardian\\vocabulary\\";
	
	/**
	 * Hier werden die Vocabulary-Objekte kombiniert.
	 * 
	 * @param businessPath	Der Dateipfad zum BUSINESS-Vocabulary
	 * @param politicsPath	Der Dateipfad zum POLITICS-Vocabulary
	 * @param sciencePath	Der Dateipfad zum SCIENCE-Vocabulary
	 * @param sportPath		Der Dateipfad zum SPORT-Vocabulary
	 * 
	 * @throws IOException	Beim Laden oder Schreiben der Vocabulary-Objekte ist was schiefgegangen. Sind die Pfade alle korrekt? 
	 */
	public void combine(String businessPath, String politicsPath, String sciencePath, String sportPath) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Vocabulary total = new Vocabulary();
		
		int j = 1;
		
		LinkedList<String> topics = new LinkedList<String>();
		topics.add(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + businessPath);
		topics.add(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + politicsPath);
		topics.add(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + sciencePath);
		topics.add(DictionaryCreator.PATH_DIRECTORY_VOCABULARY + sportPath);
		
		for(String topic : topics) {
			Vocabulary voc = VocabularyCombiner.loadVocabulary(topic);
			int size = voc.size();
			int i = 1;
			for(String word : voc.getWords()) {
				total.add(word);
				System.out.println("Topic " + j + ": " + i + " Word(s) of " + size + " finished");
				i++;
			}
			System.out.println("Topic " + j + " finished.");
			j++;
		}
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDirectory + "combinedVocabularyCleanFiltered.json"), StandardCharsets.UTF_8));
		writer.write(gson.toJson(total));
		writer.close();
	}
	
	/**
	 * Diese Methode deserialisiert mit {@link GSON} ein Vocabulary-Objekt.
	 * 
	 * @param vocabularyPath	Der Pfad zur JSON-Datei des Vocabulary-Objektes, das deserialisiert werden soll. 
	 * 
	 * @return	Das deserialisierte Vocabulary-Objekt aus der JSON-Datei
	 * 
	 * @throws IOException	Beim Laden des Vocabulary-Objekts ist was schiefgegangen. Ist der Pfad korrekt? 
	 */
	public static Vocabulary loadVocabulary(String vocabularyPath) throws IOException {
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(vocabularyPath), StandardCharsets.UTF_8));
		Vocabulary voc = gson.fromJson(reader, Vocabulary.class);
		reader.close();
		return voc;
	}
}
