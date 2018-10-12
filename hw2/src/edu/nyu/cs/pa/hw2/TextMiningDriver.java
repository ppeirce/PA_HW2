package edu.nyu.cs.pa.hw2;

import java.io.File;
import java.io.IOException;

public class TextMiningDriver {
    public static void main(String[] args) throws IOException {                
        for (File file : DataFiles.FILES) {
            Preprocessor.removeStopWordsTokenizeStemLemmatize(file);
            
        }
    }
}
