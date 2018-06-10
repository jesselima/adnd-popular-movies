package com.udacity.popularmovies;

/**
 * Created by jesse on 10/06/18.
 * This is a part of the project adnd-popular-movies.
 */
public class TmdbApiKey {

    private static final String api_Key = "PASTE_YOUR_API_KEY_HERE";
    private static final String baseUrl = "https://api.themoviedb.org/3/";

    public static String getApiKey() {
        return api_Key;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
