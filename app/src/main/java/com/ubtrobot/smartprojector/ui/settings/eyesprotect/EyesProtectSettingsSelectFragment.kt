package com.ubtrobot.smartprojector.ui.settings.eyesprotect

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.ListDividerDecoration
import com.ubtrobot.smartprojector.databinding.FragmentEyesProtectSettingsSelectBinding
import java.lang.IllegalArgumentException
import kotlin.math.roundToInt

/**
 * TODO 待确认
 */
class EyesProtectSettingsSelectFragment : Fragment() {
    private var type: Int? = null
    private var _binding: FragmentEyesProtectSettingsSelectBinding? = null
    private val binding get() = _binding!!
    private var listener: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        } else {
            throw IllegalArgumentException("Activity must implement OnFragmentActionListener")
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(ARG_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEyesProtectSettingsSelectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            listener?.onBack()
        }

        binding.rcSelection.layoutManager = LinearLayoutManager(context)
        binding.rcSelection.addItemDecoration(ListDividerDecoration(
            lineHeight = resources.getDimension(R.dimen._1dp).roundToInt(),
            lineColor = resources.getColor(R.color.color_menu_split),
            padding = resources.getDimension(R.dimen._40dp),
            ignoreLastChildNum = 0
        ))
        binding.rcSelection.adapter = TimeSelectionAdapter(listOf("1分钟", "10分钟", "30分钟", "1小时"))
    }

    interface OnFragmentActionListener {
        fun onBack()
    }

    companion object {
        const val TYPE_SELECT_USE_TIME = 0
        const val TYPE_SELECT_REST_TIME = 1

        private const val ARG_TYPE = "ARG_TYPE"
        fun newInstance(type: Int) =
                EyesProtectSettingsSelectFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_TYPE, type)
                    }
                }
    }
}