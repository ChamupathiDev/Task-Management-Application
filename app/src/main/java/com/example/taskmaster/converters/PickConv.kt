package com.example.taskmaster.converters

import androidx.room.TypeConverter
import java.util.Date

class PickConv {

    @TypeConverter
    fun fromTimestamp(value:Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date:Date): Long {
        return date.time
    }
}