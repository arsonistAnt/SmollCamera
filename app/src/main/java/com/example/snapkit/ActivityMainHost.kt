package com.example.snapkit

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class ActivityMainHost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTimber()
        setSystemWindows()
        setContentView(R.layout.activity_main_host)
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
