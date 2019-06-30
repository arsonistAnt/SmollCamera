package com.example.snapkit.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.PhotoViewItemBinding
import com.example.snapkit.domain.ImageFile

class GalleryAdapter :
    ListAdapter<ImageFile, GalleryAdapter.PhotoViewHolder>(DiffImageFileCallBack) {

    class PhotoViewHolder(var photo: PhotoViewItemBinding) : RecyclerView.ViewHolder(photo.root) {
        // TODO: Provide image URI info, this is for later use.
        fun bind() {
            photo.executePendingBindings()
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
        return PhotoViewHolder(PhotoViewItemBinding.inflate(layoutInflater))
    }


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        var imageView = holder.photo.photoView
        Glide.with(imageView.context)
            .load(getItem(position).filePath)
            .centerCrop()
            .into(imageView)
    }
}
