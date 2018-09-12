package com.udacity.popularmovies.models;

/**
 * Created by jesse on 31/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieVideo {
    private String videoKey;
    private String videoName;
    private String videoType;
    private String videoSite;
    private int videoSize;
    private String videoThumbnailUrl;

    public MovieVideo() {}

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

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoSite() {
        return videoSite;
    }

    public void setVideoSite(String videoSite) {
        this.videoSite = videoSite;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public void setVideoThumbnailUrl(String videoThumbnailUrl) {
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

}
