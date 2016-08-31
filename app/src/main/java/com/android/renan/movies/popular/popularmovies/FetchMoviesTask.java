package com.android.renan.movies.popular.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }


    private void getMoviesFromJson(String moviesJsonStr) {
        // TODO fetch more pages

        final String TMDB_RESULT_LIST = "results";
        final String TMDB_PLOT_SYNOPSYS = "overview";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_USER_RATING = "vote_average";

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULT_LIST);

            for(int i = 0 ; i < moviesArray.length(); i++) {
                String overview;
                String originalTitle;
                String releaseDate;
                String posterPath;
                double voteAverage;

                JSONObject movieData = moviesArray.getJSONObject(i);

                overview = movieData.getString(TMDB_PLOT_SYNOPSYS);
                originalTitle = movieData.getString(TMDB_ORIGINAL_TITLE);
                releaseDate = movieData.getString(TMDB_RELEASE_DATE);
                posterPath = movieData.getString(TMDB_POSTER_PATH);
                voteAverage = movieData.getDouble(TMDB_USER_RATING);

                Log.v(LOG_TAG, overview);
                Log.v(LOG_TAG, originalTitle);
                Log.v(LOG_TAG, releaseDate);
                Log.v(LOG_TAG, posterPath);
                Log.v(LOG_TAG, String.valueOf(voteAverage));
            }

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
                    .appendQueryParameter(API_KEY_PARAM, Utility.getApiKey())
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