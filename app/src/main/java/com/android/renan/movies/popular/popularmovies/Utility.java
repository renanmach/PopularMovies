package com.android.renan.movies.popular.popularmovies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String[] readFilesFromDirectory(String directory) {
        File dirFile = new File(directory);
        if(!dirFile.isDirectory())
            return null;

        List<String> filesArray = new ArrayList<>();

        for(File f: dirFile.listFiles()) {
            if(!f.isDirectory())
                filesArray.add(f.getAbsolutePath());
        }

        return (String[])filesArray.toArray();
    }
}


