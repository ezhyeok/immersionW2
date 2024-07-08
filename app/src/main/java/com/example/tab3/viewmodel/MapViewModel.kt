package com.example.tab3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tab3.StoreList
import com.example.tab3.StoreResponse
import com.example.tab3.RemoteDataSourceImpl
import com.example.tab3.BaseViewModel
import com.example.tab3.applySchedulers

class MapViewModel : BaseViewModel() {
    private val remoteDataSourceImpl = RemoteDataSourceImpl()
    private val _surroundStoreListLiveData = MutableLiveData<StoreResponse>()
    private val _searchResultStoreListLiveData = MutableLiveData<StoreResponse>()

    // 주변 가맹점 목록 LiveData
    val surroundStoreListLiveData: LiveData<StoreResponse>
        get() = _surroundStoreListLiveData

    // 검색결과 가맹점 목록 LiveData
    val searchResultStoreListLiveData: LiveData<StoreResponse>
        get() = _searchResultStoreListLiveData

    fun getSurroundStoreList(longitude: Double, latitude: Double) {
        addDisposable(
            remoteDataSourceImpl.getSurroundStoreList(x=longitude.toString(), y=latitude.toString())
                .applySchedulers()
                .subscribe(
                    {
                        _surroundStoreListLiveData.value = it
                        Log.d("test1", it.toString())
                    }, {
                        Log.d("test2", it.toString())
                    }
                )
        )
    }

    fun getSearchResultStoreList(userQuery: String, longitude: Double, latitude: Double) {
        addDisposable(
            remoteDataSourceImpl.getSearchResultStoreList(userQuery, longitude.toString(), latitude.toString())
                .applySchedulers()
                .subscribe(
                    {
                        _searchResultStoreListLiveData.value = it
                        Log.d("TEST1", it.toString())
                    }, {
                        Log.d("TEST2", it.toString())
                    }
                )
        )
    }
}
