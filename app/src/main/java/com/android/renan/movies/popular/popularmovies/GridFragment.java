package com.android.renan.movies.popular.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract;

import java.util.ArrayList;
import java.util.List;


public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIES_LOADER = 0;
    private GridCustomAdapter mMoviesAdapter;
    private static final String LOG_TAG = GridFragment.class.getSimpleName();
    public static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private List<Integer> mMovieId;
    public static final String MOVIE_ID_EXTRA =
            "com.android.renan.movie.popular.popularmovies.MOVIE_ID";
    private Handler mHandler;
    private String mSortOrder;


    private static String[] MOVIES_COLUMNS = {
            PopMoviesContract.MoviesInfoEntry._ID,
            PopMoviesContract.MoviesInfoEntry.COLUMN_ORIGINAL_TITLE,
            PopMoviesContract.MoviesInfoEntry.COLUMN_USER_RATING,
            PopMoviesContract.MoviesInfoEntry.COLUMN_PLOT_SYNOPSIS,
            PopMoviesContract.MoviesInfoEntry.COLUMN_RELEASE_DATE,
            PopMoviesContract.MoviesInfoEntry.COLUMN_POSTER_IMAGE,
            PopMoviesContract.MoviesInfoEntry.COLUMN_MOVIE_ID,
            PopMoviesContract.MoviesInfoEntry.COLUMN_POPULARITY
    };

    static final int COL_MOVIES_ID = 0;
    static final int COL_ORIGINAL_TITLE = 1;
    static final int COL_USER_RATING = 2;
    static final int COL_PLOT_SYNOPSIS = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_POSTER_IMAGE = 5;
    static final int COL_MOVIE_ID = 6;
    static final int COL_POPULARITY = 7;

    public GridFragment() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        restartLoader();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSortOrder = Utility.getPreferredSortOrder(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_grid, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesAdapter = new GridCustomAdapter(getActivity().getApplicationContext(), new ArrayList<String>());

        GridView gridView = (GridView) inflater.inflate(R.layout.fragment_grid, container, false);
        gridView.setAdapter(mMoviesAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                if(mMovieId != null)
                    intent.putExtra(MOVIE_ID_EXTRA, mMovieId.get(position));
                startActivity(intent);
            }
        });

        return gridView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                PopMoviesContract.MoviesInfoEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                null,
                null,
                Utility.getPreferredSortOrderDB(getContext()) + " LIMIT 20"); // TODO this LIMIT must be in the URI
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> posters = new ArrayList<>();
        mMovieId = new ArrayList<>();

        if(data != null && data.moveToFirst()) {
            do {
                posters.add(BASE_POSTER_URL+data.getString(COL_POSTER_IMAGE));
                mMovieId.add(data.getInt(COL_MOVIE_ID));
            } while(data.moveToNext());

            mMoviesAdapter.clear();
            mMoviesAdapter.addAll(posters);
            mMoviesAdapter.notifyDataSetChanged();
        }

        else {
            fetchMovies();
        }
    }

    private void fetchMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity(), mHandler);
        fetchMoviesTask.execute(Utility.getPreferredSortOrder(getContext())); // TODO get prefs
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.clear();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        String sortOrder = Utility.getPreferredSortOrder(getContext());

        Log.v(LOG_TAG, sortOrder + " " + mSortOrder);

        if(sortOrder != null && !sortOrder.equals(mSortOrder)) {
            onMoviesChanged();
            mSortOrder = sortOrder;
        }
    }

    public void onMoviesChanged() {
        fetchMovies();
        restartLoader();
    }
}
