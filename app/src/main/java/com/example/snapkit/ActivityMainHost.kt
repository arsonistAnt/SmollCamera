package com.example.snapkit

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import timber.log.Timber

class ActivityMainHost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTimber()
        setSystemWindows()
        setContentView(R.layout.activity_main_host)
    }

    override fun onBackPressed() {
        // Get the callback result from the current fragment in the navigation host fragment.
        try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment
            var consumed = false
            navHostFragment?.apply {
                // Get the current fragment from the NavHostFragment
                val currentFragment = navHostFragment.childFragmentManager.fragments[0]
                val activityHostListener = currentFragment as? ActivityMainHostListener
                activityHostListener?.let {
                    consumed = activityHostListener.onBackButtonPressed()
                }
            }
            if (!consumed)
                super.onBackPressed()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Setup Timber logging in our activity.
     */
    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
        Timber.i("Timber has been planted.")
    }

    /**
     * Set initial system window configurations.
     */
    private fun setSystemWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}

/**
 * A listener interface for fragments to implement call backs on the ActivityMainHost.
 */
interface ActivityMainHostListener {

    /**
     * An event listener for onBackPressed() in the ActivityMainHost.
     *
     * @return a boolean that determines whether or not the event has been consumed.
     */
    fun onBackButtonPressed(): Boolean
}
