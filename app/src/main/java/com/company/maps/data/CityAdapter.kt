package com.company.maps.data

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.company.maps.R

class CityAdapter(private var cityNames: ArrayList<String>?) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    var data: ArrayList<String>?
        get() = cityNames
        set(newList) {
            cityNames = ArrayList(newList!!.sorted().toList())
            notifyDataSetChanged()
        }

    init {
        cityNames!!.sorted()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.city_text_view, parent, false) as TextView
        return CityViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = cityNames!![position]

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return cityNames!!.size
    }

    class CityViewHolder(// each data item is just a string in this case
            val textView: TextView) : RecyclerView.ViewHolder(textView)

}
