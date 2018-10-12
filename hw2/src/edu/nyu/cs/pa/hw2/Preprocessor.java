package edu.nyu.cs.pa.hw2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public final class Preprocessor {
    private static Set<String> stopWordSet = new HashSet<String>();
    
    static void removeStopWordsTokenizeStemLemmatize(File file) throws IOException {
        File outFile = new File(generateOutputFilePath(file.toString(), "removeStopWords"));
        outFile.mkdirs();
        
        // must populate the set the first time this method is called,
        // it will remain populated for subsequent calls
        if (stopWordSet.isEmpty()) {
            fillStopWordSet();
        }
                
        String docString = generateStringFromDocument(file);
        Document doc = new Document(docString);
        processDocumentAndSaveToFile(doc, outFile);
    }
    
    private static void processDocumentAndSaveToFile(Document document, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Sentence sentence : document.sentences()) {
                for (String lemma : sentence.lemmas()) {
                    if (!stopWordSet.contains(lemma)) {
                        writer.write(lemma);
                        writer.write("\n");
                    }
                }
            }
        } 
    }
    
    private static String generateStringFromDocument(File file) throws IOException {
        String documentString = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                documentString = documentString + scanner.next() + " ";
            }
        }
        return documentString;
    }
    
    private static String generateOutputFilePath(String inputPath, String subFolder) {
        return inputPath.substring(0, 5) + subFolder + inputPath.substring(13);
    }
    
    private static void fillStopWordSet() throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File("data/stopwords.txt"))) {
            while (scanner.hasNext()) {
                stopWordSet.add(scanner.next());
            }
        }
    }
}
