package com.ubtrobot.smartprojector.ui.infant

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.*

class InfantMainFragment : Fragment() {
    private var page: Int = 0

    private lateinit var bindingPageOne: FragmentInfantPageInterestBinding
//    private val bindingPageOne get() = _bindingPageOne!!
    private lateinit var bindingPageTwo: FragmentInfantPageHabitBinding
    private lateinit var bindingPageThree: FragmentInfantPageThinkingTrainingBinding
    private lateinit var bindingPageFour: FragmentInfantPageEntertainmentBinding
//    private val bindingPageTwo get() = _bindingPageTwo!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            page = getInt(ARG_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (page) {
            0 -> {
                bindingPageOne = FragmentInfantPageInterestBinding.inflate(inflater, container, false)
                return bindingPageOne.root
            }
            1 -> {
                bindingPageTwo = FragmentInfantPageHabitBinding.inflate(inflater, container, false)
                return bindingPageTwo.root
            }
            2 -> {
                bindingPageThree = FragmentInfantPageThinkingTrainingBinding.inflate(inflater, container, false)
                return bindingPageThree.root
            }
            3 -> {
                bindingPageFour = FragmentInfantPageEntertainmentBinding.inflate(inflater, container, false)
                return bindingPageFour.root
            }
            else -> throw java.lang.IllegalArgumentException("fragment page not found")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _bindingPageOne = null
//        _bindingPageTwo = null
    }



    companion object {
        private const val ARG_PAGE = "ARG_PAGE"
        fun newInstance(page: Int) = InfantMainFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE, page)
            }
        }
    }
}