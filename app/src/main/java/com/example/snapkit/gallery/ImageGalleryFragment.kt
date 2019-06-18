package com.example.snapkit.gallery

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.snapkit.databinding.FragmentImageGalleryViewBinding
import com.example.snapkit.getDCIMDirectory
import com.example.snapkit.getFilesFromDirectory

//TODO: Implement image gallery for the photos and videos.
class ImageGalleryFragment: Fragment() {
    lateinit var binding:FragmentImageGalleryViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageGalleryViewBinding.inflate(layoutInflater)

        //TODO: Temporary way to get the images into the recycler view, eventually will implement room will delete later.
        var imageFilePaths = getFilesFromDirectory(arrayOf("jpg", "JPG"), getDCIMDirectory()!!).map { it.path }
        //TODO: Temporary way to get the images into the recycler view, eventually will implement room will delete later.

        // Recycler view needs a layout manager and a user defined Adapter class that extends RecyclerAdapter.
        var layoutManager = GridLayoutManager(requireContext(), 3)
        var galleryAdapter = GalleryAdapter(imageFilePaths)

        binding.galleryRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = galleryAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the color of the background to white.
        view.setBackgroundColor(Color.WHITE)
        super.onViewCreated(view, savedInstanceState)
    }
}