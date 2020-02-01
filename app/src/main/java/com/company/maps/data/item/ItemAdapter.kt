package com.company.maps.data.item

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.company.maps.R
import com.company.maps.data.county.Country

class ItemAdapter(countries: MutableList<Country>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<Item>()

    init {
        items.clear()
        countries.map { country ->
            items.add(Item(country.getName(), COUNTRY, country))
            items.addAll(country.getCityList().sorted().map { city -> Item(city.getName(), CITY, null) })
        }
    }

    fun get(position: Int): Item {
        return items[position]
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].country != null) {
            COUNTRY
        } else {
            CITY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CITY) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.city_text_view, parent, false) as TextView
            CountryViewHolder2(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.country_text_view, parent, false) as TextView
            CityViewHolder2(view)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        val itemName = items[position].name

        if (viewType == COUNTRY && itemName == "") {
            holder.itemView.findViewById<TextView>(R.id.text).text = holder.itemView.context.getText(R.string.noCountry)
            return
        }

        holder.itemView.findViewById<TextView>(R.id.text).text = itemName
    }

    class CityViewHolder2(textView: TextView) : RecyclerView.ViewHolder(textView)
    class CountryViewHolder2(textView: TextView) : RecyclerView.ViewHolder(textView)

}