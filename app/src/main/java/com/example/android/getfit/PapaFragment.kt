package com.example.android.getfit

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
}