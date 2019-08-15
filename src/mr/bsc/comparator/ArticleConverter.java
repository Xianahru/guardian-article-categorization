package mr.bsc.comparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mr.bsc.guardian.NewspaperArticle;
import mr.bsc.guardian.NewspaperArticleCollection;


/**
 * Diese Klasse dient dazu, per Hand ausgesuchte Artikel in eine {@link NewspaperArticleCollection} zu überführen.
 * Der Artikel muss dafür in einer normalen txt-Datei vorhanden sein. Die erste Zeile dieser Datei wird dann als Überschrift behandelt, 
 * die restlichen Zeilen werden als Körper des Artikels behandelt.
 * 
 * @author Marius Rosenbaum
 *
 */
public class ArticleConverter {

	/**
	 * Hier werden die Zeitungsartikel aller 4 Kategorien in nutzbare JSON-Dateien umgewandelt.
	 * 
	 * @param args
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File businessDir = new File("E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Business\\");
		convertArticles(businessDir, gson, new String("Business"));
		File politicsDir = new File("E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Politics\\");
		convertArticles(politicsDir, gson, new String("Politics"));
		File scienceDir = new File("E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Science\\");
		convertArticles(scienceDir, gson, new String("Science"));
		File sportDir = new File("E:\\Bachelorarbeit Texte\\Aehnlichkeit\\Sport\\");
		convertArticles(sportDir, gson, new String("Sport"));
	}
	
	/**
	 * Diese Methode führt alle Dateien in einem gewählten Verzeichnis zusammen in eine JSON-Datei.
	 * Diese Datei kann dann mit {@link Gson} in eine {@link NewspaperArticleCollection} deserialisiert werden.
	 * 
	 * @param directory Das Verzeichnis, aus dessen alle Dateien zusammengeführt werden sollen
	 * @param gson		Das {@link Gson}-Objekt, das für das deserialisieren zuständig ist
	 * @param topic		Die Kategorie. Wird dafür genutzt, die entstandene JSON-Datei zu benennen.
	 * 
	 * @throws IOException
	 */
	private static void convertArticles(File directory, Gson gson, String topic) throws IOException {
		NewspaperArticleCollection articles = new NewspaperArticleCollection();
		
		File[] directoryListing = directory.listFiles();
		if(directoryListing != null) {
			BufferedReader reader = null;
			for (File f : directoryListing) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
				String title = reader.readLine(); //Erste Zeile ist der Titel
				System.out.println(title);
				String content = "";
				String line;
				//Der Rest ist der Textkörper des Artikels
				do {
					line = reader.readLine();
					if (line != null) content += line;
				} while (line != null);
				System.out.println(content);
				
				articles.add(new NewspaperArticle(title, content));
			}
			reader.close();
			//Hier wird die Datei erstellt
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\Bachelorarbeit Texte\\Aehnlichkeit\\" + topic + "\\" + topic + ".json"), StandardCharsets.UTF_8));
			writer.write(gson.toJson(articles));
			writer.close();
		}
	}
}
