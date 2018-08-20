
package com.udacity.popularmovies.config;

/**
 * This class is used to keep the API Key in just one place in the project.
 * Making it centralized and and reusable.
 */

public class ApiKey {

    private static final String API_KEY = "PASTE_YOUR_API_KEY_HERE";

    public static String getApiKey() {
        return API_KEY;
    }
}
