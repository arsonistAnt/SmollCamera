package com.example.snapkit.camera

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.snapkit.R
import com.example.snapkit.databinding.FragmentCameraViewBinding
import com.example.snapkit.utils.getAlertDialog
import com.example.snapkit.utils.hasPermissions
import com.example.snapkit.utils.requestForPermissions
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult

class CameraViewFragment : Fragment() {
    lateinit var binding: FragmentCameraViewBinding
    lateinit var viewModel: CameraViewModel
    lateinit var camera: CameraView
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Setup data binding expressions and objects.
        binding = FragmentCameraViewBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        binding.viewModel = viewModel
        //binding.lifecycleOwner = this

        // Observe the values from the viewModel.
        initObservers()
        // Set onClickListeners for states/events that doesn't need to be tracked by the View Model.
        setOnClickListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsForCamera()
    }

    /**
     * Before opening the camera check if user has the permissions.
     */
    private fun checkPermissionsForCamera() {
        // Permissions need to be requested at runtime if API level >= 23
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!hasPermissions(requireContext(), *permissions)) {
                val cameraAlertDialog = getAlertDialog(requireContext())
                cameraAlertDialog.setMessage(getString(R.string.camera_dialog_message))
                cameraAlertDialog.setTitle(R.string.permissions_dialog_title)

                // Request user for permission before opening the camera.
                requestForPermissions(requireActivity(), cameraAlertDialog, *permissions)
            } else {
                initCameraView()
            }
        }
    }

    /**
     * Sets up the CameraView and apply any other settings to the camera functionality.
     */
    private fun initCameraView() {
        // Initialize the object with data binding.
        camera = binding.camera
        camera.setLifecycleOwner(viewLifecycleOwner)
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

        // Handle navigation when user clicks the gallery button.
        viewModel.navigateToGallery.observe(viewLifecycleOwner, Observer { navigateToGallery ->
            if (navigateToGallery) {
                var navController = findNavController()
                navController.navigate(R.id.action_cameraViewFragment2_to_imageGalleryFragment)
                viewModel.onGalleryButtonFinished()
            }

        })

        // Tell the fragment to start the save file animation.
        viewModel.savingFile.observe(viewLifecycleOwner, Observer { saving ->
            if (saving == true) {

            } else {
                Toast.makeText(
                    activity!!.applicationContext,
                    "File saved!",
                    Toast.LENGTH_SHORT
                ).show()
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