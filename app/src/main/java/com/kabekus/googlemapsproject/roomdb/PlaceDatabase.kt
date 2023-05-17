package com.kabekus.googlemapsproject.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kabekus.googlemapsproject.model.Place

@Database(entities = arrayOf(Place::class), version = 1)
//arrayOf(Place::class) = [Place::class] şeklinde de yazılabilir.
abstract class PlaceDatabase : RoomDatabase(){
    abstract fun placeDAO() : PlaceDAO
}