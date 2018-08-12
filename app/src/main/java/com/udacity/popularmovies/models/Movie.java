package com.udacity.popularmovies.models;


import java.util.Arrays;

public class Movie {

    private int movieId;
    private String moviePosterPath;

    // Rubric requirement
    private String movieOriginalTitle;
    private String movieOverview;
    private String movieReleaseDate;
    private double movieVoteAverage;

    // Additional info
    private int movieMdbId;
    private String movieTitle;
    private String movieOriginalLanguage;
    private int movieVoteCount;
    private String movieBackdropPath;
    private double moviePopularity;
    private String movieTagline;
    private int movieBuget;
    private int movieRevenue;
    private int movieRunTime;
    private String movieGenres;

    // Empty constructor
    public Movie() {}

    // Constructor to use in posters movie list object
    public Movie(int movieId, String moviePosterPath, String movieOriginalTitle) {
        this.movieId = movieId;
        this.moviePosterPath = moviePosterPath;
        this.movieOriginalTitle = movieOriginalTitle;
    }

    // Constructor for movie details object.
    public Movie(int movieId, String moviePosterPath, String movieOriginalTitle, String movieOverview, String movieReleaseDate, double movieVoteAverage, int movieMdbId, String movieTitle, String movieOriginalLanguage, int movieVoteCount, String movieBackdropPath, double moviePopularity, String movieTagline, int movieBuget, int movieRevenue, int movieRunTime, String movieGenres) {
        this.movieId = movieId;
        this.moviePosterPath = moviePosterPath;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieOverview = movieOverview;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAverage = movieVoteAverage;
        this.movieMdbId = movieMdbId;
        this.movieTitle = movieTitle;
        this.movieOriginalLanguage = movieOriginalLanguage;
        this.movieVoteCount = movieVoteCount;
        this.movieBackdropPath = movieBackdropPath;
        this.moviePopularity = moviePopularity;
        this.movieTagline = movieTagline;
        this.movieBuget = movieBuget;
        this.movieRevenue = movieRevenue;
        this.movieRunTime = movieRunTime;
        this.movieGenres = movieGenres;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public void setMovieOriginalTitle(String movieOriginalTitle) {
        this.movieOriginalTitle = movieOriginalTitle;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public double getMovieVoteAverage() {
        return movieVoteAverage;
    }

    public void setMovieVoteAverage(double movieVoteAverage) {
        this.movieVoteAverage = movieVoteAverage;
    }

    public int getMovieMdbId() {
        return movieMdbId;
    }

    public void setMovieMdbId(int movieMdbId) {
        this.movieMdbId = movieMdbId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieOriginalLanguage() {
        return movieOriginalLanguage;
    }

    public void setMovieOriginalLanguage(String movieOriginalLanguage) {
        this.movieOriginalLanguage = movieOriginalLanguage;
    }

    public int getMovieVoteCount() {
        return movieVoteCount;
    }

    public void setMovieVoteCount(int movieVoteCount) {
        this.movieVoteCount = movieVoteCount;
    }

    public String getMovieBackdropPath() {
        return movieBackdropPath;
    }

    public void setMovieBackdropPath(String movieBackdropPath) {
        this.movieBackdropPath = movieBackdropPath;
    }

    public double getMoviePopularity() {
        return moviePopularity;
    }

    public void setMoviePopularity(double moviePopularity) {
        this.moviePopularity = moviePopularity;
    }

    public String getMovieTagline() {
        return movieTagline;
    }

    public void setMovieTagline(String movieTagline) {
        this.movieTagline = movieTagline;
    }

    public int getMovieBuget() {
        return movieBuget;
    }

    public void setMovieBuget(int movieBuget) {
        this.movieBuget = movieBuget;
    }

    public int getMovieRevenue() {
        return movieRevenue;
    }

    public void setMovieRevenue(int movieRevenue) {
        this.movieRevenue = movieRevenue;
    }

    public int getMovieRunTime() {
        return movieRunTime;
    }

    public void setMovieRunTime(int movieRunTime) {
        this.movieRunTime = movieRunTime;
    }

    public String getMovieGenres() {
        return movieGenres;
    }

    public void setMovieGenres(String movieGenres) {
        this.movieGenres = movieGenres;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId=" + movieId +
                ", moviePosterPath='" + moviePosterPath + '\'' +
                ", movieOriginalTitle='" + movieOriginalTitle + '\'' +
                ", movieOverview='" + movieOverview + '\'' +
                ", movieReleaseDate='" + movieReleaseDate + '\'' +
                ", movieVoteAverage=" + movieVoteAverage +
                ", movieMdbId=" + movieMdbId +
                ", movieTitle='" + movieTitle + '\'' +
                ", movieOriginalLanguage='" + movieOriginalLanguage + '\'' +
                ", movieGenres=" + movieGenres +
                ", movieVoteCount=" + movieVoteCount +
                ", movieBackdropPath='" + movieBackdropPath + '\'' +
                ", moviePopularity=" + moviePopularity +
                ", movieTagline='" + movieTagline + '\'' +
                ", movieBuget=" + movieBuget +
                ", movieRevenue=" + movieRevenue +
                ", movieRunTime=" + movieRunTime +
                '}';
    }
}
