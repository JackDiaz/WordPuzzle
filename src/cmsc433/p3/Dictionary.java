// DO NOT CHANGE THIS CLASS
package cmsc433.p3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * The <code>Dictionary</code> is a set of words used to determine
 * whether a given solution for a grid is legal. The score for a given
 * word depends on its length and its frequency in the dictionary.
 * 
 * This class is immutable, so it is thread-safe.
 */
public class Dictionary implements Iterable<String> {
	public final int MAX_WORD_LEN = 50;
	private final HashSet<String> words;
	private final int[] dist;
	private final int[] score;
	private final int max;

	private Dictionary(HashSet<String> words) throws IllegalArgumentException {
		int[] cdist = new int[MAX_WORD_LEN];
		for (int i = 0; i<MAX_WORD_LEN; i++)
			cdist[i] = 0;
		for (String word: words) {
			int sz = word.length();
			if (sz > MAX_WORD_LEN) 
				throw new IllegalArgumentException("word too long");
			cdist[sz] ++;
		}
		int cmax = 0;
		for (int i = 0; i<MAX_WORD_LEN; i++)
			if (cmax < cdist[i]) cmax = cdist[i];
		this.words = words;
		this.dist = cdist;
		this.max = cmax;
		
		score = new int[MAX_WORD_LEN];
		score[0]=1;
		for(int i=1;i<=3;i++)
			score[i]=i;
		for(int i=4;i<MAX_WORD_LEN;i++)
		{
			score[i]=score[i-1]+1;
			if(dist[i]<=max/2)
				score[i]++;
			if(dist[i]<=max/4)
				score[i]++;
			if(dist[i]==0)
				score[i]=score[i-1];
		}
	}

	/**
	 * Constructs a dictionary from an input file. The file
	 * should contain one word per line. Duplicate words
	 * are discarded.
	 * @param file - contains the words for the dictionary
	 * @return a dictionary object representing these words
	 */
	public static Dictionary makeDict(String file) {
		HashSet<String> wordsFromFile = new HashSet<String>();
		BufferedReader fileReader = null;
		
		try {
			fileReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file);
			System.exit(1);
		}

		try {
			String line = fileReader.readLine();
			while (line != null) {
				wordsFromFile.add(line);
				line = fileReader.readLine();
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error while file was open: " + file);
			System.exit(1);
		}

		return new Dictionary(wordsFromFile);
	}
	
	/**
	 * Determines whether a given word is in the dictionary.
	 * @param word
	 * @return whether it appears in the dictionary
	 */
	public boolean member(String word) {
		return words.contains(word);
	}

	/**
	 * Computes the score for the given word, based on its length
	 * and the distribution of words having that length in the
	 * dictionary.
	 * @param word
	 * @return the score for the word
	 */
	public int score(String word) {
		if (!this.member(word)) return 0;
		return score[word.length()];
	}

	/**
	 * Returns the score that a word would have gotten based only on
	 * it's length. 
	 * @param wordLength
	 * @return the score for the word
	 */
	public int getScore(int wordLength)
	{
		return score[wordLength];
	}
	
	public Iterator<String> iterator() {
		return words.iterator();
	}
}