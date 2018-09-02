package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.config.ApiConfig;
import com.udacity.popularmovies.models.MovieVideo;

import java.util.ArrayList;

/**
 * Created by jesse on 01/09/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideoViewHolder> {

    private static final String LOG_TAG = MovieVideosAdapter.class.getSimpleName();

    private final ArrayList<MovieVideo> movieVideoList;
    private final Context mContext;

    public MovieVideosAdapter(ArrayList<MovieVideo> movieList, Context mContext) {
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
    public void onBindViewHolder(@NonNull MovieVideoViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        holder.videoTitle.setText(movieVideoList.get(adapterPosition).getVideoName());
        holder.videoType.setText(movieVideoList.get(adapterPosition).getVideoType());
        holder.videoSite.setText(movieVideoList.get(adapterPosition).getVideoSite());
        holder.videoSize.setText(movieVideoList.get(adapterPosition).getVideoSize());

        final String videoID = movieVideoList.get(adapterPosition).getVideoKey();
        holder.buttonViewOnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(ApiConfig.getBaseVideoUrlYoutube() + videoID);
                Log.v("VIDEO LINK", ApiConfig.getBaseVideoUrlYoutube() + videoID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieVideoList.size();
    }


    public static class MovieVideoViewHolder extends RecyclerView.ViewHolder {

        private final TextView videoTitle;
        private final TextView videoType;
        private final TextView videoSite;
        private final TextView videoSize;
        private final Button buttonViewOnWeb;


        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.tv_video_title);
            videoType = itemView.findViewById(R.id.tv_video_type);
            videoSite = itemView.findViewById(R.id.tv_video_site);
            videoSize = itemView.findViewById(R.id.tv_video_size);
            buttonViewOnWeb = itemView.findViewById(R.id.bt_see_on_youtube);
        }
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }

}
