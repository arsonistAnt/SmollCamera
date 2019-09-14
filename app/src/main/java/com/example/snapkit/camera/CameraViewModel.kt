package com.example.snapkit.camera

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snapkit.database.MediaFile
import com.example.snapkit.database.getDatabase
import com.example.snapkit.utils.generateImageFile
import com.example.snapkit.utils.getDCIMDirectory
import com.example.snapkit.utils.getImageFromMediaStore
import com.example.snapkit.utils.scanForMediaFiles
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class CameraFlash {
    OFF,
    ON,
    AUTO
}

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    // If the user has clicked on the capture image button.
    private val _flashSettings = MutableLiveData<CameraFlash>().apply { value = CameraFlash.OFF }
    val flashSettings: LiveData<CameraFlash>
        get() = _flashSettings


    // If the user has clicked on the capture image button.
    private val _captureImage = MutableLiveData<Boolean>()
    val captureImageState: LiveData<Boolean>
        get() = _captureImage

    // If the file is in the process of being written to the image directory.
    private val _savingFile = MutableLiveData<Boolean>()
    val savingFile: LiveData<Boolean>
        get() = _savingFile

    // If the user wants to navigate to the Image Gallery fragment.
    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery: LiveData<Boolean>
        get() = _navigateToGallery

    // If the user has granted permissions for the CameraViewFragment.
    private val _isCameraInitialized = MutableLiveData<Boolean>()
    val isCameraInitialized: LiveData<Boolean>
        get() = _isCameraInitialized

    /**
     * Set _flashSettings to the next flash setting state when flash button is clicked.
     */
    fun onFlashButtonClicked() {
        val flashSettingEnums = CameraFlash.values()
        val currentFlashSettingsPos = _flashSettings.value?.ordinal
        currentFlashSettingsPos?.apply {
            val newEnumPosition = (currentFlashSettingsPos + 1) % flashSettingEnums.size
            _flashSettings.value = flashSettingEnums[newEnumPosition]
        }
    }

    /**
     * Set _captureImage to true when capture button is clicked.
     */
    fun cameraInitialized() {
        _isCameraInitialized.value = true
    }

    /**
     * Set _isCameraInitialized to false when user has not yet provided permissions for this fragment.
     */
    fun cameraNotInitialized() {
        _isCameraInitialized.value = false
    }

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
     * Set _navigateToGallery to true when capture button is clicked.
     */
    fun onGalleryButtonClicked() {
        _navigateToGallery.value = true
    }

    /**
     * Set _navigateToGallery to false when navigating to the Gallery fragment is done.
     */
    fun onGalleryButtonFinished() {
        _navigateToGallery.value = false
    }

    /**
     * Set _inImagePreviewState to true when the image file is done saving to the Media Storage.
     */
    private fun storeFileComplete() {
        _savingFile.value = false
        Toast.makeText(
            getApplication() as Context,
            "File save complete!",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Stores the file path into the Room database.
     *
     * @param context the context.
     * @param filePath the file path string.
     */
    private fun insertFileToCache(context: Context, filePath: String) {
        val imageFile = getImageFromMediaStore(context, filePath)
        val db = getDatabase(context).mediaFileDao()
        CoroutineScope(Job()).launch {
            imageFile?.let {
                db.insertMediaFile(
                    MediaFile(
                        it.filePath,
                        it.dateCreated,
                        it.timeCreated,
                        it.dateTakenLong
                    )
                )
            }
        }
    }

    /**
     * Write the image file to the image directory and set _savingFile to true.
     *
     * @param imageResult the PictureResult object that will be written to the image directory.
     */
    fun storeFile(imageResult: PictureResult) {
        _savingFile.value = true

        // Write image to the DCIM directory.
        try {
            val imageDirectory = getDCIMDirectory()
            val imageFile = generateImageFile(imageDirectory!!)
            imageResult.let {
                it.toFile(imageFile) {
                    scanForMediaFiles(
                        getApplication(),
                        arrayOf(imageFile.path),
                        ::insertFileToCache
                    )
                    storeFileComplete()
                }
            }
        } catch (e: Exception) {
            Timber.e("$e")
            Toast.makeText(
                getApplication() as Context,
                "Failed to save file!",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}