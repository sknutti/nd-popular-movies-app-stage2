package com.sknutti.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.sknutti.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        // table name
        public static final String TABLE_MOVIES = "movie";

        // table columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_RATING = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_LENGTH = "duration";
        public static final String COLUMN_MOVIE_IS_FAVORITE = "is_favorite";

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TITLE = 1;
        public static final int COL_MOVIE_SYNOPSIS = 2;
        public static final int COL_RELEASE_DATE = 3;
        public static final int COL_MOVIE_RATING = 4;
        public static final int COL_POSTER_PATH = 5;
        public static final int COL_POPULARITY = 6;
        public static final int COL_MOVIE_LENGTH = 7;
        public static final int COL_MOVIE_IS_FAVORITE = 8;

        public static final String[] MOVIE_PROJECTION = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_TITLE,
                MovieEntry.COLUMN_MOVIE_SYNOPSIS,
                MovieEntry.COLUMN_RELEASE_DATE,
                MovieEntry.COLUMN_MOVIE_RATING,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_POPULARITY,
                MovieEntry.COLUMN_MOVIE_LENGTH,
                MovieEntry.COLUMN_MOVIE_IS_FAVORITE
        };

        public static final String[] DETAIL_PROJECTION = {
                TABLE_MOVIES + "." + _ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_MOVIE_SYNOPSIS,
                COLUMN_RELEASE_DATE,
                COLUMN_MOVIE_RATING,
                COLUMN_POSTER_PATH,
                COLUMN_POPULARITY,
                COLUMN_MOVIE_LENGTH,
                TrailerEntry.COLUMN_TRAILER_NAME,
                TrailerEntry.COLUMN_TRAILER_SITE,
                TrailerEntry.COLUMN_TRAILER_KEY,
                ReviewEntry.COLUMN_REVIEW_AUTHOR,
                ReviewEntry.COLUMN_REVIEW_CONTENT
        };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_MOVIES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithDetailsUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).appendPath("details").build();
        }
    }

    public static final class TrailerEntry implements BaseColumns {
        // table name
        public static final String TABLE_TRAILERS = "trailer";

        // table columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_SITE = "site";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_TRAILER_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        public static final int COL_TRAILER_ID = 0;
        public static final int COL_TRAILER_NAME = 1;
        public static final int COL_TRAILER_SITE = 2;
        public static final int COL_TRAILER_KEY = 3;
        public static final int COL_TRAILER_SIZE = 4;
        public static final int COL_TYPE = 5;

        public static final String[] TRAILER_PROJECTION = {
                _ID,
                COLUMN_TRAILER_NAME,
                COLUMN_TRAILER_SITE,
                COLUMN_TRAILER_KEY,
                COLUMN_TRAILER_SIZE,
                COLUMN_TYPE
        };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TRAILERS).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TRAILERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TRAILERS;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        // table name
        public static final String TABLE_REVIEWS = "review";

        // table columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_REVIEW_URL = "url";

        public static final int COL_REVIEW_ID = 0;
        public static final int COL_REVIEW_AUTHOR = 1;
        public static final int COL_REVIEW_CONTENT = 2;
        public static final int COL_REVIEW_URL = 3;

        public static final String[] REVIEW_PROJECTION = {
                _ID,
                COLUMN_REVIEW_AUTHOR,
                COLUMN_REVIEW_CONTENT,
                COLUMN_REVIEW_URL
        };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_REVIEWS).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEWS;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
