package com.example.snapkit.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.snapkit.database.FavoritedImage
import com.example.snapkit.database.MediaFile
import com.example.snapkit.database.MediaFileDatabase
import com.example.snapkit.database.getDatabase
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.utils.deleteFilesFromMediaStore
import com.example.snapkit.utils.getImagesFromMediaStore
import com.example.snapkit.utils.toMediaFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MediaFileRepository(private val application: Context) {
    private var mediaDB: MediaFileDatabase = getDatabase(application)

    // Co-routine variables.
    private var repositoryJob = Job()
    private var repositoryScope = CoroutineScope(repositoryJob)

    /**
     * Return the MediaFile records asynchronously in the cache.
     *
     * @return a list of MediaFile objects.
     */
    private suspend fun getMediaFilesAsync(): List<MediaFile> {
        return mediaDB.mediaFileDao().getMediaFilesAsync()
    }

    /**
     * Retrieve image files from the MediaStore and return a list of ImageFile objects.
     *
     * @return a list of ImageFile objects.
     */
    private fun getMediaStoreImages(): List<ImageFile> {
        return getImagesFromMediaStore(application)
    }

    /**
     * Change the hearted member property to true in each ImageFile object if its uri is in the favorites table.
     *
     * @param imageFiles the list of ImageFile objects that will have its hearted member property modified.
     */
    fun modifyHeartedAttributesAsync(imageFiles: List<ImageFile>) {
        repositoryScope.launch {
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

    /**
     * Return the MediaFile records in the cache.
     *
     * @return a live data list of MediaFile objects.
     */
    fun getMediaFiles(): LiveData<List<MediaFile>> {
        return mediaDB.mediaFileDao().getMediaFiles()
    }

    /**
     * Return the FavoritedImage records in the cache.
     *
     * @return a live data list of FavoritedImage objects.
     */
    fun getFavoriteFiles(): LiveData<List<FavoritedImage>> {
        return mediaDB.favoritedImagesDao().getFavorites()
    }

    /**
     * Add an image to the favorited_images table.
     *
     * @param favoriteImage a database entity object that will be added to the favorite_images table.
     */
    fun addToFavoritesDB(favoriteImage: FavoritedImage) {
        repositoryScope.launch {
            mediaDB.favoritedImagesDao().insertFileUri(favoriteImage)
        }
    }

    /**
     * Fetch new image files and remove stale ones from the MediaFileDatabase. The new images will be fetched from the
     * media store.
     */
    fun updateMediaFiles() {
        repositoryScope.launch {
            // Fetch the latest files from the media store.
            val mediaStoreFiles = async {
                getMediaStoreImages().toMediaFiles()
            }
            // Fetch the cached files from the room mediaDB.
            val cachedFiles = async {
                getMediaFilesAsync()
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
     * Remove image an file from the media store and the cache.
     *
     * @param imageFile an ImageFile object that will be used for deletion.
     */
    fun removeImageFile(imageFile: ImageFile) {
        repositoryScope.launch {
            // Remove the file from the media store.

            deleteFilesFromMediaStore(application, arrayOf(imageFile.filePath))
            // Remove the file from the cache
            val deletedMediaFile =
                MediaFile(imageFile.filePath, imageFile.dateCreated, imageFile.timeCreated, imageFile.dateTakenLong)
            mediaDB.mediaFileDao().delete(arrayListOf(deletedMediaFile))
            mediaDB.favoritedImagesDao().delete(listOf(FavoritedImage(imageFile.filePath)))
        }
    }

    /**
     * Remove multiple iamge files from the media store and the cache.
     *
     * @param imageFiles the list of ImageFile objects to be deleted.
     */
    fun removeImageFiles(imageFiles: List<ImageFile>) {
        repositoryScope.launch {
            // Remove the files from the cache
            val staleMediaFiles = imageFiles.toMediaFiles()
            mediaDB.mediaFileDao().delete(staleMediaFiles)
            // Remove the files from the media store.
            val staleUris = staleMediaFiles.map { it.uri }
            deleteFilesFromMediaStore(application, staleUris.toTypedArray())
            // Remove the file uri from the favorites cache.
            val staleFavoritedUris = staleMediaFiles.map { FavoritedImage(it.uri) }
            mediaDB.favoritedImagesDao().delete(staleFavoritedUris)
        }
    }

    /**
     * Remove an image to the favorited_images table.
     *
     * @param favoriteImage a database entity object that will be removed from the favorite_images table.
     */
    fun removeFromFavoritesDB(favoriteImage: FavoritedImage) {
        repositoryScope.launch {
            mediaDB.favoritedImagesDao().delete(listOf(favoriteImage))
        }
    }
}