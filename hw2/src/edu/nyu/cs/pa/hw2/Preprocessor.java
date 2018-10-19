package edu.nyu.cs.pa.hw2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public final class Preprocessor {
    private static Set<String> stopWordSet = new HashSet<String>();
    private static Set<String> NESet = new HashSet<String>();
    private static File outFileSWTSL;
    private static File outFileNER;
    private static String docString;
    
    static void removeStopWordsTokenizeStemLemmatize(File file) throws IOException {
        // must populate the set the first time this method is called,
        // it will remain populated for subsequent calls
        if (stopWordSet.isEmpty()) {
            fillStopWordSet();
        }
        
        outFileSWTSL = new File(generateOutputFilePath(file.toString(), "removeStopWords"));
        docString = generateStringFromDocument(file);
        Document doc = new Document(docString);
        removeStopWordsLemmatizeAndSave(doc, outFileSWTSL);
    }
    
    static void extractNER(File file) throws IOException {
        // the input file is the last outFile. Generate a new output file
        // apply NER to the input and save it
        outFileNER = new File(generateOutputFilePath(file.toString(), "applyNER"));
        docString = generateStringFromDocument(outFileSWTSL);
        Document doc = new Document(docString);
        addNamedEntitiesToNESet(doc);
        applyNamedEntityExtractionAndSave(doc, outFileNER);
    }
    
    static File slidingWindow(File file) throws IOException {
        File outFileSW = new File(generateOutputFilePath(file.toString(), "applySW"));
        String docAsString = generateStringFromDocument(outFileNER);
        Map<String, Integer> multipleThreeGrams = generateFilteredThreeGrams(docAsString);
        mergeNGrams(docAsString, multipleThreeGrams, outFileSW);
        Map<String, Integer> multipleTwoGrams = generateFilteredTwoGrams(docAsString);
        mergeNGrams(docAsString, multipleTwoGrams, outFileSW);       
        
        return outFileSW;
    }
    
    private static Map<String, Integer> generateFilteredThreeGrams(String document) {
        Map<String, Integer> threeGrams = new HashMap<String, Integer>();
        String[] words = document.split(" ");
        for (int i = 0; i < words.length-2; i++) {
            String threeGram = words[i] + " " + words[i+1] + " " + words[i+2];
            if (!threeGrams.containsKey(threeGram)) {
                threeGrams.put(threeGram, 1);
            } else {
                threeGrams.put(threeGram, threeGrams.get(threeGram) + 1);
            }
        }
        
        Map<String, Integer> filteredThreeGrams = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : threeGrams.entrySet()) {
            String threeGram = entry.getKey();
            Integer count = entry.getValue();
            if (count > 3) {
                filteredThreeGrams.put(threeGram, count);
            }
        }

        return filteredThreeGrams;
    }
    
    private static Map<String, Integer> generateFilteredTwoGrams(String document) {
        Map<String, Integer> twoGrams = new HashMap<String, Integer>();
        String[] words = document.split(" ");
        for (int i = 0; i < words.length-1; i++) {
            String twoGram = words[i] + " " + words[i+1];
            if (!twoGrams.containsKey(twoGram)) {
                twoGrams.put(twoGram, 1);
            } else {
                twoGrams.put(twoGram, twoGrams.get(twoGram) + 1);
            }
        }
        
        Map<String, Integer> filteredTwoGrams = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : twoGrams.entrySet()) {
            String twoGram = entry.getKey();
            Integer count = entry.getValue();
            if (count > 3) {
                filteredTwoGrams.put(twoGram, count);
            }
        }
        
        return filteredTwoGrams;
    }
    
    private static void mergeNGrams(String doc, Map<String, Integer> multipleNGrams, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String nGram : multipleNGrams.keySet()) {
                if (doc.contains(nGram)) {
                    doc = doc.replaceAll(nGram, nGram.replaceAll(" ", "_"));
                }
            }
            writer.write(doc);
        }
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
                    if (!stopWordSet.contains(lemma) && !lemma.matches(".*\\d+.*")) {
                        writer.write(lemma);
                        writer.write("\n");
                    }
                }
            }
        } 
    }
    
    static String generateStringFromDocument(File file) throws IOException {
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
    
    static void clearStopWordSet() {
        stopWordSet.clear();
    }
}
