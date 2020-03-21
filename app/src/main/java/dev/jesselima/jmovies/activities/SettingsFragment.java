package dev.jesselima.jmovies.activities;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import dev.jesselima.jmovies.R;

/**
 * Created by jesse on 25/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_movies);
    }
}
