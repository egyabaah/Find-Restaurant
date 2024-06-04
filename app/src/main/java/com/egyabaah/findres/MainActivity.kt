package com.egyabaah.findres

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var popupWindow: PopupWindow
    private lateinit var adapter: RestaurantsAdapter
    private lateinit var yelpService: IYelpService
    private lateinit var restaurants: MutableList<YelpRestaurant>
    private var toolbarBottom = 0
    private var searchTerm = "breakfast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        restaurants = mutableListOf()
        adapter = RestaurantsAdapter(this, restaurants)
        binding.rvRestaurants.adapter = adapter
        binding.rvRestaurants.layoutManager = LinearLayoutManager(this)

        val popupLayout = LayoutInflater.from(this).inflate(R.layout.menu_popup_filter, view, false)
        popupWindow = PopupWindow(this)
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.contentView = popupLayout
        popupWindow.width = LayoutParams.MATCH_PARENT
        popupWindow.isFocusable = true




        // Event listeners
        binding.ibFilterMenu.setOnClickListener{
            popupWindow.showAtLocation(popupLayout, 0, 0, toolbarBottom)
        }

        binding.etSearchBox.setOnEditorActionListener { textView, actionId, event ->
            // User input
            val text = binding.etSearchBox.text.toString().trim()
            if (text.isNotEmpty()) {
                // Set search term to user inputted
                searchTerm = text
                searchForRestaurants()
                // Lose focus and dismiss keyboard
                if (textView != null) {
                    textView.clearFocus()
                    hideKeypad(textView)
                }
            } else {
                Toast.makeText(this, "Please enter valid search term", Toast.LENGTH_SHORT).show()
            }
            true
        }


        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        yelpService = retrofit.create(IYelpService::class.java)
        searchForRestaurants()

    }

    private fun searchForRestaurants() {
        if (!this::yelpService.isInitialized){
            return
        }
        yelpService.searchRestaurants("Bearer $API_KEY", searchTerm, "New York")
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(
                    call: Call<YelpSearchResult>,
                    response: Response<YelpSearchResult>
                ) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "Didn't receive valid response body from Yelp API...exiting")
                        return
                    }
                    // Remove old search results in restaurants list
                    restaurants.clear()
                    restaurants.addAll(body.restaurants)
    //                Log.i(TAG, restaurants.toString())
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }
            })
    }

    private fun hideKeypad(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val toolbarCoords : IntArray = intArrayOf(0, 0)
        binding.tbMain.getLocationInWindow(toolbarCoords)
        // Get the bottom position of toolbar on the screen
        toolbarBottom = toolbarCoords[1] + binding.tbMain.height
//        Log.i(TAG, Arrays.toString(toolbarCoords))
    }
}