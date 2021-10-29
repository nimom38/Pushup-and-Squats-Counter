package com.example.android.getfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.android.getfit.data.AppDatabase
import com.example.android.getfit.databinding.FragmentSeePastCountsBinding

class SeePastCounts : Fragment() {
    private lateinit var binding: FragmentSeePastCountsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeePastCountsBinding.inflate(layoutInflater, container, false)

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = AppDatabase.getInstance(application).dao
        val viewModelFactory = SeePastCountsViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val viewModel = ViewModelProvider(this, viewModelFactory).get(SeePastCountsViewModel::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)




        return binding.root
    }
}