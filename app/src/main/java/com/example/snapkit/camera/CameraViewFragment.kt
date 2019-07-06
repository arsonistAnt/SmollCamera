package com.example.snapkit.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.snapkit.databinding.FragmentCameraViewBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult


class CameraViewFragment : Fragment() {
    lateinit var binding: FragmentCameraViewBinding
    lateinit var viewModel: CameraViewModel
    lateinit var camera: CameraView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Setup data binding expressions and objects.
        binding = FragmentCameraViewBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Setup the CameraView
        initCameraView()
        // Observe the values from the viewModel.
        initObservers()
        // Set onClickListeners for states/events that doesn't need to be tracked by the View Model.
        setOnClickListeners()
        return binding.root
    }

    /**
     * Sets up the CameraView and apply any other settings to the camera functionality.
     */
    private fun initCameraView() {
        // Initialize the object with data binding.
        camera = binding.camera
        camera.setLifecycleOwner(viewLifecycleOwner)
        /**
         * The preview image should be instantly available to the user, hence taking the snapshot first which is quicker to process.
         * After the snapshot has been taken another call will take a high quality photo which will take longer to process. Eventually this fix should be
         * optimized/refactored by modifying the CameraView library to handle pausing the PreviewSurface.
         */
        camera.addCameraListener(object : CameraListener() {
            //TODO: Handle when user interrupts snapshot process via home button or swiping up on the home button.
            override fun onPictureTaken(result: PictureResult) {
                viewModel.storeFile(result)
            }
        })
    }

    /**
     * Set onClickListeners for events/states that don't need to be tracked by the CameraViewModel.
     */
    private fun setOnClickListeners() {
        // Set a listener to toggle Front/Back facing camera.
        binding.cameraFacingButton.setOnClickListener {
            camera.toggleFacing()
        }
    }

    /**
     * Subscribe observers to CameraViewModel's data/states.
     */
    private fun initObservers() {
        // Tell this fragment to observe the capture image state and call the image capture function.
        viewModel.captureImageState.observe(viewLifecycleOwner, Observer { captureState ->
            if (captureState == true) {
                // Start image capture.
                camera.takePicture()
                viewModel.onCaptureButtonFinished()
            }
        })

        // Tell the fragment to start the save file animation.
        viewModel.savingFile.observe(viewLifecycleOwner, Observer { saving ->
            if (saving == true) {
                //TODO: Start loading animation.
            } else {
                //TODO: End loading animation.
                Toast.makeText(
                    activity!!.applicationContext,
                    "File saved!",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Start the media scan as a service that way it will persist even when the user
                // Re-index the image directory so the media content provider is aware of the newly added file.
                try {
                } catch (e: Exception) {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Failed to save file!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}