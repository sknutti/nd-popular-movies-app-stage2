package com.sknutti.popularmovies;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sknutti.popularmovies.data.MovieContract;

/**
 * Created by sknutti on 8/3/15.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter mTrailersAdapter;
    private static final int TRAILER_CURSOR_LOADER_ID = 1;

    public TrailerFragment() { }

    public static TrailerFragment newInstance(Uri uri) {
        TrailerFragment fragment = new TrailerFragment();
        Bundle args = new Bundle();
        args.putLong("id", ContentUris.parseId(uri));
        fragment.setArguments(args);
        return fragment;
    }

    public static class ViewHolder {
        public final ListView trailersListView;

        public ViewHolder(View view){
            trailersListView = (ListView) view.findViewById(R.id.trailer_list);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);

        mTrailersAdapter = new TrailerAdapter(getActivity(), null, 0, TRAILER_CURSOR_LOADER_ID);
        viewHolder.trailersListView.setAdapter(mTrailersAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        getLoaderManager().initLoader(TRAILER_CURSOR_LOADER_ID, args, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String [] selectionArgs = null;
        if (args != null){
            selection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(args.get("id"))};
        }

        return new CursorLoader(getActivity(),
                MovieContract.TrailerEntry.CONTENT_URI,
                MovieContract.TrailerEntry.TRAILER_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTrailersAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailersAdapter.swapCursor(null);
    }
}
