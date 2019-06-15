package com.example.snapkit.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snapkit.generateImageFile
import com.example.snapkit.getDCIMDirectory
import com.otaliastudios.cameraview.PictureResult

class CameraViewModel : ViewModel() {
    // If the user has clicked on the capture image button.
    private val _captureImage = MutableLiveData<Boolean>()
    val captureImageState: LiveData<Boolean>
        get() = _captureImage

    // If the user has just finished pressing the capture image button and are in the image preview state.
    private val _inImagePreviewState = MutableLiveData<Boolean>()
    val inImagePreviewState: LiveData<Boolean>
        get() = _inImagePreviewState

    // If the file is in the process of being written to the image directory.
    private val _savingFile = MutableLiveData<Boolean>()
    val savingFile: LiveData<Boolean>
        get() = _savingFile

    // TODO: Encapsulate this into a class.
    // Stores the JPEG image in bytes.
    private var imageResult: PictureResult? = null
    // Stores the file image path
    private var imageFilePath: String? = null


    //TODO: Keep a state where user wants to navigate to the gallery by a button click.

    /**
     * Set _captureImage to true when capture button is clicked.
     */
    fun onCaptureButtonClicked() {
        _captureImage.value = true
    }

    /**
     * Set _captureImage to false when capture button event is finished.
     */
    fun onCaptureButtonFinished() {
        _captureImage.value = false
    }

    /**
     * Set _inImagePreviewState to true when user is in the preview image state.
     */
    fun onPreviewImageState(imageResult: PictureResult) {
        this.imageResult = imageResult
        _inImagePreviewState.value = true
    }

    /**
     * Set _inImagePreviewState to false when user is finished with the preview state.
     */
    fun onPreviewImageStateFinished() {
        // Remove reference to image bytes to garbage collect.
        imageResult = null
        _inImagePreviewState.value = false
    }


    /**
     * Write the image file to the image directory and set _savingFile to true.
     */
    fun storeFile() {
        _savingFile.value = true

        // Write image to the DCIM directory.
        try {
            var imageDirectory = getDCIMDirectory()
            var imageFile = generateImageFile(imageDirectory)
            imageFilePath = imageFile.path
            imageResult!!.toFile(imageFile) {
                storeFileComplete()
            }
        } catch (e: Exception) {
            Log.e("CameraViewModel", e.message)
        }
    }

    /**
     * Set _inImagePreviewState to true when the image file is done saving to the Media Storage.
     */
    private fun storeFileComplete() {
        _savingFile.value = false
    }

    /**
     * Return the path of the image result.
     */
    fun getImageResultPath() = imageFilePath


}