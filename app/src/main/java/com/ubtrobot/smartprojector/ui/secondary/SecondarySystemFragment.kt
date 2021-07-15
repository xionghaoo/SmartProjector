package com.ubtrobot.smartprojector.ui.secondary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R

/**
 * 中学系统
 */
class SecondarySystemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_secondary_system, container, false)
    }

    companion object {
        fun newInstance() = SecondarySystemFragment()
    }
}