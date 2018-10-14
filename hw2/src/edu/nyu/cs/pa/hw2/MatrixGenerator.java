package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;
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

    static int[][] fillDocumentTermMatrix(TreeSet<String> terms, List<File> preprocessedFiles) throws IOException {
        int[][] dtMatrix = new int[24][terms.size()];
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
                dtMatrix[rowToFill][terms.headSet(word).size()] += 1;
            }
            rowToFill += 1;
        }
        
        return dtMatrix;
    }
    
}
