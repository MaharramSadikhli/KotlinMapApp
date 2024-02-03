package com.imsoft.kotlinmapapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Place(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "latitude")
    var lat: Double,

    @ColumnInfo(name = "longitude")
    var lng: Double

) {

    @PrimaryKey(autoGenerate = true)
    var id = 0

}