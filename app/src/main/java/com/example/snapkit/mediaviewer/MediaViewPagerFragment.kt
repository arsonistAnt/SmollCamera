package com.example.snapkit.mediaviewer

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    private val safeFragmentArgs: MediaViewPagerFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaViewPagerBinding.inflate(inflater)
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)
        if (hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            initMediaPager()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val mediaDialog = getAlertDialog(requireContext())
            mediaDialog.setMessage(getString(R.string.storage_dialog_message))
            mediaDialog.setTitle(R.string.permissions_dialog_title)
            requestForPermissions(requireActivity(), mediaDialog)
        } else {
            updateMediaPager()
        }

    }

    private fun initMediaPager() {
        // Use a recycler view and turn it into a view pager.

        val mediaViewPager = binding.mediaViewer
        // TODO: Null check for shared data.
        val mediaViewPagerAdapter = MediaViewPagerAdapter(sharedGallery.mediaFiles.value!!, childFragmentManager)
        mediaViewPager.adapter = mediaViewPagerAdapter
        mediaViewPager.pageMargin = PAGE_MARGIN.dp.toPx()
        // Set position of the pager to the one that the user clicked in the ThumbnailGalleryFragment.
        mediaViewPager.currentItem = safeFragmentArgs.clickPosition
    }

    private fun updateMediaPager() {
        val mediaViewPager = binding.mediaViewer
        // TODO: Null check for shared data.
        val mediaViewPagerAdapter = MediaViewPagerAdapter(sharedGallery.mediaFiles.value!!, childFragmentManager)
        mediaViewPager.adapter = mediaViewPagerAdapter
        mediaViewPager.currentItem = safeFragmentArgs.clickPosition
    }


}