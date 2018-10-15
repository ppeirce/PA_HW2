package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TextMiningDriver {
    private static List<File> preprocessedFiles = new ArrayList<File>();
    private static double[][] tfidfMatrix;
    private static String[][] keywordsForDocumentFoldersMatrix;
    private static ClusteringManager clusteredMatricesByEuclideanDistance;
    private static ClusteringManager clusteredMatricesByCosineSimilarity;
    
    private static double[][] tfidfMatrixGenerator() throws IOException {
        for (File file : DataFiles.ORIGINAL_FILES) {
            Preprocessor.removeStopWordsTokenizeStemLemmatize(file);
            Preprocessor.clearStopWordSet();
            Preprocessor.extractNER(file);
            preprocessedFiles.add(Preprocessor.slidingWindow(file));
        }
        
        TreeSet<String> setOfAllTerms = MatrixGenerator.createSetOfAllTerms(preprocessedFiles);
        int[][] documentTermMatrix = MatrixGenerator.fillDocumentTermMatrix(setOfAllTerms, preprocessedFiles);
        double[][] tfidfTransformedMatrix = MatrixGenerator.transformMatrixWithTFIDF(documentTermMatrix);
        keywordsForDocumentFoldersMatrix = MatrixGenerator.generateKeywords(setOfAllTerms, tfidfTransformedMatrix);

        return tfidfTransformedMatrix;
    }
    
    public static void main(String[] args) throws IOException {
        tfidfMatrix = tfidfMatrixGenerator();
        clusteredMatricesByEuclideanDistance = new ClusteringManager(tfidfMatrix, 3, Similarity.EUCLIDEAN);
        clusteredMatricesByCosineSimilarity = new ClusteringManager(tfidfMatrix, 3, Similarity.COSINE);        
    }

}
