package com.example.tab3

import android.net.Uri
import androidx.room.*

@Entity(tableName = "tb_diary", primaryKeys = ["year", "month", "date"])

data class Diary(
    var year: Int,
    var month: Int,
    var date: Int,
    var diaryImg: Uri?,
    var diaryContent: String?,
    var place_name: String?=null,
    var category_name: String?=null,
    var address_name: String?=null,
    var id: String?=null,
    var phone: String?=null,
    var x: Double?=null,
    var y: Double?=null

)