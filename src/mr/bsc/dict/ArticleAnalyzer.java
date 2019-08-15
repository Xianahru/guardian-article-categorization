package mr.bsc.dict;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import mr.bsc.guardian.NewspaperArticle;

/**
 * Diese Klasse vereint zwei Methoden, um einen Zeitungsartikel in seine Wörter zu zerlegen.
 * Die Methode {@link #analyze(NewspaperArticle, boolean)} entscheidet dabei, ob ein Zeitungsartikel ohne Filtern von Symbolen und
 * Stoppwörtern in <code>KEY:VALUE</code>-Paare zerlegt wird, oder mit.
 * 
 * @author Marius Rosenbaum
 *
 */
public class ArticleAnalyzer {
		
	private StopWords stop;
	private Symbols symbols;
	
	public ArticleAnalyzer() throws FileNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Wie wir mittlerweile wissen, ist ein hübsches JSON die halbe Miete!
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(DictionaryCreator.PATH_STOPWORDS), StandardCharsets.UTF_8));
		this.stop = gson.fromJson(reader, StopWords.class);	
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(DictionaryCreator.PATH_SYMBOLS), StandardCharsets.UTF_8));
		this.symbols = gson.fromJson(reader, Symbols.class);

	}
	
	/**
	 * Diese Methode entscheidet, wie ein Zeitungsartikel zerlegt wird. Wenn {@code filtering} true ist, dann werden Symbole und 
	 * Stoppwörter herausgefiltert. Gleichzeitig werden Komposita als solche erkannt. Benn {@code filtering} false ist, dann findet
	 * ein solches Filtern nicht statt.
	 * 
	 * @param n			Der Artikel, der zerlegt werden soll.
	 * @param filtering	Soll gefiltert werden oder nicht?
	 * 
	 * @return Der in {@code KEY:VALUE}-Paare zerlegte Zeitungsartikel in der Form einer {@code HashMap<String, Integer>}
	 */
	public HashMap<String, Integer> analyze(NewspaperArticle n, boolean filtering) {
		if(filtering) {
			return analyze(n, this.symbols, this.stop);
		} else {
			return analyze(n);
		}
	}
	
	/**
	 * Zerlegt einen Zeitungsartikel in {@code KEY:VALUE}-Paare, ohne ihn zu filtern.
	 * 
	 * @param n Der Artikel, der zerlegt werden soll.
	 * 
	 * @return Der in {@code KEY:VALUE}-Paare zerlegte Zeitungsartikel in der Form einer {@code HashMap<String, Integer>}
	 */
	private HashMap<String, Integer> analyze(NewspaperArticle n) {
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		
		Document doc = new Document(n.getHeadline() + ". " + n.getContent());
		for(Sentence sent : doc.sentences()) {
			for(String word : sent.words()) {
				String lemma = sent.lemma(sent.words().indexOf(word)).toLowerCase();
				if(words.keySet().contains(lemma)) {
					words.put(lemma, words.get(lemma) + 1);
				} else {
					words.put(lemma, 1);			
				}
			}
		}
		
		return words;
	}

	/**
	 * Zerlegt einen Zeitungsartikel in {@code KEY:VALUE}-Paare, und filtert dabei Symbole und Stoppwörter. Gleichzeitig werden Komposita
	 * als solche erkannt.
	 * 
	 * @param n 	Der Artikel, der zerlegt werden soll.
	 * @param sym	Die Symbole, die rausgefiltert werden sollen.
	 * @param stop	Die Stoppwörter, die rausgefiltert werden sollen.
	 * 
	 * @return Der in {@code KEY:VALUE}-Paare zerlegte Zeitungsartikel in der Form einer {@code HashMap<String, Integer>}
	 */
	private HashMap<String, Integer> analyze(NewspaperArticle n, Symbols sym, StopWords stop) {
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		
		Document doc = new Document(n.getHeadline() + ". " + n.getContent());
		for(Sentence sent : doc.sentences()) {
			ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
			
			/**
			 * Wenn der Satz nur 1 Wort enthält, dann können keine Komposita enthalten sein.
			 */
			if(sent.words().size() >= 2) {
				ArrayList<Integer> nameIndexes = findNameIndexes(sent.posTags());
				ArrayList<String> names = generateWordsFromIndexes(sent, nameIndexes);
				for(String name : names) {
					if(words.keySet().contains(name)) {
						words.put(name, words.get(name) + 1);
					} else {
						words.put(name, 1);			
					}
				}
				
				ArrayList<Integer> compoundNounIndexes = findCompoundNounIndexes(sent.posTags());
				ArrayList<String> compoundNouns = generateWordsFromIndexes(sent, compoundNounIndexes);
				for(String s : compoundNouns) {
					String noun = s.toLowerCase();
					if(words.keySet().contains(noun)) {
						words.put(noun, words.get(noun) + 1);
					} else {
						words.put(noun, 1);			
					}
				}
				
				//Fügt die Indizes der erkannten Namen und Komposita in eine Liste, damit diese Inzies übersprungen werden können.
				for(int index : nameIndexes) 			{ if(index != -1) usedIndexes.add(index);}
				for(int index : compoundNounIndexes) 	{ if(index != -1) usedIndexes.add(index);}
			}
				
			for(int j = 0; j < sent.words().size(); j++) {
				if(usedIndexes.contains(j)) { //Wurde bereits erkannt, kann überpsrungen werden.
					continue;
				} else {
					String lemma = sent.lemma(j).toLowerCase();
					if(isStoppwort(lemma, stop)) { //Ist das Wort ein Stoppwort und muss gefiltert werden?
						continue;
					} else if (isSymbol(lemma, sym)) { //Ist das Wort ein Symbol und muss gefiltert werden?
						continue;
					}
					//Wort kann hinzugefügt werden.
					if(words.keySet().contains(lemma)) { 
						words.put(lemma, words.get(lemma) + 1); //Wort enthalten, also Anzahl erhöhen.
					} else {
						words.put(lemma, 1); //Wort nicht enthalten, also neuen Eintrag anlegen.
					}
				}
			}
		}
		
		return words;
	}
	
	/**
	 * Überprüft, ob ein String im {@code Symbol}-Objekt enthalten ist.
	 * 
	 * @param word 	Der String, der überpüft werden soll.
	 * @param sym	Das {@code Symbol}-Objekt, das die zu filternden Symbole enthält.
	 * 
	 * @return true, falls es sich bei dem String um ein Symbol handelt.
	 */
	private boolean isSymbol(String word,  Symbols sym) {
		return sym.isSymbol(word);
	}
	
	/**
	 * Überprüft, ob ein String im {@code Stopwords}-Objekt enthalten ist.
	 * 
	 * @param word 	Der String, der überpüft werden soll.
	 * @param sym	Das {@code Stopwords}-Objekt, das die zu filternden Symbole enthält.
	 * 
	 * @return true, falls es sich bei dem String um ein Stoppwort handelt.
	 */
	private boolean isStoppwort(String word, StopWords stop) {
		return stop.isStoppwort(word);
	}
	
	/**
	 * Diese Methode findet die Namen in einem Satz.
	 * 
	 * @param tags	Eine Liste mit den POS-Tags eines Satzes.
	 * 
	 * @return Eine Liste mit den Indizes, an deren Position sich ein Name befindet.
	 */
	private ArrayList<Integer> findNameIndexes(List<String> tags) {
    	ArrayList<Integer> posTagIndexes = new ArrayList<Integer>();

    	int index = 0;
    	for(String tag : tags) {
    		if(tag.equals("NNP")||tag.equals("NNPS")){ //NNP und NNPS sind POS-Tags, die auf einen Namen hinweisen.
    			posTagIndexes.add(index);
    		} else {
    			posTagIndexes.add(-1);
    		}
    		index += 1;
    	}
    	
    	return posTagIndexes;
	}
	
	/**
	 * Diese Methode findet die Nomen in einem Satz. Sie wird benötigt, um Komposita zu erkennen.
	 * 
	 * @param tags	Eine Liste mit den POS-Tags eines Satzes.
	 * 
	 * @return Eine Liste mit den Indizes, an deren Position sich ein Nomen befindet.
	 */
	private ArrayList<Integer> findCompoundNounIndexes(List<String> tags) {    	
    	ArrayList<Integer> posTagIndexes = new ArrayList<Integer>();

    	int index = 0;
    	for(String tag : tags) {
    		if(tag.equals("NN")||tag.equals("NNS")){ //NN und NNS sind POS-Tags, die auf ein Nomen hinweisen.
    			posTagIndexes.add(index);
    		} else {
    			posTagIndexes.add(-1);	
    		}
    		index += 1;
    	}
    	
    	return posTagIndexes;
	}
	
	/**
	 * Diese Methode extrahiert aus einem gegebenen Satz die Wörter, deren Indizes sich in der übergebenen Liste befinden.
	 * Wörter, die aufeinanderfolgen, werden dabei als ein einzelner Begriff behandelt und zurückgegeben. So werden Eigennamen,
	 * die aus mehr als einem Wort bestehen (Angela Merkel) und Nominalkomposita (wasching machine) als ein Begriff erkannt.
	 * Nebenbei werden so alle Nomen, egal ob Nominalkomposita oder nicht, bereits extrahiert.
	 * 
	 * @param sent 		Der Satz, aus dem die Wörter generiert werden sollen.
	 * @param indizes	Die Indizes der Wörter, die extrahiert werden sollen.
	 * 
	 * @return Eine Liste von Wörtern
	 */
	private ArrayList<String> generateWordsFromIndexes(Sentence sent, ArrayList<Integer> indizes) {   	
    	ArrayList<String> words = new ArrayList<String>();
    	String temp = "";
    	for(int index : indizes) {
    		if(index != -1) {
    			//Wort soll extrahiert werden, muss hinzugefügt werden
    			if (temp.equals("")) {
    				//Neue Komposition - von vorne anfangen
    				temp += sent.lemma(index);        				
    			} else {
    				//Teil einer Komposition, hinten anhängen
    				temp += " " + sent.lemma(index);
    			}
    		} else {
    			//Handelt sich nicht um ein Wort, das extrahiert werden soll
    			//Wenn Wort angefangen wurde
    			//Gebautes Wort in die Liste eintragen, dann zurücksetzen auf ""
    			if(!temp.equals("")) {
    				words.add(temp);
    				temp = "";      				
    			}
    		}
    	}
    	return words;
	}
	
}
