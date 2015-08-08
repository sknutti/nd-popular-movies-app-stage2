package com.sknutti.popularmovies;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by sknutti on 8/6/15.
 */
public class DetailActivity extends ActionBarActivity {

    private long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            mMovieId = ContentUris.parseId(uri);
            String title = getIntent().getExtras().getString(MainActivity.MOVIE_TITLE);

            DetailFragment fragment = DetailFragment.newInstance(uri, title);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, fragment)
                    .addToBackStack(null).commit();
        }
    }
}
