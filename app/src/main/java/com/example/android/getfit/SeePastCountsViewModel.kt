package com.example.android.getfit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.android.getfit.data.Dao
import com.example.android.getfit.data.Table
import kotlinx.coroutines.launch

class SeePastCountsViewModel(val database: Dao,
                             application: Application
) : AndroidViewModel(application) {

    lateinit var data: LiveData<List<Table>>

    init {
        viewModelScope.launch {
            data = database.getTable()
        }
    }



}