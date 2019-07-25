package com.example.snapkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//import androidx.core.app.ActivityCompat
//import android.Manifest

class ActivityMainHost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)

        //TODO: Request app permissions.
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }

}
