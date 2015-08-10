package com.sknutti.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sknutti on 8/5/15.
 */
public class Utility {
    private static final String PREF_FAVORITES = "favorites";

    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_most_popular));
    }

    public static boolean isFavorite(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_favorites_key), Boolean.parseBoolean(context.getString(R.string.pref_favorites_default)));
    }
}
