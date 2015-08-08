package com.sknutti.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    private static final int MOVIE = 100;
    private static final int MOVIE_BY_ID = 101;
    private static final int MOVIE_WITH_DETAIL = 102;
    private static final int TRAILER = 200;
    private static final int REVIEW = 300;

    private static final SQLiteQueryBuilder sMovieWithDetailQueryBuilder;

    static{
        sMovieWithDetailQueryBuilder = new SQLiteQueryBuilder();

        //movie INNER JOIN trailer ON movie._id = trailer.movie_id INNER JOIN review ON movie._id = review.movie_id
        sMovieWithDetailQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_MOVIES + " INNER JOIN " +
                MovieContract.TrailerEntry.TABLE_TRAILERS +
                " ON " + MovieContract.MovieEntry.TABLE_MOVIES +
                "." + MovieContract.MovieEntry._ID +
                " = " + MovieContract.TrailerEntry.TABLE_TRAILERS +
                "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INNER JOIN " +
                MovieContract.ReviewEntry.TABLE_REVIEWS +
                " ON " + MovieContract.MovieEntry.TABLE_MOVIES +
                "." + MovieContract.MovieEntry._ID +
                " = " + MovieContract.ReviewEntry.TABLE_REVIEWS +
                "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.MovieEntry.TABLE_MOVIES, MOVIE);
        uriMatcher.addURI(authority, MovieContract.MovieEntry.TABLE_MOVIES + "/#", MOVIE_BY_ID);
        uriMatcher.addURI(authority, MovieContract.MovieEntry.TABLE_MOVIES + "/#/*", MOVIE_WITH_DETAIL);
        uriMatcher.addURI(authority, MovieContract.TrailerEntry.TABLE_TRAILERS, TRAILER);
        uriMatcher.addURI(authority, MovieContract.ReviewEntry.TABLE_REVIEWS, REVIEW);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_BY_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_WITH_DETAIL: {
                retCursor = sMovieWithDetailQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_BY_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null);
                break;
            }
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_TRAILERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_REVIEWS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_MOVIES, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_TRAILERS, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_REVIEWS, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
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

        switch (match) {
            case MOVIE: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_MOVIES, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_TRAILERS, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_REVIEWS, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_MOVIES, values, selection, selectionArgs);
                break;
            }
            case MOVIE_BY_ID: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_MOVIES,
                        values,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_TRAILERS, values, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_REVIEWS, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_MOVIES, null, value);
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
            case TRAILER:
                db.beginTransaction();
                int trailerReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_TRAILERS, null, value);
                        if (_id != -1) {
                            trailerReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return trailerReturnCount;
            case REVIEW:
                db.beginTransaction();
                int reviewReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_REVIEWS, null, value);
                        if (_id != -1) {
                            reviewReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return reviewReturnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}