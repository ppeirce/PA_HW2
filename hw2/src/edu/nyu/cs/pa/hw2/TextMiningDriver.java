package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

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
        switch (similarityType) {
        case EUCLIDEAN: 
            clusteredMatrices = new ClusteringManager(tfidfMatrix, SimilarityType.EUCLIDEAN);
            System.out.println("Clustering based on Euclidiean distance:");
            clusteredMatrices.cluster(3);
            System.out.println("Euclidean clustering complete.\n");
            break;
        case COSINE:
            clusteredMatrices = new ClusteringManager(tfidfMatrix, SimilarityType.COSINE);
            System.out.println("Clustering based on Cosine similarity:");
            clusteredMatrices.cluster(3);
            System.out.println("Cosine clustering complete.\n");
            break;
        default:
            break;
        }
        
//        clusteredMatricesByEuclideanDistance = new ClusteringManager(tfidfMatrix, Similarity.EUCLIDEAN);
//        clusteredMatricesByCosineSimilarity = new ClusteringManager(tfidfMatrix, Similarity.COSINE);
//
//        
//        System.out.println("Clustering based on Euclidiean distance:");
//        clusteredMatricesByEuclideanDistance.cluster(3);
//        System.out.println("Euclidean clustering complete.\n");
//        
//        System.out.println("Clustering based on Cosine similarity:");
//        clusteredMatricesByCosineSimilarity.cluster(3);
//        System.out.println("Cosine clustering complete.\n");
    }
    
    private static void performEvaluation() {
        evaluator = new Evaluator(clusteredMatrices.clusters);
        int[][] confusionMatrix = evaluator.generateConfusionMatrix();
        double[] recallScores = evaluator.generateRecall();
        double[] precisionScores = evaluator.generatePrecision();
        double[] f1Scores = evaluator.generatef1Scores();
        System.out.println(Arrays.toString(f1Scores));
    }
    
    public static void main(String[] args) throws IOException {
        tfidfMatrix = tfidfMatrixGenerator();
        performClustering(SimilarityType.COSINE);
        performEvaluation();
    }

}
