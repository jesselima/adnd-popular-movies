package com.udacity.popularmovies.shared.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * This class get the device display width and returns the width divided by 185. The result is used
 * to add/remove more columns on the grid of posters.
 * <p>
 * SOURCE: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
 */
public class AdaptiveGridLayout {

    /**
     * @param context of the application
     * @return the width divide by 185.
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

}
