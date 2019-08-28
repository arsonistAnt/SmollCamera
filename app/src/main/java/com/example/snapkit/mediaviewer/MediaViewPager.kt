package com.example.snapkit.mediaviewer

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MediaViewPager : ViewPager {
    private lateinit var mGestureDetector: GestureDetector

    // Overridden constructors
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setupTouchListeners()
    }

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

    /**
     * Dispatch touch events to the mGestureDetector object.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Provide an empty GestureDetector and SimpleOnGestureListener for initialization.
     */
    private fun setupTouchListeners() {
        val baseGestureListener = GestureDetector.SimpleOnGestureListener()
        mGestureDetector = GestureDetector(context, baseGestureListener)
    }

    /**
     * Provides callbacks to the single tap listener in the gesture detector.
     *
     * @param tapCallBack the user defined function being passed and called in the onSingleTapConfirmed listener.
     */
    fun onSingleTap(tapCallBack: () -> Unit) {
        val newGestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                tapCallBack()
                return super.onSingleTapConfirmed(e)
            }
        }
        mGestureDetector = GestureDetector(context, newGestureListener)
    }
}