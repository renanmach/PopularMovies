package com.android.renan.movies.popular.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract;
import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract.MoviesInfoEntry;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static final String DETAIL_URI = "URI";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private int mMovieId;

    // detail loader id
    private static final int DETAIL_LOADER = 0;

    // Views
    TextView mOriginalTitle;
    ImageView mPoster;
    TextView mRelease;
    TextView mRuntime;
    TextView mUserRating;
    Button mFavoriteButton;
    TextView mSynopsis;


    private static final String[] DETAIL_COLUMNS = {
            MoviesInfoEntry.COLUMN_RUNTIME,
            MoviesInfoEntry.COLUMN_ORIGINAL_TITLE,
            MoviesInfoEntry.COLUMN_POPULARITY,
            MoviesInfoEntry.COLUMN_PLOT_SYNOPSIS,
            MoviesInfoEntry.TABLE_NAME + "." + MoviesInfoEntry._ID,
            MoviesInfoEntry.COLUMN_USER_RATING,
            MoviesInfoEntry.COLUMN_POSTER_IMAGE,
            MoviesInfoEntry.COLUMN_RELEASE_DATE
    };

    public static final int COL_RUNTIME = 0;
    public static final int COL_ORIGINAL_TITLE = 1;
    public static final int COL_POPULARITY = 2;
    public static final int COL_PLOT_SYNOPSIS = 3;
    public static final int COL_MOVIES_ID = 4;
    public static final int COL_USER_RATING = 5;
    public static final int COL_POSTER_IMAGE = 6;
    public static final int COL_RELEASE_DATE = 7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieId = getActivity().getIntent().getIntExtra(GridFragment.MOVIE_ID_EXTRA, -1);

        // TODO move somewhere else
        getRuntime();
        //setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mOriginalTitle = (TextView) rootView.findViewById(R.id.detail_original_title);
        mPoster = (ImageView) rootView.findViewById(R.id.detail_poster);
        mRelease = (TextView) rootView.findViewById(R.id.detail_release_year);
        mRuntime = (TextView) rootView.findViewById(R.id.detail_runtime);
        mUserRating = (TextView) rootView.findViewById(R.id.detail_user_rating);
        mFavoriteButton = (Button) rootView.findViewById(R.id.detail_favorite_button);
        mSynopsis = (TextView) rootView.findViewById(R.id.detail_synopsis);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = PopMoviesContract.MoviesInfoEntry.buildMovieUri(mMovieId);

        return new CursorLoader(
                getActivity(),
                uri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mOriginalTitle.setText(data.getString(COL_ORIGINAL_TITLE));
            mRelease.setText(data.getString(COL_RELEASE_DATE));
            mSynopsis.setText(data.getString(COL_PLOT_SYNOPSIS));
            mUserRating.setText(String.valueOf(data.getDouble(COL_USER_RATING)));
            mRuntime.setText(String.valueOf(data.getInt(COL_RUNTIME)));
            Picasso.with(getContext())
                    .load(GridFragment.BASE_POSTER_URL+ data.getString(COL_POSTER_IMAGE))
                    .fit()
                    .into(mPoster);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void getRuntime() {
        FetchRunTimeTask fetchRunTimeTask = new FetchRunTimeTask(getActivity(), mMovieId);
        fetchRunTimeTask.execute();
    }
}
