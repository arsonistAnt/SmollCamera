package com.example.snapkit.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.FragmentCameraViewBinding
import com.example.snapkit.scanForMediaFiles
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
        // Setup button_capture_image as an observer.
        initObservers()
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
         * After the snapshot has been taken we take a high quality photo which will take longer to process. Eventually this fix should be
         * optimized/refactored by modifying the CameraView library to handle pausing the PreviewSurface.
         */
        camera.addCameraListener(object : CameraListener() {
            //TODO: Handle when user interrupts snapshot process via home button or swiping up on the home button.
            override fun onPictureTaken(result: PictureResult) {
                //TODO: Optimize preview by changing the CameraView lib to pause between take and onTaken states.
                // https://github.com/natario1/CameraView/issues/476
                if (result.isSnapshot) {
                    // Load picture result into image view with glide.
                    Glide.with(this@CameraViewFragment)
                        .load(result.data)
                        .into(binding.imagePreview)

                    // Switch to the preview image state after the picture has been taken.
                    camera.takePicture()
                    viewModel.onPreviewImageState(result)
                } else {
                    viewModel.storeFile()
                }
            }
        })
    }

    /**
     * Show the buttons and the preview image itself to the user in the image preview state of the app.
     * By default if called without an argument, the function will set the visibility of the UI to VISIBLE.
     *
     * @param show a boolean that decides whether the buttons in the preview state should be shown.
     */
    private fun showImagePreviewUI(show: Boolean = true) {
        if (show) {
            binding.imagePreview.visibility = View.VISIBLE
            binding.exitPreviewButton.visibility = View.VISIBLE
            binding.buttonCaptureImage.visibility = View.GONE
        } else {
            // Remove current image from ImageView otherwise the user will see
            // previous images from past captures.
            Glide.with(this@CameraViewFragment)
                .clear(binding.imagePreview)
            binding.imagePreview.visibility = View.GONE
            binding.exitPreviewButton.visibility = View.GONE
            binding.buttonCaptureImage.visibility = View.VISIBLE
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
                camera.takePictureSnapshot()
                viewModel.onCaptureButtonFinished()
            }
        })

        // Tell the fragment to handle the preview UI when the user in the image preview state.
        viewModel.inImagePreviewState.observe(viewLifecycleOwner, Observer { finishCapturedState ->
            if (finishCapturedState == true) {
                showImagePreviewUI()
            } else {
                // This case is reached when the user presses the "X" button in the preview UI.
                showImagePreviewUI(false)
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

                // Re-index the image directory so the media content provider is aware of the newly added file.
                try {
                    scanForMediaFiles(activity!!.applicationContext, arrayOf(viewModel.getImageResultPath()!!))
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