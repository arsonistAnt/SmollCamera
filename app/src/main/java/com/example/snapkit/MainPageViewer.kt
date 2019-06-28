package com.example.snapkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.snapkit.camera.CameraViewFragment
import com.example.snapkit.databinding.MainPageViewerBinding
import com.example.snapkit.gallery.ImageGalleryFragment

/**
 * Number of pages users can slide through in the SnapKit app.
 */
private const val NUM_PAGES = 2

/**
 * The page that the user will start with in the main pager UI.
 */
private const val START_POSITION = 1

class MainPageViewer : AppCompatActivity() {
    private lateinit var binding:MainPageViewerBinding
    private lateinit var mPager:ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create data binding objects.
        initDataBindingObjects()

        // Initialize and setup the view pager for the main UI.
        initMainUIPager()
    }

    private fun initDataBindingObjects() {
        binding = DataBindingUtil.setContentView(this, R.layout.main_page_viewer)
        mPager = binding.pagerMainMenu
    }

    private fun initMainUIPager() {
        mPager.adapter = MenuSlidePageAdapter(supportFragmentManager)
        //Set the starting position of the main page to the second page.
        mPager.currentItem = START_POSITION
    }

    private inner class MenuSlidePageAdapter(fragmentManager: FragmentManager):
            FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment =
            when(position){
                1 -> CameraViewFragment()
                else -> ImageGalleryFragment()
            }

        override fun getCount(): Int = NUM_PAGES
    }
}
