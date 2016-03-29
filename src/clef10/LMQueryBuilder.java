package clef10;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.terrier.structures.Index;

/**
 * Connects to the Terrier Index and read terms and frequencies for a given set of documents (representing test set of CLEF 2010)
 * Compute KL-divergence between language model of the query document and the background language model (of collection)
 * Sort term based on KL-divergence and get top k terms
 * For each query print top k terms of the query (abiding by the format of Terrier Query language)
 */
public class LMQueryBuilder {

	static { System.setProperty("terrier.home","/Users/usiusi/tools/terrier-3.0"); }
	private static Index index;
	private static long N;

	private LMQueryBuilder(String indexDirectory, String indexPrefix) {
		index = Index.createIndex(indexDirectory, indexPrefix);
		N = index.getCollectionStatistics().getNumberOfTokens();

		System.out.println("Number of Documents: " + index.getCollectionStatistics().getNumberOfDocuments());
		System.out.println("Number of Tokens: "	+ index.getCollectionStatistics().getNumberOfTokens());
		System.out.println("Number of Unique Terms: " + index.getCollectionStatistics().getNumberOfUniqueTerms());
	}

	public static void main(String[] args) throws FileNotFoundException {
		String indexPrefix = "data";
		String indexDirectory = "/Users/usiusi/tools/terrier-3.0/var/index";
		LMQueryBuilder LMQueryAbstractBuilder = new LMQueryBuilder(indexDirectory, indexPrefix);

		int topTerms;
		if(args.length>0)
			topTerms = Integer.parseInt(args[0]);
		else
		{
			System.out.println("top terms is missing");
			return;
		}
		int weightedOutputQuery;
		if(args.length>1)
			weightedOutputQuery = Integer.parseInt(args[1]);
		else
		{
			System.out.println("weightedOutputQuery is missing");
			return;
		}

		String sectionName;
		if(args.length>2)
			sectionName = args[2];
		else
		{
			System.out.println("section name is missing");
			return;
		}
		String docIdsList;
		if(args.length>3)
			docIdsList = args[3];
		else
		{
			System.out.println("docId list is missing");
			return;
		}

		String printFileName;
		if(args.length>4)
			printFileName = args[4];
		else
		{
			System.out.println("printFilename is missing");
			return;
		}
		int freqThreshold;
		if(args.length>5)
			freqThreshold = Integer.parseInt(args[5]);
		else
		{
			System.out.println("freq threshold is missing");
			return;
		}

		HashMap<String, Integer > docIds = LMQueryAbstractBuilder.readDocIdsFromIndex(docIdsList);
	
		for (String queryId: docIds.keySet()){
			HashMap<Integer,Double> queryMLEstimate = Utils.computeAvgRelFreq(docIds.get(queryId), freqThreshold, index);
			ArrayList<IntDouble> queryKLDivergence = Utils.computeKLDivergence(queryMLEstimate, index, N);

			if(weightedOutputQuery == 3 /* normalized weights */){
				String filename = printFileName+"/normwFreqT"+freqThreshold+sectionName+ "Top"+topTerms+".txt";
				LMQueryAbstractBuilder.printQueryTermsAndNormalizedWeights(queryKLDivergence, topTerms,filename,queryId);
			}
		}


			
	}

	private HashMap <String, Integer> readDocIdsFromIndex(String filename) {
		HashMap <String, Integer> docIdsMap = new HashMap <>();
		
		try {
			Scanner scanner = new Scanner(new File(filename));
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("\\s+");
				docIdsMap.put( line[0]  , index.getMetaIndex().getDocument("docno", line[1]) );
			}
		} 
		catch (Exception e) { e.printStackTrace(); }
		return docIdsMap;
	}

	private void printQueryTermsAndNormalizedWeights(ArrayList<IntDouble> weights, int max, String filename, String queryid)  {
		int i;
		PrintWriter p = null;
		try {
			p = new PrintWriter(new FileWriter(new File(filename), true /* append */));
			p.println("<topic>");
			p.println("<num> " + queryid + " </num> ");
			p.print("<narr> ");
			i=0;
			double sum=0.0;
			Collections.sort(weights);
			for (IntDouble idWeight: weights){
				if(i==max) break;
				sum+=idWeight.getVal();
				i++;
			}
			i=0;
			for (IntDouble idWeight: weights) {
				if (i == max) break;
				String term = index.getLexicon().getLexiconEntry(idWeight.getId()).getKey();
				term =  term.replaceAll("[^a-zA-Z]", " ").trim();

				if(!term.trim().equals("") && term.length()>=3 ){
					p.print(" "+term + "^" + (idWeight.getVal())/sum );
					i++;
				}
			}
			
			p.println(" </narr> ");
			p.println("</topic> ");
			p.println();
		}
		catch (IOException e)
	    {
	      e.printStackTrace();
	      // deal with the exception
	    }
	    finally
	    {
			if (p != null) {
				p.close();
			}
		}
		
	}
}
