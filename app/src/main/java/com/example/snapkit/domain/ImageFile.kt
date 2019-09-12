package com.example.snapkit.domain

/**
 * Data class that will be used to store the file path, date creation and time creation of a file from the
 * MediaStore.Image content provider.
 *
 * @property filePath Location of the file on the external storage of the android device.
 * @property dateCreated The creation date of the file from the DATE_TAKEN column in the MediaStore. (e.g. "yyyy-MM-dd")
 * @property timeCreated The creation time of the file from the DATE_TAKEN column formatted into a time. (e.g. "E HH:mm:ss")
 */
data class ImageFile(
    var filePath: String,
    var dateCreated: String,
    var timeCreated: String,
    var dateTakenLong: Long,
    var hearted: Boolean = false
)