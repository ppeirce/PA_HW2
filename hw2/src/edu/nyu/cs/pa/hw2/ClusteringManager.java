package edu.nyu.cs.pa.hw2;

import java.util.Arrays;
import java.util.Random;

public class ClusteringManager {
    private final double[][] tfidfMatrix;
    private final SimilarityType similarityType;
    private int k;
    private double[][] prototypes;
    public int[][] clusters;
    public enum ClusteringType { KMEANS, KMEANSPLUSPLUS };
    
    private final int CLUSTER_UPDATE_LIMIT = 1000;

    public ClusteringManager(double[][] tfidfMatrix, SimilarityType s) {
        this.tfidfMatrix = tfidfMatrix;
        this.similarityType = s;
        printSimilarityType(s);
    }
    
    private void printSimilarityType(SimilarityType s) {
        switch (s) {
        case EUCLIDEAN:
            System.out.println("Clustering based on Euclidean distance:");
            break;
        case COSINE:
            System.out.println("Clustering based on Cosine similarity:");
        }
    }
    
    // this is the "driving" method for k means algorithm 
    // it will iteratively update the prototypes until they stop changing
    // then it will return k arrays of ints, each array being a cluster and each int referring to a document
    public int[][] cluster(int k, ClusteringType t) {
        int numLoops = 0;
        this.k = k;
        
        switch (t) {
        case KMEANS:
            this.prototypes = generateKPrototypes(k, tfidfMatrix);
            System.out.println("Generating prototypes with K Means algorithm");
            break;
        case KMEANSPLUSPLUS:
            this.prototypes = generateKPrototypesWithEnhancedAlgorithm(k, tfidfMatrix);
            System.out.println("Generating prototypes with K Means ++ algorithm");
            break;
        default:
            break;
        }
        
//        this.prototypes = generateKPrototypes(k, tfidfMatrix);
        clusters = new int[k][tfidfMatrix.length];
        boolean clustersAreStable = false;
        
        // for each document vector, calculate the distance to the three prototypes, then assign 
        // it to the cluster with the closest one
        while ((!clustersAreStable) && (numLoops < CLUSTER_UPDATE_LIMIT)) {
            int[][] newClusters = calculateNewClusters();
            numLoops++;
            // update the prototypes as the average of their clusters
            updatePrototypes(newClusters);
            
            // if the clusters haven't changed, the clusters are final and we stop updating
            clustersAreStable = Arrays.deepEquals(clusters, newClusters);
            // otherwise, we set the clusters to their new values and go again
            clusters = deepCopy(newClusters);
        }
        System.out.println("**Final cluster:");
        for (int[] cluster : clusters) {
            System.out.println(Arrays.toString(cluster));
        }
        return clusters;
    }
    
    private int[][] calculateNewClusters() {
        int[][] newClusters = new int[k][tfidfMatrix.length];
        // for each document vector, assign it to the correct cluster based on its distance to the prototypes
        for (int i = 0; i < tfidfMatrix.length; i++) {
            // for each prototype, calculate the distance and find and save the shortest one
            int shortestIndex = 0;
            double shortestDistance = Double.MAX_VALUE;
            for (int j = 0; j < prototypes.length; j++) {
                double distance;
                if (similarityType == SimilarityType.EUCLIDEAN) {
                    distance = findEuclidianDistance(prototypes[j], tfidfMatrix[i]);
                } else {
                    distance = findCosineSimilarity(prototypes[j], tfidfMatrix[i]);
                }
                if (distance < shortestDistance) {
                    shortestIndex = j;
                    shortestDistance = distance;
                }
            }
            // now put the int representing the matrix in the cluster
            newClusters[shortestIndex][i] = 1;
        }

        return newClusters;
    }
    
