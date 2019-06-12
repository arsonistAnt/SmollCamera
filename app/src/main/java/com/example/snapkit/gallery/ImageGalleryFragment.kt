package com.example.snapkit.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.snapkit.databinding.FragmentImageGalleryViewBinding
//TODO: Implement image gallery for the photos and videos.
class ImageGalleryFragment: Fragment() {
    lateinit var binding:FragmentImageGalleryViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageGalleryViewBinding.inflate(layoutInflater)

        return binding.root
    }
}