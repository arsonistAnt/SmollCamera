package com.example.snapkit.mediaviewer

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.snapkit.R
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentMediaViewPagerBinding
import com.example.snapkit.utils.*
import timber.log.Timber

// Store the page margin value (in dp)
private const val PAGE_MARGIN = 24

class MediaViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentMediaViewPagerBinding
    private lateinit var sharedGallery: SharedGalleryViewModel
    private lateinit var mediaViewPager: ViewPager
    private val safeFragmentArgs: MediaViewPagerFragmentArgs by navArgs()
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaViewPagerBinding.inflate(inflater)
        Timber.i("OnCreate()")
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)
        initMediaPager()
        initObserversShared()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
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
        })
    }


}