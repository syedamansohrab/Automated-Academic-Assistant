package com.smartcs.indexer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader; // <-- The new import

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DocumentIndexer {
    private IndexWriter writer;

    public DocumentIndexer(String indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException {
        writer.close();
    }

    public void indexPdfDocument(File pdfFile) throws IOException {
        System.out.println("Indexing: " + pdfFile.getName());
        
        String extractedText = "";
        
        // <-- The fixed line using Loader.loadPDF -->
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        } catch (Exception e) {
            System.err.println("Could not read PDF: " + pdfFile.getName());
            return;
        }

        Document doc = new Document();

        doc.add(new StringField("filename", pdfFile.getName(), Field.Store.YES));
        doc.add(new StringField("filepath", pdfFile.getAbsolutePath(), Field.Store.YES));
        doc.add(new TextField("content", extractedText, Field.Store.YES));

        writer.addDocument(doc);
    }
}