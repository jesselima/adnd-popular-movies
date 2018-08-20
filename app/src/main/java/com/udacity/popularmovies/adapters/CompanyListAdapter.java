package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Company;

import java.util.ArrayList;

/**
 * An {@link CompanyListAdapter} knows how to create a list item layout for each company item
 * in the data source (a list of {@link Company} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like RecyclerView
 * to be displayed to the user.
 * Official Documentation: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter
 */
public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder>{

    @SuppressWarnings("unused")
    private static final String LOG_TAG = CompanyListAdapter.class.getSimpleName();

    private final ArrayList<Company> companies;
    private final Context context;

    /**
     * Constructs a new {@link CompanyListAdapter}.
     *
     * @param context  of the app
     * @param companies is the list of companies, which is the data source of the adapter
     */
    public CompanyListAdapter(Context context, ArrayList<Company> companies) {
        this.context = context;
        this.companies = companies;
    }

    /**
     * Inflates the layout for each company item and return that view.
     * @param viewType The view type of the new View.
     * @param parent  The ViewGroup into which the new View will be added after it is bound to an
     *                adapter position. It's the ViewGroup object used by the Inflater.
     * @return a company item view object represents the inflated layout filled with
     * data for each item(company) in the list on the UI
     * <p>
     * Official Documentation:
     * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter#onBindViewHolder
     */
    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_item_list, parent, false);
        return new CompanyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param position The position of the item within the adapter's data set.
     * @param holder The ViewGroup object used by the Inflater. The ViewHolder which should be updated
     *              to represent the contents of the item at the given position in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CompanyListAdapter.CompanyViewHolder holder, final int position) {

        final int adapterPosition = holder.getAdapterPosition();

        // Updates the UI with the company name for the given adapter position
        holder.textViewCompanyName.setText(companies.get(position).getCompanyName());
        // Updates the UI with the company logo for the given adapter position
        Picasso.get()
                .load(companies.get(position).getCompanyLogoPath())
                .into(holder.imageViewCompanyLogo);

        // Handling clicks on each companies image
        holder.imageViewCompanyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int companyId = companies.get(adapterPosition).getCompanyId();
                Log.v("COMPANY ID", String.valueOf(companyId));
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * RecyclerView.Adapter implementations should subclass ViewHolder and add fields for caching potentially expensive findViewById(int) results.
     * All UI references the item list does not require recall all IDs over and over again while the view is recycled.
     */
    public static class CompanyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewCompanyLogo;
        private final TextView textViewCompanyName;

        CompanyViewHolder(View itemView) {
            super(itemView);
            imageViewCompanyLogo = itemView.findViewById(R.id.iv_production_company);
            textViewCompanyName = itemView.findViewById(R.id.tv_company_name);
        }
    }

}
