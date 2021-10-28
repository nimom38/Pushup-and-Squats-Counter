package com.example.android.getfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.getfit.databinding.FragmentCountingStoppedBinding


private const val PUSHUPS = "pushups"
private const val SQUATS = "squats"
private const val TIME = "time"


class CountingStopped : Fragment() {

    private lateinit var binding: FragmentCountingStoppedBinding

    private var pushups: Int? = null
    private var squats: Int? = null
    private var time: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pushups = it.getInt(PUSHUPS)
            squats = it.getInt(SQUATS)
            time = it.getInt(TIME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountingStoppedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}