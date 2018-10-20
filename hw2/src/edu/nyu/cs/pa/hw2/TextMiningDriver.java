package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import edu.nyu.cs.pa.hw2.ClusteringManager.ClusteringType;

public class TextMiningDriver {
    private static List<File> preprocessedFiles = new ArrayList<File>();
    private static double[][] tfidfMatrix;
    private static String[][] keywordsForDocumentFoldersMatrix;
    private static ClusteringManager clusteredMatrices;
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
    
    private static void performClustering(SimilarityType similarityType) {
        clusteredMatrices = new ClusteringManager(tfidfMatrix, similarityType);
        clusteredMatrices.cluster(3, ClusteringType.KMEANSPLUSPLUS);
    }
    
    private static void performEvaluation() {
        evaluator = new Evaluator(clusteredMatrices.clusters);
        int[][] confusionMatrix = evaluator.generateConfusionMatrix();
        double[] f1Scores = evaluator.generatef1Scores();
    }
    
    public static void main(String[] args) throws IOException {
        tfidfMatrix = tfidfMatrixGenerator();
        performClustering(SimilarityType.COSINE);
        performEvaluation();
    }

}
