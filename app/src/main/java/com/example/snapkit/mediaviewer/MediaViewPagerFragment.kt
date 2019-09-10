package com.example.snapkit.mediaviewer

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.snapkit.R
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentMediaViewPagerBinding
import com.example.snapkit.utils.*

// Store the page margin value (in dp)
private const val PAGE_MARGIN = 24

class MediaViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentMediaViewPagerBinding
    private lateinit var sharedGallery: SharedGalleryViewModel
    private lateinit var mediaViewPager: MediaViewPager
    private var sysWindowsVisible = true
    private val safeFragmentArgs: MediaViewPagerFragmentArgs by navArgs()
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaViewPagerBinding.inflate(inflater)
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)
        initBottomNavBar()
        initMenuClickListeners()
        initMediaPager()
        initObserversShared()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        ViewCompat.requestApplyInsets(binding.mediaMenuLayout)
        if (!hasPermissions(requireContext(), *permissions)) {
            val mediaDialog = getAlertDialog(requireContext())
            mediaDialog.setMessage(getString(R.string.storage_dialog_message))
            mediaDialog.setTitle(R.string.permissions_dialog_title)
            requestForPermissions(requireActivity(), mediaDialog, *permissions)
        } else {
            sharedGallery.updateImageFiles()
        }
    }

    /**
     * Setup basic configurations for the Media Viewpager.
     */
    private fun initMediaPager() {
        mediaViewPager = binding.mediaViewer
        mediaViewPager.pageMargin = PAGE_MARGIN.dp.toPx()
        // Set position of the pager to the one that the user clicked in the ThumbnailGalleryFragment.
        mediaViewPager.currentItem = safeFragmentArgs.clickPosition
    }

    /**
     * Subscribe observer to the shared ViewModel data.
     */
    private fun initObserversShared() {
        sharedGallery.mediaFiles.observe(viewLifecycleOwner, Observer { imageList ->
            val mediaViewPager = binding.mediaViewer
            val mediaViewPagerAdapter = MediaViewPagerAdapter(imageList, childFragmentManager)
            mediaViewPager.adapter = mediaViewPagerAdapter
            mediaViewPager.currentItem = safeFragmentArgs.clickPosition
            mediaViewPager.onSingleTap {
                toggleSystemUI()
            }
        })
    }

    /**
     * Hide status bar and navigation bar.
     */
    private fun hideSysWindows(activity: Window) {
        activity.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    /**
     * Show status bar and navigation bar.
     */
    private fun showSysWindows(activity: Window) {
        activity.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    /**
     * Allow toggling of navigation and status bars with a single tap.
     */
    private fun toggleSystemUI() {
        val activityWindow = requireActivity().window
        sysWindowsVisible = if (sysWindowsVisible) {
            hideSysWindows(activityWindow)
            binding.mediaMenuLayout.visibility = View.GONE
            false
        } else {
            showSysWindows(activityWindow)
            binding.mediaMenuLayout.visibility = View.VISIBLE
            true
        }
    }

    /**
     * Set parameters for the bottom navigation bar.
     */
    private fun initBottomNavBar() {
        val menuLayout = binding.mediaMenuLayout
        val menuMarginParams = menuLayout.layoutParams as ViewGroup.MarginLayoutParams
        val bottomMargin = menuMarginParams.bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(menuLayout) { _, insets ->
            menuMarginParams.bottomMargin = bottomMargin + insets.systemWindowInsetBottom
            insets
        }
    }

    /**
     * Create on click listeners for the buttons in the bottom menu layout.
     */
    private fun initMenuClickListeners() {
        binding.shareImageButton.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}