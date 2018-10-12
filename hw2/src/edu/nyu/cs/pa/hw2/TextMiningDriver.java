package edu.nyu.cs.pa.hw2;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TextMiningDriver {
    public static void main(String[] args) throws IOException {                
        for (File file : DataFiles.FILES) {
            Preprocessor.removeStopWordsFromTextFile(file);
        }
    }
}
