package com.example.android.getfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.getfit.databinding.FragmentCameraBinding


private const val PUSHUPS = "pushups"
private const val SQUATS = "squats"

class Camera : Fragment() {
    private var pushups: Boolean? = null
    private var squats: Boolean? = null

    private lateinit var binding: FragmentCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pushups = it.getBoolean(PUSHUPS)
            squats = it.getBoolean(SQUATS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}