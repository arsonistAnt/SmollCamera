package com.example.snapkit.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "media_file")
data class MediaFile(
    @PrimaryKey
    val uri: String,
    val creationDate: String,
    val creationTime: String,
    val dateTakenLong: Long,
    val hearted: Boolean = false
)
@Dao
interface MediaFileDao {
    @Insert
    suspend fun insertMediaFile(mediaFile: MediaFile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaFiles: List<MediaFile>)

    @Query("SELECT * FROM media_file ORDER BY dateTakenLong DESC")
    fun getMediaFiles(): LiveData<List<MediaFile>>

    @Query("SELECT * FROM media_file ORDER BY dateTakenLong DESC")
    suspend fun getMediaFilesAsync(): List<MediaFile>

    @Delete
    suspend fun delete(mediaFile: List<MediaFile>)

    @Update
    suspend fun updateMediaFiles(vararg mediaFiles: MediaFile)

}

@Entity(tableName = "favorite_images")
data class FavoritedImage(
    @PrimaryKey
    val uri: String
)

@Dao
interface FavoritedImageDao {
    @Insert
    suspend fun insertFileUri(favoriteImage: FavoritedImage)

    @Delete
    suspend fun delete(favoriteImage: List<FavoritedImage>)

    @Query("SELECT * FROM favorite_images")
    fun getFavorites(): LiveData<List<FavoritedImage>>

    @Query("SELECT * FROM favorite_images")
    fun getFavoritesAsync(): List<FavoritedImage>
}

@Database(entities = [MediaFile::class, FavoritedImage::class], version = 4)
abstract class MediaFileDatabase : RoomDatabase() {
    abstract fun mediaFileDao(): MediaFileDao
    abstract fun favoritedImagesDao(): FavoritedImageDao
}

private lateinit var INSTANCE: MediaFileDatabase

fun getDatabase(context: Context): MediaFileDatabase {
    synchronized(MediaFileDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE =
                Room.databaseBuilder(context.applicationContext, MediaFileDatabase::class.java, "media-file-database")
                    .fallbackToDestructiveMigration()
                    .build()
        }
        return INSTANCE
    }
}



