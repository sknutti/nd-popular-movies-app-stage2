package com.sknutti.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by sknutti on 8/7/15.
 */
public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private Uri mUri;

    public DetailPagerAdapter(FragmentManager fm, Uri uri) {
        super(fm);
        mUri = uri;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 1:
                return TrailerFragment.newInstance(mUri);
            case 2:
                return ReviewFragment.newInstance(mUri);
            default:
                return MovieFragment.newInstance(mUri);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 1:
                return "Trailers";
            case 2:
                return "Reviews";
            default:
                return "Details";
        }
    }
}
