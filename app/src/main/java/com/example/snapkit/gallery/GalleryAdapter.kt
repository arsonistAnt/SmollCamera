package com.example.snapkit.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapkit.databinding.PhotoViewItemBinding

//TODO: Replace with a ListAdapter to use DiffUtil upon implementing Room database.
class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(private var photo: PhotoViewItemBinding) : RecyclerView.ViewHolder(photo.root) {
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
        return 20
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        //TODO: Use bind method in the PhotoViewHolder class.
    }
}
