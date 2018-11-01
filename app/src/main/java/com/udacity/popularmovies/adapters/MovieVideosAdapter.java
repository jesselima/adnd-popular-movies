package com.udacity.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.models.MovieVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesse on 01/09/18.
 * This is a part of the project adnd-popular-movies.
 */
@SuppressWarnings("unused")
public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideoViewHolder> {

    private static final String LOG_TAG = MovieVideosAdapter.class.getSimpleName();
    private static final String YOUTUBE_KEY = BuildConfig.YOUTUBE_KEY;

    private final List<MovieVideo> movieVideoList;
    private final Context mContext;

    public MovieVideosAdapter(Context mContext, List<MovieVideo> movieList) {
        this.movieVideoList = movieList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MovieVideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_video, viewGroup, false);
        return new MovieVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieVideoViewHolder holder, final int position) {
        final int adapterPosition = holder.getAdapterPosition();

        holder.videoTitle.setText(movieVideoList.get(adapterPosition).getVideoName());
        holder.videoType.setText(movieVideoList.get(adapterPosition).getVideoType());
        holder.videoSite.setText(movieVideoList.get(adapterPosition).getVideoSite());
        holder.videoSize.setText(String.valueOf(movieVideoList.get(adapterPosition).getVideoSize()));

        Picasso.get()
                .load(movieVideoList.get(position).getVideoThumbnailUrl())
                .placeholder(R.drawable.video_poster_place_holder)
                .fit().centerInside()
                .error(R.drawable.video_poster_place_holder)
                .into(holder.videoThumbnailUrl);

        final String videoTitle = movieVideoList.get(adapterPosition).getVideoName();
        final String videoID = movieVideoList.get(adapterPosition).getVideoKey();
        final String videoSite = movieVideoList.get(adapterPosition).getVideoSite();

        holder.itemView.setTag(videoID);
        final Uri uriWebPage = Uri.parse(ApiConfig.getBaseVideoUrlYoutube() + videoID);

        holder.iconPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(mContext.getString(R.string.video_site_youtube), videoSite)) {
                    playThisVideo(/*videoID,*/ adapterPosition);
                } else {
                    Toast.makeText(mContext, R.string.warning_only_youtube_supported, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.buttonShareUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String infoToShared = videoTitle + "\n" +
                        ApiConfig.getBaseVideoUrlYoutube() + videoID;
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, infoToShared);
                intent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share_to)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieVideoList.size();
    }

    private void playThisVideo(/*String videoID, */int index) {

        List<String> videoIds = new ArrayList<>();
        for (int i = 0; movieVideoList.size() > i; i++) {
            videoIds.add(movieVideoList.get(i).getVideoKey());
        }

        Intent intent = YouTubeStandalonePlayer.createVideosIntent(
                (Activity) mContext,            /* Activity/Application  */
                YOUTUBE_KEY,   /* API KEY */
                videoIds,                       /* List of videoIds */
                index,                          /* startIndex */
                1,                          /* timeMillis */
                true,                       /* autoplay */
                true                       /* lightboxMode */
        );

        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, R.string.warning_device_can_not_play_video, Toast.LENGTH_SHORT).show();
        }
    }

    public static class MovieVideoViewHolder extends RecyclerView.ViewHolder {

        private final TextView videoTitle;
        private final TextView videoType;
        private final TextView videoSite;
        private final TextView videoSize;
        private final ImageView videoThumbnailUrl;
        private final Button buttonShareUrl;
        private final ImageView iconPlayVideo;


        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.tv_video_title);
            videoType = itemView.findViewById(R.id.tv_video_type);
            videoSite = itemView.findViewById(R.id.tv_video_site);
            videoSize = itemView.findViewById(R.id.tv_video_size);
            videoThumbnailUrl = itemView.findViewById(R.id.iv_movie_video_poster);

            buttonShareUrl = itemView.findViewById(R.id.bt_share_youtube_url);
            iconPlayVideo = itemView.findViewById(R.id.iv_movie_video_play_icon);
        }
    }

}
