Build queries from the patent query documents found in the CLEF-IP 2010 test set:
 Connects to the Terrier Index and read terms and frequencies for a given set of documents (representing test set of CLEF-IP 2010)
 Compute KL-divergence between language model of the query document and the background language model (of collection)
 Sort term based on KL-divergence and get top k terms
 For each query print top k terms of the query (abiding by the format of Terrier Query language)
 
