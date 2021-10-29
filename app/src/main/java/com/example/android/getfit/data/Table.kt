package com.example.android.getfit.data

import androidx.room.*


@Entity(
    tableName = "table"
)
data class Table(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "duration")
    val duration: String,

    @ColumnInfo(name = "pushups")
    val pushups: String,

    @ColumnInfo(name = "squats")
    val squats: String
) {

}