    private int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }

        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;

    }
    
    private void updatePrototypes(int[][] updateClusters) {        
        for (int i = 0; i < k; i++) {
            double[] averages = new double[tfidfMatrix[0].length];
            int countInCluster = 0;
            for (int j = 0; j < updateClusters[i].length; j++) {
                if (updateClusters[i][j] == 1) {  // it belongs in this cluster, add its values to the sum matrix
                    countInCluster += 1;
                    for (int a = 0; a < tfidfMatrix.length; a++) {
                        averages[a] += tfidfMatrix[j][a];
                    }
                }
            }
            for (int j = 0; j < averages.length; j++) {
                averages[j] = averages[j] / countInCluster;
            }
            prototypes[i] = averages;
        }
    }
    
    private static double findCosineSimilarity(double[] a, double[] b) {
        double normalA = 0.0;
        double normalB = 0.0;
        double dotProduct = 0.0;
        for (int i = 0; i < a.length; i++) {
            normalA += Math.pow(a[i], 2);
            normalB += Math.pow(b[i], 2);
            dotProduct += a[i] * b[i];
        }   
        return dotProduct / (Math.sqrt(normalA) * Math.sqrt(normalB));
    }
    
    private double findEuclidianDistance(double[] a, double[] b) {
        double distance = 0;
        for (int i = 0; i < a.length; i++) {
            distance += (b[i] - a[i]) * (b[i] - a[i]);
        }
        return Math.sqrt(distance);
    }
    
    private void logClusters(boolean clustersAreStable, int[][] newClusters, int[][] clusters2) {
        System.out.println("newClusters");
        for (int[] cluster : newClusters) {
            System.out.println(Arrays.toString(cluster));
        }
        System.out.println();        
    }

    private double[][] generateKPrototypes(int k, double[][] tfidfMatrix) {
        double[] maxTFIDFValuesMatrix = findMaxValuesForEachDimension(tfidfMatrix);
        double[][] prototypes = new double[k][tfidfMatrix[0].length];
        
        // picking prototypes as random document vectors
//        for (int i = 0; i < prototypes.length; i++) {
//            Random rnd = new Random();
//            prototypes[i] = tfidfMatrix[rnd.nextInt(tfidfMatrix.length)];
//        }
        
        // generating novel prototypes with value in the range of the document vectors
        for (double[] prototype : prototypes) {
            for (int i = 0; i < prototype.length; i++) {
                prototype[i] = Math.random() * maxTFIDFValuesMatrix[i];
            }
        }
        
        return prototypes;
    }
    
    private double[][] generateKPrototypesWithEnhancedAlgorithm(int k, double[][] tfidfMatrix) {
        double[][] prototypes = new double[k][tfidfMatrix[0].length];
        
        // find first prototype by picking a random document vector
        Random rnd = new Random();
        prototypes[0] = tfidfMatrix[rnd.nextInt(tfidfMatrix.length)];
        
        // find second prototype
        // first find the distance from the first prototype to each vector
        double[] distancesSquaredFromFirstPrototypeToAllVectors = new double[tfidfMatrix.length];
        double sumOfArray = 0.0;
        for (int i = 0; i < tfidfMatrix.length; i++) {
            double n = findEuclidianDistance(tfidfMatrix[i], prototypes[0]);
            distancesSquaredFromFirstPrototypeToAllVectors[i] = n * n;
            sumOfArray += n * n;
        }
        
        // normalize the value in the array
        double[] normalizedDistances = new double[tfidfMatrix.length];
        for (int i = 0; i < tfidfMatrix.length; i++) {
            normalizedDistances[i] = distancesSquaredFromFirstPrototypeToAllVectors[i] / sumOfArray;
        }
        
        // generate a random  number (0,1] and find the first element where the sum of the normalized distances
        // up to that point in the array are greater than the random number
        double randomDouble = rnd.nextDouble();
        double sum = 0.0;
        for (int i = 0; i < normalizedDistances.length; i++) {
            sum += normalizedDistances[i];
            if (sum > randomDouble) {
                prototypes[1] = tfidfMatrix[i];
                break;
            }
        }
        
        // find the third prototype
        // first find the distance from each vector to the nearest prototype
        double[] distancesSquaredFromVectorsToNearestPrototypes = new double[tfidfMatrix.length];
        sumOfArray = 0.0;
        for (int i = 0; i < tfidfMatrix.length; i++) {
            double distToFirstPrototype = findEuclidianDistance(tfidfMatrix[i], prototypes[0]);
            double distToSecondPrototype = findEuclidianDistance(tfidfMatrix[i], prototypes[1]);
            double shorterDistance = (distToFirstPrototype < distToSecondPrototype) ? distToFirstPrototype : distToSecondPrototype;
            distancesSquaredFromVectorsToNearestPrototypes[i] = shorterDistance * shorterDistance;
            sumOfArray += shorterDistance * shorterDistance;
        }
        
        // normalize the values in the array
        double[] normalized = new double[tfidfMatrix.length];
        for (int i = 0; i < tfidfMatrix.length; i++) {
            normalized[i] = distancesSquaredFromVectorsToNearestPrototypes[i] / sumOfArray;
        }
        
        // generate a random  number (0,1] and find the first element where the sum of the normalized distances
        // up to that point in the array are greater than the random number
        randomDouble = rnd.nextDouble();
        sum = 0.0;
        for (int i = 0; i < normalized.length; i++) {
            sum += normalized[i];
            if (sum > randomDouble) {
                prototypes[2] = tfidfMatrix[i];
                break;
            }
        }
        
        return prototypes;
    }
    
    public void printPrototypes() {
        for (double[] prototype : prototypes) {
            System.out.println(Arrays.toString(prototype));
        }
    }

    private double[] findMaxValuesForEachDimension(double[][] matrix) {
        double[] maxValues = new double[matrix[0].length];
        for (double[] vector : matrix) {
            for (int i = 0; i < vector.length; i++) {
                if (vector[i] > maxValues[i]) {
                    maxValues[i] = vector[i];
                }
            }
        }
        return maxValues;
    }
    
}
