package com.android.renan.movies.popular.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract;

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


    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popularity));
    }

    public static String getPreferredSortOrderDB(Context context) {
        String sortOrder = getPreferredSortOrder(context);

        if(sortOrder.equals(context.getString(R.string.pref_sort_popularity))) {
            return PopMoviesContract.MoviesInfoEntry.COLUMN_POPULARITY + " DESC";
        }
        else if(sortOrder.equals(context.getString(R.string.pref_sort_top_rated))) {
            return PopMoviesContract.MoviesInfoEntry.COLUMN_USER_RATING + " DESC";
        }

        Log.w(LOG_TAG, "Warning: sorting order -" + sortOrder +"- not found");
        return null;
    }


    public static String formatUserRating(Context context, double userRating) {
        return context.getString(R.string.format_user_rating, userRating);
    }

    public static String formatRuntime(Context context, int runtime) {
        return context.getString(R.string.format_runtime, runtime);
    }

    // return the year from date string of type YYYY-MM-DD
    public static String getYearFromDateStr(String dateStr) {
        if(dateStr.length() > 4) {
            return dateStr.substring(0, 4);
        }
        else
            return dateStr;
    }

}


