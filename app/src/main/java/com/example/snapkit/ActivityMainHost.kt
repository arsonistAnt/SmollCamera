package com.example.snapkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class ActivityMainHost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTimber()
        setContentView(R.layout.activity_main_host)
    }

    /**
     * Setup Timber logging in our activity.
     */
    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
        Timber.i("Timber has been planted.")
    }

}
