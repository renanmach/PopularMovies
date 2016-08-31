package com.android.renan.movies.popular.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract.MoviesInfoEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }


    private void getMoviesFromJson(String moviesJsonStr) {
        // TODO fetch more pages

        final String TMDB_ID = "id";
        final String TMDB_RESULT_LIST = "results";
        final String TMDB_PLOT_SYNOPSYS = "overview";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_POPULARITY = "popularity";

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULT_LIST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for(int i = 0 ; i < moviesArray.length(); i++) {
                String overview;
                String originalTitle;
                String releaseDate;
                String posterPath;
                double voteAverage;
                double popularity;
                int id;

                JSONObject movieData = moviesArray.getJSONObject(i);

                id = movieData.getInt(TMDB_ID);
                overview = movieData.getString(TMDB_PLOT_SYNOPSYS);
                originalTitle = movieData.getString(TMDB_ORIGINAL_TITLE);
                releaseDate = movieData.getString(TMDB_RELEASE_DATE);
                posterPath = movieData.getString(TMDB_POSTER_PATH);
                voteAverage = movieData.getDouble(TMDB_USER_RATING);
                popularity = movieData.getDouble(TMDB_POPULARITY);

                ContentValues cv = new ContentValues();
                cv.put(MoviesInfoEntry.COLUMN_MOVIE_ID, id);
                cv.put(MoviesInfoEntry.COLUMN_POSTER_IMAGE, posterPath);
                cv.put(MoviesInfoEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                cv.put(MoviesInfoEntry.COLUMN_PLOT_SYNOPSIS, overview);
                cv.put(MoviesInfoEntry.COLUMN_RELEASE_DATE, releaseDate);
                cv.put(MoviesInfoEntry.COLUMN_USER_RATING, voteAverage);
                cv.put(MoviesInfoEntry.COLUMN_POPULARITY, popularity);
                cVVector.add(cv);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesInfoEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        // the list can be popular or top_rated
        String listType = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {
            // Construct the URL for the themoviedb API
            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+listType;
            final String API_KEY_PARAM = "api_key";
            final String PAGE_PARAM = "page";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                    .appendQueryParameter(PAGE_PARAM, String.valueOf(1))
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if(inputStream == null) {
                Log.w(LOG_TAG, "Warning: API returned null");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            if(stringBuffer.length() == 0) {
                return null;
            }

            moviesJsonStr = stringBuffer.toString();
            getMoviesFromJson(moviesJsonStr);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}
