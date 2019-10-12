package com.example.snapkit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.snapkit.database.FavoritedImage
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.repository.MediaFileRepository
import com.example.snapkit.utils.toImageFiles

/**
 * A class to share Media information across all fragments, this will be considered the main source of truth.
 */
class SharedGalleryViewModel(application: Application) : AndroidViewModel(application) {
    // Store cached media file info into a live data.
    var mediaFiles: LiveData<List<ImageFile>>

    // Store list of favorite image uris, will mainly be used for toggling the heart icon in the MediaViewPager.
    var favoriteImagesUri: LiveData<List<String>>

    // A repository object that handles fetching/updating/removing image files.
    private var imageRepository = MediaFileRepository(application)

    // Stores the current image position in the MediaViewer, this is used to snap to the correct scroll position in the image gallery.
    var currentPosFromMediaViewer = 0

    // Keep track of transition from thumbnail gallery to MediaViewPager.
    var transitionToMediaViewPager = false

    init {
        // Modify the return value so that it will return a LiveData ImageFile list instead of a MediaFile list.
        mediaFiles = Transformations.map(imageRepository.getMediaFiles()) { mediaFiles ->
            val imageFiles = mediaFiles.toImageFiles()
            imageRepository.modifyHeartedAttributesAsync(imageFiles)
            imageFiles
        }
        // Modify the return value so that we get string uri paths instead.
        favoriteImagesUri = Transformations.map(imageRepository.getFavoriteFiles()) { favorites ->
            favorites.map { it.uri }
        }
    }

    /**
     * Update the image file cache in the ImageRepository.
     *
     * @see MediaFileRepository.updateMediaFiles
     */
    fun updateImageFiles() {
        imageRepository.updateMediaFiles()
    }

    /**
     * Remove image an file from the application.
     *
     * @param imageFile an ImageFile object that will be used for deletion.
     */
    fun removeImageFile(imageFile: ImageFile) {
        imageRepository.removeImageFile(imageFile)
    }

    /**
     * Batch remove image files from the application.
     *
     * @param imageFiles ImageFile objects that will be used for deletion.
     */
    fun removeImageFiles(imageFiles: List<ImageFile>) {
        imageRepository.removeImageFiles(imageFiles)
    }

    /**
     * Add image to favorites.
     *
     * @param favoriteImage a FavoritedImage object that will be added to favorites.
     */
    fun addToFavorites(favoriteImage: FavoritedImage) {
        imageRepository.addToFavoritesDB(favoriteImage)
    }

    /**
     * Remove an image from favorites
     *
     * @param favoriteImage a FavoritedImage object that will be remove from the favorites.
     */
    fun removeFavorite(favoriteImage: FavoritedImage) {
        imageRepository.removeFromFavoritesDB(favoriteImage)
    }
}