package com.example.snapkit.mediaviewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.snapkit.domain.ImageFile

class MediaViewPagerAdapter(private val imageList: List<ImageFile>, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * Return a PhotoViewFragment with an assigned file path.
     *
     * @param position the position of the fragment in the view pager.
     * @return a PhotoViewFragment object.
     */
    override fun getItem(position: Int): Fragment {
        val imageFile = imageList[position]
        return PhotoViewFragment().apply { filePath = imageFile.filePath }
    }

    /**
     * Return the list count.
     *
     * @return the size of the image list.
     */
    override fun getCount(): Int {
        return imageList.size
    }

    /**
     * Return an ImageFile object at the position of the list.
     *
     * @return an ImageFile object
     */
    fun getImageFile(position: Int): ImageFile = imageList[position]

}




