package com.android.renan.movies.popular.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.renan.movies.popular.popularmovies.data.PopMoviesContract.MoviesInfoEntry;

public class PopMoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "movies.db";

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_INFORMATION_TABLE = "CREATE TABLE "
                + MoviesInfoEntry.TABLE_NAME
                + " ( "
                + MoviesInfoEntry._ID + " INTEGER PRIMARY KEY, "
                + MoviesInfoEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MoviesInfoEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, "
                + MoviesInfoEntry.COLUMN_POSTER_IMAGE + " TEXT NOT NULL, "
                + MoviesInfoEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MoviesInfoEntry.COLUMN_USER_RATING + " REAL NOT NULL, "
                + MoviesInfoEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
                + MoviesInfoEntry.COLUMN_RUNTIME + " INTEGER  DEFAULT -1, "
                + MoviesInfoEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL "
                + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_INFORMATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesInfoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
