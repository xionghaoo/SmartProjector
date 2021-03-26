package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.*
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaHomeActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * 桌面 - 一级页面
 */
@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var page: Int? = null

    private var _bindingPageOne: FragmentMainPageAssistantBinding? = null
    private var _bindingPageTwo: FragmentMainPageChineseBinding? = null
    private var _bindingPageThree: FragmentMainPageEnglishBinding? = null
    private var _bindingPageFour: FragmentMainPageMathematicsBinding? = null
    private var _bindingPageFive: FragmentMainPageAiProgramBinding? = null
    private var _bindingPageSix: FragmentMainPageRecommendBinding? = null

    private val bindingPageOne get() = _bindingPageOne!!
    private val bindingPageTwo get() = _bindingPageTwo!!
    private val bindingPageThree get() = _bindingPageThree!!
    private val bindingPageFour get() = _bindingPageFour!!
    private val bindingPageFive get() = _bindingPageFive!!
    private val bindingPageSix get() = _bindingPageSix!!

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
                _bindingPageOne = FragmentMainPageAssistantBinding.inflate(inflater, container, false)
                bindingPageOne.root
            }
            1 -> {
                _bindingPageTwo = FragmentMainPageChineseBinding.inflate(inflater, container, false)
                bindingPageTwo.root
            }
            2 -> {
                _bindingPageThree = FragmentMainPageEnglishBinding.inflate(inflater, container, false)
                bindingPageThree.root
            }
            3 -> {
                _bindingPageFour = FragmentMainPageMathematicsBinding.inflate(inflater, container, false)
                bindingPageFour.root
            }
            4 -> {
                _bindingPageFive = FragmentMainPageAiProgramBinding.inflate(inflater, container, false)
                bindingPageFive.root
            }
            5 -> {
                _bindingPageSix = FragmentMainPageRecommendBinding.inflate(inflater, container, false)
                bindingPageSix.root
            }
            else -> throw IllegalStateException("not found main fragment layout")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingPageOne = null
        _bindingPageTwo = null
        _bindingPageThree = null
        _bindingPageFour = null
        _bindingPageFive = null
        _bindingPageSix = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(page) {
            0 -> bindPageOneView()
            1 -> bindPageTwoView()
            2 -> bindPageThreeView()
            3 -> bindPageFourView()
            4 -> bindPageFiveView()
            5 -> bindPageSixView()
        }
    }

    /**
     * ai智能辅助
     */
    private fun bindPageOneView() {
        GlideApp.with(requireContext())
            .load(R.mipmap.ic_assistant_finger_read)
            .into(bindingPageOne.ivAssistantFingerRead)
        bindingPageOne.cardAssistantFingerRead.setSelectListener {
            ToastUtil.showToast(requireContext(), "课本点读")
        }
        GlideApp.with(requireContext())
            .load(R.mipmap.ic_assistant_ip_role)
            .into(bindingPageOne.ivIpRole)

        GlideApp.with(requireContext())
            .load(R.mipmap.ic_assistant_search_questions)
            .into(bindingPageOne.ivSearchQuestions)

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
     * 语文
     */
    private fun bindPageTwoView() {
        GlideApp.with(requireContext())
                .load(R.mipmap.ic_chinese_main)
                .into(bindingPageTwo.ivChineseMain)
        bindingPageTwo.cardChineseClassroom.setSelectListener {
            ChineseDetailActivity.start(requireContext(), ChineseDetailActivity.TYPE_CLASSROOM)
        }

        bindingPageTwo.cardChineseInterest.setSelectListener {
            ChineseDetailActivity.start(requireContext(), ChineseDetailActivity.TYPE_INTEREST)
        }

//        bindingPageTwo.flApps.removeAllViews()
//        CoroutineScope(Dispatchers.Default).launch {
//            val appList = PackageUtil.appList(requireActivity())
//            withContext(Dispatchers.Main) {
//                appList.forEach { app ->
//                    val appView = AppLauncherView(requireContext())
//                    appView.setLabel(app.appName)
//                    appView.setIcon(app.icon)
//                    val paddingVertical = ResourceUtil.convertDpToPixel(20f, requireContext()).roundToInt()
//                    val p = (bindingPageTwo.flApps.width - ResourceUtil.convertDpToPixel(120f, context) * 3
//                            - ResourceUtil.convertDpToPixel(10f, context) * 2) / 6 - 1
//                    val paddingHorizontal = ResourceUtil.convertDpToPixel(p, requireContext()).roundToInt()
//                    appView.setPadding(
//                        paddingHorizontal,
//                        paddingVertical,
//                        paddingHorizontal,
//                        paddingVertical
//                    )
//                    appView.setOnClickListener {
//                        PackageUtil.startApp(requireActivity(), app.packageName)
//                    }
//                    bindingPageTwo.flApps.addView(appView)
//                }
//            }
//        }
    }

    /**
     * 桌面3
     */
    private fun bindPageThreeView() {

    }

    private fun bindPageFourView() {
        GlideApp.with(requireContext())
                .load(R.mipmap.ic_mathematics_main)
                .into(bindingPageFour.ivMathematicsMain)
        bindingPageFour.menuSearch.setOnClickListener {
            startPlainActivity(TuyaHomeActivity::class.java)
        }
//        bindingPageFour.menuStartProgramming.setOnClickListener {
//            startPlainActivity(TestActivity::class.java)
//        }
    }

    private fun bindPageFiveView() {

    }

    private fun bindPageSixView() {

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