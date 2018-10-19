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
    private static Evaluator evaluator;
    
    private static double[][] tfidfMatrixGenerator() throws IOException {
        for (File file : DataFiles.ORIGINAL_FILES) {
            Preprocessor.removeStopWordsTokenizeStemLemmatize(file);
            Preprocessor.clearStopWordSet();
            Preprocessor.extractNER(file);
            preprocessedFiles.add(Preprocessor.slidingWindow(file));
        }
        
        TreeSet<String> setOfAllTerms = MatrixGenerator.createSetOfAllTerms(preprocessedFiles);
        List<String> listofAllTerms = new ArrayList<String>();
        listofAllTerms.addAll(setOfAllTerms);
        int[][] documentTermMatrix = MatrixGenerator.fillDocumentTermMatrix(setOfAllTerms, preprocessedFiles);
        double[][] tfidfTransformedMatrix = MatrixGenerator.transformMatrixWithTFIDF(documentTermMatrix);
        keywordsForDocumentFoldersMatrix = MatrixGenerator.generateKeywords(setOfAllTerms, tfidfTransformedMatrix);

        return tfidfTransformedMatrix;
    }
    
    private static void performClustering() {
        clusteredMatricesByEuclideanDistance = new ClusteringManager(tfidfMatrix, Similarity.EUCLIDEAN);
        clusteredMatricesByCosineSimilarity = new ClusteringManager(tfidfMatrix, Similarity.COSINE);

        
        System.out.println("Clustering based on Euclidiean distance:");
        clusteredMatricesByEuclideanDistance.cluster(3);
        System.out.println("Euclidean clustering complete.\n");
        
        System.out.println("Clustering based on Cosine similarity:");
        clusteredMatricesByCosineSimilarity.cluster(3);
        System.out.println("Cosine clustering complete.\n");
    }
    
    private static void performEvaluation() {
        
    }
    
    public static void main(String[] args) throws IOException {
        tfidfMatrix = tfidfMatrixGenerator();
        performClustering();
        performEvaluation();
    }

}
