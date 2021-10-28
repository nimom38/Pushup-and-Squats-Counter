package com.example.android.getfit

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class CameraViewModel (safeContext: Application) : AndroidViewModel(safeContext) {
    var prothom: Boolean = true
    var isFlash: Boolean = false
    var which_camera: Int = 1
    val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)
    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
    var pushups: Int = 0
    var squats: Int = 0
    var isStart: Boolean = false
    var start_time: Long = 0

    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()


    val poseDetector = PoseDetection.getClient(options)


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CameraViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}