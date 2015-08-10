package com.sknutti.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.sknutti.popularmovies.R;
import com.sknutti.popularmovies.api.tmdbApi;
import com.sknutti.popularmovies.data.MovieContract;
import com.sknutti.popularmovies.model.Movie;
import com.sknutti.popularmovies.model.MovieList;
import com.sknutti.popularmovies.model.Review;
import com.sknutti.popularmovies.model.ReviewList;
import com.sknutti.popularmovies.model.Trailer;
import com.sknutti.popularmovies.model.TrailerList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sknutti on 8/10/15.
 */
public class SyncAdapter  extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 120;   // seconds * minutes, sync interval
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String API = "http://api.themoviedb.org/3";
    private static final String API_KEY = "8c7d6ffe3386288b6e01fec70a0c04e6";


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
        final tmdbApi api = restAdapter.create(tmdbApi.class);

        api.getTopMovies(API_KEY, new Callback<MovieList>() {
            @Override
            public void success(MovieList movieList, Response response) {
                ContentResolver resolver = getContext().getContentResolver();
                //save to database or update
                for (Movie movie : movieList.getMovies()) {
                    final Integer movieId = movie.getId();
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry._ID, movieId);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movie.getVoteAverage());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getOverview());
                    values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_LENGTH, 0);

                    Cursor result = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            MovieContract.MovieEntry._ID + " = ?",
                            new String[] {String.valueOf(movie.getId())},
                            null);
                    if (result.getCount() > 0) {
                        resolver.update(MovieContract.MovieEntry.CONTENT_URI,
                                values,
                                MovieContract.MovieEntry._ID + " = ?",
                                new String[]{String.valueOf(movie.getId())});
                    } else {
                        resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
                    }

                    api.getMovieDetails(movieId, API_KEY, new Callback<Movie>() {
                        @Override
                        public void success(Movie movie, Response response) {
                            ContentValues values = new ContentValues();
                            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_LENGTH, movie.getRuntime());

                            getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                                    values,
                                    MovieContract.MovieEntry._ID + " = ?",
                                    new String[]{String.valueOf(movie.getId())});
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            // what do you do when there's a failure??
                            Log.e(LOG_TAG, "Failed to fetch movie id #" + movieId + " data from TMDB...");
                        }

                    });

                    api.getMovieTrailers(movieId, API_KEY, new Callback<TrailerList>() {
                        @Override
                        public void success(TrailerList trailerList, Response response) {
                            for (Trailer trailer : trailerList.getTrailers()) {
                                ContentValues values = new ContentValues();
                                values.put(MovieContract.TrailerEntry._ID, trailer.getId());
                                values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                                values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());
                                values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, trailer.getSite());
                                values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
                                values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE, trailer.getSize());
                                values.put(MovieContract.TrailerEntry.COLUMN_TYPE, trailer.getType());

                                Cursor result = getContext().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
                                        null,
                                        MovieContract.TrailerEntry._ID + " = ?",
                                        new String[]{String.valueOf(trailer.getId())},
                                        null);
                                if (result.getCount() == 0) {
                                    getContext().getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, values);
                                }
                                result.close();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            // what do you do when there's a failure??
                            Log.e(LOG_TAG, "Failed to fetch trailers for movie id #" + movieId + " from TMDB...");
                        }

                    });

                    api.getMovieReviews(movieId, API_KEY, new Callback<ReviewList>() {
                        @Override
                        public void success(ReviewList reviewList, Response response) {
                            for (Review review : reviewList.getReviews()) {
                                ContentValues values = new ContentValues();
                                values.put(MovieContract.ReviewEntry._ID, review.getId());
                                values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                                values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
                                values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());
                                values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.getUrl());

                                Cursor result = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
                                        null,
                                        MovieContract.ReviewEntry._ID + " = ?",
                                        new String[]{String.valueOf(review.getId())},
                                        null);
                                if (result.getCount() == 0) {
                                    getContext().getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, values);
                                }
                                result.close();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            // what do you do when there's a failure??
                            Log.e(LOG_TAG, "Failed to fetch reviews for movie id #" + movieId + " from TMDB...");
                        }

                    });

                    result.close();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                // what do you do when there's a failure??
                Log.e(LOG_TAG, "Failed to fetch data from TMDB...");
            }

        });
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
