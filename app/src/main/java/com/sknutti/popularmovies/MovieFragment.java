package com.sknutti.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

import com.sknutti.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by sknutti on 8/3/15.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Cursor mDetailCursor;
    private View mRootView;
    private Long mMovieId;
    private static final int CURSOR_LOADER_ID = 0;

    public MovieFragment() { }

    public static MovieFragment newInstance(Uri uri) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        fragment.mMovieId = ContentUris.parseId(uri);
        args.putLong("id", fragment.mMovieId);
        fragment.setArguments(args);
        return fragment;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView releaseDateView;
        public final TextView lengthView;
        public final TextView ratingView;
        public final TextView synopsisView;
        public final Button button;


        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.movie_poster);
            releaseDateView = (TextView) view.findViewById(R.id.movie_release_date);
            lengthView = (TextView) view.findViewById(R.id.movie_length);
            ratingView = (TextView) view.findViewById(R.id.movie_rating);
            synopsisView = (TextView) view.findViewById(R.id.movie_synopsis);
            button = (Button) view.findViewById(R.id.button_favorite);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        final ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);
        mRootView = rootView;

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_favorite) {
                    boolean setToFavorite = viewHolder.button.getText().toString().equals(getResources().getString(R.string.mark_favorite_button_text));

                    ContentValues values = new ContentValues();
                    int val = 0;
                    if (setToFavorite) {
                        val = 1;
                    }
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE, val);
                    int result = getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values, MovieContract.MovieEntry._ID + " = ?", new String[]{String.valueOf(mMovieId)});

                    if (result > 0 && setToFavorite) {
                        viewHolder.button.setText(getResources().getString(R.string.is_favorite_button_text));
                    } else {
                        viewHolder.button.setText(getResources().getString(R.string.mark_favorite_button_text));
                    }
//                    MainActivityFragment fragment = (MainActivityFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//                    fragment.favoriteChanged();
//                    favoriteChanged();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    public void favoriteChanged() {
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String [] selectionArgs = null;
        if (args != null){
            selection = MovieContract.MovieEntry._ID + " = ?";
            selectionArgs = new String[]{String.valueOf(args.get("id"))};
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ViewHolder viewHolder = (ViewHolder) mRootView.getTag();

        mDetailCursor = data;
        mDetailCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);

        Picasso.with(getActivity())
                .load(MovieAdapter.BASE_POSTER_URI + MovieAdapter.PHONE_SIZE + mDetailCursor.getString(MovieContract.MovieEntry.COL_POSTER_PATH))
                .into(viewHolder.imageView);
        viewHolder.releaseDateView.setText(mDetailCursor.getString(MovieContract.MovieEntry.COL_RELEASE_DATE).substring(0,4));
        viewHolder.lengthView.setText(mDetailCursor.getString(MovieContract.MovieEntry.COL_MOVIE_LENGTH) + " min");
        viewHolder.ratingView.setText(mDetailCursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATING) + "/10");
        viewHolder.synopsisView.setText(mDetailCursor.getString(MovieContract.MovieEntry.COL_MOVIE_SYNOPSIS));
        if (mDetailCursor.getInt(MovieContract.MovieEntry.COL_MOVIE_IS_FAVORITE) == 1) {
            viewHolder.button.setText(getResources().getString(R.string.is_favorite_button_text));
        } else {
            viewHolder.button.setText(getResources().getString(R.string.mark_favorite_button_text));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailCursor = null;
    }
}
