package com.example.android.getfit

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.android.getfit.classification.PoseClassifierProcessor
import com.example.android.getfit.data.AppDatabase
import com.example.android.getfit.data.Dao
import com.example.android.getfit.data.Table
import com.example.android.getfit.databinding.FragmentCameraBinding
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


private const val PUSHUPS = "pushups"
private const val SQUATS = "squats"

class Camera : Fragment() {



    private var pushups: Boolean? = null
    private var squats: Boolean? = null

    private lateinit var binding: FragmentCameraBinding

    private var graphicOverlay: GraphicOverlay? = null

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    private val classificationExecutor: Executor = Executors.newSingleThreadExecutor()

    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private lateinit var safeContext: Context

    private lateinit var application: Application
    private lateinit var dataSource: Dao

    private lateinit var text_to_speech: TextToSpeech


    private val viewModel: CameraViewModel by lazy {
        ViewModelProvider(this, CameraViewModel.Factory(dataSource, activity!!.application))
            .get(CameraViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
        application = requireNotNull(this.activity).application
        dataSource = AppDatabase.getInstance(application).dao

        val mOrientationListener: OrientationEventListener = object : OrientationEventListener(
            application
        ) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == 0) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                else if(orientation == 180) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                }
                else if (orientation == 90) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
                else if (orientation == 270) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable()
        }
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




        text_to_speech = TextToSpeech(
            application
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                text_to_speech.setLanguage(Locale.UK)
            }
        }

        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)

        graphicOverlay = binding.graphicOverlay

        if( viewModel.mute ) {
            binding.mute.setImageResource(R.drawable.mute_red)
        }
        if( viewModel.isStart ) {
            binding.cardStartStop.setBackgroundColor(Color.parseColor("#b71c1c"))
            binding.cardButton.text = "Stop"
            binding.cardButton.setTextColor(Color.parseColor("#ffffff"))
        }

        binding.back.setOnClickListener {
            if(!viewModel.isStart) findNavController().navigateUp()
            else {
                binding.cardStartStop?.performClick()
            }
        }

        binding.flash.setOnClickListener {
            if (viewModel.isFlash) {
                binding.flash.setImageResource(R.drawable.flash_off_white)
                viewModel.isFlash = false
            } else {
                binding.flash.setImageResource(R.drawable.flash_on_white)
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
                binding.flash.setImageResource(R.drawable.flash_off_white)
            }
            bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
        }

        binding.info.setOnClickListener {
            if( (pushups == true) && (squats == true) ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing pushups and squats", Toast.LENGTH_SHORT).show()
            else if( pushups == true ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing pushups", Toast.LENGTH_SHORT).show()
            else if( squats == true ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing squats", Toast.LENGTH_SHORT).show()
        }

        binding.cardStartStop?.setOnClickListener {
            if(viewModel.isStart) {
                val temp: Long = (System.currentTimeMillis()/1000) - viewModel.start_time
                val direction =
                    CameraDirections.actionCameraToCountingStopped(viewModel.pushups_cnt, viewModel.squats_cnt, temp.toInt())
                var yo = TimeUtils.getTime() + " - " + TimeUtils.getDay() + " - " + TimeUtils.getMonth() + ", " + TimeUtils.getYear();
                lifecycleScope.launch {
                    viewModel.database.insert(Table( dateTime = yo, duration = "Duration: " + getTime(temp.toInt()).toString(), pushups = "Pushups: " + viewModel.pushups_cnt.toString(), squats = "Squats: " + viewModel.squats_cnt.toString() ))
                }
                findNavController().navigate(direction)
            }
            else {
                viewModel.start_time = System.currentTimeMillis()/1000
                viewModel.isStart = true
                binding.cardStartStop.setBackgroundColor(Color.parseColor("#b71c1c"))
                binding.cardButton.text = "Stop"
                binding.cardButton.setTextColor(Color.parseColor("#ffffff"))
                bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
            }
        }

        binding.mute?.setOnClickListener {
            viewModel.mute = !viewModel.mute
            if( viewModel.mute ) {
                binding.mute.setImageResource(R.drawable.mute_red)
                Toast.makeText(application, "Voice mode deactivated", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.mute.setImageResource(R.drawable.mute_white)
                Toast.makeText(application, "Voice mode activated", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.prothom) {
            if( (pushups == true) && (squats == true) ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing pushups and squats", Toast.LENGTH_SHORT).show()
            else if( pushups == true ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing pushups", Toast.LENGTH_SHORT).show()
            else if( squats == true ) Toast.makeText(application, "Place camera in such a way that is can clearly see you doing squats", Toast.LENGTH_SHORT).show()

            Toast.makeText(application, "Voice mode activated", Toast.LENGTH_SHORT).show()
            viewModel.prothom = false
        }
        if(viewModel.which_camera == 0) {
            binding.flash.visibility = View.INVISIBLE
            viewModel.isFlash = false
        }
        if(viewModel.isFlash == true) {
            binding.flash.setImageResource(R.drawable.flash_on_white)
        }
        if((viewModel.isFlash == false) && (viewModel.which_camera == 1)) {
            binding.flash.setImageResource(R.drawable.flash_off_white)
        }
        bindUseCases(viewModel.which_camera, viewModel.isFlash, viewModel.isStart)
    }

    private fun bindUseCases(which_camera: Int, isFlashOn: Boolean, isStart: Boolean) {
        var needUpdateGraphicOverlayImageSourceInfo: Boolean = true

        Log.d("Camera", "hohoho")

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
            Log.d("Camera", "dis3")
            val result = viewModel.poseDetector.process(image)
                .continueWith(classificationExecutor, { results ->
                    Log.d("Camera", "dis1")
                        val pose = results.getResult()
                        var classificationResult: List<String> = java.util.ArrayList()
                        classificationResult = viewModel.poseClassifierProcessor!!.getPoseResult(pose)
                        PoseWithClassification(pose, classificationResult)
//                    Log.d("Camera", "dis1")
                    }
                )
                .addOnSuccessListener(executor, { results ->
                    var ff: Boolean = false
                    Log.d("Camera", "dis2")
                    var temp = results.classificationResult
                    assert(temp.size <= 2)
                    if(temp.size == 2) {
                        var hu = temp[0]
                        var hu_list = stringToWords(hu)
                        assert(hu_list.size == 4 || hu_list.size == 0)
                        if(hu_list.size == 4) {
                            if ( (hu_list[0] == "pushups_down") && (pushups == true) ) {
                                ff = true
                                var haha: Boolean = false
                                if( (viewModel.now != "pushups") ) {
                                    viewModel.now = "pushups"
                                    ++viewModel.pushups
                                    viewModel.squats = 0
                                    haha = true
                                }
                                else if( viewModel.pushups_cnt < hu_list[2].toInt() ) {
                                    ++viewModel.pushups
                                    haha = true
                                }
                                viewModel.pushups_cnt = hu_list[2].toInt()
                                var toSpeak = "Pushup number " + viewModel.pushups_cnt.toString()
                                if(viewModel.pushups > 3) toSpeak = viewModel.pushups_cnt.toString()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    if (!viewModel.mute && haha) text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null,null);
                                } else {
                                    if (!viewModel.mute && haha) text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            } else if( (hu_list[0] == "squats_down") && (squats == true) ) {
                                ff = true
                                var haha: Boolean = false
                                if( (viewModel.now != "squats") ) {
                                    viewModel.now = "squats"
                                    ++viewModel.squats
                                    viewModel.pushups = 0
                                    haha = true
                                }
                                else if( viewModel.squats_cnt < hu_list[2].toInt() ) {
                                    ++viewModel.squats
                                    haha = true
                                }
                                viewModel.squats_cnt = hu_list[2].toInt()
                                var toSpeak = "Squat number " + viewModel.squats_cnt.toString()
                                if(viewModel.squats > 3) toSpeak = viewModel.squats_cnt.toString()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    if (!viewModel.mute && haha) text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null,null);
                                } else {
                                    if (!viewModel.mute && haha) text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                            binding.tv1.text = "PUSHUPS: " + viewModel.pushups_cnt.toString()
                            binding.tv2.text = "SQUATS: " + viewModel.squats_cnt.toString()
                        }
                    }
                    graphicOverlay!!.clear()
                    graphicOverlay.add(
                        PoseGraphic(
                            graphicOverlay,
                            results.pose,
//                            results,
                            true,
                            true,
                            true,
                            results.classificationResult,
//                        ArrayList()
                            ff
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

    fun stringToWords(s : String) = s.trim().splitToSequence(' ')
//        .filter { it.isNotEmpty() }
        .filter { it.isNotBlank() }
        .toList()

    private fun getTime(ttime: Int): String {
        var time = ttime
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