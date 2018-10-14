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
        
        TreeSet<String> setOfAllTerms = MatrixGenerator.createSetOfAllTerms(preprocessedFiles);
        int[][] documentTermMatrix = MatrixGenerator.fillDocumentTermMatrix(setOfAllTerms, preprocessedFiles);
        double[][] tfidfTransformedMatrix = MatrixGenerator.transformMatrixWithTFIDF(documentTermMatrix);
        
        // TODO
        // next step: for each document folder (group of 8 documents), combine the vectors, find the 
        // top n-occuring keywords for each folder (5? 10? more?)
        // to be returned: an array of three arrays, one for each document folder, each containing 
        // the top-n-occuring keyword strings
        // in order for a method to accomplish this, it needs: the TreeSet setOfAllTerms (to retrieve the 
        // associated Strings based on their index) and the double[][] tfidfTransformedMatrix (to find
        // the most important terms for each document)
        String[][] keywordsByDocumentFolder = MatrixGenerator.generateKeywords(setOfAllTerms, tfidfTransformedMatrix);
    }
}
