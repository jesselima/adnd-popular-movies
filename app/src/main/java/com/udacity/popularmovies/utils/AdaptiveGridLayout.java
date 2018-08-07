package com.udacity.popularmovies.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 *
 * SOURCE: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
 */
public class AdaptiveGridLayout {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

}
