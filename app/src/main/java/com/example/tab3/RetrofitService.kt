package com.example.tab3

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RetrofitService {
    @GET("/")
    fun getSurroundStoreList(): Single<StoreResponse>

    @GET("/keyword/")
    fun getSearchResultStoreList(
        @Query(value = "storename", encoded = true) userQuery: String
    ): Single<StoreResponse>
}