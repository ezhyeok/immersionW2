package com.example.tab3

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApiService {

    @GET("v2/local/search/keyword.json")
    fun searchLocal(
        @Query(value = "query", encoded = true) query: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int=6000,
        @Query("sort") sort: String="accuracy"
    ): Single<StoreResponse>
    @GET("v2/local/search/keyword.json")
    fun getSurroundStoreList(
        @Query(value = "query", encoded = true) query: String="식당",
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int=1000
        ): Single<StoreResponse>
    @GET("v2/local/search/keyword.json")
    fun getSearchResultStoreList(
        @Query(value = "query", encoded = true) query: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int=6000,
        @Query("sort") sort: String="accuracy"
    ): Single<StoreResponse>
}