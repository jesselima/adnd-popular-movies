package com.udacity.popularmovies.utils;

import android.util.Log;

import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.config.ApiConfig.JsonKey;
import com.udacity.popularmovies.models.Company;
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
                String originalTitle = currentMovieResult.optString(JsonKey.ORIGINAL_TITLE);

                // Instantiate a Movie class object and add the JSON data as inputs parameters.
                Movie movieItem = new Movie(movieId, fullPosterPathUrl, originalTitle);
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

            JSONArray spokenLanguagesArray = rootJsonObject.optJSONArray(JsonKey.SPOKEN_LANGUAGES);
            JSONArray genresArray = rootJsonObject.optJSONArray(JsonKey.GENRES);

            StringBuilder spokenLanguage = new StringBuilder();
            if (rootJsonObject.has(JsonKey.SPOKEN_LANGUAGES)) {
                for (int i = 0; i < spokenLanguagesArray.length(); i++){
                    if (i > 0){
                        spokenLanguage.append(", ");
                    }
                    JSONObject spokenLanguageObject = spokenLanguagesArray.getJSONObject(i);
                    spokenLanguage.append(spokenLanguageObject.optString(JsonKey.NAME));
                }
            }

            StringBuilder genres = new StringBuilder();
            if (rootJsonObject.has(JsonKey.GENRES)) {
                for (int i = 0; i < genresArray.length(); i++) {
                    if (i > 0){
                        genres.append(" | ");
                    }
                    JSONObject genreObject = genresArray.getJSONObject(i);
                    genres.append(genreObject.optString(JsonKey.NAME));
                }

            }

            movie.setMovieId(rootJsonObject.optInt(JsonKey.ID));
            movie.setMovieOriginalTitle(rootJsonObject.optString(JsonKey.ORIGINAL_TITLE));
            movie.setMovieOverview(rootJsonObject.optString(JsonKey.OVERVIEW));
            movie.setMovieReleaseDate(rootJsonObject.optString(JsonKey.RELEASE_DATE));
            movie.setMovieVoteAverage(rootJsonObject.optDouble(JsonKey.VOTE_AVERAGE));
            // Additional Info
            movie.setMovieTitle(rootJsonObject.optString(JsonKey.TITLE));
            movie.setMovieOriginalLanguage(spokenLanguage.toString());
            movie.setMovieTagline(rootJsonObject.optString(JsonKey.TAGLINE));
            movie.setMoviePopularity(rootJsonObject.optDouble(JsonKey.POPULARITY));
            movie.setMovieVoteCount(rootJsonObject.optInt(JsonKey.VOTE_COUNT));
            movie.setMovieMdbId(rootJsonObject.optInt(JsonKey.MDB_ID));
            movie.setMovieBuget(rootJsonObject.optInt(JsonKey.BUDGET));
            movie.setMovieRevenue(rootJsonObject.optInt(JsonKey.REVENUE));
            movie.setMovieRunTime(rootJsonObject.optInt(JsonKey.RUNTIME));
            movie.setMovieGenres(genres.toString());
            movie.setMoviePosterPath(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W500 + rootJsonObject.optString(JsonKey.POSTER_PATH));
            movie.setMovieBackdropPath(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W780 + rootJsonObject.optString(JsonKey.BACKDROP_PATH));

            ArrayList<Company> companies = new ArrayList<>();
            Company company = new Company();

            if (rootJsonObject.has(JsonKey.PRODUCTION_COMPANIES)){

                JSONArray companiesJsonArray = rootJsonObject.getJSONArray(JsonKey.PRODUCTION_COMPANIES);

                for (int i = 0; companiesJsonArray.length() > i; i++){

                    JSONObject companyItem = companiesJsonArray.getJSONObject(i);
                        company.setCompanyId(companyItem.optInt(JsonKey.ID));
                        company.setCompanyLogoPath(companyItem.optString(JsonKey.LOGO_PATH));
                        company.setCompanyName(companyItem.optString(JsonKey.NAME));
                        company.setCompanyCountry(companyItem.optString(JsonKey.ORIGIN_COUNTRY));
                    companies.add(company);

                    Log.v("Company Item: ", company.toString());
                }
            }

            Log.v("MOVIE ID: ==>", String.valueOf(rootJsonObject.optInt(JsonKey.ID)));

            movie.setCompaniesArrayList(companies);
            movie.setMovieHomepage(rootJsonObject.optString(JsonKey.HOMEPAGE));

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie
        return movie;
    }

}
