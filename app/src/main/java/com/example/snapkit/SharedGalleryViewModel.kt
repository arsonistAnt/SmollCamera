package com.example.snapkit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snapkit.database.MediaFileDatabase
import com.example.snapkit.database.getDatabase
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.utils.getImagesFromMediaStore
import com.example.snapkit.utils.toImageFiles
import com.example.snapkit.utils.toMediaFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * A class to share Media information across all fragments, this will be considered the main source of truth.
 */
class SharedGalleryViewModel(application: Application) : AndroidViewModel(application) {
    // Store cached media file info into a live data.
    var mediaFiles: LiveData<List<ImageFile>>
    // Co-routine variables.
    private var viewModelJob = Job()
    private var viewModelScope = CoroutineScope(viewModelJob)
    // Initialize the media cache Room Database
    private var mediaDB: MediaFileDatabase = getDatabase(application)

    init {
        // Modify the return value so that it will return a LiveData ImageFile list instead of a MediaFile list.
        mediaFiles = Transformations.map(mediaDB.mediaFileDao().getMediaFiles()) { mediaFiles ->
            mediaFiles.toImageFiles()
        }
    }

    /**
     * Fetch new image files and remove stale ones from the MediaFileDatabase. The new images will be fetched from the
     * media store.
     */
    fun updateImageFiles() {
        viewModelScope.launch {
            // Fetch the latest files from the media store.
            val mediaStoreFiles = async {
                getImagesFromMediaStore(getApplication()).toMediaFiles()
            }
            // Fetch the cached files from the room mediaDB.
            val cachedFiles = async {
                mediaDB.mediaFileDao().getMediaFilesAsync()
            }
            // Remove any stale records from the database.
            if (cachedFiles.await().size != mediaStoreFiles.await().size) {
                val updatedFiles = mediaStoreFiles.await()
                val staleFiles = cachedFiles.await().subtract(updatedFiles).toList()

                mediaDB.mediaFileDao().delete(staleFiles)
            }
            // Upsert the newly fetched files to the database.
            mediaDB.mediaFileDao().insertAll(mediaStoreFiles.await())
        }
    }
}