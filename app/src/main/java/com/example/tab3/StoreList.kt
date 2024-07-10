package com.example.tab3

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreList(val storeList: ArrayList<StoreResponseItem>) : Parcelable

@Parcelize
data class StoreList2(val storeList: ArrayList<StoreItem>) : Parcelable
