package com.example.tab3

import com.example.tab3.NetworkHelper.retrofitService
import io.reactivex.rxjava3.core.Single

class RemoteDataSourceImpl : NaverApiService {
    override fun searchLocal(query: String, x: String, y: String, radius: Int, sort: String): Single<StoreResponse> {
        return retrofitService.searchLocal(query, x, y, radius, sort)
    }

    override fun getSurroundStoreList(query: String, x: String, y: String, radius: Int): Single<StoreResponse>{
        return retrofitService.getSurroundStoreList(query, x, y, radius)
    }

    override fun getSearchResultStoreList(query: String, x: String, y: String, radius: Int, sort: String): Single<StoreResponse> {
        return retrofitService.getSearchResultStoreList(query, x, y, radius, sort)
    }
}
