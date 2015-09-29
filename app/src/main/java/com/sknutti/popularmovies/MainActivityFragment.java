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
}
