package com.udacity.popularmovies.models;

/**
 * Created by jesse on 31/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieVideo {
    private final String videoKey;
    private final String videoName;
    private final String videoType;
    private final String videoSite;
    private final int videoSize;
    private final String videoThumbnailUrl;

    public MovieVideo(String videoKey, String videoName, String videoType, String videoSite, int videoSize, String videoThumbnailUrl) {
        this.videoKey = videoKey;
        this.videoName = videoName;
        this.videoType = videoType;
        this.videoSite = videoSite;
        this.videoSize = videoSize;
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoType() {
        return videoType;
    }

    public String getVideoSite() {
        return videoSite;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    @Override
    public String toString() {
        return "MovieVideo{"        + '\'' +
                "videoKey='"        + videoKey + '\'' +
                ", videoName='"     + videoName + '\'' +
                ", videoType='"     + videoType + '\'' +
                ", videoSite='"     + videoSite + '\'' +
                ", videoSize="      + videoSize +
                ", videoThumbnailUrl='" + videoThumbnailUrl + '\'' +
                '}';
    }
}
