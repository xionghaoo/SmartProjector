package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import timber.log.Timber

/**
 * 课程视频
 */
class EducationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("EducationFragment onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_education, container, false)
    }

    override fun onDestroyView() {
        Timber.d("EducationFragment onDestroyView")
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = EducationFragment()
    }
}