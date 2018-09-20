package com.udacity.popularmovies.models;

/**
 * Created by jesse on 31/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieReview {

    private final String reviewID;
    private final String reviewAuthor;
    private final String reviewAContent;
    private final String reviewUrl;

    public MovieReview(String reviewID, String reviewAuthor, String reviewAContent, String reviewUrl) {
        this.reviewID = reviewID;
        this.reviewAuthor = reviewAuthor;
        this.reviewAContent = reviewAContent;
        this.reviewUrl = reviewUrl;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewAContent() {
        return reviewAContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    @Override
    public String toString() {
        return "MovieReview{"           + '\'' +
                "  reviewID='"          + reviewID + '\'' +
                ", reviewAuthor='"      + reviewAuthor + '\'' +
                ", reviewAContent='"    + reviewAContent + '\'' +
                ", reviewUrl='"         + reviewUrl + '\'' +
                '}';
    }
}
