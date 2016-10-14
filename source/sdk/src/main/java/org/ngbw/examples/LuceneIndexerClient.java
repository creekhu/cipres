package org.ngbw.examples;

import org.ngbw.sdk.dataresources.lucene.LuceneDatasetIndexer;


public class LuceneIndexerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length == 0)
			args = new String[1];
		args[0] = "NDB"; //TODO
		LuceneDatasetIndexer.main(args);
	}
}
