package com.smartcs.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private IndexSearcher indexSearcher;
    private DirectoryReader reader;

    // A simple data class to hold our results so they can be converted to JSON
    public static class SearchResult {
        public String filename;
        public float score;

        public SearchResult(String filename, float score) {
            this.filename = filename;
            this.score = score;
        }
    }

    public Searcher(String indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        reader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(reader);
    }

    // Now returns a List of SearchResults instead of void
    public List<SearchResult> search(String queryString, int topN) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(queryString);

        TopDocs results = indexSearcher.search(query, topN);
        ScoreDoc[] hits = results.scoreDocs;

        List<SearchResult> responseList = new ArrayList<>();

        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document d = indexSearcher.doc(docId);
            // Add each hit to our list
            responseList.add(new SearchResult(d.get("filename"), hit.score));
        }

        return responseList;
    }

    public void close() throws IOException {
        reader.close();
    }
}