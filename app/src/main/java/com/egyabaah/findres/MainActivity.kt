package com.egyabaah.findres

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
private const val API_KEY =
    "PvEZw149U34MI8JTT17UNHCowJ_SVTHtjwDGRbxnS8Nc4BZP9VF_-xEYptOZkEwL8sCUBaYM-IE-ll4jVDBygonb9Zs1yWH2tduzq7DfO7_6wW-iQ1ktpRlJOA9OZnYx"

// Max radius accepted by Yelp API
private const val MAX_SEARCH_RADIUS = 40000
private const val DEFAULT_SEARCH_TERM = "Avocado"
private const val DEFAULT_SORT_BY = "best_match"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var spSortBy: Spinner
    private lateinit var restaurantsAdapter: RestaurantsAdapter
    private lateinit var yelpService: IYelpService
    private lateinit var restaurants: MutableList<YelpRestaurant>
    private var toolbarBottom = 0
    private var searchTerm = DEFAULT_SEARCH_TERM
    private var searchRaduis = MAX_SEARCH_RADIUS

    // Allowed values = 1 - 4
    private var searchPrice1: String? = null
    private var searchPrice2: String? = null
    private var searchPrice3: String? = null
    private var searchPrice4: String? = null
    private var searchSortBy = DEFAULT_SORT_BY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // RecycleView
        restaurants = mutableListOf()
        restaurantsAdapter = RestaurantsAdapter(this, restaurants)
        binding.rvRestaurants.adapter = restaurantsAdapter
        binding.rvRestaurants.layoutManager = LinearLayoutManager(this)

        // PopUp Window
        val popupLayout = LayoutInflater.from(this).inflate(R.layout.menu_popup_filter, view, false)
        popupWindow = PopupWindow(this)
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.contentView = popupLayout
        popupWindow.width = LayoutParams.MATCH_PARENT
        popupWindow.isFocusable = true

        // Spinner
        val spSortByList = listOf("Best match", "Ratings", "No. of Reviews", "Distance")
        spSortBy = popupLayout.findViewById(R.id.spSortBy)
        val spSortByAdapter = ArrayAdapter(this, R.layout.spinner_list, spSortByList)
        spSortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSortBy.adapter = spSortByAdapter

        // Other Filter options
        val btnFilterSubmit = popupLayout.findViewById<Button>(R.id.btnFilterSubmit)
        val rgRadius = popupLayout.findViewById<RadioGroup>(R.id.rgRadius)
        val cbPrice1 = popupLayout.findViewById<CheckBox>(R.id.cbPrice1)
        val cbPrice2 = popupLayout.findViewById<CheckBox>(R.id.cbPrice2)
        val cbPrice3 = popupLayout.findViewById<CheckBox>(R.id.cbPrice3)
        val cbPrice4 = popupLayout.findViewById<CheckBox>(R.id.cbPrice4)

        // Event listeners
        spSortBy.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                postion: Int,
                id: Long
            ) {
                searchSortBy = when (postion) {
                    1 -> "rating"
                    2 -> "review_count"
                    3 -> "distance"
                    else -> DEFAULT_SORT_BY
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        btnFilterSubmit.setOnClickListener {

            // Get selected filter options
            searchRaduis = when (rgRadius.checkedRadioButtonId) {
                R.id.rb1_0km -> 1000
                R.id.rb10_0km -> 10000
                R.id.rb20_0km -> 20000
                else -> MAX_SEARCH_RADIUS
            }
            searchPrice1 = when {
                cbPrice1.isChecked -> "1"
                else -> null
            }
            searchPrice2 = when {
                cbPrice2.isChecked -> "2"
                else -> null
            }
            searchPrice3 = when {
                cbPrice3.isChecked -> "3"
                else -> null
            }
            searchPrice4 = when {
                cbPrice4.isChecked -> "4"
                else -> null
            }
            searchForRestaurants()
            popupWindow.dismiss()
        }

        binding.ibFilterMenu.setOnClickListener {
            // Show popup window
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


        // Retrofit
        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        yelpService = retrofit.create(IYelpService::class.java)
        searchForRestaurants()

    }

    private fun searchForRestaurants() {
        if (!this::yelpService.isInitialized) {
            return
        }
//        TODO("Replace hardcoded location to increase app functionality")
        yelpService.searchRestaurants(
            "Bearer $API_KEY",
            searchTerm,
            "New York",
            searchPrice1,
            searchPrice2,
            searchPrice3,
            searchPrice4,
            searchSortBy
        )
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
                    restaurantsAdapter.notifyDataSetChanged()
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
        val toolbarCords: IntArray = intArrayOf(0, 0)
        binding.tbMain.getLocationInWindow(toolbarCords)
        // Get the bottom position of toolbar on the screen
//        Log.i(TAG, Arrays.toString(toolbarCords))
        toolbarBottom = toolbarCords[1] + binding.tbMain.height
    }
}