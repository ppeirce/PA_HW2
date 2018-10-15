package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        for (String term : setOfAllTerms) {
//            System.out.print(term + " ");
        }
//        System.out.println();
        List<String> listOfAllTerms = new ArrayList<String>();
        listOfAllTerms.addAll(setOfAllTerms);
        for (String term : listOfAllTerms) {
//            System.out.print(term + " ");
        }
        return setOfAllTerms;
    }

    static int[][] fillDocumentTermMatrix(TreeSet<String> setOfAllTerms, List<File> preprocessedFiles) throws IOException {
        int[][] dtMatrix = new int[DataFiles.ORIGINAL_FILES.length][setOfAllTerms.size()];
        int rowToFill = 0;
        List<String> listOfAllTerms = new ArrayList<String>();
        listOfAllTerms.addAll(setOfAllTerms);
        
        for (File file : preprocessedFiles) {
            String document = Preprocessor.generateStringFromDocument(file);
            String[] words = document.split(" ");
            for (String word : words) {
                dtMatrix[rowToFill][listOfAllTerms.indexOf(word)] += 1;
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
                if (tfRow[i] != 0) {
                    tfRow[i] = tfRow[i] * idfArray[i];
                }
            }
        }
                
        return tfMatrix;
    }

    static String[][] generateKeywords(TreeSet<String> setOfAllTerms, double[][] tfidfTransformedMatrix) {
        String[][] keywords = new String[3][3];
        // first step: merge each group of 8 documents from the tfidf matrix so there are 3 Arrays
        double[][] mergedDocumentFolders = MatrixGenerator.createMergedDocuments(tfidfTransformedMatrix);
        
        // find largest value in array
        // iterate over the array by index
        // when a new largest value is found, save the index
        // to find the top 5 largest values, save 5 indices initialized to zero
        // if a new value is larger than the first saved value, replace the fifth largest with the fourth, the
        // fourth with the third, the third with the second, the second with the first, and the first with the new
        int folderIndex = 0;
        for (double[] documentArray : mergedDocumentFolders) {
            // TODO find the index of the five largest values in the array
            int[] largestThree = {0, 0, 0};
            double[] largestThreeValues = { 0.0, 0.0, 0.0 };
            for (int i = 0; i < documentArray.length; i++) {
                if (documentArray[i] > documentArray[largestThree[0]]) {
                    largestThree[2] = largestThree[1];
                    largestThree[1] = largestThree[0];
                    largestThree[0] = i;
                    
                    largestThreeValues[2] = largestThreeValues[1];
                    largestThreeValues[1] = largestThreeValues[0];
                    largestThreeValues[0] = documentArray[i];
                    
                    keywords[folderIndex][2] = keywords[folderIndex][1];
                    keywords[folderIndex][1] = keywords[folderIndex][0];
                    keywords[folderIndex][0] = getKthElementFromTreeSet(largestThree[0], setOfAllTerms);
                    
//                    System.out.println(Arrays.toString(largestThree));
//                    System.out.println(Arrays.toString(largestThreeValues));
//                    System.out.println(Arrays.toString(keywords[folderIndex]));
                }
            }
            System.out.println(Arrays.toString(keywords[folderIndex]));
            folderIndex += 1;
        }
        
        
        return keywords;
    }
    
    private static String getKthElementFromTreeSet(int k, TreeSet<String> setOfAllTerms) {
        Iterator<String> it = setOfAllTerms.iterator();
        int i = 0;
        String current = null;
        while (it.hasNext() && i < k) {
            current = it.next();
            i++;
        }
        return current;
    }

    private static double[][] createMergedDocuments(double[][] tfidfMatrix) {
        double[] documentFolderOne = MatrixGenerator.mergeArrays(0, 8, tfidfMatrix);
        double[] documentFolderTwo = MatrixGenerator.mergeArrays(8, 16, tfidfMatrix);
        double[] documentFolderThree = MatrixGenerator.mergeArrays(16, 24, tfidfMatrix);
        double[][] mergedDocumentFolders = { documentFolderOne, documentFolderTwo, documentFolderThree };
        return mergedDocumentFolders;
    }

    // TODO reread this to make sure it's doing the right thing
    private static double[] mergeArrays(int firstFile, int lastFile, double[][] tfidfMatrix) {
        double[] mergedArray = new double[tfidfMatrix[firstFile].length];
        for (int i = firstFile; i < lastFile; i++) {
            for (int j = 0; j < mergedArray.length; j++) {
                mergedArray[j] += tfidfMatrix[i][j];
            }
        }
        
        return mergedArray;
    }
    
}
