package com.example.snapkit.mediaviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.snapkit.SharedGalleryViewModel
import com.example.snapkit.databinding.FragmentMediaViewPagerBinding

class MediaViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentMediaViewPagerBinding
    private lateinit var sharedGallery: SharedGalleryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaViewPagerBinding.inflate(inflater)
        sharedGallery = ViewModelProviders.of(requireActivity()).get(SharedGalleryViewModel::class.java)
        initMediaPager()
        return binding.root
    }

    private fun initMediaPager() {
        // Use a recycler view and turn it into a view pager.
        var recyclerViewPager = binding.mediaViewer
        var mediaViewPagerAdapter = MediaViewPagerAdapter()
        mediaViewPagerAdapter.submitList(sharedGallery.mediaFiles.value)
        var linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        var pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerViewPager)


        recyclerViewPager.apply {
            layoutManager = linearLayoutManager
            adapter = mediaViewPagerAdapter

        }


    }
}