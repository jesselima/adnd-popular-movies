package com.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.models.Company;

import java.util.ArrayList;

/**
 * Created by jesse on 13/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder>{

    private static final String LOG_TAG = CompanyListAdapter.class.getSimpleName();

    private final ArrayList<Company> companies;
    private final Context context;

    public CompanyListAdapter(Context context, ArrayList<Company> companies) {
        this.context = context;
        this.companies = companies;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_item_list, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyListAdapter.CompanyViewHolder holder, final int position) {

        final int adapterPosition = holder.getAdapterPosition();

        holder.textViewCompanyName.setText(companies.get(position).getCompanyName());

        Picasso.get()
                .load(companies.get(position).getCompanyLogoPath())
                .into(holder.imageViewCompanyLogo);

        // Handling clicks on each companies image
        holder.imageViewCompanyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String companyName = companies.get(adapterPosition).getCompanyName();
                Toast.makeText(context, companyName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewCompanyLogo;
        private final TextView textViewCompanyName;

        public CompanyViewHolder(View itemView) {
            super(itemView);
            imageViewCompanyLogo = itemView.findViewById(R.id.iv_production_company);
            textViewCompanyName = itemView.findViewById(R.id.tv_company_name);
        }
    }

}
