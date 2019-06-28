package com.example.snapkit.gallery

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.snapkit.databinding.FragmentImageGalleryViewBinding

class ImageGalleryFragment: Fragment() {
    lateinit var binding:FragmentImageGalleryViewBinding
    lateinit var viewModel: ImageGalleryViewModel
    lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageGalleryViewBinding.inflate(layoutInflater)

        initRecyclerView()
        initViewModel()

        binding.button.setOnClickListener {
            viewModel.updateImageFiles()
        }

        return binding.root
    }

    /**
     * Setup the RecyclerView to display images in the devices external storage.
     * GridLayoutManager & GalleryAdapter is also initialized in this code block.
     */
    private fun initRecyclerView() {
        var layoutManager = GridLayoutManager(requireContext(), 3)
        galleryAdapter = GalleryAdapter()

        // Recycler view needs a layout manager and a user defined Adapter class that extends RecyclerAdapter.
        binding.galleryRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = galleryAdapter
        }
    }

    /**
     * Setup the ViewModel and any observable objects.
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ImageGalleryViewModel::class.java)
        viewModel.imageFiles.observe(viewLifecycleOwner, Observer { imageFiles ->
            galleryAdapter.submitList(imageFiles)
        })

        //Update the cached data to the latest changes from the MediaStore.
        viewModel.updateImageFiles()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the color of the background to white.
        view.setBackgroundColor(Color.WHITE)
        super.onViewCreated(view, savedInstanceState)
    }
}