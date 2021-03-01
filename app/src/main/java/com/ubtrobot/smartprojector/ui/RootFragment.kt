package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentRootBinding

class RootFragment : Fragment() {

    private var _bindingRoot: FragmentRootBinding? = null
    private val binding get() = _bindingRoot!!
    private var page: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            page = it.getString(ARG_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindingRoot = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = RootPageAdapter()
    }

    private inner class RootPageAdapter : FragmentStatePagerAdapter(childFragmentManager) {
        override fun getCount(): Int = 3

        override fun getItem(position: Int): Fragment {
            TODO("Not yet implemented")
        }
    }

    companion object {
        private const val ARG_PAGE = "arg_page"

        fun newInstance(param1: String, param2: String) =
            RootFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE, param1)
                }
            }
    }
}