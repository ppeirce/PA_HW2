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
    private static Set<String> NESet = new HashSet<String>();
    
    static void removeStopWordsTokenizeStemLemmatize(File file) throws IOException {
        // must populate the set the first time this method is called,
        // it will remain populated for subsequent calls
        if (stopWordSet.isEmpty()) {
            fillStopWordSet();
        }
        
        File outFile = new File(generateOutputFilePath(file.toString(), "removeStopWords"));
//        outFile.mkdirs();
        String docString = generateStringFromDocument(file);
        Document doc = new Document(docString);
        removeStopWordsLemmatizeAndSave(doc, outFile);
        
        // the input file is the last outFile. Generate a new output file
        // apply NER to the input and save it
        File outFileNER = new File(generateOutputFilePath(file.toString(), "applyNER"));
        docString = generateStringFromDocument(outFile);
        doc = new Document(docString);
        addNamedEntitiesToNESet(doc);
        applyNamedEntityExtractionAndSave(doc, outFileNER);
    }
    
    private static void addNamedEntitiesToNESet(Document document) {
        for (Sentence sentence : document.sentences()) {
            for (String ne : sentence.mentions()) {
                if (ne.contains(" ")) {
                    NESet.add(ne);
                }
            }
        }
    }
    
    private static void applyNamedEntityExtractionAndSave(Document document, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Sentence sentence : document.sentences()) {
                String s = sentence.toString();
                for (String ne : NESet) {
                    if (s.contains(ne)) {
                        s = s.replaceAll(ne, ne.replaceAll(" ", "_"));
                    }
                }
                writer.write(s);
            }
        }
    }
    
    private static void removeStopWordsLemmatizeAndSave(Document document, File file) throws IOException {
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
        String newPath = inputPath.substring(0, 5) + subFolder + inputPath.substring(13);
        
        // make the correct directories while excluding the file names
        new File(newPath.substring(0,8+subFolder.length())).mkdirs();
        return newPath;
    }
    
    private static void fillStopWordSet() throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File("data/stopwords.txt"))) {
            while (scanner.hasNext()) {
                stopWordSet.add(scanner.next());
            }
        }
    }
}
