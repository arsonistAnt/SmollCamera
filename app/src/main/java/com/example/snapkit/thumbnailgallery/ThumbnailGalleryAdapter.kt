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
        var imageView = holder.thumbnail.thumbnailView
        var filePath = getItem(position).filePath
        imageView.setOnClickListener {
            onClickListener.onClick(position)
        }

        // TODO: Move this glide implementation into the BindingAdapter.kt.
        Glide.with(imageView.context)
            .load(filePath)
            .centerCrop()
            .into(imageView)
    }
}

class OnClickThumbnailListener(var onClickListener: (position: Int) -> Unit) {

    fun onClick(position: Int) {
        onClickListener(position)
    }
}
