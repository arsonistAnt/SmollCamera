package com.example.snapkit.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snapkit.database.MediaFileDatabase
import com.example.snapkit.database.getDatabase
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.getImagesFromMediaStore
import com.example.snapkit.toImageFiles
import com.example.snapkit.toMediaFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ImageGalleryViewModel(application: Application) : AndroidViewModel(application) {
    // Store the ImageFile objects to observe for changes.
    var imageFiles: LiveData<List<ImageFile>>
    private var db: MediaFileDatabase = getDatabase(application)
    // Co-routine variables.
    private var viewModelJob = Job()
    private var viewModelScope = CoroutineScope(viewModelJob)

    init {
        // Modify the return value so that it will return a LiveData ImageFile list instead of a MediaFile list.
        imageFiles = Transformations.map(db.mediaFileDao().getMediaFiles()) { mediaFiles ->
            mediaFiles.toImageFiles()
        }
    }

    //TODO: Use MediatorLiveData to observe when the image
    fun updateImageFiles() {
        viewModelScope.launch {
            // Fetch the latest files from the media store.
            var mediaStoreFiles = async {
                getImagesFromMediaStore(getApplication()).toMediaFiles()
            }
            // Fetch the cached files from the room db.
            var cachedFiles = async {
                db.mediaFileDao().getMediaFilesAsync()
            }
            // Remove any stale records from the database.
            if (cachedFiles.await().size != mediaStoreFiles.await().size) {
                var updatedFiles = mediaStoreFiles.await()
                var staleFiles = cachedFiles.await().subtract(updatedFiles).toList()

                db.mediaFileDao().delete(staleFiles)
            }
            // Upsert the newly fetched files to the database.
            db.mediaFileDao().insertAll(mediaStoreFiles.await())
        }
    }
}