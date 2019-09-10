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
    fun sharePhoto() {
        _sharePhoto.value = true
    }

    /**
     * Set _sharePhoto to false once sharing intent is finished.
     */
    fun sharePhotoDone() {
        _sharePhoto.value = false
    }
}