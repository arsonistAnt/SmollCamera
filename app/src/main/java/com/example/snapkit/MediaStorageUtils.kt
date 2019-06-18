package com.example.snapkit

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import org.apache.commons.io.FileUtils
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
 * @param constraint the string pattern used to filter the directory.
 * @return a list of files based on a constraint provided.
 */
fun getFilesFromDirectory(fileExts: Array<String>, directory: File): List<File> {
    return FileUtils.listFiles(directory, fileExts, true) as List<File>
}

