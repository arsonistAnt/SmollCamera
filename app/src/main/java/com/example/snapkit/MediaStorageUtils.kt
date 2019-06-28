package com.example.snapkit

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.snapkit.database.MediaFile
import com.example.snapkit.domain.ImageFile
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


const val IMAGE_FILE_SUFFIX = ".JPG"
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
 */
fun scanForMediaFiles(context: Context, filePaths: Array<String>) {
    try {
        MediaScannerConnection.scanFile(context, filePaths, null,
            object : MediaScannerConnection.MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {}
                override fun onScanCompleted(p0: String?, p1: Uri?) {}
            })
    } catch (e: Exception) {
        Log.e(CLASS_TAG, e.message)
    }

}

/**
 * Use the MediaStore content resolver to retrieve URI data about the image store on the android device.
 *
 * @param context the context where the function was called.
 * @return a list of image file URIs.
 */
fun getImagesFromMediaStore(context: Context): List<ImageFile> {
    var dataColumns = arrayOf(
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_TAKEN
    )
    // Select this table located at this URI.
    var select = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    // Order by
    var orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    var contentResolver = context.contentResolver
    var mCursor = contentResolver.query(select, dataColumns, null, null, orderBy)
    var filePaths = arrayListOf<ImageFile>()

    mCursor.apply {
        var dataUriIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
        var dateTakenIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)

        while (moveToNext()) {
            var dateTakenString = getString(dateTakenIndex)
            var dataUriString = getString(dataUriIndex)
            val dateTaken =
                Instant.ofEpochMilli(dateTakenString.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateFormat = dateTaken.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
            val timeFormat = dateTaken.format(DateTimeFormatter.ofPattern("E HH:mm:ss", Locale.ENGLISH))
            filePaths.add(ImageFile(dataUriString, dateFormat, timeFormat))
        }
    }
    return filePaths
}


fun getImageFromMediaStore(context: Context, fileName: String): ImageFile? {
    var dataColumns = arrayOf(
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_TAKEN
    )
    // Select this table located at this URI.
    var select = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    var dataString = MediaStore.Images.Media.DATA
    // Order by
    var orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    var contentResolver = context.contentResolver
    var mCursor = contentResolver.query(select, dataColumns, "$dataString = ?", arrayOf(fileName), orderBy)
    var imageFile: ImageFile? = null

    mCursor.apply {
        var dataUriIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA)
        var dateTakenIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)

        while (moveToNext()) {
            var dateTakenString = getString(dateTakenIndex)
            var dataUriString = getString(dataUriIndex)
            val dateTaken =
                Instant.ofEpochMilli(dateTakenString.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateFormat = dateTaken.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
            val timeFormat = dateTaken.format(DateTimeFormatter.ofPattern("E HH:mm:ss", Locale.ENGLISH))
            imageFile = ImageFile(dataUriString, dateFormat, timeFormat)
        }
    }
    return imageFile
}


/**
 * Convert database entity to a list of ImageFile POJO's.
 */
fun List<MediaFile>.toImageFiles(): List<ImageFile> {
    return map {
        ImageFile(it.uri, it.creationDate, it.creationTime)
    }
}

/**
 * Convert ImageFile list to a list MediaFile entities.
 */
fun List<ImageFile>.toMediaFiles(): List<MediaFile> {
    return map {
        MediaFile(it.filePath, it.dateCreated, it.timeCreated)
    }
}

