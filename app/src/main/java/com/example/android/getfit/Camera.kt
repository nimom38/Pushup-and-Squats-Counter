package com.example.android.getfit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.getfit.databinding.FragmentCameraBinding
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage


private const val PUSHUPS = "pushups"
private const val SQUATS = "squats"

class Camera : Fragment() {
    private var pushups: Boolean? = null
    private var squats: Boolean? = null

    private lateinit var binding: FragmentCameraBinding

    private var graphicOverlay: GraphicOverlay? = null

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private lateinit var safeContext: Context

    private val viewModel: CameraViewModel by lazy {
        ViewModelProvider(this, CameraViewModel.Factory(activity!!.application))
            .get(CameraViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

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

        graphicOverlay = binding.graphicOverlay

        binding.back.setOnClickListener {
            if(!viewModel.isStart) findNavController().navigateUp()
            else {
                binding.cardStartStop!!.performClick()
            }
        }

        binding.flash.setOnClickListener {
            if (viewModel.isFlash) {
                binding.flash.setImageResource(R.drawable.flash_off)
                viewModel.isFlash = false
            } else {
                binding.flash.setImageResource(R.drawable.flash_on)
                viewModel.isFlash = true
            }
            bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
        }

        binding.cameraFlip.setOnClickListener {
            viewModel.which_camera = 1 - viewModel.which_camera
            if(viewModel.which_camera == 0) {
                binding.flash.visibility = View.INVISIBLE
                viewModel.isFlash = false
            }
            else {
                binding.flash.visibility = View.VISIBLE
                binding.flash.setImageResource(R.drawable.flash_off)
            }
            bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
        }

        binding.info.setOnClickListener {
            if( (pushups == true) && (squats == true) ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing pushups and squats", Toast.LENGTH_LONG)
            else if( pushups == true ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing pushups", Toast.LENGTH_LONG)
            else if( squats == true ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing squats", Toast.LENGTH_LONG)
        }

        binding.cardStartStop!!.setOnClickListener {
            if(viewModel.isStart) {
                val temp: Long = (System.currentTimeMillis()/1000) - viewModel.start_time
                val direction =
                    CameraDirections.actionCameraToCountingStopped(viewModel.pushups, viewModel.squats, temp.toInt())
                findNavController().navigate(direction)
            }
            else {
                viewModel.start_time = System.currentTimeMillis()/1000
                viewModel.isStart = true
                bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.prothom) {
            if( (pushups == true) && (squats == true) ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing pushups and squats", Toast.LENGTH_LONG)
            else if( pushups == true ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing pushups", Toast.LENGTH_LONG)
            else if( squats == true ) Toast.makeText(safeContext, "Place camera in such a way that is can clearly see you doing squats", Toast.LENGTH_LONG)

            viewModel.isFlash = false
            viewModel.which_camera = 0
            binding.flash.visibility = View.INVISIBLE
            viewModel.prothom = false
        }
        if(viewModel.which_camera == 0) {
            binding.flash.visibility = View.INVISIBLE
            viewModel.isFlash = false
        }
        if(viewModel.isFlash == true) {
            binding.flash.setImageResource(R.drawable.flash_on)
        }
        if((viewModel.isFlash == false) && (viewModel.which_camera == 1)) {
            binding.flash.setImageResource(R.drawable.flash_off)
        }
        bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
    }

    private fun bindUseCases(which_camera: Int, isFlashOn: Boolean, isStart: Boolean) {
        var needUpdateGraphicOverlayImageSourceInfo: Boolean = true

        viewModel.cameraProviderFuture.addListener(Runnable {
            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            Log.d("FaceDetection", "huhuhuh")

            imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(
                    // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                    // thus we can just runs the analyzer itself on main thread.
                    ContextCompat.getMainExecutor(safeContext),
                    ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                        Log.d("FaceDetection", "huhuhuh2")
                        if (needUpdateGraphicOverlayImageSourceInfo) {
                            val isImageFlipped = which_camera == CameraSelector.LENS_FACING_FRONT
                            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                            if (rotationDegrees == 0 || rotationDegrees == 180) {
                                Log.d("FaceDetection", "huhuhuh4")
                                graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, isImageFlipped)
                            } else {
                                Log.d("FaceDetection", "huhuhuh5")
                                graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, isImageFlipped)
                                Log.d("FaceDetection", "huhuhuh6")
                            }
                            needUpdateGraphicOverlayImageSourceInfo = false
                        }
                        try {
                            Log.d("FaceDetection", "huhuhuh3")
                            processImageProxy(imageProxy, graphicOverlay)
                        } catch (e: MlKitException) {
                            Log.e("FaceDetection", "Failed to process image. Error: " + e.localizedMessage)
                            Toast.makeText(safeContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

//             Select back camera as a default
            val cameraSelector = if (which_camera == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
            } else {
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            }

            try {
                // Unbind use cases before rebinding
                viewModel.cameraProvider.unbindAll()

                // Bind use cases to camera
                if( isStart ) viewModel.cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer).cameraControl.enableTorch(isFlashOn)
                else viewModel.cameraProvider.bindToLifecycle(this, cameraSelector, preview).cameraControl.enableTorch(isFlashOn)
            } catch(exc: Exception) {
                Log.e("FaceDetection", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(safeContext))

    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay?) {
        // Base pose detector with streaming frames, when depending on the pose-detection sdk
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val result = viewModel.poseDetector.process(image)
                .addOnSuccessListener(executor, { results ->
                    graphicOverlay!!.clear()
                    graphicOverlay.add(
                        PoseGraphic(
                            graphicOverlay,
                            results,
                            true,
                            true,
                            true,
                            ArrayList()
                        )
                    )
                    graphicOverlay.postInvalidate()
                })
                .addOnFailureListener(executor, { e ->
                    Log.e("PoseEstimation", "Face detection failed $e")
                })
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }

    }
}