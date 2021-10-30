package com.example.android.getfit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = FragmentCountingStoppedBinding.inflate(layoutInflater, container, false)

        binding.imageButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.pushup.text = pushups.toString() + " pushups"
        binding.squat.text = squats.toString() + " squats"
        binding.time.text = getTime()

        return binding.root
    }

    private fun getTime(): String {
        var hour = time!!.div(3600)
        time = time!! % 3600
        var minute = time!!.div(60)
        time = time!! % 60
        var sec = time

        var ans : String = "Time: "

        if( hour == 0 && minute == 0 ) {
            ans += sec.toString() + "s"
        }
        else {
            if(hour > 0) ans += hour.toString() + "h"
            if(minute > 0) ans += minute.toString() + "m"
            if (sec != null) {
                if(sec > 0) ans += sec.toString() + "s"
            }
        }
        return ans
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}