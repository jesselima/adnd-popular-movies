package dev.jesselima.jmovies.activities;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import dev.jesselima.jmovies.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_movies);
    }
}
