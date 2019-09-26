package com.example.snapkit.mediaviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModel : ViewModel() {
    // If user has clicked the album image button, navigate to the ThumbnailGallery.
    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery: LiveData<Boolean>
        get() = _navigateToGallery

    // If user has clicked on the Share button, create a implicit SHARE intent.
    private val _sharePhoto = MutableLiveData<Boolean>()
    val sharePhoto: LiveData<Boolean>
        get() = _sharePhoto

    // If user has clicked on the Heart button, toggle heart image.
    private val _heartButtonPressed = MutableLiveData<Boolean>()
    val heartButtonPressed: LiveData<Boolean>
        get() = _heartButtonPressed

    // If user has clicked on the Heart button, toggle heart image.
    private val _trashButtonPressed = MutableLiveData<Boolean>()
    val trashButtonPressed: LiveData<Boolean>
        get() = _trashButtonPressed

    // Store the current item position of the MediaViewPager, used to snap back to the next position after deleting an image.
    var currentItemPosition = 0

    /**
     * Set _heartButtonPressed to true if user has clicked on heart button.
     */
    fun heartButtonClicked() {
        _heartButtonPressed.value = true
    }

    /**
     * Set _heartButtonPressed to false once handling of the toggle is done.
     */
    fun heartButtonClickedDone() {
        _heartButtonPressed.value = false
    }

    /**
     * Set _navigateToGallery to true if user has clicked on image button.
     */
    fun navigateToGallery() {
        _navigateToGallery.value = true
    }

    /**
     * Set _navigateToGallery to false once navigation is finished.
     */
    fun navigateToGalleryDone() {
        _navigateToGallery.value = false
    }

    /**
     * Set _sharePhoto to true if user has clicked on the share button.
     */
    fun shareButtonClicked() {
        _sharePhoto.value = true
    }

    /**
     * Set _sharePhoto to false once sharing intent is finished.
     */
    fun shareButtonClickedDone() {
        _sharePhoto.value = false
    }

    /**
     * Set _sharePhoto to true if user has clicked on the share button.
     */
    fun trashButtonClicked() {
        _trashButtonPressed.value = true
    }

    /**
     * Set _sharePhoto to false once sharing intent is finished.
     */
    fun trashButtonClickedDone() {
        _trashButtonPressed.value = false
    }
}