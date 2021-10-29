package com.example.android.getfit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.android.getfit.data.Dao

class SeePastCountsViewModel(val database: Dao,
                             application: Application
) : AndroidViewModel(application) {

}