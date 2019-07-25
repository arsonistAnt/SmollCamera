package com.example.snapkit.utils

import android.content.res.Resources
import com.example.snapkit.database.MediaFile
import com.example.snapkit.domain.ImageFile

/**
 * Convert database entity to a list of ImageFile POJO's.
 */
fun List<MediaFile>.toImageFiles(): List<ImageFile> {
    return map {
        ImageFile(it.uri, it.creationDate, it.creationTime, it.dateTakenLong)
    }
}

/**
 * Convert ImageFile list to a list MediaFile entities.
 */
fun List<ImageFile>.toMediaFiles(): List<MediaFile> {
    return map {
        MediaFile(it.filePath, it.dateCreated, it.timeCreated, it.dateTakenLong)
    }
}

/**
 * Convert dp to px.
 */
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Syntactic sugar, instead of using Int.toPx() this usage -> Int.dp.toPx() makes more sense.
 */
val Int.dp: Int
    get() = this