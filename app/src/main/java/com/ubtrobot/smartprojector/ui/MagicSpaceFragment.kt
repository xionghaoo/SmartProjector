package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import timber.log.Timber

/**
 * 魔法空间
 */
class MagicSpaceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("MagicSpaceFragment onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_magic_space, container, false)
    }



    override fun onDestroyView() {
        Timber.d("MagicSpaceFragment onDestroyView")
        super.onDestroyView()
    }

    companion object {

        fun newInstance() = MagicSpaceFragment()

    }
}