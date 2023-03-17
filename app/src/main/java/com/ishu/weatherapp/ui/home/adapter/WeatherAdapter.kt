package com.ishu.weatherapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ishu.weatherapp.R
import com.ishu.weatherapp.data.models.Weather
import com.ishu.weatherapp.utils.ApiHandle

class WeatherAdapter(val context: Context,
                     val data: ArrayList<Weather>,
                     val itemClickListener: (Weather) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        val item = data[position]

        holder.tvMain.text = item.main
        holder.tvDesc.text = item.description

        var img_path = ApiHandle.img_url+item.icon+ ApiHandle.img_ext

        //Glide is use to load image from url in your imageview.
        Glide.with(context)
            .load(img_path)
            .fitCenter() //
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.imgWeather)


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvMain: TextView = itemView.findViewById(R.id.tvMain)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val imgWeather: ImageView = itemView.findViewById(R.id.imgWeather)
    }
}