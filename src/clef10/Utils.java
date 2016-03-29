package clef10;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.terrier.structures.Index;
import org.terrier.structures.LexiconEntry;

/**
 * This class provides various utility functions to build a language model from the query
 * (a language model: a distribution of terms and their respective weights).
 */

public class Utils {
    /**
     * Calculates the avg relative frequency for terms of a given document
     */
    public static HashMap<Integer, Double> computeAvgRelFreq(int docId, int freqThreshold, Index index) {
        HashMap<Integer, Double> relFreq = new HashMap<>();
        try {

            int[][] terms = index.getDirectIndex().getTerms(docId);
            double docLength = index.getDocumentIndex().getDocumentLength(docId);
            if (terms != null) {
                for (int j = 0; j < terms[0].length; j++) {
                    int termId = terms[0][j];
                    int count = terms[1][j];

                    Double previous = 0.0;
                    //only terms with frequency more than a threshold is considered
                    if (count >= freqThreshold)
                        relFreq.put(termId, previous + count / docLength);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relFreq;
    }

    /**
     * Compute KL-divergence for terms in queries: re-weight terms with respect to collection frequency.
     */
    public static ArrayList<IntDouble> computeKLDivergence(HashMap<Integer, Double> queryMLEstimate, Index index, long N) {

        ArrayList<IntDouble> q_KL = new ArrayList<>(queryMLEstimate.size());
        double total = 0.0;
        for (int termId : queryMLEstimate.keySet()) {
            double p1 = queryMLEstimate.get(termId);
            double p2 = collectionRelFreq(termId, index, N);
            if (p1 > p2) {
                double val = p1 * Math.log(p1 / p2);
                q_KL.add(new IntDouble(termId, val));
                total += val;
            }
        }
        for (IntDouble intDouble : q_KL)
            q_KL.add(new IntDouble(intDouble.getId(), intDouble.getVal() / total));
        return q_KL;
    }

    public static double collectionRelFreq(int termId, Index index, long N) {
        double p = 0d;
        try {
            LexiconEntry le = index.getLexicon().getLexiconEntry(termId).getValue();
            if (le != null) p = le.getFrequency() / (double) N;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}



