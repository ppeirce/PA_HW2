package edu.nyu.cs.pa.hw2;

import java.util.Arrays;

public class Evaluator {
    public final int[][] clusters;
    private int[][] confusionMatrix;
    private double[] recallScores;
    private double[] precisionScores;
    private double[] f1Scores;
    
    public Evaluator(int[][] clusters) {
        this.clusters = clusters;
    }

    public int[][] generateConfusionMatrix() {
        confusionMatrix = new int[3][3];
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < 8; j++) {
                confusionMatrix[i][0] += clusters[i][j];
            }
            for (int j = 8; j < 16; j++) {
                confusionMatrix[i][1] += clusters[i][j];
            }
            for (int j = 16; j < 24; j++) {
                confusionMatrix[i][2] += clusters[i][j];
            }
        }
        
        System.out.println("Confusion Matrix: ");
        for (int[] v : confusionMatrix) {
            System.out.println(Arrays.toString(v));
        }
        
        return confusionMatrix;
    }
    
    // number of relevant items retrieved / total number of relevant items
    public double[] generateRecall() {
        recallScores = new double[3];
        for (int i = 0; i < recallScores.length; i++) {
            recallScores[i] = confusionMatrix[i][i] / 8.0;
        }
        return recallScores;
    }

    // number of relevant items retrieved / total number of items retrieved
    public double[] generatePrecision() {
        precisionScores = new double[3];
        for (int i = 0; i < precisionScores.length; i++) {
            double rowSum = 0.0;
            for (int j : confusionMatrix[i]) {
                rowSum += j;
            }
            if (rowSum == 0) {
                precisionScores[i] = 0;
            } else {
                precisionScores[i] = confusionMatrix[i][i] / rowSum;
            }
        }
        return precisionScores;
    }
    
    public double[] generatef1Scores() {
        f1Scores = new double[3];
        for (int i = 0; i < f1Scores.length; i++) {
            double numerator = precisionScores[i]*recallScores[i];
            double denominator = precisionScores[i]+recallScores[i];
            if (denominator == 0) {
                f1Scores[i] = 0;
            } else {
                f1Scores[i] = (2 * (numerator/denominator));
            }
        }
        return f1Scores;
    }
}