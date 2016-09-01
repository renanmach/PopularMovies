package com.android.renan.movies.popular.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract.MoviesInfoEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchRunTimeTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchRunTimeTask.class.getSimpleName();
    private final Context mContext;
    private final int mMovieId;

    public FetchRunTimeTask(Context context, int movieId) {
        mContext = context;
        mMovieId = movieId;
    }

    private void getRuntimeFromJson(String moviesJsonStr) {
        final String TMDB_RUNTIME = "runtime";

        try {
            JSONObject movieJson = new JSONObject(moviesJsonStr);
            ContentValues cv = new ContentValues();
            cv.put(MoviesInfoEntry.COLUMN_RUNTIME, movieJson.getInt(TMDB_RUNTIME));

            mContext.getContentResolver().update(MoviesInfoEntry.buildMovieUri(mMovieId), cv, null, null);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the themoviedb API to query the movie by the id
            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+String.valueOf(mMovieId);
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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
            getRuntimeFromJson(stringBuffer.toString());
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
