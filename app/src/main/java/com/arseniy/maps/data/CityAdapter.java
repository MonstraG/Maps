package com.arseniy.maps.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arseniy.maps.R;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private ArrayList<String> cityNames;

    static class CityViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final TextView textView;

        CityViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public ArrayList<String> getData() {
        return cityNames;
    }

    public void setData(ArrayList<String> newArrayList) {
        cityNames = newArrayList;
        cityNames.sort(String::compareTo);
        notifyDataSetChanged();
    }

    public CityAdapter(ArrayList<String> cities) {
        cityNames = cities;
        cityNames.sort(String::compareTo);
    }


    @NonNull
    @Override
    public CityAdapter.CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_text_view, parent, false);
        return new CityViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(cityNames.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cityNames.size();
    }

}
