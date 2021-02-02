package com.dania.productfinder.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreService {
    @GET("appclienteservices/services/v3/plp")
    fun searchPlp(@Query("force-plp") force: String,
                  @Query("search-string") search: String,
                  @Query("page-number") page: Int,
                  @Query("number-of-items-per-page") items: Int): LiveData<APIResponse<SearchResponse>>

    @GET("appclienteservices/services/v3/plp")
    fun searchPlpCall(@Query("force-plp") force: String,
                      @Query("search-string") search: String,
                      @Query("page-number") page: Int,
                      @Query("number-of-items-per-page") items: Int): Call<SearchResponse>
}