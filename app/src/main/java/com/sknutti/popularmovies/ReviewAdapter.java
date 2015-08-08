package com.sknutti.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sknutti.popularmovies.data.MovieContract;

public class ReviewAdapter extends CursorAdapter {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private Context mContext;
    private static int mLoaderId;

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view){
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

    public ReviewAdapter(Context context, Cursor c, int flags, int loaderId) {
        super(context, c, flags);
        mContext = context;
        Log.d(LOG_TAG, "Creating cursor...");
        mLoaderId = loaderId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.review_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String author = cursor.getString(MovieContract.ReviewEntry.COL_REVIEW_AUTHOR);
        viewHolder.authorView.setText(author);

        String content = cursor.getString(MovieContract.ReviewEntry.COL_REVIEW_CONTENT);
        viewHolder.contentView.setText(content);
    }
}