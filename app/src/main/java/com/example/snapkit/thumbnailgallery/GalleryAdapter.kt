package com.example.snapkit.thumbnailgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.ThumbnailViewItemBinding
import com.example.snapkit.domain.ImageFile

class GalleryAdapter(private val onClickListener: OnClickThumbnailListener) :
    ListAdapter<ImageFile, GalleryAdapter.PhotoViewHolder>(DiffImageFileCallBack) {

    class PhotoViewHolder(var thumbnail: ThumbnailViewItemBinding) : RecyclerView.ViewHolder(thumbnail.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(ThumbnailViewItemBinding.inflate(layoutInflater))
    }


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        var imageView = holder.thumbnail.thumbnailView
        var filePath = getItem(position).filePath
        imageView.setOnClickListener {
            onClickListener.onClick(getItem(position).filePath)
        }

        // TODO: Move this glide implementation into the BindingAdapter.kt.
        Glide.with(imageView.context)
            .load(filePath)
            .centerCrop()
            .into(imageView)
    }
}

class OnClickThumbnailListener(var onClickListener: (filePath: String) -> Unit) {

    fun onClick(filePath: String) {
        onClickListener(filePath)
    }
}
