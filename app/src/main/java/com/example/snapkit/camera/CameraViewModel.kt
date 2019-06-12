package com.example.snapkit.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    // If the user has clicked on the snap shot button.
    private val _captureImage = MutableLiveData<Boolean>()
    val captureImageState: LiveData<Boolean>
        get() = _captureImage

    //TODO: Keep a state where user wants to save the image that has been captured.
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
}