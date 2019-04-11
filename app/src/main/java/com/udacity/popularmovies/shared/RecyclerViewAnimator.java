package com.udacity.popularmovies.shared;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by JesseFariasdeLima on 4/10/2019.
 * This is a part of the project adnd-popular-movies.
 */
public final class RecyclerViewAnimator {

    public static void runLayoutAnimation(final RecyclerView recyclerView, int animationResourceId ) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, animationResourceId);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
