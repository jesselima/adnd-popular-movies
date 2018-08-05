package com.udacity.popularmovies.models;


import java.util.Arrays;

public class Movie {

    private int mId;
    private String mPosterPath;

    // Rubric requirement
    private String mOriginalTitle;
    private String mOverview;
    private String mReleaseDate;
    private double mVoteAverage;

    // Additional info
    private int mMdbId;
    private String mTitle;
    private String mOriginalLanguage;
    private int mGenreIds[];
    private int mVoteCount;
    private String mBackdropPath;
    private double mPopularity;
    private String mTagline;
    private int mBuget;
    private int mRevenue;
    private int mRunTime;

    // Empty constructor
    public Movie() {}

    // Constructor to use in posters movie list object
    public Movie(int mId, String mPosterPath) {
        this.mId = mId;
        this.mPosterPath = mPosterPath;
    }

    // Constructor for movie details object.
    public Movie(int mId, String mPosterPath, String mOriginalTitle, String mOverview, String mReleaseDate, double mVoteAverage, int mMdbId, String mTitle, String mOriginalLanguage, int[] mGenreIds, int mVoteCount, String mBackdropPath, double mPopularity, String mTagline, int mBuget, int mRevenue, int mRunTime) {
        this.mId = mId;
        this.mPosterPath = mPosterPath;
        this.mOriginalTitle = mOriginalTitle;
        this.mOverview = mOverview;
        this.mReleaseDate = mReleaseDate;
        this.mVoteAverage = mVoteAverage;
        this.mMdbId = mMdbId;
        this.mTitle = mTitle;
        this.mOriginalLanguage = mOriginalLanguage;
        this.mGenreIds = mGenreIds;
        this.mVoteCount = mVoteCount;
        this.mBackdropPath = mBackdropPath;
        this.mPopularity = mPopularity;
        this.mTagline = mTagline;
        this.mBuget = mBuget;
        this.mRevenue = mRevenue;
        this.mRunTime = mRunTime;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
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

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public int getmMdbId() {
        return mMdbId;
    }

    public void setmMdbId(int mMdbId) {
        this.mMdbId = mMdbId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setmOriginalLanguage(String mOriginalLanguage) {
        this.mOriginalLanguage = mOriginalLanguage;
    }

    public int[] getmGenreIds() {
        return mGenreIds;
    }

    public void setmGenreIds(int[] mGenreIds) {
        this.mGenreIds = mGenreIds;
    }

    public int getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(int mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public void setmBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getmTagline() {
        return mTagline;
    }

    public void setmTagline(String mTagline) {
        this.mTagline = mTagline;
    }

    public int getmBuget() {
        return mBuget;
    }

    public void setmBuget(int mBuget) {
        this.mBuget = mBuget;
    }

    public int getmRevenue() {
        return mRevenue;
    }

    public void setmRevenue(int mRevenue) {
        this.mRevenue = mRevenue;
    }

    public int getmRunTime() {
        return mRunTime;
    }

    public void setmRunTime(int mRunTime) {
        this.mRunTime = mRunTime;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId =" + mId +
                ", mPosterPath ='" + mPosterPath + '\'' +
                ", mOriginalTitle ='" + mOriginalTitle + '\'' +
                ", mOverview ='" + mOverview + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mVoteAverage=" + mVoteAverage +
                ", mMdbId=" + mMdbId +
                ", mTitle='" + mTitle + '\'' +
                ", mOriginalLanguage='" + mOriginalLanguage + '\'' +
                ", mGenreIds=" + Arrays.toString(mGenreIds) +
                ", mVoteCount=" + mVoteCount +
                ", mBackdropPath='" + mBackdropPath + '\'' +
                ", mPopularity=" + mPopularity +
                ", mTagline='" + mTagline + '\'' +
                ", mBuget=" + mBuget +
                ", mRevenue=" + mRevenue +
                ", mRunTime=" + mRunTime +
                '}';
    }
}
