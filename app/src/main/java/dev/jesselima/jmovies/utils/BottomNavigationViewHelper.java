package dev.jesselima.jmovies.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;

/** Authoring:
 * Created by Nabeel K. (https://stackoverflow.com/users/1769274/nabeel-k)
 * StackOverflow: https://stackoverflow.com/questions/41649494/how-to-remove-icon-animation-for-bottom-navigation-view-in-android
 */
public class BottomNavigationViewHelper {

    private static final String LOG_TAG = BottomNavigationViewHelper.class.getSimpleName();

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShifting(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "Unable to change value of shift mode", e);
        }
    }
}
