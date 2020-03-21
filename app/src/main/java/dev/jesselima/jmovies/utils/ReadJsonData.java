package dev.jesselima.jmovies.utils;

import android.util.Log;

import dev.jesselima.jmovies.config.ApiConfig;
import dev.jesselima.jmovies.config.ApiConfig.JsonKey;
import dev.jesselima.jmovies.models.Movie;
import dev.jesselima.jmovies.models.MovieProductionCompany;
import dev.jesselima.jmovies.models.MovieReview;
import dev.jesselima.jmovies.models.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static dev.jesselima.jmovies.config.ApiConfig.getMovieBaseImageUrl;

/**
 * When the static method are called they will extract data from JSON and return movie.
 */
final class ReadJsonData {

    private ReadJsonData() {
    }

    /**
     * @param jsonResponseMovieList is the JSON input in a String format.
     * @return a list of {@link Movie} objects.
     */
    public static List<Movie> extractMovieListData(String jsonResponseMovieList) {

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
                String fullPosterPathUrl = getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W342 + posterPathId;
                String originalTitle = currentMovieResult.optString(JsonKey.ORIGINAL_TITLE);

                String releaseDate = DateUtils.simpleDateFormat(currentMovieResult.optString(JsonKey.RELEASE_DATE));
                double voteAverage = currentMovieResult.optDouble(JsonKey.VOTE_AVERAGE);

                // Instantiate a Movie class object and add the JSON data as inputs parameters.
                movieList.add(new Movie(movieId, fullPosterPathUrl, originalTitle, releaseDate, voteAverage));
            }

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie
        return movieList;
    }


    /**
     * @param jsonResponseMovieDetails is the JSON input in a String format.
     * @return a {@link Movie} object with movie details.
     */
    public static Movie extractMovieDetailsData(String jsonResponseMovieDetails) {

        Movie movie = new Movie();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseMovieDetails);

            JSONArray spokenLanguagesArray = rootJsonObject.optJSONArray(JsonKey.SPOKEN_LANGUAGES);
            JSONArray genresArray = rootJsonObject.optJSONArray(JsonKey.GENRES);

            StringBuilder spokenLanguage = new StringBuilder();
            if (rootJsonObject.has(JsonKey.SPOKEN_LANGUAGES)) {
                for (int i = 0; i < spokenLanguagesArray.length(); i++) {
                    if (i > 0) {
                        spokenLanguage.append(", ");
                    }
                    JSONObject spokenLanguageObject = spokenLanguagesArray.getJSONObject(i);
                    spokenLanguage.append(spokenLanguageObject.optString(JsonKey.NAME));
                }
            }

            StringBuilder genres = new StringBuilder();
            if (rootJsonObject.has(JsonKey.GENRES)) {
                for (int i = 0; i < genresArray.length(); i++) {
                    if (i > 0) {
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
            movie.setMovieHomepage(rootJsonObject.optString(JsonKey.HOMEPAGE));

            List<MovieProductionCompany> companies = new ArrayList<>();

            if (rootJsonObject.has(JsonKey.PRODUCTION_COMPANIES)) {
                JSONArray companiesJsonArray = rootJsonObject.getJSONArray(JsonKey.PRODUCTION_COMPANIES);
                for (int i = 0; companiesJsonArray.length() > i; i++) {
                    JSONObject companyItem = companiesJsonArray.getJSONObject(i);
                    MovieProductionCompany movieProductionCompany = new MovieProductionCompany();
                    movieProductionCompany.setCompanyId(companyItem.optInt(JsonKey.ID));
                    movieProductionCompany.setCompanyLogoPath(ApiConfig.getMovieBaseImageUrl() + ApiConfig.UrlParamKey.IMAGE_POSTER_W342 + companyItem.optString(JsonKey.LOGO_PATH));
                    movieProductionCompany.setCompanyName(companyItem.optString(JsonKey.NAME));
                    movieProductionCompany.setCompanyCountry(companyItem.optString(JsonKey.ORIGIN_COUNTRY));
                    companies.add(movieProductionCompany);
                }
                movie.setCompaniesArrayList(companies);
            }

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie
        return movie;
    }


    /**
     * @param jsonResponseMovieVideoList is the JSON input in a String format.
     * @return a list of {@link MovieVideo} objects.
     */
    public static List<MovieVideo> extractMovieVideoList(String jsonResponseMovieVideoList) {

        List<MovieVideo> movieVideoList = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseMovieVideoList);

            // Get the array of results (movies videos data)
            JSONArray resultsArray = rootJsonObject.getJSONArray(JsonKey.RESULTS);

            // For each position in the movieArray (inside the JSONArray object)
            // extract the JSON data from such position in the array and put the data into a new MovieVideo class object.
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single Movie object in the movieArray (in within the list of MovieVideo)
                JSONObject currentMovieVideoItemData = resultsArray.getJSONObject(i);

                String videoKey = currentMovieVideoItemData.optString("key");
                String videoName = currentMovieVideoItemData.optString("name");
                String videoType = currentMovieVideoItemData.optString("type");
                String videoSite = currentMovieVideoItemData.optString("site");
                int videoSize = currentMovieVideoItemData.optInt("size");

                String videoThumbnailUrl = "https://img.youtube.com/vi/" + videoKey + "/mqdefault.jpg";

                // Instantiate a Movie class object and add the JSON data as inputs parameters.
                movieVideoList.add(new MovieVideo(videoKey, videoName, videoType, videoSite, videoSize, videoThumbnailUrl));
            }

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie videos
        return movieVideoList;
    }


    /**
     * @param jsonResponseMovieReviewList is the JSON input in a String format.
     * @return a list of {@link MovieVideo} objects.
     */
    public static List<MovieReview> extractMovieReviewList(String jsonResponseMovieReviewList) {

        List<MovieReview> movieReviewsList = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseMovieReviewList);

            // Get the array of results (movies videos data)
            JSONArray resultsArray = rootJsonObject.getJSONArray(JsonKey.RESULTS);

            // For each position in the movieArray (inside the JSONArray object)
            // extract the JSON data from such position in the array and put the data into a new Movie class object.
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single Movie object in the movieArray (in within the list of Movie)
                JSONObject currentMovieReviewItemData = resultsArray.getJSONObject(i);

                String reviewId = currentMovieReviewItemData.optString("id");
                String reviewAuthor = currentMovieReviewItemData.optString("author");
                String reviewContent = currentMovieReviewItemData.optString("content");
                String reviewUrl = currentMovieReviewItemData.optString("url");

                // Instantiate a Movie class object and add the JSON data as inputs parameters.
                MovieReview movieReviewItem = new MovieReview(reviewId, reviewAuthor, reviewContent, reviewUrl);
                movieReviewsList.add(movieReviewItem);
            }

        } catch (JSONException e) {
            Log.e("QueryUtilsMovieList", "Problem parsing the movie JSON results", e);
        }
        // Return the list of movie videos
        return movieReviewsList;
    }
}
