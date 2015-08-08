package com.sknutti.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.ClickCallback {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String MOVIE_TITLE = "title";
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_detail_container) != null) {
            isTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
//                        .commit();
//            }
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri, String title) {
        if (isTwoPane) {
            DetailFragment detailFragment = DetailFragment.newInstance(uri, title);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment)
                    .addToBackStack(null).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(uri);
            intent.putExtra(MOVIE_TITLE, title);
            startActivity(intent);
        }
    }
}
