package com.example.snapkit.thumbnailgallery

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapkit.R
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentThumbnailGalleryViewBinding
import com.example.snapkit.utils.getAlertDialog
import com.example.snapkit.utils.hasPermissions
import com.example.snapkit.utils.requestForPermissions

class ThumbnailGalleryFragment : Fragment() {
    lateinit var binding: FragmentThumbnailGalleryViewBinding
    lateinit var sharedGallery: SharedGalleryViewModel
    lateinit var thumbnailGalleryAdapter: ThumbnailGalleryAdapter

    private val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThumbnailGalleryViewBinding.inflate(layoutInflater)
        initViewModel()
        initRecyclerView()
        setupSystemWindows()
        binding.button.setOnClickListener {
            sharedGallery.updateImageFiles()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        ViewCompat.requestApplyInsets(binding.galleryLayout)
        // Make sure user has given access to storage permissions before updating the thumbnail gallery.
        checkPermissionForStorage()
    }

    /**
     * Before updating the gallery, check if user has the permissions.
     */
    private fun checkPermissionForStorage() {
        // Permissions need to be requested at runtime if API level >= 23
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!hasPermissions(requireContext(), permission)) {
                val thumbnailGalleryDialog = getAlertDialog(requireContext())
                thumbnailGalleryDialog.setMessage(getString(R.string.storage_dialog_message))
                thumbnailGalleryDialog.setTitle(R.string.permissions_dialog_title)

                // Request user for permission before opening the gallery.
                requestForPermissions(requireActivity(), thumbnailGalleryDialog, permission)
            } else {
                // Update the cached data to the latest changes from the MediaStore.
                // This is the initial time that the data will be fetched from the database.
                sharedGallery.updateImageFiles()
            }
        }
    }

    /**
     * Setup the RecyclerView to display images in the devices external storage.
     * GridLayoutManager & ThumbnailGalleryAdapter is also initialized in this code block.
     */
    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        thumbnailGalleryAdapter = ThumbnailGalleryAdapter(OnClickThumbnailListener { clickPosition ->
            // Navigate to the ImageViewer when any of the image thumbnail is clicked.
            val navController = findNavController()
            // Pass the filePath args to the ImageViewerFragment using safe args.
            val actionToMediaViewPager =
                ThumbnailGalleryFragmentDirections.actionImageGalleryFragmentToMediaViewPagerFragment(clickPosition)
            navController.navigate(actionToMediaViewPager)
        })
        // Recycler view needs a layout manager and a user defined Adapter class that extends RecyclerAdapter.
        binding.galleryRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = thumbnailGalleryAdapter
        }
        thumbnailGalleryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1) {
                    thumbnailGalleryAdapter.notifyItemMoved(positionStart, 0)
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
            thumbnailGalleryAdapter.submitList(thumbnailFiles)
        })
    }

    /**
     * Setup the system windows.
     */
    private fun setupSystemWindows() {
        val topPadding = binding.galleryLayout.paddingTop
        val botPadding = binding.galleryLayout.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.galleryLayout) { view, windowInsets ->
            view.updatePadding(
                top = windowInsets.systemWindowInsetTop + topPadding,
                bottom = windowInsets.systemWindowInsetBottom + botPadding
            )
            windowInsets
        }
    }
}