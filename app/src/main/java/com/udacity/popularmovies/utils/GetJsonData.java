package com.udacity.popularmovies.utils;

import android.util.Log;

import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.JsonKey;
import com.udacity.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.popularmovies.config.ApiConfig.getMovieBaseImageUrl;

/**
 * Created by jesse on 03/08/18.
 * This is a part of the project adnd-popular-movies.
 */
final class GetJsonData {

    private GetJsonData() {
    }

    public static List<Movie> extractMovieListData(String jsonResponseMovieList) {
        Log.d("Running ", "extractMovieListData()");

        List<Movie> movieList = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseMovieList);

            // Get the array of results (movies)
            JSONArray resultsArray = rootJsonObject.getJSONArray(JsonKey.RESULTS);

            // For each position in the movieArray (inside the JSONArray object)
            // extract the JSON data from such position in the array and put the data into a new Movie class object.
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single Movie object in the movieArray (in within the list of Movie)
                JSONObject currentMovieResult = resultsArray.getJSONObject(i);

                int movieId = currentMovieResult.optInt(JsonKey.ID);
                String posterPathId = currentMovieResult.optString(JsonKey.POSTER_PATH);
                String fullPosterPathUrl = getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W500 + posterPathId;

                // Instantiate a Movie class object and add the JSON data as inputs parameters.
                Movie movieItem = new Movie(movieId, fullPosterPathUrl);
                movieList.add(movieItem);
            }

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie
        return movieList;
    }


    public static Movie extractMovieDetailsData(String jsonResponseMovieDetails) {
        Log.d("Running ", "extractMovieListData()");

        Movie movie = new Movie();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseMovieDetails);

            movie.setMovieId(rootJsonObject.optInt(JsonKey.ID));
            movie.setMovieOriginalTitle(rootJsonObject.optString(JsonKey.ORIGINAL_TITLE));
            movie.setMovieTitle(rootJsonObject.optString(JsonKey.TITLE));
            movie.setMovieOverview(rootJsonObject.optString(JsonKey.OVERVIEW));
            movie.setMovieReleaseDate(rootJsonObject.optString(JsonKey.RELEASE_DATE));
            movie.setMovieOriginalLanguage(rootJsonObject.optString(JsonKey.ORIGINAL_LANGUAGE));
            movie.setMovieTagline(rootJsonObject.optString(JsonKey.TAGLINE));
            movie.setMoviePopularity(rootJsonObject.optDouble(JsonKey.POPULARITY));
            movie.setMovieVoteAverage(rootJsonObject.optDouble(JsonKey.VOTE_AVERAGE));
            movie.setMovieVoteCount(rootJsonObject.optInt(JsonKey.VOTE_COUNT));
            movie.setMovieMdbId(rootJsonObject.optInt(JsonKey.MDB_ID));
            movie.setMovieBuget(rootJsonObject.optInt(JsonKey.BUGET));
            movie.setMovieRevenue(rootJsonObject.optInt(JsonKey.REVENUE));
            movie.setMovieRunTime(rootJsonObject.optInt(JsonKey.RUNTIME));
            movie.setMoviePosterPath(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W154 + rootJsonObject.optString(JsonKey.POSTER_PATH));
            movie.setMovieBackdropPath(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W154 + rootJsonObject.optString(JsonKey.BACKDROP_PATH));

            Log.v("Movie ===>", movie.toString());

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie
        return movie;
    }

}
