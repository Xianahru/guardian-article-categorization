package mr.bsc.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mr.bsc.classifier.NaiveBayesClassifier;
import mr.bsc.comparator.ArticleComparator;
import mr.bsc.dict.DictionaryCreator;
import mr.bsc.dict.VocabularyCombiner;

public class Application {
	
	public final static String PATH_DIRECTORY_TRAINING_BUSINESS = "E:\\Bachelorarbeit Texte\\TheGuardian\\training\\business\\businessTrainingCleaned.json";
	public final static String PATH_DIRECTORY_TRAINING_POLITICS = "E:\\Bachelorarbeit Texte\\TheGuardian\\training\\politics\\politicsTrainingCleaned.json";
	public final static String PATH_DIRECTORY_TRAINING_SCIENCE = "E:\\Bachelorarbeit Texte\\TheGuardian\\training\\science\\scienceTrainingCleaned.json";
	public final static String PATH_DIRECTORY_TRAINING_SPORT = "E:\\Bachelorarbeit Texte\\TheGuardian\\training\\sport\\sportTrainingCleaned.json";
	
	public final static String PATH_DIRECTORY_VOCABULARY_BUSINESS = "cleanFiltered\\businessVocabularyCleanFiltered.json";
	public final static String PATH_DIRECTORY_VOCABULARY_POLITICS = "cleanFiltered\\politicsVocabularyCleanFiltered.json";
	public final static String PATH_DIRECTORY_VOCABULARY_SCIENCE = "cleanFiltered\\scienceVocabularyCleanFiltered.json";
	public final static String PATH_DIRECTORY_VOCABULARY_SPORT = "cleanFiltered\\sportVocabularyCleanFiltered.json";
	
//	public final static String PATH_DIRECTORY_VOCABULARY_BUSINESS = "cleaned\\businessVocabularyCleaned.json";
//	public final static String PATH_DIRECTORY_VOCABULARY_POLITICS = "cleaned\\politicsVocabularyCleaned.json";
//	public final static String PATH_DIRECTORY_VOCABULARY_SCIENCE = "cleaned\\scienceVocabularyCleaned.json";
//	public final static String PATH_DIRECTORY_VOCABULARY_SPORT = "cleaned\\sportVocabularyCleaned.json";
	
	public final static String PATH_DIRECTORY_TEST_BUSINESS = "E:\\Bachelorarbeit Texte\\TheGuardian\\test\\business\\businessTestCleaned.json";
	public final static String PATH_DIRECTORY_TEST_POLITICS = "E:\\Bachelorarbeit Texte\\TheGuardian\\test\\politics\\politicsTestCleaned.json";
	public final static String PATH_DIRECTORY_TEST_SCIENCE = "E:\\Bachelorarbeit Texte\\TheGuardian\\test\\science\\scienceTestCleaned.json";
	public final static String PATH_DIRECTORY_TEST_SPORT = "E:\\Bachelorarbeit Texte\\TheGuardian\\test\\sport\\sportTestCleaned.json";
	
	public final static String PATH_DIRECTORY_SIMILARITY_BUSINESS = "E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Business\\Business.json";
	public final static String PATH_DIRECTORY_SIMILARITY_POLITICS = "E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Politics\\Politics.json";
	public final static String PATH_DIRECTORY_SIMILARITY_SCIENCE = "E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Science\\Science.json";
	public final static String PATH_DIRECTORY_SIMILARITY_SPORT = "E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Sport\\Sport.json";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		long startTime = System.nanoTime();
//		createDictionaries(true);
//		combineVocabulary();
//		classifyDocuments(false);
//		measureArticleSimilarty(true);
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Programmdauer in Nanosekunden : " + estimatedTime);		
	}
	
	/**
	 * Diese Methode erstellt für jede Kategorie ein Dictionary.
	 * 
	 * @param filtering <p>{@code true}, eine Filterung wie bei Modell_2 wird durchgeführt</p>
	 * 					<p>{@code false}, keine Filterung wird durchgeführt</p>
	 * 
	 * @throws InterruptedException
	 */
	public static void createDictionaries(boolean filtering) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		
        tasks.add(Executors.callable(new DictionaryCreator("business", PATH_DIRECTORY_TRAINING_BUSINESS, filtering)));
        tasks.add(Executors.callable(new DictionaryCreator("politics", PATH_DIRECTORY_TRAINING_POLITICS, filtering)));
        tasks.add(Executors.callable(new DictionaryCreator("science", PATH_DIRECTORY_TRAINING_SCIENCE, filtering)));
        tasks.add(Executors.callable(new DictionaryCreator("sport", PATH_DIRECTORY_TRAINING_SPORT, filtering)));
        
        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(15L, TimeUnit.MINUTES);
	}
	
	/**
	 * Mit dieser Methode wird das Vokabular von 4 Kategorien kombiniert.
	 * 
	 * @throws IOException
	 */
	public static void combineVocabulary() throws IOException {
		VocabularyCombiner vocCombiner = new VocabularyCombiner();
		vocCombiner.combine(PATH_DIRECTORY_VOCABULARY_BUSINESS, PATH_DIRECTORY_VOCABULARY_POLITICS, PATH_DIRECTORY_VOCABULARY_SCIENCE, PATH_DIRECTORY_VOCABULARY_SPORT);
	}
	
	/**
	 * Mit dieser Methode wird das Testset klassifiziert.
	 * 
	 * @param filtering <p>{@code true}, Modell_2 wird zur Vorhersage benutzt</p>
	 * 					<p>{@code false}, Modell_1 wird zur Vorhersage benutzt</p>
	 * 
	 * @throws IOException
	 */
	public static void classifyDocuments(boolean filtering) throws IOException {
		NaiveBayesClassifier classifier = new NaiveBayesClassifier();
		classifier.classifyArticles(PATH_DIRECTORY_TEST_BUSINESS, PATH_DIRECTORY_TEST_POLITICS, PATH_DIRECTORY_TEST_SCIENCE, PATH_DIRECTORY_TEST_SPORT, filtering);
	}
	
	/**
	 * 
	 * @param filtering <p>{@code true}, eine Filterung wie bei Modell_2 wird durchgeführt</p>
	 * 					<p>{@code false}, keine Filterung wird durchgeführt</p>
	 * 
	 * @throws IOException
	 */
	public static void measureArticleSimilarty(boolean filtering) throws IOException {
		ArticleComparator comp = new ArticleComparator();
		comp.measureSimilartiy(PATH_DIRECTORY_SIMILARITY_BUSINESS, filtering);
		comp.measureSimilartiy(PATH_DIRECTORY_SIMILARITY_POLITICS, filtering);
		comp.measureSimilartiy(PATH_DIRECTORY_SIMILARITY_SCIENCE, filtering);
		comp.measureSimilartiy(PATH_DIRECTORY_SIMILARITY_SPORT, filtering);
	}
	
}
