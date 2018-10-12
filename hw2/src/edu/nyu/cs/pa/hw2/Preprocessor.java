package edu.nyu.cs.pa.hw2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public final class Preprocessor {
    private static Set<String> stopWordSet = new HashSet<String>();
    
    static void removeStopWordsFromTextFile(File file) throws IOException {
        // must populate the set the first time this method is called,
        // it will remain populated for subsequent calls
        if (stopWordSet.isEmpty()) {
            fillStopWordSet();
        }
        
        File outFile = new File(generateOutputFilePath(file.toString(), "removeStopWords"));
        outFile.mkdirs();
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            while (scanner.hasNext()) {
                String next = scanner.next();
                // if the word isn't a stop word: remove punctuation and make it lower case, then save it
                if (!stopWordSet.contains(next)) {
                    writer.write(next.replaceAll("[^a-zA-Z ]", "").toLowerCase());
                    writer.write("\n");
                }
            }
        }
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
