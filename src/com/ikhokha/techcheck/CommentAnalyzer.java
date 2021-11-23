package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAnalyzer {
	
	private File file;
	private List<String> terms;
	
	public CommentAnalyzer(File file) {
		this.file = file;
		this.terms = new ArrayList<String>();
	}
	
	public Map<String, Integer> analyze() {
		
		Map<String, Integer> resultsMap = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			
			String line = null;
			while ((line = reader.readLine()) != null) {

				// standardizing the input for better results
				line = line.toLowerCase();

				if (line.length() < 15) {
					incOccurrence(resultsMap, "SHORTER_THAN_15");
				} else {
					for (String term : this.terms) {
						// 	this code showed me that i perhaps messed up part 1
						// 	there was a dicrepency in shaker counts because there was a else if branch here
						// 	it did not account for the fact that users may talk about both products in the same sentence
						if (line.contains(term)) {
							incOccurrence(resultsMap, String.format("%s_MENTIONS", term.toUpperCase()));
						}
					}
				}

				// } else if (line.contains("mover")) {
				// 	// System.out.println("mover");
				// 	incOccurrence(resultsMap, "MOVER_MENTIONS");
				
				// } else if (line.contains("shaker")) {

				// 	incOccurrence(resultsMap, "SHAKER_MENTIONS");
				
				// }
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error processing file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
		
		return resultsMap;
		
	}
	
	/**
	 * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
	 * @param countMap the map that keeps track of counts
	 * @param key the key for the value to increment
	 */
	private void incOccurrence(Map<String, Integer> countMap, String key) {
		
		countMap.putIfAbsent(key, 0);
		countMap.put(key, countMap.get(key) + 1);
	}

	/**
	 * This method allows us to add more keywords to filter in our report
	 * @param terms string of terms split by a ","
	 */
	public List<String> addMetricTerms(String terms) {
		String[] termsArr = terms.split(",");
		for (String newTerm : termsArr) {
			newTerm = newTerm.toLowerCase();
			if(!this.terms.contains(newTerm)) {
				this.terms.add(newTerm);
			}
		}
		return this.terms;
	}

}
