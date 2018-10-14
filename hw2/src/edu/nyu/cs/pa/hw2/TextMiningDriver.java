package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TextMiningDriver {
    private static List<File> preprocessedFiles = new ArrayList<File>();
    
    public static void main(String[] args) throws IOException {                
        for (File file : DataFiles.ORIGINAL_FILES) {
            Preprocessor.removeStopWordsTokenizeStemLemmatize(file);
            Preprocessor.clearStopWordSet();
            Preprocessor.extractNER(file);
            preprocessedFiles.add(Preprocessor.slidingWindow(file));
        }
        
        TreeSet<String> terms = MatrixGenerator.createSetOfAllTerms(preprocessedFiles);
        System.out.println(terms.size());
        System.out.println(terms.toString());
        int[][] documentTermMatrix = MatrixGenerator.fillDocumentTermMatrix(terms, preprocessedFiles);
//        for (int[] document : documentTermMatrix) {
//            for (int termCount : document) {
//                System.out.print(termCount + " ");
//            }
//            System.out.println();
//        }
    }
}
