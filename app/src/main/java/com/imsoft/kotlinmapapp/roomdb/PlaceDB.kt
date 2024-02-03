package com.imsoft.kotlinmapapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imsoft.kotlinmapapp.model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDB: RoomDatabase() {
    abstract fun placeDao(): PlaceDao

}