package com.sknutti.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 18;

    static final String DATABASE_NAME = "popularmovies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_MOVIES + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " DECIMAL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_LENGTH + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + ", " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " +
                MovieContract.TrailerEntry.TABLE_TRAILERS + " (" +
                MovieContract.TrailerEntry._ID + " TEXT PRIMARY KEY," +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_SITE + " TEXT, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_KEY + " TEXT, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE + " INTEGER, " +
                MovieContract.TrailerEntry.COLUMN_TYPE + " STRING, " +
                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_MOVIES + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.TrailerEntry._ID + ", " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.ReviewEntry.TABLE_REVIEWS + " (" +
                MovieContract.ReviewEntry._ID + " TEXT PRIMARY KEY," +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_URL + " TEXT, " +
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_MOVIES + " (" + MovieContract.MovieEntry._ID + "), " +
                " UNIQUE (" + MovieContract.ReviewEntry._ID + ", " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_TRAILERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_REVIEWS);
        onCreate(sqLiteDatabase);
    }
}
