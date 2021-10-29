package com.example.android.getfit.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface Dao {
    @Query("SELECT * FROM `table`")
    fun getTable(): LiveData<List<Table>>

    @Query("DELETE FROM `table` WHERE id=:id")
    suspend fun delete(id: Int)
}