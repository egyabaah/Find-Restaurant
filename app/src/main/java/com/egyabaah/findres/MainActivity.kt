package com.egyabaah.findres

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.egyabaah.findres.databinding.ActivityMainBinding
import com.egyabaah.findres.models.YelpRestaurant
import com.egyabaah.findres.models.YelpSearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = " https://api.yelp.com/v3/"
private const val API_KEY = "PvEZw149U34MI8JTT17UNHCowJ_SVTHtjwDGRbxnS8Nc4BZP9VF_-xEYptOZkEwL8sCUBaYM-IE-ll4jVDBygonb9Zs1yWH2tduzq7DfO7_6wW-iQ1ktpRlJOA9OZnYx"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        binding.rvRestaurants.adapter = adapter
        binding.rvRestaurants.layoutManager = LinearLayoutManager(this)


        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val yelpService = retrofit.create(IYelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_KEY","breakfast", "New York").enqueue(object : Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Didn't receive valid response body from Yelp API...exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
//                Log.i(TAG, restaurants.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })

    }
}