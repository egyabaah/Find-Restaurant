package com.egyabaah.findres

import android.content.Context
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.egyabaah.findres.models.YelpRestaurant
import com.egyabaah.findres.databinding.ItemRestaurantBinding

class RestaurantsAdapter(val context: Context, val restaurants: List<YelpRestaurant>) : RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false))
    }
    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.bind(restaurant)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: YelpRestaurant) {
            itemView.findViewById<TextView>(R.id.tvName).text = restaurant.name
            itemView.findViewById<RatingBar>(R.id.ratingBar).rating = restaurant.rating.toFloat()
            itemView.findViewById<TextView>(R.id.tvDistance).text = restaurant.displayDistance()
            itemView.findViewById<TextView>(R.id.tvPrice).text = restaurant.price
            itemView.findViewById<TextView>(R.id.tvAddress).text = restaurant.location.address
            itemView.findViewById<TextView>(R.id.tvCategory).text = restaurant.categories[0].title
            itemView.findViewById<TextView>(R.id.tvNumReviews).text = "${restaurant.numReviews} Reviews"
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            Glide.with(context)
                .load(restaurant.imageUrl)
                .centerCrop()
                .transform(RoundedCorners(20))
                .into(imageView)
        }
    }


}
