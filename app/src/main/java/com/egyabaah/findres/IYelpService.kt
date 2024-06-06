package com.egyabaah.findres

import com.egyabaah.findres.models.YelpSearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface IYelpService {
    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        // Set all or any of them to null to not include them or it in request
        @Query("price") price1 : String?,
        @Query("price") price2 : String?,
        @Query("price") price3 : String?,
        @Query("price") price4 : String?,
        @Query("sort_by") sortBy : String) : Call<YelpSearchResult>
}