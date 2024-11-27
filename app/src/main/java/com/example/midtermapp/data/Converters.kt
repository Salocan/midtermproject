package com.example.midtermapp.data

import androidx.room.TypeConverter
import androidx.lifecycle.MutableLiveData

class Converters {
    @TypeConverter
    fun fromBoolean(value: Boolean): MutableLiveData<Boolean> {
        return MutableLiveData(value)
    }

    @TypeConverter
    fun toBoolean(liveData: MutableLiveData<Boolean>): Boolean {
        return liveData.value ?: false
    }
}