package com.udacity.popularmovies.models;


public class Movie {
    private String mTitle;
    private String mOriginalTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOriginalLanguage;
    private int mId;
    private int mGenreIds[];
    private double mVoteAverage;
    private int mVoteCount;
    private double mPopularity;


    public Movie(int mId, String mPosterPath) {
        this.mId = mId;
        this.mPosterPath = mPosterPath;

    }

    public Movie(String mTitle, String mOriginalTitle, String mOverview, String mReleaseDate, String mPosterPath, String mBackdropPath, String mOriginalLanguage, int mId, int[] mGenreIds, double mVoteAverage, int mVoteCount, double mPopularity) {
        this.mTitle = mTitle;
        this.mOriginalTitle = mOriginalTitle;
        this.mOverview = mOverview;
        this.mReleaseDate = mReleaseDate;
        this.mPosterPath = mPosterPath;
        this.mBackdropPath = mBackdropPath;
        this.mOriginalLanguage = mOriginalLanguage;
        this.mId = mId;
        this.mGenreIds = mGenreIds;
        this.mVoteAverage = mVoteAverage;
        this.mVoteCount = mVoteCount;
        this.mPopularity = mPopularity;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public void setmBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setmOriginalLanguage(String mOriginalLanguage) {
        this.mOriginalLanguage = mOriginalLanguage;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int[] getmGenreIds() {
        return mGenreIds;
    }

    public void setmGenreIds(int[] mGenreIds) {
        this.mGenreIds = mGenreIds;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public int getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(int mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }
}
