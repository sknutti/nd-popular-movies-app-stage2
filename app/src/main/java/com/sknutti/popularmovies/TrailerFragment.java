package com.sknutti.popularmovies;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sknutti.popularmovies.data.MovieContract;

/**
 * Created by sknutti on 8/3/15.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter mTrailersAdapter;
    private ShareActionProvider mShareActionProvider;
    private Uri mYoutubeUri;
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
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.menu.menu_trailer_fragment);
        if (item == null) {
            inflater.inflate(R.menu.menu_trailer_fragment, menu);
        }

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mYoutubeUri != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mYoutubeUri);
        return shareIntent;
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
        if (data != null && data.moveToFirst()) {
            String youtubeKey = data.getString(MovieContract.TrailerEntry.COL_TRAILER_KEY);
            mYoutubeUri = Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey);

            mTrailersAdapter.swapCursor(data);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailersAdapter.swapCursor(null);
    }
}
