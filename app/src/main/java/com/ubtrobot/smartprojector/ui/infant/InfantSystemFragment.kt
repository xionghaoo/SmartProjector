package com.ubtrobot.smartprojector.ui.infant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentInfantSystemBinding

/**
 * 幼儿系统
 */
class InfantSystemFragment : Fragment() {

    private lateinit var binding: FragmentInfantSystemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfantSystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(R.mipmap.ic_launcher_bg)
            .centerCrop()
            .into(binding.ivMainBackground)
    }

    companion object {
        fun newInstance() = InfantSystemFragment()
    }
}