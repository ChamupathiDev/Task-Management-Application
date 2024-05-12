package com.example.taskmaster.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.example.taskmaster.converters.PickConv
import com.example.taskmaster.dao.PickDao
import com.example.taskmaster.models.Pick

@Database(
    entities = [Pick::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PickConv::class)
abstract class PickDB : RoomDatabase() {

    abstract val pickDao : PickDao

    companion object {
        @Volatile
        private var INSTANCE: PickDB? = null
        fun getInstance(context: Context): PickDB {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PickDB::class.java,
                    "pick_db"
                ).build().also {
                    INSTANCE = it
                }
            }

        }
    }

}