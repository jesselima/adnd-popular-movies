package com.udacity.popularmovies.utils;

import android.text.TextUtils;
import android.util.Log;

import com.udacity.popularmovies.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * This class offers Helper methods related to requesting and receiving movie details data from The TMDB.
 */
public final class QueryUtilsMovieDetails {

    /**
     * Tag for the log messages output
     */
    private static final String LOG_TAG = QueryUtilsMovieDetails.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtilsMovieDetails} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtilsMovieDetails (and an object instance of QueryUtilsMovieList is not needed).
     */
    private QueryUtilsMovieDetails() {
    }

    /**
     * Query the API data from the server and return details of a movie {@link Movie} object.
     *
     * @param requestUrl is the URL request to the API. This requestUrl (in this case) will come from inside of onCreateLoader method in {@link com.udacity.popularmovies.activities.MovieDetailsActivity}
     * @return Movie details data.
     */
    public static Movie fetchMovieData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Ops! Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a movie object {@link Movie}
        // Return a Movie {@link Movie}
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     *
     * @param stringUrl is the String URl for the request
     * @return a URL object
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url is the given URL object
     * @return a json in a String data type
     * @throws IOException if there is a problem during the request throw a error at the log.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, do not make the request.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
     *
     * @param inputStream is the stream of data from the server.
     * @return a String with the JSON data inside it.
     * @throws IOException gives a error info if inputStream does not work.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static Movie extractFeatureFromJson(String movieDetails) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieDetails)) {
            return null;
        }

        return ReadJsonData.extractMovieDetailsData(movieDetails);
    }

}

