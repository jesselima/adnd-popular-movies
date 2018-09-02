package com.udacity.popularmovies.models;

/**
 * Created by jesse on 31/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieReview {

    private String reviewID;
    private String reviewAuthor;
    private String reviewAContent;
    private String reviewUrl;

    public MovieReview() {
    }

    public MovieReview(String reviewID, String reviewAuthor, String reviewAContent, String reviewUrl) {
        this.reviewID = reviewID;
        this.reviewAuthor = reviewAuthor;
        this.reviewAContent = reviewAContent;
        this.reviewUrl = reviewUrl;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewAContent() {
        return reviewAContent;
    }

    public void setReviewAContent(String reviewAContent) {
        this.reviewAContent = reviewAContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }
}
