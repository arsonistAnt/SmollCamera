package com.example.snapkit.thumbnailgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.ItemThumbnailViewBinding
import com.example.snapkit.domain.ImageFile

class ThumbnailGalleryAdapter(private val onClickListener: OnClickThumbnailListener) :
    ListAdapter<ImageFile, ThumbnailGalleryAdapter.ThumbnailViewHolder>(DiffImageFileCallBack) {

    class ThumbnailViewHolder(var thumbnail: ItemThumbnailViewBinding) : RecyclerView.ViewHolder(thumbnail.root) {
        // TODO: Provide image URI info, this is for later use.
        fun bind() {
            thumbnail.executePendingBindings()
        }
    }

    companion object DiffImageFileCallBack : DiffUtil.ItemCallback<ImageFile>() {

        override fun areItemsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {

            return oldItem.filePath == newItem.filePath
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ThumbnailViewHolder(ItemThumbnailViewBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val imageView = holder.thumbnail.thumbnailView
        val imageFile = getItem(position)
        val filePath = getItem(position).filePath
        // Call the OnClickThumbnailListener in the onClick method of the ImageView.
        imageView.setOnClickListener {
            onClickListener.onClick(position, imageFile)
        }
        // Load the image with glide.
        Glide.with(imageView.context)
            .load(filePath)
            .centerCrop()
            .into(imageView)
    }
}

/**
 * An OnClickListener class for the ThumbnailGalleryAdapter class.
 */
class OnClickThumbnailListener(var onClickListener: (position: Int, imageFile: ImageFile) -> Unit) {

    /**
     * An onClick listener event for the ViewHolder.
     *
     * @param position the position of the view holder in the adapter.
     * @param imageFile the ImageFile object that's relative to the position in the adapter.
     */
    fun onClick(position: Int, imageFile: ImageFile) {
        onClickListener(position, imageFile)
    }
}
