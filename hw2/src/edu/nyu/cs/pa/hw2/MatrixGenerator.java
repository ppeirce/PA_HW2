package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class MatrixGenerator {

    static TreeSet<String> createSetOfAllTerms(List<File> preprocessedFiles) throws IOException {
        TreeSet<String> setOfAllTerms = new TreeSet<String>();
        for (File file : preprocessedFiles) {
            String document = Preprocessor.generateStringFromDocument(file);
            String[] words = document.split(" ");
            for (String word : words) {
                setOfAllTerms.add(word);
            }
        }
        
        return setOfAllTerms;
    }

    static int[][] fillDocumentTermMatrix(TreeSet<String> setOfAllTerms, List<File> preprocessedFiles) throws IOException {
        int[][] dtMatrix = new int[DataFiles.ORIGINAL_FILES.length][setOfAllTerms.size()];
        int rowToFill = 0;
        for (File file : preprocessedFiles) {
            String document = Preprocessor.generateStringFromDocument(file);
            String[] words = document.split(" ");
            for (String word : words) {
                // This is a potentially confusing line of code. Here's an explanation:
                // In order to correctly fill the document term matrix, we need to know the correct index 
                // in the matrix of the term that was found in the document. We use the TreeSet terms (which is
                // sorted) to do this. TreeSet has a method which returns a subset of the original set
                // that contains only items which are strictly less than the passed object. In this case, 
                // we pass in the term we're looking for, creating a new subset of terms where the searched
                // term is the last object in the set. The size of this new set corresponds to the index of
                // the searched term in the original set and the column index the document-term matrix. 
                dtMatrix[rowToFill][setOfAllTerms.headSet(word).size()] += 1;
            }
            rowToFill += 1;
        }
        
        return dtMatrix;
    }

    static double[][] transformMatrixWithTFIDF(int[][] documentTermMatrix) {
        double[][] tfMatrix = new double[documentTermMatrix.length][documentTermMatrix[0].length];
        double[] idfArray = new double[documentTermMatrix[0].length];
        int rowIndex = 0;
        
        for (int[] documentRow : documentTermMatrix) {
            Double documentLength = 0.0;
            int documentIndex = 0;
            
            // this first loop over each document term array finds the total length of the document in question
            // as a function of the sum of the words for the TF score, and generates the matrix that maintains
            // how many documents contain each word for the IDF score
            for (int termCount : documentRow) {
                documentLength += termCount;
                if (termCount != 0) {
                    idfArray[documentIndex] += 1;
                }
                documentIndex += 1;
            }
            
            // this loop fills the new matrix for the TF score where each value is the number of times 
            // the word appeared in the document divided by the total length of the document
            int columnIndex = 0;
            for (int termCount : documentRow) {
                tfMatrix[rowIndex][columnIndex] = termCount / documentLength;
                columnIndex += 1;
            }
            
            rowIndex += 1;
        }
        
        // one final loop to finalize the values for the array holding the IDF values
        for (int i = 0; i < idfArray.length; i++) {
            idfArray[i] = Math.log(24 / idfArray[i]);
        }
        
        // now apply the IDF value for each term to the corresponding value in each document in the matrix
        for (double[] tfRow : tfMatrix) {
            for (int i = 0; i < tfRow.length; i++) {
                tfRow[i] = tfRow[i] * idfArray[i];
            }
        }
        
        for (double d : tfMatrix[0]) {
            System.out.print(d + " ");
        }
        
        return tfMatrix;
    }
    
}
