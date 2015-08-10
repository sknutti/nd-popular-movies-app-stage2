package com.sknutti.popularmovies;

import android.content.ContentUris;
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
import android.widget.AdapterView;
import android.widget.GridView;

import com.sknutti.popularmovies.data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    public static final int CURSOR_LOADER_ID = 0;

    public MainActivityFragment() {
    }

    public interface ClickCallback {
        void onItemSelected(Uri uri, String title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movies_grid);
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0, CURSOR_LOADER_ID);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mMovieAdapter.getItem(position);
                Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, cursor.getInt(MovieContract.MovieEntry.COL_MOVIE_ID));

                ((ClickCallback) getActivity()).onItemSelected(uri, cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE));
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        super.onResume();
    }

    public void favoriteChanged() {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortType = Utility.getPreferredSort(getActivity()).replace(".", " ");
        String selection = null;
        String [] selectionArgs = null;
        if (Utility.isFavorite(getActivity())){
            selection = MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " = ?";
            selectionArgs = new String[]{"1"};
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_PROJECTION,
                selection,
                selectionArgs,
                sortType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

//    private void updateMovies() {
//        FetchMovieTask movieTask = new FetchMovieTask();
//        String sortOrder = Utility.getPreferredSort(getActivity());
//        movieTask.execute(sortOrder);
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
////        updateMovies();
//    }

//    public class FetchMovieTask extends AsyncTask<String, Void, Void> {
//
//        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
//        private static final String API = "http://api.themoviedb.org/3";
//        private static final String API_KEY = "8c7d6ffe3386288b6e01fec70a0c04e6";
//
//        @Override
//        protected Void doInBackground(String... params) {
//            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
//            final tmdbApi api = restAdapter.create(tmdbApi.class);
//
//            api.getTopMovies(API_KEY, new Callback<MovieList>() {
//                @Override
//                public void success(MovieList movieList, Response response) {
//                    ContentResolver resolver = getActivity().getContentResolver();
//                    //save to database or update
//                    for (Movie movie : movieList.getMovies()) {
//                        final Integer movieId = movie.getId();
//                        ContentValues values = new ContentValues();
//                        values.put(MovieContract.MovieEntry._ID, movieId);
//                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
//                        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
//                        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
//                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movie.getVoteAverage());
//                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getOverview());
//                        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
//                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_LENGTH, 0);
//
//                        Cursor result = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
//                                null,
//                                MovieContract.MovieEntry._ID + " = ?",
//                                new String[] {String.valueOf(movie.getId())},
//                                null);
//                        if (result.getCount() > 0) {
//                            resolver.update(MovieContract.MovieEntry.CONTENT_URI,
//                                    values,
//                                    MovieContract.MovieEntry._ID + " = ?",
//                                    new String[]{String.valueOf(movie.getId())});
//                        } else {
//                            resolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);
//                        }
//
//                        api.getMovieDetails(movieId, API_KEY, new Callback<Movie>() {
//                            @Override
//                            public void success(Movie movie, Response response) {
//                                ContentValues values = new ContentValues();
//                                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_LENGTH, movie.getRuntime());
//
//                                getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
//                                        values,
//                                        MovieContract.MovieEntry._ID + " = ?",
//                                        new String[]{String.valueOf(movie.getId())});
//                            }
//
//                            @Override
//                            public void failure(RetrofitError error) {
//                                // what do you do when there's a failure??
//                                Log.e(LOG_TAG, "Failed to fetch movie id #" + movieId + " data from TMDB...");
//                            }
//
//                        });
//
//                        api.getMovieTrailers(movieId, API_KEY, new Callback<TrailerList>() {
//                            @Override
//                            public void success(TrailerList trailerList, Response response) {
//                                for (Trailer trailer : trailerList.getTrailers()) {
//                                    ContentValues values = new ContentValues();
//                                    values.put(MovieContract.TrailerEntry._ID, trailer.getId());
//                                    values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
//                                    values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());
//                                    values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, trailer.getSite());
//                                    values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
//                                    values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE, trailer.getSize());
//                                    values.put(MovieContract.TrailerEntry.COLUMN_TYPE, trailer.getType());
//
//                                    Cursor result = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
//                                            null,
//                                            MovieContract.TrailerEntry._ID + " = ?",
//                                            new String[]{String.valueOf(trailer.getId())},
//                                            null);
//                                    if (result.getCount() == 0) {
//                                        getActivity().getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, values);
//                                    }
//                                    result.close();
//                                }
//                            }
//
//                            @Override
//                            public void failure(RetrofitError error) {
//                                // what do you do when there's a failure??
//                                Log.e(LOG_TAG, "Failed to fetch trailers for movie id #" + movieId + " from TMDB...");
//                            }
//
//                        });
//
//                        api.getMovieReviews(movieId, API_KEY, new Callback<ReviewList>() {
//                            @Override
//                            public void success(ReviewList reviewList, Response response) {
//                                for (Review review : reviewList.getReviews()) {
//                                    ContentValues values = new ContentValues();
//                                    values.put(MovieContract.ReviewEntry._ID, review.getId());
//                                    values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
//                                    values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
//                                    values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());
//                                    values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.getUrl());
//
//                                    Cursor result = getActivity().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
//                                            null,
//                                            MovieContract.ReviewEntry._ID + " = ?",
//                                            new String[]{String.valueOf(review.getId())},
//                                            null);
//                                    if (result.getCount() == 0) {
//                                        getActivity().getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, values);
//                                    }
//                                    result.close();
//                                }
//                            }
//
//                            @Override
//                            public void failure(RetrofitError error) {
//                                // what do you do when there's a failure??
//                                Log.e(LOG_TAG, "Failed to fetch reviews for movie id #" + movieId + " from TMDB...");
//                            }
//
//                        });
//
//                        result.close();
//                    }
//
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    // what do you do when there's a failure??
//                    Log.e(LOG_TAG, "Failed to fetch data from TMDB...");
//                }
//
//            });
//            return null;
//        }
//    }
}
