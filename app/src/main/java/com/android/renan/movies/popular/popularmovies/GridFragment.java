package com.android.renan.movies.popular.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract;


public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIES_LOADER = 0;
    private GridCustomAdapter mMoviesAdapter;

    private static String[] MOVIES_COLUMNS = {
            PopMoviesContract.MoviesInfoEntry._ID,
            PopMoviesContract.MoviesInfoEntry.COLUMN_ORIGINAL_TITLE,
            PopMoviesContract.MoviesInfoEntry.COLUMN_USER_RATING,
            PopMoviesContract.MoviesInfoEntry.COLUMN_PLOT_SYNOPSIS,
            PopMoviesContract.MoviesInfoEntry.COLUMN_RELEASE_DATE,
            PopMoviesContract.MoviesInfoEntry.COLUMN_POSTER_IMAGE
    };

    static final int COL_MOVIES_ID = 0;
    static final int COL_ORIGINAL_TITLE = 1;
    static final int COL_USER_RATING = 2;
    static final int COL_PLOT_SYNOPSIS = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_POSTER_IMAGE = 5;

    public static String[] eatFoodyImages = {
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ",
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //TODO inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesAdapter = new GridCustomAdapter(getActivity().getApplicationContext(), new String[0]);

        GridView gridView = (GridView) inflater.inflate(R.layout.fragment_grid, container, false);
        gridView.setAdapter(mMoviesAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                startActivity(intent);
            }
        });


        // TODO move to another place
        updateMovies();
        //getLoaderManager().restartLoader(MOVIES_LOADER, null, this);

        return gridView;
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
        fetchMoviesTask.execute("top_rated"); // TODO get prefs
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
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
