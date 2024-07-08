package com.example.tab3
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

@Parcelize
data class StoreResponseItem(
    val place_name: String,
    val distance: String,
    val place_url: String,
    val category_name: String,
    val address_name: String,
    val road_address_name: String,
    val id: String,
    val phone: String,
    val category_group_code: String,
    val category_group_name: String,
    val x: String,
    val y: String
) : TedClusterItem, Parcelable {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(y.toDouble(), x.toDouble())
    }

}
