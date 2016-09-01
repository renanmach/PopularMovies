package com.android.renan.movies.popular.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PopMoviesProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopMoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_DETAIL = 101;


    //location.location_setting = ?
    private static final String sMovieIdSelection =
            PopMoviesContract.MoviesInfoEntry.TABLE_NAME+
                    "." + PopMoviesContract.MoviesInfoEntry.COLUMN_MOVIE_ID + " = ? ";


    @Override
    public boolean onCreate() {
        mOpenHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch(sUriMatcher.match(uri)) {
            case MOVIES:
                retCursor = db.query(
                        PopMoviesContract.MoviesInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_DETAIL:
                retCursor = getMovieWithInfo(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retCursor;
    }

    // queries the database and returns a cursor with information of the movie
    private Cursor getMovieWithInfo(Uri uri, String[] projection, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int id = PopMoviesContract.MoviesInfoEntry.getMovieIdFromUri(uri);
        String selection = sMovieIdSelection;
        String[] selectionArgs={String.valueOf(id)};

         Cursor retCursor = db.query(
                 PopMoviesContract.MoviesInfoEntry.TABLE_NAME,
                 projection,
                 selection,
                 selectionArgs,
                 null,
                 null,
                 null);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return PopMoviesContract.MoviesInfoEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return PopMoviesContract.MoviesInfoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(PopMoviesContract.MoviesInfoEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = PopMoviesContract.MoviesInfoEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if(selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        PopMoviesContract.MoviesInfoEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match) {
            case MOVIES:
                rowsUpdated = db.update(PopMoviesContract.MoviesInfoEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_DETAIL: {
                int id = PopMoviesContract.MoviesInfoEntry.getMovieIdFromUri(uri);
                String[] sArgs={String.valueOf(id)};
                rowsUpdated = db.update(PopMoviesContract.MoviesInfoEntry.TABLE_NAME,
                        contentValues,
                        sMovieIdSelection,
                        sArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PopMoviesContract.PATH_MOVIES_INFO, MOVIES);
        matcher.addURI(authority, PopMoviesContract.PATH_MOVIES_INFO +"/#", MOVIE_DETAIL);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(PopMoviesContract.MoviesInfoEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
