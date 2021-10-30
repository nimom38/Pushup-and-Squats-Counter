package com.example.android.getfit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.getfit.data.AppDatabase
import com.example.android.getfit.data.Datum
import com.example.android.getfit.databinding.FragmentSeePastCountsBinding

class SeePastCounts : Fragment() {
    private lateinit var binding: FragmentSeePastCountsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = FragmentSeePastCountsBinding.inflate(layoutInflater, container, false)

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = AppDatabase.getInstance(application).dao
        val viewModelFactory = SeePastCountsViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val viewModel = ViewModelProvider(this, viewModelFactory).get(SeePastCountsViewModel::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        val adapter = SeePastCountsAdapter()

        binding.cardList.adapter = adapter

        binding.imageButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.data.observe(viewLifecycleOwner, Observer { items ->
            var hmm = ArrayList<Datum.Card>()
            for(item in items) {
                hmm.add(Datum.Card(item.id, item.dateTime, item.duration, item.pushups, item.squats))
            }
            adapter.submitList(hmm)
        })
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}