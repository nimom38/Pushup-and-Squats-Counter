package com.example.android.getfit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.getfit.databinding.FragmentStartCountingBinding

class StartCounting : Fragment() {

    private lateinit var binding: FragmentStartCountingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = FragmentStartCountingBinding.inflate(layoutInflater, container, false)

        binding.cardPushupsOnly.setOnClickListener{
            val direction =
                StartCountingDirections.actionStartCountingToCamera(true, false)
            findNavController().navigate(direction)
        }

        binding.cardSquatsOnly.setOnClickListener{
            val direction =
                StartCountingDirections.actionStartCountingToCamera(false, true)
            findNavController().navigate(direction)
        }

        binding.cardBoth.setOnClickListener{
            val direction =
                StartCountingDirections.actionStartCountingToCamera(true, true)
            findNavController().navigate(direction)
        }

        binding.imageButton2.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}