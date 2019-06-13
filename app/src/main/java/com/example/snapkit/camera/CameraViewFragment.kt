package com.example.snapkit.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.snapkit.databinding.FragmentCameraViewBinding
import com.otaliastudios.cameraview.BitmapCallback
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult



class CameraViewFragment : Fragment() {
    lateinit var binding: FragmentCameraViewBinding
    lateinit var viewModel: CameraViewModel
    lateinit var camera: CameraView
    lateinit var capturedImage: Bitmap


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
        // Override on picture taken CameraListener
        camera.addCameraListener(object : CameraListener() {
            //TODO: Handle when user interrupts snapshot process via home button or swiping up on the home button.
            override fun onPictureTaken(result: PictureResult) {
                // Picture was taken!
                result.data
                result.toBitmap { convertedBitmap ->
                    capturedImage = convertedBitmap!!

                    // Show the user the captured image in the imagePreview view.
                    binding.imagePreview.setImageBitmap(capturedImage)
                }

                // Switch to the preview image state after the picture has been taken.
                viewModel.onPreviewImageState()
            }
        })
    }

    /**
     * Show the buttons and the preview image itself to the user in the image preview state of the app.
     * By default if called without an argument, the function will set the visibility of the UI to VISIBLE.
     * @param hide a boolean that decides whether the buttons in the preview state should be shown.
     */
    private fun showImagePreviewUI(show: Boolean = true) {
        if (show) {
            binding.imagePreview.visibility = View.VISIBLE
            binding.exitPreviewButton.visibility = View.VISIBLE
            binding.buttonCaptureImage.visibility = View.GONE
        } else {
            binding.imagePreview.apply {
                visibility = View.GONE
                // Must remove current bitmap from ImageView otherwise the user will see
                // previous images from past captures.
                setImageBitmap(null)
            }
            binding.exitPreviewButton.visibility = View.GONE
            binding.buttonCaptureImage.visibility = View.VISIBLE
            // Recycle unused bitmap.
            capturedImage?.recycle()
        }

    }


    /**
     * Setup the observers to watch the CameraViewModel data.
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

        // Tell the fragment to handle the preview UI when the user in the image preview state.
        viewModel.inImagePreviewState.observe(viewLifecycleOwner, Observer { finishCapturedState ->
            if (finishCapturedState == true) {
                showImagePreviewUI()
            } else {
                // This case is reached when the user presses the "X" button in the preview UI.
                showImagePreviewUI(false)
            }

        })
    }


}