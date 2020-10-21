package lse;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword. Every key in the keywordsIndex hash table is a keyword.
	 * The associated value for a keyword is an array list of (document,frequency) pairs for the documents in which the keyword occurs,
     * arranged in descending order of frequencies. A (document,frequency) pair is held in an Occurrence object.
     * The Occurrence class is defined in the LittleSearchEngine.java file, at the top.
     * In an Occurrence object, the document field is the name of the document, which is basically the file name, e.g. AliceCh1.txt. n documents.
     * The array list is maintained in DESCENDING order of frequencies.
	 */

	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */

	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */

	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile)
	throws FileNotFoundException {
	    HashMap<String, Occurrence> hashley = new HashMap<>();
	    Scanner sc = new Scanner(new File(docFile));

	   	while(sc.hasNext()) {
	   		String word = getKeyword(sc.next());

	   		if(word != null) {
	   		    if(hashley.containsKey(word)) {
                    hashley.get(word).frequency++;
                } else {
                    hashley.put(word, new Occurrence(docFile, 1));
                }
            }
		}

		return hashley;
	}

    /**
     * Merges the keywords for a single document into the master keywordsIndex
     * hash table. For each keyword, its Occurrence in the current document
     * must be inserted in the correct place (according to descending order of
     * frequency) in the same keyword's Occurrence list in the master hash table.
     * This is done by calling the insertLastOccurrence method.
     *
     * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
        for(String key : kws.keySet()) {
            if(keywordsIndex.containsKey(key)) {
                ArrayList<Occurrence> exists = keywordsIndex.get(key);
                exists.add(kws.get(key));

                insertLastOccurrence(exists);
                keywordsIndex.put(key, exists);
            } else {
                ArrayList<Occurrence> newList = new ArrayList<>();
                newList.add(kws.get(key));

                insertLastOccurrence(newList);
                keywordsIndex.put(key, newList);
            }
        }
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */

	public String getKeyword(String word) {
	    if(word.length() <= 1 || word == null)
	        return null;

		int breakPoint = word.length() - 1;

        while(!Character.isLetter(word.charAt(0))) {
            if(word.length() == 1)
                return null;
            else
                word = word.substring(1, word.length());
        }

	    while(!Character.isLetter(word.charAt(word.length() - 1))) {
            if(word.length() == 1)
                return null;
            else
                word = word.substring(0, word.length() - 1);
        }

	    if(!word.chars().allMatch(Character::isLetter) || noiseWords.contains(word))
	        return null;

		return word.toLowerCase();
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> ret = new ArrayList<>(), freq = new ArrayList<>();

		int target = occs.get(occs.size() - 1).frequency;
		int lo = 0, hi = occs.size() - 2;

		for(Occurrence occ : occs)
		    freq.add(occ.frequency);

		while(lo <= hi) {
		    int mid = (lo + hi) / 2;
		    ret.add(mid);

		    if(freq.get(mid) < target)
		        hi = mid - 1;
		    else if(freq.get(mid) > target)
		        lo = mid + 1;
		    else
		        break;
        }

		return ret;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
	    ArrayList<String> results = new ArrayList<>();
	    ArrayList<Occurrence> first = keywordsIndex.get(kw1), second = keywordsIndex.get(kw2);

	    if(first == null && second == null) {
	        return null;
        } else if(first == null) {
	        for(int i = 0; i < second.size() && i < 4; i++)
	            results.add(second.get(i).document);

            return reverse(results);
        } else if(second == null) {
	         for(int i = 0; i < first.size() && i < 4; i++)
	             results.add(first.get(i).document);

	         return reverse(results);
        } else {
	        while(results.size() < 5) {
	            if(first.isEmpty() && second.isEmpty()) {
                    return (results.isEmpty()) ? null : reverse(results);
                } else if(first.isEmpty()) {
	                while(!second.isEmpty() && results.size() < 5)
                        results.add(second.remove(0).document);

	                return reverse(results);
                } else if(second.isEmpty()) {
	                while(!first.isEmpty() && results.size() < 5)
                        results.add(first.remove(0).document);

	                return reverse(results);
                } else {
	                if(first.get(0).frequency >= second.get(0).frequency)
	                    results.add(first.remove(0).document);
	                else
	                    results.add(second.remove(0).document);
                }
            }
        }

		return reverse(results);
	}

	private ArrayList<String> reverse(ArrayList<String> oldList) {
	    ArrayList<String> ret = new ArrayList<>();

	    for(String i : oldList)
	        ret.add(0, i);

	    return ret;
    }
}