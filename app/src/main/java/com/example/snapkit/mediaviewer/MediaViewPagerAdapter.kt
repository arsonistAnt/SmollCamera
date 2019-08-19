package com.example.snapkit.mediaviewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.snapkit.domain.ImageFile

class MediaViewPagerAdapter(private val imageList: List<ImageFile>, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val imageFile = imageList[position]
        return PhotoViewFragment().apply { filePath = imageFile.filePath }
    }

    override fun getCount(): Int {
        return imageList.size
    }
}




