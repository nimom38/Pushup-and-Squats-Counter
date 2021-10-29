package com.example.android.getfit.data

class Datum {
    class InnerCard(val id: Int, val time: String, val duration: String, val pushups: Int, val squats: Int)
    class OuterCard(val id: Int, val day: String)
}