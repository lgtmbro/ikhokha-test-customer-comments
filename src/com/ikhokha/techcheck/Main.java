package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) {
		List<Thread> threadsInUse = new ArrayList<Thread>();;
		Map<String, Integer> results = new HashMap<String, Integer>();
		int docsProcessed = 0;
		int maxThreads = 3; //default unless provided as a --thread x param
		String keywordsToFind = "ik pos,mover,shaker";

		int argPos;
		for(argPos = 0; argPos < args.length; argPos++) {
            if(args[argPos].contains("--threads")) {
				maxThreads = Integer.parseInt(args[argPos + 1]);
			}
		}

		System.out.println(String.format("RUNNING REPORTS ON %s THREADS", maxThreads));
				
		File docPath = new File("docs");
		File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

		System.out.println(String.format("%s DOCS FOUND", commentFiles.length));

		final long startTime = System.currentTimeMillis();
		
		while(docsProcessed != commentFiles.length) {
			if (threadsInUse.size() < maxThreads) {
				CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFiles[docsProcessed], keywordsToFind, results);
				threadsInUse.add(new Thread(commentAnalyzer));
				threadsInUse.get(threadsInUse.size() - 1).start();
				docsProcessed++;
			}
			
			List<Thread> deadThreads = new ArrayList<Thread>();

			for (Thread commentAnalyzerThread : threadsInUse) {
				if (!commentAnalyzerThread.isAlive()) {
					deadThreads.add(commentAnalyzerThread);
				}
			}

			for (Thread deadThread : deadThreads) {
				threadsInUse.remove(deadThread);
			}
		}
		
		System.out.println("RESULTS\n=======");
		results.forEach((k,v) -> System.out.println(k + " : " + v));
		System.out.println(String.format("Completed in %s ms", (System.currentTimeMillis() - startTime)));
	}

}
