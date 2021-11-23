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
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class CommentAnalyzer implements Callable<Map<String, Integer>>{
	
	private File file;
	private List<String> terms;

	@Override
	public Map<String, Integer> call() {
		return this.analyze();
	}
	
	public CommentAnalyzer(File file, String terms) {
		this.file = file;
		this.terms = new ArrayList<String>();
		this.addKeywords(terms);
	}
	
	public Map<String, Integer> analyze() {

		Map<String, Integer> resultsMap = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			
			String line = null;
			while ((line = reader.readLine()) != null) {

				line = line.toLowerCase();

				if (line.length() < 15) {
					incOccurrence(resultsMap, "SHORTER_THAN_15");
				}

				if (line.contains("?")) {
					incOccurrence(resultsMap, "QUESTIONS");
				}

				Pattern urlPattern = Pattern.compile(
					"\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
					Pattern.CASE_INSENSITIVE);
				if (urlPattern.matcher(line).find()) {
					incOccurrence(resultsMap, "SPAM");
				}

				for (String term : this.terms) {
					if (line.contains(term)) {
						incOccurrence(resultsMap, String.format("%s_MENTIONS", term.toUpperCase()));
					}
				}
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
	public List<String> addKeywords(String terms) {
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
