package edu.nyu.cs.pa.hw2;

public class ClusteringManager {
    private double[][] tfidfMatrix;
    private int k;
    private Similarity similarityType;

    public ClusteringManager(double[][] tfidfMatrix, int k, Similarity euclidean) {
        this.tfidfMatrix = tfidfMatrix;
        this.k = k;
        this.similarityType = euclidean;
    }
    
    

}
