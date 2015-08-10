package com.sknutti.popularmovies;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * Created by sknutti on 8/3/15.
 */
public class DetailFragment extends Fragment {
    private static final String URI_KEY = "uri_key";
    private static final String TITLE_KEY = "title_key";
    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private View mRootView;
    private Uri mUri;
    private String mTitle;
    private Long mMovieId;

    public DetailFragment() { }

    public static DetailFragment newInstance(Uri uri, String title) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.mUri = uri;
        fragment.mTitle = title;
        fragment.mMovieId = ContentUris.parseId(uri);
        args.putLong("id", fragment.mMovieId);
        fragment.setArguments(args);
        return fragment;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.movie_poster);
            titleView = (TextView) view.findViewById(R.id.movie_title);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);
        mRootView = rootView;

        if (savedInstanceState != null && savedInstanceState.containsKey(URI_KEY)) {
            mUri = savedInstanceState.getParcelable(URI_KEY);
            mTitle = savedInstanceState.getString(TITLE_KEY);
        }

        mDetailPagerAdapter = new DetailPagerAdapter(getActivity().getSupportFragmentManager(), mUri);
        mViewPager = (ViewPager) rootView.findViewById(R.id.detail_pager);
        mViewPager.setAdapter(mDetailPagerAdapter);

        //Bind the indicator to the adapter
        TitlePageIndicator indicator = (TitlePageIndicator) rootView.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ViewHolder viewHolder = (ViewHolder) mRootView.getTag();
        viewHolder.titleView.setText(mTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(URI_KEY, mUri);
        outState.putString(TITLE_KEY, mTitle);
        super.onSaveInstanceState(outState);
    }
}
