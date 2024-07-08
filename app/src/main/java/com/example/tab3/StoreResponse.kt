package com.example.tab3

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class StoreResponse(
    val meta: Meta,
    val documents: List<StoreResponseItem>
):Parcelable

@Parcelize
data class Meta(
    val same_name: SameName,
    val pageable_count: Int,
    val total_count: Int,
    val is_end: Boolean
) : Parcelable

@Parcelize
data class SameName(
    val region: List<String>,
    val keyword: String,
    val selected_region: String
) : Parcelable