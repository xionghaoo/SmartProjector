package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ubtrobot.smartprojector.MockData
import com.ubtrobot.smartprojector.databinding.*
import com.ubtrobot.smartprojector.ui.video.VideoItem
import com.ubtrobot.smartprojector.ui.video.VideoPlayerActivity
import com.ubtrobot.smartprojector.utils.JXWAppManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 桌面 - 一级页面
 */
@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var page: Int? = null
//    @Inject
//    lateinit var getLearnAppManager: GetLearnAppManager
    @Inject
    lateinit var jxwAppManager: JXWAppManager

    private var listener: OnFragmentActionListener? = null

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
        }
    }

    /**
     * ai智能辅助
     */
    private fun bindPageOneView() {
        bindingPageOne.btnAssistantChinese.setOnClickListener {
            jxwAppManager.startBookFingerRead(requireContext(), "语文")
        }
        bindingPageOne.btnAssistantEnglish.setOnClickListener {
            jxwAppManager.startBookFingerRead(requireContext(), "英语")
        }
        bindingPageOne.btnAssistantMath.setOnClickListener {
            jxwAppManager.startBookFingerRead(requireContext(), "数学")
        }
        bindingPageOne.btnAssistantMorals.setOnClickListener {
            jxwAppManager.startBookFingerRead(requireContext(), "科学")
        }

        bindingPageOne.cardAssistantFamousTeacherClassroom.setOnClickListener {
            jxwAppManager.startFamousTeacherClassroom(requireContext(), "语文")
        }

        bindingPageOne.cardAssistantFingerRead.setOnClickListener {
            jxwAppManager.startFingerRead(requireContext(), "keben")
        }


//        GlideApp.with(requireContext())
//            .load(R.mipmap.ic_assistant_finger_read)
//            .into(bindingPageOne.ivAssistantFingerRead)
//        GlideApp.with(requireContext())
//            .load(R.mipmap.ic_assistant_ip_role)
//            .into(bindingPageOne.ivIpRole)
//
//        GlideApp.with(requireContext())
//            .load(R.mipmap.ic_assistant_search_questions)
//            .into(bindingPageOne.ivSearchQuestions)
//
//        bindingPageOne.cardAssistantFingerRead.setSelectListener {
//            getLearnAppManager.startChinesePage(requireContext(), "custom_page_apk_dian_du")
//        }
//        bindingPageOne.cardAssistantWordBook.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_zhidu_dan_ci")
//        }
//        bindingPageOne.cardAssistantChineseWordBook.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_zhidu_zi_ci")
//        }
//        bindingPageOne.cardAssistantWrongBook.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_zhidu_cuo_ti")
//        }
//        bindingPageOne.cardAssistantQueryWord.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_cha_dan_ci")
//        }
//        bindingPageOne.cardAssistantQueryChineseWord.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_cha_zi_ci")
//        }
//        bindingPageOne.cardSearchQuestions.setSelectListener {
//            getLearnAppManager.startAiPage(requireContext(), "custom_page_sou_ti")
//        }
    }

    /**
     * 语文
     */
    private fun bindPageTwoView() {
        bindingPageTwo.tvChinesePrepareRead.setOnClickListener {
            jxwAppManager.startBookFingerRead(requireActivity(), "语文");
        }

        bindingPageTwo.btnChineseLearnPinyin.setOnClickListener {
            listener?.onItemSelected(it)
        }
//        GlideApp.with(requireContext())
//                .load(R.mipmap.ic_chinese_main)
//                .into(bindingPageTwo.ivChineseMain)
//        bindingPageTwo.cardChineseClassroom.setSelectListener {
//            ChineseDetailActivity.start(requireContext(), ChineseDetailActivity.TYPE_CLASSROOM)
//        }
//
//        bindingPageTwo.cardChineseInterest.setSelectListener {
//            ChineseDetailActivity.start(requireContext(), ChineseDetailActivity.TYPE_INTEREST)
//        }
//
//        bindingPageTwo.cardChineseCoursebook.setSelectListener {
//            getLearnAppManager.startChinesePage(requireContext(), "同步新点读")
//        }
//
//        bindingPageTwo.cardChineseLearnNewWord.setSelectListener {
//            getLearnAppManager.startChinesePage(requireContext(), "生字词")
//        }
//
//        bindingPageTwo.cardChineseLearnPinyin.setSelectListener {
//            getLearnAppManager.startChinesePage(requireContext(), "拼音学习")
//        }
    }

    /**
     * 英语
     */
    private fun bindPageThreeView() {
//        bindingPageThree.cardEnglishClassroom.setSelectListener {
//            startPlainActivity(EnglishClassroomActivity::class.java)
//        }
//
//        bindingPageThree.cardEnglishCoursebook.setSelectListener {
//            getLearnAppManager.startEnglishPage(requireContext(), "同步新点读")
//        }
//
//        bindingPageThree.cardEnglishOralPractise.setSelectListener {
//            getLearnAppManager.startEnglishPage(requireContext(), "同步英语评测")
//        }
//
//        bindingPageThree.cardEnglishWordBook.setSelectListener {
//            getLearnAppManager.startEnglishPage(requireContext(), "分类词汇")
//        }
//
//        bindingPageThree.cardEnglishInterest.setSelectListener {
//        }
    }

    /**
     * 数学
     */
    private fun bindPageFourView() {
//        GlideApp.with(requireContext())
//                .load(R.mipmap.ic_mathematics_main)
//                .into(bindingPageFour.ivMathematicsMain)
//        bindingPageFour.cardMathematicsClassroom.setSelectListener {
//            startPlainActivity(MathematicsClassroomActivity::class.java)
//        }
//        bindingPageFour.cardMathematicsCoursebook.setSelectListener {
//            getLearnAppManager.startMathematicsPage(requireContext(), "同步新点读")
//        }
//        bindingPageFour.cardMathematicsSearchQuestion.setSelectListener {
//            getLearnAppManager.startMathematicsPage(requireContext(), "custom_page_sou_ti")
//        }
//        bindingPageFour.cardMathematicsWrongBook.setSelectListener {
//            getLearnAppManager.startMathematicsPage(requireContext(), "custom_page_zhidu_cuo_ti")
//        }
//        bindingPageFour.cardMathematicsCalculate.setSelectListener {
//            getLearnAppManager.startMathematicsPage(requireContext(), "速算闯关")
//        }
    }

    /**
     * AI编程
     */
    private fun bindPageFiveView() {
        bindingPageFive.tvProgramTeachingVideo.setOnClickListener {
            val items = ArrayList<VideoItem>()
            items.add(VideoItem("视频1", MockData.video1))
            items.add(VideoItem("视频2", MockData.video2))
            items.add(VideoItem("视频3", MockData.video3))
            items.add(VideoItem("视频4", MockData.video4))
            VideoPlayerActivity.start(requireContext(), items)
        }
    }

    interface OnFragmentActionListener {
        fun onItemSelected(v: View)
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