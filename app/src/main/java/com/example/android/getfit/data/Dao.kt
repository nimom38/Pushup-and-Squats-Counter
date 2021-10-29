package com.example.android.getfit.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Query("SELECT * FROM `table`")
    fun getTable(): LiveData<List<Table>>

    @Query("DELETE FROM `table` WHERE id=:id")
    suspend fun delete(id: Int)

    @Insert
    suspend fun insert(table: Table)
}