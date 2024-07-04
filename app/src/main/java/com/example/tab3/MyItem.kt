package com.example.tab3
import android.net.Uri

data class MyItem(val profile: Int, val name: String, val number: String, var isFavorite: Boolean, var image:String?=null, var selcted:Int?=null) {
    fun toggleFavorite() {
        isFavorite = !isFavorite
    }
}
