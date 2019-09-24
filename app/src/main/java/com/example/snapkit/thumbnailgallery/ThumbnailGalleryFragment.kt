package com.example.snapkit.thumbnailgallery

import android.Manifest
import android.os.Bundle
import android.view.KeyEvent
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
import com.example.snapkit.ActivityMainHostListener
import com.example.snapkit.R
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentThumbnailGalleryViewBinding
import com.example.snapkit.domain.ImageFile
import com.example.snapkit.utils.getPermissionAlertDialog
import com.example.snapkit.utils.hasPermissions
import com.example.snapkit.utils.requestForPermissions

class ThumbnailGalleryFragment : Fragment(), ActivityMainHostListener {
    lateinit var binding: FragmentThumbnailGalleryViewBinding
    lateinit var sharedGallery: SharedGalleryViewModel
    lateinit var thumbnailGalleryAdapter: ThumbnailGalleryAdapter
    private val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    // Helps determine the image shown state of the thumbnail gallery.
    private enum class FilterMode { NEUTRAL, FAVORITES, ALL }

    private var currentFilterMode = FilterMode.NEUTRAL


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThumbnailGalleryViewBinding.inflate(layoutInflater)
        initViewModel()
        initRecyclerView()
        setupSystemWindows()
        setupToolBar()
        return binding.root
    }

    override fun onBackButtonPressed(): Boolean {
        if (thumbnailGalleryAdapter.longPressDeleteEnabled) {
            showToolBarItemsOnSelection()
            thumbnailGalleryAdapter.disableLongPressDeletion()
            return true
        }
        return false
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
                val thumbnailGalleryDialog = getPermissionAlertDialog(requireContext())
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
        // Setup the ThumbnailGalleryAdapter and any click events that needs to be handled.
        thumbnailGalleryAdapter = ThumbnailGalleryAdapter(object : OnClickThumbnailListener {
            override fun onClick(position: Int, imageFile: ImageFile, view: View) {
                // Navigate to the ImageViewer when any of the image thumbnail is clicked.
                val navController = findNavController()
                // Click position passed from adapter is no longer reliable, use the index of the shared list instead.
                val indexPosition = sharedGallery.mediaFiles.value?.indexOf(imageFile)
                // Pass the clickPosition to the MediaViewer using safe args.
                indexPosition?.let {
                    val actionToMediaViewPager =
                        ThumbnailGalleryFragmentDirections.actionImageGalleryFragmentToMediaViewPagerFragment(
                            indexPosition
                        )
                    navController.navigate(actionToMediaViewPager)
                }
            }

            override fun onLongClick(position: Int, imageFile: ImageFile, view: View) {
                binding.thumbnailToolbar.apply {
                    setNavigationIcon(R.drawable.ic_clear_menu_item)
                    hideToolBarItemsOnSelection()
                }
            }

            override fun onSelectedItemsEmpty() {
                showToolBarItemsOnSelection()
                thumbnailGalleryAdapter.disableLongPressDeletion()
            }
        })
        // Recycler view needs a layout manager and a user defined Adapter class that extends RecyclerAdapter.
        binding.galleryRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = thumbnailGalleryAdapter
        }

        thumbnailGalleryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1 && (currentFilterMode != FilterMode.FAVORITES))
                    thumbnailGalleryAdapter.notifyItemMoved(positionStart, 0)
            }
        })

        binding.galleryRecyclerView.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK &&
                thumbnailGalleryAdapter.longPressDeleteEnabled
            ) {
                thumbnailGalleryAdapter.disableLongPressDeletion()
            }
            true
        }
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

    /**
     * Configure the tool bar for this fragment.
     */
    private fun setupToolBar() {
        val toolBar = binding.thumbnailToolbar
        toolBar.inflateMenu(R.menu.thumbnail_gallery_menu)
        toolBar.setNavigationOnClickListener {
            showToolBarItemsOnSelection()
            thumbnailGalleryAdapter.disableLongPressDeletion()
        }

        // Initially hide some menu items that shouldn't be shown.
        toolBar.menu.findItem(R.id.trash_menu_item).apply { isVisible = false }
        val allFilterMenuItem = toolBar.menu.findItem(R.id.all_filter_menu_item).apply { isVisible = false }
        val favoritesFilterMenuItem = toolBar.menu.findItem(R.id.favorites_filter_menu_item)

        toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.camera_menu_item -> {
                    val navController = findNavController()
                    val actionToCamera =
                        ThumbnailGalleryFragmentDirections.actionImageGalleryFragmentToCameraViewFragment2()
                    navController.navigate(actionToCamera)
                    true
                }
                R.id.all_filter_menu_item -> {
                    // Show all images
                    thumbnailGalleryAdapter.submitList(sharedGallery.mediaFiles.value)
                    // Hide the "Show All Images" option and show the "Filter by Favorites"
                    favoritesFilterMenuItem.isVisible = true
                    allFilterMenuItem.isVisible = false
                    currentFilterMode = FilterMode.ALL
                    true
                }
                R.id.favorites_filter_menu_item -> {
                    // Replace the current in the adapter with the list of favorite images.
                    val favoritesList = sharedGallery.mediaFiles.value?.filter { file -> file.hearted }
                    thumbnailGalleryAdapter.submitList(favoritesList)

                    // Hide the "Filter by Favorites" option and show the "Show All Images"
                    favoritesFilterMenuItem.isVisible = false
                    allFilterMenuItem.isVisible = true
                    currentFilterMode = FilterMode.FAVORITES
                    true
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }
        }
    }

    /**
     * Hide certain tool bar items when in selection mode.
     */
    private fun hideToolBarItemsOnSelection() {
        val toolBar = binding.thumbnailToolbar
        toolBar.apply {
            menu.findItem(R.id.trash_menu_item).apply { isVisible = true }
            menu.findItem(R.id.camera_menu_item).apply { isVisible = false }
            val favoritesFilter = menu.findItem(R.id.favorites_filter_menu_item)
            val allFilter = menu.findItem(R.id.all_filter_menu_item)
            // Hide the correct menu item since the favorites filter and all filters hide each other's visibility already.
            if (allFilter.isVisible)
                allFilter.isVisible = false
            else
                favoritesFilter.isVisible = false
        }
    }

    /**
     * Re-show tool bar items that's been hidden from the selection mode.
     */
    private fun showToolBarItemsOnSelection() {
        val toolBar = binding.thumbnailToolbar
        // Remove the navigation "X" button on the top left.
        toolBar.navigationIcon = null
        toolBar.apply {
            menu.findItem(R.id.trash_menu_item).apply { isVisible = false }
            menu.findItem(R.id.camera_menu_item).apply { isVisible = true }
            val favoritesFilter = menu.findItem(R.id.favorites_filter_menu_item)
            val allFilter = menu.findItem(R.id.all_filter_menu_item)
            // Use the currentFilterMode to determine which filter option should be shown.
            when (currentFilterMode) {
                // Show the all filter if the selection mode was done in the FILTER mode.
                FilterMode.FAVORITES -> allFilter.isVisible = true
                // Show the favorites filter if the selection mode was done in the ALL mode.
                FilterMode.ALL -> favoritesFilter.isVisible = true
                // If neutral mode then make favoritesFilter visible.
                else -> favoritesFilter.isVisible = true
            }
        }
    }
}