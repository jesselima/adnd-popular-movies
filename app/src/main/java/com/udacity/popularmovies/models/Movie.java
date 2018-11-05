package com.udacity.popularmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable{

    private int     movieId;
    private String  moviePosterPath;

    // Rubric requirement
    private String  movieOriginalTitle;
    private String  movieOverview;
    private String  movieReleaseDate;
    private double  movieVoteAverage;

    // Additional info
    private int     movieMdbId;
    private String  movieTitle;
    private String  movieOriginalLanguage;
    private int     movieVoteCount;
    private String  movieBackdropPath;
    private double  moviePopularity;
    private String  movieTagline;
    private int     movieBuget;
    private int     movieRevenue;
    private int     movieRunTime;
    private String  movieGenres;
    private List<MovieProductionCompany> companiesArrayList;
    private String  movieHomepage;

    // Empty constructor
    public Movie() {
    }

    // Constructor to use in posters movie list object
    public Movie(int movieId, String moviePosterPath, String movieOriginalTitle, String movieReleaseDate, double movieVoteAverage) {
        this.movieId = movieId;
        this.moviePosterPath = moviePosterPath;
        this.movieOriginalTitle = movieOriginalTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAverage = movieVoteAverage;
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

    public void setMovieMdbId(int movieMdbId) {
        this.movieMdbId = movieMdbId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieSpokenLanguage() {
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

    public List<MovieProductionCompany> getCompaniesArrayList() {
        return companiesArrayList;
    }

    public void setCompaniesArrayList(List<MovieProductionCompany> companiesArrayList) {
        this.companiesArrayList = companiesArrayList;
    }

    public String getMovieHomepage() {
        return movieHomepage;
    }

    public void setMovieHomepage(String movieHomepage) {
        this.movieHomepage = movieHomepage;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId="                  + movieId +
                ", moviePosterPath='"       + moviePosterPath + '\'' +
                ", movieOriginalTitle='"    + movieOriginalTitle + '\'' +
                ", movieOverview='"         + movieOverview + '\'' +
                ", movieReleaseDate='"      + movieReleaseDate + '\'' +
                ", movieVoteAverage="       + movieVoteAverage +
                ", movieMdbId="             + movieMdbId +
                ", movieTitle='"            + movieTitle + '\'' +
                ", movieOriginalLanguage='" + movieOriginalLanguage + '\'' +
                ", movieVoteCount="         + movieVoteCount +
                ", movieBackdropPath='"     + movieBackdropPath + '\'' +
                ", moviePopularity="        + moviePopularity +
                ", movieTagline='"          + movieTagline + '\'' +
                ", movieBuget="             + movieBuget +
                ", movieRevenue="           + movieRevenue +
                ", movieRunTime="           + movieRunTime +
                ", movieGenres='"           + movieGenres + '\'' +
                ", companiesArrayList="     + companiesArrayList +
                ", movieHomepage='"         + movieHomepage + '\'' +
                '}';
    }


    public Movie(Parcel parcel) {
        movieId = parcel.readInt();
        moviePosterPath = parcel.readString();
        movieOriginalTitle = parcel.readString();
        movieOverview = parcel.readString();
        movieReleaseDate = parcel.readString();
        movieVoteAverage = parcel.readDouble();
        movieMdbId = parcel.readInt();
        movieTitle = parcel.readString();
        movieOriginalLanguage = parcel.readString();
        movieVoteCount = parcel.readInt();
        movieBackdropPath = parcel.readString();
        moviePopularity = parcel.readDouble();
        movieTagline = parcel.readString();
        movieBuget = parcel.readInt();
        movieRevenue = parcel.readInt();
        movieRunTime = parcel.readInt();
        movieGenres = parcel.readString();
        companiesArrayList = new ArrayList<>();
        movieHomepage = parcel.readString();
    }

    //write object values to parcel for storage
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //write all properties to the parcel
        dest.writeInt(movieId);
        dest.writeString(moviePosterPath);
        dest.writeString(movieOriginalTitle);
        dest.writeString(movieOverview);
        dest.writeString(movieReleaseDate);
        dest.writeDouble(movieVoteAverage);
        dest.writeInt(movieMdbId);
        dest.writeString(movieTitle);
        dest.writeString(movieOriginalLanguage);
        dest.writeInt(movieVoteCount);
        dest.writeString(movieBackdropPath);
        dest.writeDouble(moviePopularity);
        dest.writeString(movieTagline);
        dest.writeInt(movieBuget);
        dest.writeInt(movieRevenue);
        dest.writeInt(movieRunTime);
        dest.writeString(movieGenres);
        dest.writeList(companiesArrayList);
        dest.writeString(movieHomepage);
    }

    //creator - used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

}
