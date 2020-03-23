package dev.jesselima.jmovies.adapters;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by jesse on 3/21/20.
 * This is a part of the project jmovies.
 */
public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    public static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 20;

    private static boolean loadMore = true;

    private GridLayoutManager gridLayoutManager;

    public PaginationListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount            = gridLayoutManager.getChildCount();
        int totalItemCount              = gridLayoutManager.getItemCount();
        int firstVisibleItemPosition    = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition     = gridLayoutManager.findLastVisibleItemPosition();
        int findLastCompletelyVisibleItemPosition     = gridLayoutManager.findLastCompletelyVisibleItemPosition();
        int findLastVisibleItemPosition     = gridLayoutManager.findLastVisibleItemPosition();

        Log.d(">>> dx", String.valueOf(dx));
        Log.d(">>> dx", String.valueOf(dy));
        Log.d(">>> ", "visibleItemCount: " + visibleItemCount);
        Log.d(">>> ", "totalItemCount: " + totalItemCount);
        Log.d(">>> ", "firstVisibleItemPos: " + firstVisibleItemPosition);
        Log.d(">>> ", "lastVisibleItemPos: "  + lastVisibleItemPosition);
        Log.d(">>> ", "findLastCompletelyVisibleItemPosition :" + findLastCompletelyVisibleItemPosition);
        Log.d(">>> ", "findLastVisibleItemPosition :" + findLastVisibleItemPosition);


        int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();
        if (lastVisiblePosition == recyclerView.getChildCount()) {
            if (loadMore) {
                loadMore = false;
                loadMoreMovies();
            }
        }

//        if(isLoading() && !isLasPage()) {
//            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                && firstVisibleItemPosition >= 0
//                && totalItemCount >= PAGE_SIZE
//            ){
//
//                loadMoreMovies();
//            }
//        }


    }


    protected abstract void loadMoreMovies();

    public abstract boolean isLasPage();

    public abstract boolean isLoading();

}
