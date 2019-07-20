package com.example.snapkit.thumbnailgallery

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapkit.databinding.FragmentThumbnailGalleryViewBinding

class ThumbnailGalleryFragment : Fragment() {
    lateinit var binding: FragmentThumbnailGalleryViewBinding
    lateinit var sharedGallery: SharedGalleryViewModel
    lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThumbnailGalleryViewBinding.inflate(layoutInflater)
        initRecyclerView()
        initViewModel()
        binding.button.setOnClickListener {
            sharedGallery.updateImageFiles()
        }
        return binding.root
    }

    /**
     * Setup the RecyclerView to display images in the devices external storage.
     * GridLayoutManager & GalleryAdapter is also initialized in this code block.
     */
    private fun initRecyclerView() {
        var layoutManager = GridLayoutManager(requireContext(), 3)
        galleryAdapter = GalleryAdapter(OnClickThumbnailListener { filePath ->
            // Navigate to the ImageViewer when any of the image thumbnail is clicked.
            var navController = findNavController()
            // Pass the filePath args to the ImageViewerFragment using safe args.
            var actionToImageViewer =
                ThumbnailGalleryFragmentDirections.actionImageGalleryFragmentToImageViewerFragment(filePath)
            navController.navigate(actionToImageViewer)
        })
        // Recycler view needs a layout manager and a user defined Adapter class that extends RecyclerAdapter.
        binding.galleryRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = galleryAdapter
        }
        galleryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1) {
                    galleryAdapter.notifyItemMoved(positionStart, 0)
                }
            }
        })
    }

    /**
     * Setup the ViewModel and any observable objects.
     */
    private fun initViewModel() {
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)

        sharedGallery.mediaFiles.observe(viewLifecycleOwner, Observer { thumbnailFiles ->
            galleryAdapter.submitList(thumbnailFiles)
        })

        // Update the cached data to the latest changes from the MediaStore.
        // This is the initial time that the data will be fetched from the database.
        sharedGallery.updateImageFiles()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set the color of the background to white.
        view.setBackgroundColor(Color.WHITE)
        super.onViewCreated(view, savedInstanceState)
    }
}