package com.example.snapkit.CameraViewFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.snapkit.databinding.FragmentCameraViewBinding

class CameraViewFragment : Fragment() {
    lateinit var binding: FragmentCameraViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCameraViewBinding.inflate(layoutInflater)
        // TODO: Uncomment below so we can allow LiveData to observe any changes in our viewmodel for future references.
//        binding.setLifecycleOwner(this)

        return binding.root
    }
}