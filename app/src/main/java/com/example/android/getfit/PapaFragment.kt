package com.example.android.getfit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.getfit.databinding.FragmentPapaBinding


class PapaFragment : Fragment() {

    private lateinit var binding: FragmentPapaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = FragmentPapaBinding.inflate(layoutInflater, container, false)

        binding.cardStartCounting.setOnClickListener{
            val direction =
                PapaFragmentDirections.actionPapaFragmentToStartCounting()
            findNavController().navigate(direction)
        }

        binding.cardSeePastCounts.setOnClickListener{
            val direction =
                PapaFragmentDirections.actionPapaFragmentToSeePastCounts()
            findNavController().navigate(direction)
        }

        binding.cardInfo.setOnClickListener{
            val direction =
                PapaFragmentDirections.actionPapaFragmentToInfoFragment()
            findNavController().navigate(direction)
        }


        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}