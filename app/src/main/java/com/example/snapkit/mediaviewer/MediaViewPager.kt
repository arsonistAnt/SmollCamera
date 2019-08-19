package com.example.snapkit.mediaviewer

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MediaViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Catch exception that is thrown when the PhotoView is placed within the the ViewPager.
     *
     * @See: https://github.com/chrisbanes/PhotoView#issues-with-viewgroups
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (error: IllegalArgumentException) {
            false
        }
    }
}