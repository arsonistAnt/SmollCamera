package com.example.snapkit.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.PhotoViewItemBinding

//TODO: Replace with a ListAdapter to use DiffUtil upon implementing Room database.
class GalleryAdapter(val imagePaths: List<String>) : RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(var photo: PhotoViewItemBinding) : RecyclerView.ViewHolder(photo.root) {
        // TODO: Provide image URI info, this is for later use.
        fun bind() {
            photo.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(PhotoViewItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        var imageView = holder.photo.photoView
        Glide.with(imageView.context)
            .load(imagePaths[position])
            .centerCrop()
            .into(imageView)
    }
}
