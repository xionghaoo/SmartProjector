package com.ubtrobot.smartprojector.ui.infant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentAppDesktopBinding
import com.ubtrobot.smartprojector.launcher.AppManager
import timber.log.Timber

class AppDesktopFragment : Fragment() {

    private lateinit var binding: FragmentAppDesktopBinding

    private lateinit var adapter: AppDesktopAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppDesktopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AppDesktopAdapter(this)
        binding.viewPager.adapter = adapter

        AppManager.getInstance(requireContext()).getAllApps()
        AppManager.getInstance(requireContext()).addUpdateListener { apps ->
            adapter.setAppNum(apps.size)
            true
        }
    }

    companion object {
        fun newInstance() = AppDesktopFragment()
    }
}