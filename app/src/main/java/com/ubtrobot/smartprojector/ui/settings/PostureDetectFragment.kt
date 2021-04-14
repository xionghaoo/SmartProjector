package com.ubtrobot.smartprojector.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suke.widget.SwitchButton
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.repo.Repository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 姿势检测
 */
@AndroidEntryPoint
class PostureDetectFragment : Fragment() {

    @Inject
    lateinit var repo: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posture_detect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sw = view.findViewById<SwitchButton>(R.id.sw_settings_posture)
        sw.isChecked = repo.prefs.isPostureDetectOpen
        sw.setOnCheckedChangeListener { _, isChecked ->
            repo.prefs.isPostureDetectOpen = isChecked
        }
    }

    companion object {
        fun newInstance() = PostureDetectFragment()
    }
}