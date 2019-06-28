package com.example.snapkit.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "media_file")
data class MediaFile(
    @PrimaryKey
    val uri: String,
    val creationDate: String,
    val creationTime: String
)

@Dao
interface MediaFileDao {
    @Insert
    suspend fun insertMediaFile(mediaFile: MediaFile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaFiles: List<MediaFile>)

    @Query("SELECT * FROM media_file")
    fun getMediaFiles(): LiveData<List<MediaFile>>

    @Query("SELECT * FROM media_file")
    suspend fun getMediaFilesAsync(): List<MediaFile>

    @Delete
    suspend fun delete(mediaFile: List<MediaFile>)

}

@Database(entities = [MediaFile::class], version = 1)
abstract class MediaFileDatabase : RoomDatabase() {
    abstract fun mediaFileDao(): MediaFileDao
}

private lateinit var INSTANCE: MediaFileDatabase

fun getDatabase(context: Context): MediaFileDatabase {
    synchronized(MediaFileDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE =
                Room.databaseBuilder(context.applicationContext, MediaFileDatabase::class.java, "media-file-database")
                    .build()
        }
        return INSTANCE
    }
}

