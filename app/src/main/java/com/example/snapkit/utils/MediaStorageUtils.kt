package com.example.snapkit.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.snapkit.domain.ImageFile
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


const val IMAGE_FILE_SUFFIX = ".jpg"
const val CLASS_TAG = "MediaStoreUtils"

/**
 * Get a File type reference to the Digital Camera Images directory.
 *
 * @return a File object that represents the DCIM path of the android device.
 */
fun getDCIMDirectory(): File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

/**
 * Generate an empty image File with the JPEG file suffix. If the fileName arg is not provided then the
 * system time stamp will be used as the file name.
 *
 * @param absoluteFilePath the File object reference to the image file location.
 * @param fileName the name of the image file.
 */
fun generateImageFile(absoluteFilePath: File, fileName: String = getSystemTimeStamp()): File {
    // Throw an exception if the file directory doesn't exist.
    if (!absoluteFilePath.exists())
        throw FileNotFoundException()

    val newFileName = fileName + IMAGE_FILE_SUFFIX
    return File(absoluteFilePath, newFileName)
}

/**
 * Get the system time stamp in the format "ddMMyyyy_HHmmss" e.g. 15062019_115427.
 *
 * @return the string system time stamp.
 */
fun getSystemTimeStamp(): String = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())

/**
 * Tell the MediaScanner to index a list of given file paths, the new files will then be added to the media content
 * provider. Other media type apps for example a Photo Gallery will then be able to display the newly added files.
 *
 * NOTE: The MediaScanner scans the device periodically by default of the Android OS.
 *
 * @param context the application context.
 * @param filePaths a string array of file paths.
 * @param callBack a function call back that takes the context and filePath.
 */
fun scanForMediaFiles(
    context: Context,
    filePaths: Array<String>,
    callBack: (context: Context, filePath: String) -> Unit
) {
    try {
        MediaScannerConnection.scanFile(
            context,
            filePaths,
            null
        ) { filePath, uri ->
            if (uri != null) {
                callBack(context, filePath)
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to scan for the file!", Toast.LENGTH_LONG).show()
        Timber.e(e)
    }
}

/**
 * Get a list of Image data from the MediaStore.
 *
 * @param context the context where the function was called.
 * @return a list of image file URIs.
 */
fun getImagesFromMediaStore(context: Context): List<ImageFile> {
    val dataColumns = arrayOf(
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATE_ADDED
    )
    // Select this table located at this URI.
    val select = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    // Order by
    val orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    val contentResolver = context.contentResolver
    val mCursor = contentResolver?.query(select, dataColumns, null, null, orderBy)
    val filePaths = arrayListOf<ImageFile>()

    mCursor?.let {
        val dataUriIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val dateAddedIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)

        while (it.moveToNext()) {
            val dateAddedString = it.getString(dateAddedIndex)
            val dataUriString = it.getString(dataUriIndex)
            val dateTaken =
                Instant.ofEpochMilli(dateAddedString.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateFormat = dateTaken.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
            val timeFormat = dateTaken.format(DateTimeFormatter.ofPattern("E HH:mm:ss", Locale.ENGLISH))
            filePaths.add(ImageFile(dataUriString, dateFormat, timeFormat, dateAddedString.toLong()))
        }
    }
    mCursor?.close()
    return filePaths
}


/**
 * Get an image file from the media store.
 *
 * @param context the context where the function was called.
 * @param filePath the path the image was stored in.
 * @return an ImageFile object.
 */
fun getImageFromMediaStore(context: Context, filePath: String): ImageFile? {
    val dataColumns = arrayOf(
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_TAKEN
    )
    // Select this table located at this URI.
    val select = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val dataString = MediaStore.Images.Media.DATA
    // Order by
    val orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    val contentResolver = context.contentResolver
    val mCursor = contentResolver.query(select, dataColumns, "$dataString = ?", arrayOf(filePath), orderBy)
    var imageFile: ImageFile? = null

    mCursor?.let {
        val dataUriIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val dateTakenIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)

        while (it.moveToNext()) {
            val dateTakenString = it.getString(dateTakenIndex)
            val dataUriString = it.getString(dataUriIndex)
            val dateTaken =
                Instant.ofEpochMilli(dateTakenString.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateFormat = dateTaken.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
            val timeFormat = dateTaken.format(DateTimeFormatter.ofPattern("E HH:mm:ss", Locale.ENGLISH))
            imageFile = ImageFile(dataUriString, dateFormat, timeFormat, dateTakenString.toLong())
        }
    }
    mCursor?.close()
    return imageFile
}

/**
 * Remove the image file data from the media store.
 *
 * @param context the application context
 * @param filePath the image location of the file to remove.
 */
fun deleteFileFromMediaStore(context: Context, filePath: String) {
    try {
        val dcimDir = getDCIMDirectory()
        dcimDir?.apply {
            val contentResolver = context.contentResolver
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            contentResolver.delete(uri, MediaStore.Files.FileColumns.DATA + "=?", arrayOf(filePath))
            // Update media store for good measure.
            scanForMediaFiles(context, arrayOf(dcimDir.absolutePath)) { _, _ -> }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to delete image!", Toast.LENGTH_LONG).show()
        Timber.e(e)
    }
}