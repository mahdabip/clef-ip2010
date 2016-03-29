package clef10;

import org.terrier.querying.Manager;
import org.terrier.structures.Index;

/**
 * Index configurations.
 */
public class IndexCommons {

    static { System.setProperty("terrier.home","/Users/usiusi/tools/terrier-3.0"); }
    private static Index index;
    private static long tokenNumber;
    private Manager manager;

    protected IndexCommons(String path, String prefix) {
        index = Index.createIndex(path, prefix);
        tokenNumber = index.getCollectionStatistics().getNumberOfTokens();
        manager = new Manager(index);

        System.out.println("Number of Documents: " + index.getCollectionStatistics().getNumberOfDocuments());
        System.out.println("Number of Tokens: "	+ index.getCollectionStatistics().getNumberOfTokens());
        System.out.println("Number of Unique Terms: " + index.getCollectionStatistics().getNumberOfUniqueTerms());
    }

    public Index getIndex() {
        return index;
    }

    public long getTokenNumber(){
        return tokenNumber;
    }

    public Manager getManager() {
        return manager;
    }

}

