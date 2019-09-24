package com.example.snapkit

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snapkit.database.FavoritedImage
import com.example.snapkit.database.MediaFile
import com.example.snapkit.database.MediaFileDatabase
import com.example.snapkit.database.getDatabase
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.utils.deleteFilesFromMediaStore
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

    // Store list of favorite image uris, will mainly be used for toggling the heart icon in the MediaViewPager.
    var favoriteImagesUri: LiveData<List<String>>

    // Co-routine variables.
    private var viewModelJob = Job()
    private var viewModelScope = CoroutineScope(viewModelJob)
    // Initialize the media cache Room Database
    private var mediaDB: MediaFileDatabase = getDatabase(application)

    init {
        // Modify the return value so that it will return a LiveData ImageFile list instead of a MediaFile list.
        mediaFiles = Transformations.map(mediaDB.mediaFileDao().getMediaFiles()) { mediaFiles ->
            val imageFiles = mediaFiles.toImageFiles()
            attachHeartedAttributeAsync(imageFiles)
            imageFiles
        }
        // Modify the return value so that we get string uri paths instead.
        favoriteImagesUri = Transformations.map(mediaDB.favoritedImagesDao().getFavorites()) { favorites ->
            favorites.map { it.uri }
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
            val updatedFiles = mediaStoreFiles.await()
            val staleFiles = cachedFiles.await().subtract(updatedFiles).toList()
            mediaDB.mediaFileDao().delete(staleFiles)

            // Update-insert the newly fetched files to the database.
            mediaDB.mediaFileDao().insertAll(mediaStoreFiles.await())
        }
    }

    /**
     * Remove image an file from the application.
     *
     * @param imageFile an ImageFile object that will be used for deletion.
     */
    fun removeImageFile(imageFile: ImageFile) {
        val context = getApplication() as Context
        viewModelScope.launch {
            // Remove the file from the media store.

            deleteFilesFromMediaStore(context, arrayOf(imageFile.filePath))
            // Remove the file from the cache
            val deletedMediaFile =
                MediaFile(imageFile.filePath, imageFile.dateCreated, imageFile.timeCreated, imageFile.dateTakenLong)
            mediaDB.mediaFileDao().delete(arrayListOf(deletedMediaFile))
            mediaDB.favoritedImagesDao().delete(listOf(FavoritedImage(imageFile.filePath)))
        }
    }

    /**
     * Batch remove image files from the application.
     *
     * @param imageFiles ImageFile objects that will be used for deletion.
     */
    fun removeImageFiles(imageFiles: List<ImageFile>) {
        val context = getApplication() as Context
        viewModelScope.launch {
            // Remove the files from the cache
            val staleMediaFiles = imageFiles.toMediaFiles()
            mediaDB.mediaFileDao().delete(staleMediaFiles)
            // Remove the files from the media store.
            val staleUris = staleMediaFiles.map { it.uri }
            deleteFilesFromMediaStore(context, staleUris.toTypedArray())
            val staleFavoritedUris = staleMediaFiles.map { FavoritedImage(it.uri) }
            mediaDB.favoritedImagesDao().delete(staleFavoritedUris)
        }
    }

    /**
     * Add an image to the favorited_images table.
     *
     * @param favoriteImage a database entity object that will be added to the favorite_images table.
     */
    fun addToFavoritesDB(favoriteImage: FavoritedImage) {
        viewModelScope.launch {
            mediaDB.favoritedImagesDao().insertFileUri(favoriteImage)
        }
    }

    /**
     * Remove an image from the favorited_images table.
     *
     * @param favoriteImage a database entity object that will be removed from the favorite_images table.
     */
    fun removeFromFavoritesDB(favoriteImage: FavoritedImage) {
        viewModelScope.launch {
            mediaDB.favoritedImagesDao().delete(listOf(favoriteImage))
        }
    }

    /**
     * Change the hearted member property to true in each ImageFile object if its uri is in the favorites table.
     *
     * @param imageFiles the list of ImageFile objects that will have its hearted member property modified.
     */
    private fun attachHeartedAttributeAsync(imageFiles: List<ImageFile>) {
        viewModelScope.launch {
            val favoriteImages = mediaDB.favoritedImagesDao().getFavoritesAsync()
            imageFiles.map { file ->
                // Check if the image file in the mediaDB is in the favorited images database.
                val favoritesObject = favoriteImages.find { file.filePath == it.uri }
                favoritesObject?.apply {
                    file.hearted = true
                }
                file
            }
        }
    }
}