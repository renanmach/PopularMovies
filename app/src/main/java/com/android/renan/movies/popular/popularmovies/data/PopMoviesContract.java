package com.android.renan.movies.popular.popularmovies.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PopMoviesContract  {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.android.renan.movies.popular.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path appended to the base content URI movies information
    public static final String PATH_MOVIES_INFO = "movies_info";

    public static final class MoviesInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_MOVIES_INFO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_INFO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_INFO;

        public static final String TABLE_NAME = "movies_info";

        // Column with the poster foreign key TODO create a poster table
        public static final String COLUMN_POSTER_KEY = "poster_id";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // movie poster image thumbnail TODO this will be replaced by the downloaded image later on
        public static final String COLUMN_POSTER_IMAGE = "poster_image";

        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }
}
