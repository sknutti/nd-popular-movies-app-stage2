package com.sknutti.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sknutti on 8/5/15.
 */
public class Utility {
    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_most_popular));
    }

    public static boolean setFavorite(Context context, String movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean currentValue = prefs.getBoolean(movieId, false);
        if (currentValue) {
            prefs.edit().putBoolean(movieId, false).commit();
            return false;
        } else {
            prefs.edit().putBoolean(movieId, true).commit();
            return true;
        }
    }

    public static boolean isFavorite(Context context, String movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(movieId, false);
    }
}
