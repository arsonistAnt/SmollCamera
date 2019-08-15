package com.example.snapkit.mediaviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentMediaViewPagerBinding
import com.example.snapkit.utils.dp
import com.example.snapkit.utils.toPx

// Store the page margin value (in dp)
private const val PAGE_MARGIN = 24

class MediaViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentMediaViewPagerBinding
    private lateinit var sharedGallery: SharedGalleryViewModel
    private val safeFragmentArgs: MediaViewPagerFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaViewPagerBinding.inflate(inflater)
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)
        initMediaPager()
        return binding.root
    }

    private fun initMediaPager() {
        // Use a recycler view and turn it into a view pager.

        val mediaViewPager = binding.mediaViewer
        // TODO: Null check for shared data.
        val mediaViewPagerAdapter = MediaViewPagerAdapter(sharedGallery.mediaFiles.value!!, requireFragmentManager())
        mediaViewPager.adapter = mediaViewPagerAdapter
        mediaViewPager.pageMargin = PAGE_MARGIN.dp.toPx()
        // Set position of the pager to the one that the user clicked in the ThumbnailGalleryFragment.
        mediaViewPager.currentItem = safeFragmentArgs.clickPosition
    }
}