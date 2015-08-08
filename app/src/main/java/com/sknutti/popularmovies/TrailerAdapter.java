package com.sknutti.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sknutti.popularmovies.data.MovieContract;

public class TrailerAdapter extends CursorAdapter {
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    public static final Uri BASE_POSTER_URI = Uri.parse("http://image.tmdb.org/t/p/");
    public static final String PHONE_SIZE = "w185";

    private Context mContext;
    private static int mLoaderId;

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;


        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.trailer_play_button);
            textView = (TextView) view.findViewById(R.id.trailer_description);
        }
    }

    public TrailerAdapter(Context context, Cursor c, int flags, int loaderId) {
        super(context, c, flags);
        mContext = context;
        Log.d(LOG_TAG, "Creating cursor...");
        mLoaderId = loaderId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.trailer_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        final String youtubeKey = cursor.getString(MovieContract.TrailerEntry.COL_TRAILER_KEY);
        String trailerDescription = cursor.getString(MovieContract.TrailerEntry.COL_TRAILER_NAME);
        viewHolder.textView.setText(trailerDescription);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri youtubeUri = Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey);
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
            }
        });
    }
}