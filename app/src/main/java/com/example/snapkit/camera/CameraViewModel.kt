package com.example.snapkit.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    // If the user has clicked on the capture image button.
    private val _captureImage = MutableLiveData<Boolean>()
    val captureImageState: LiveData<Boolean>
        get() = _captureImage

    // If the user has just finished pressing the capture image button and are in the image preview state.
    private val _inImagePreviewState = MutableLiveData<Boolean>()
    val inImagePreviewState: LiveData<Boolean>
        get() = _inImagePreviewState

    //TODO: Save bitMap that has been captured.
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
    fun onPreviewImageState() {
        _inImagePreviewState.value = true
    }

    /**
     * Set _inImagePreviewState to false when user is finished with the preview state.
     */
    fun onPreviewImageStateFinished() {
        _inImagePreviewState.value = false
    }

}