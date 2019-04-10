package com.udacity.popularmovies.shared;

import android.content.res.Resources;

import com.udacity.popularmovies.config.ApiConfig;

import java.util.Arrays;

/**
 * Created by jesse on 30/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class LanguageUtils {

    private LanguageUtils() {
    }

    @SuppressWarnings("UnusedReturnValue")
    public static String checkSystemLanguage(String loadApiLanguage) {
        String[] apiLanguagesList = ApiConfig.LANGUAGES;

        // Verify the system language is available in the API languages. If it does not, API data will be load in english by default.
        if (Arrays.asList(apiLanguagesList).contains(Resources.getSystem().getConfiguration().locale.getLanguage())) {
            loadApiLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
        }
        return loadApiLanguage;
    }
}
