package com.example.snapkit.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.FragmentImageViewerBinding
import kotlinx.android.synthetic.main.fragment_image_viewer.*

class ImageViewerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var binding = FragmentImageViewerBinding.inflate(inflater)
        // inflater.inflate(R.layout.fragment_image_viewer, container, false)
        return binding.root
    }


    fun setImageView() {
        if (arguments != null) {
            var filePath = arguments!!["file"]
            var imageViewer = image_viewer
            Glide.with(imageViewer.context)
                .load(filePath)
                .into(imageViewer)
        }
    }
}