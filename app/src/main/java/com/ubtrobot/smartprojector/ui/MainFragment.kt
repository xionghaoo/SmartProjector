package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.ubtrobot.smartprojector.databinding.FragmentMainPage1Binding
import com.ubtrobot.smartprojector.databinding.FragmentMainPage2Binding
import com.ubtrobot.smartprojector.databinding.FragmentMainPage3Binding
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.test.TestActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaHomeActivity
import com.ubtrobot.smartprojector.utils.PackageUtil
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.widgets.AppLauncherView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var page: Int? = null
    private var _bindingPageOne: FragmentMainPage1Binding? = null
    private var _bindingPageTwo: FragmentMainPage2Binding? = null
    private var _bindingPageThree: FragmentMainPage3Binding? = null
    private val bindingPageOne get() = _bindingPageOne!!
    private val bindingPageTwo get() = _bindingPageTwo!!
    private val bindingPageThree get() = _bindingPageThree!!

    private var isInitialLayout: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.also {
            page = it.getInt(ARG_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (page) {
            0 -> {
                _bindingPageOne = FragmentMainPage1Binding.inflate(inflater, container, false)
                bindingPageOne.root
            }
            1 -> {
                _bindingPageTwo = FragmentMainPage2Binding.inflate(inflater, container, false)
                bindingPageTwo.root
            }
            2 -> {
                _bindingPageThree = FragmentMainPage3Binding.inflate(inflater, container, false)
                bindingPageThree.root
            }
            else -> throw IllegalStateException("not found main fragment layout")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingPageOne = null
        _bindingPageTwo = null
        _bindingPageThree = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(page) {
            0 -> bindPageOneView()
            1 -> bindPageTwoView()
            3 -> bindPageThreeView()
        }
    }

    /**
     * 桌面1
     */
    private fun bindPageOneView() {
//        bindingPageOne.menuRead.setOnFocusChangeListener { v, hasFocus ->
//            Timber.d("menu hasFocus: $hasFocus")
//        }
//        bindingPageOne.root.requestFocus()

//        bindingPageOne.menuLevel.setOnClickListener {
//            startPlainActivity(SettingsActivity::class.java)
//        }

//        bindingPageOne.menuSearch.setSelectListener {
//            startPlainActivity(TuyaHomeActivity::class.java)
//        }
//        bindingPageOne.menuRead.setSelectListener {
//            ToastUtil.showToast(requireContext(), "指尖点读")
//        }
    }

    /**
     * 桌面2
     */
    private fun bindPageTwoView() {
        bindingPageTwo.flApps.removeAllViews()
        CoroutineScope(Dispatchers.Default).launch {
            val appList = PackageUtil.appList(requireActivity())
            withContext(Dispatchers.Main) {
                appList.forEach { app ->
                    val appView = AppLauncherView(requireContext())
                    appView.setLabel(app.appName)
                    appView.setIcon(app.icon)
                    val paddingVertical = ResourceUtil.convertDpToPixel(20f, requireContext()).roundToInt()
                    val p = (bindingPageTwo.flApps.width - ResourceUtil.convertDpToPixel(120f, context) * 3
                            - ResourceUtil.convertDpToPixel(10f, context) * 2) / 6 - 1
                    val paddingHorizontal = ResourceUtil.convertDpToPixel(p, requireContext()).roundToInt()
                    appView.setPadding(
                        paddingHorizontal,
                        paddingVertical,
                        paddingHorizontal,
                        paddingVertical
                    )
                    appView.setOnClickListener {
                        PackageUtil.startApp(requireActivity(), app.packageName)
                    }
                    bindingPageTwo.flApps.addView(appView)
                }
            }
        }
    }

    /**
     * 桌面3
     */
    private fun bindPageThreeView() {

    }

    companion object {
        private const val ARG_PAGE = "ARG_PAGE"

        fun newInstance(page: Int) = MainFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE, page)
            }
        }
    }
}