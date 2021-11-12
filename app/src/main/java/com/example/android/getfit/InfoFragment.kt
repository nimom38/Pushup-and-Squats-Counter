package com.example.android.getfit

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.findNavController
import com.example.android.getfit.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    private val description: String = "<p> Machine learning was used to make this app. The domains of machine learning that we used are Pose Estimation and K-means clustering. <br/> Pose Estimation is used to estimate the key points of the human body and track their movements. <br/> K-means clustering is an unsupervised machine learning algorithm. It basically takes the dataset and searches for pattern inside it and group the similar data together into a cluster. Hence multiple clusters are formed. <br/> Here in our app we trained the K-means cluster unsupervised model by training it on a dataset containing hundreds of pushup and squats images. Those images were taken from different angles so that the model could find a generalized solution and avoid overfitting.<p>"
    private val title: String = "Magic of Machine Learning"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        binding = FragmentInfoBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            var isToolbarShown = false

            // scroll change listener begins at Y = 0 when image is fully collapsed
            aicameraDetailScrollview.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                    // User scrolled past image to height of toolbar and the title text is
                    // underneath the toolbar, so the toolbar should be shown.
                    val shouldShowToolbar = scrollY > toolbar.height

                    // The new state of the toolbar differs from the previous state; update
                    // appbar and toolbar attributes.
                    if (isToolbarShown != shouldShowToolbar) {
                        isToolbarShown = shouldShowToolbar

                        // Use shadow animator to add elevation if toolbar is shown
                        appbar.isActivated = shouldShowToolbar

                        // Show the plant name if toolbar is shown
                        toolbarLayout.isTitleEnabled = shouldShowToolbar
                    }
                }
            )

            setHasOptionsMenu(true)
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            aicameraDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(description)
            }

            aicameraDetailName.text = title

        }



        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

}