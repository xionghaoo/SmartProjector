package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ubtrobot.smartprojector.MockData
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.*
import com.ubtrobot.smartprojector.ui.video.VideoItem
import com.ubtrobot.smartprojector.ui.video.VideoPlayerActivity
import com.ubtrobot.smartprojector.utils.JXWAppManager
import com.ubtrobot.smartprojector.utils.JxwAppType
import com.ubtrobot.smartprojector.utils.OnceClickStrategy
import com.ubtrobot.smartprojector.utils.ToastUtil
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
    private var _bindingPageSix: FragmentMainPageLiveBinding? = null

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
                _bindingPageSix = FragmentMainPageLiveBinding.inflate(inflater, container, false)
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

    }

    /**
     * 语文
     */
    private fun bindPageTwoView() {
        OnceClickStrategy.onceClick(bindingPageTwo.tvChinesePrepareRead) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_TOP, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_prepare_classroom, "名师课堂") {
                    jxwAppManager.startFamousTeacherClassroom(requireContext(), "语文")
                },
                HomeMenuData(R.mipmap.ic_chinese_prepare_book_read, "课本点读") {
                    jxwAppManager.startBookFingerRead(requireActivity(), "语文");
                },
                HomeMenuData(R.mipmap.ic_chinese_prepare_finger_read, "指尖阅读") {
                    jxwAppManager.startFingerRead(requireContext(), "keben")
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageTwo.cardChineseClassic) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_classic_book_explain, "课本讲解") {
                    jxwAppManager.startSyncAssistPage(requireContext(), "语文")
                },
                HomeMenuData(R.mipmap.ic_chinese_classic_learn_word, "字词学习") {
                },
                HomeMenuData(R.mipmap.ic_chinese_classic_recite_text, "背诵课文") {
                },
                HomeMenuData(R.mipmap.ic_chinese_classic_extension, "专题扩展") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.CHINESE_EXTENSION)
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageTwo.cardChineseAfterSchool) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_after_school_word, "字词听写") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.ZICI_LISTEN)
                },
                HomeMenuData(R.mipmap.ic_chinese_tools_wrong_book, "错题本") {
                },
                HomeMenuData(R.mipmap.ic_chinese_after_school_exam, "同步试题") {
                }
            ))
        }

        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseFontFundamental) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.LEFT_TOP, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "拼音学习") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.LEARN_PINYIN)
                },
                HomeMenuData(R.mipmap.ic_chinese_bihua, "笔画名称") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.BIHUA_NAME)
                },
                HomeMenuData(R.mipmap.ic_chinese_pianpang, "偏旁部首") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.PIANPANG_BUSHOU)
                },
                HomeMenuData(R.mipmap.ic_chinese_bishun, "笔顺规则") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.BISHUN_RULE)
                },
                HomeMenuData(R.mipmap.ic_chinese_hanzi, "汉字学习") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.LEARN_CHINESE)
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseReadWrite) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.LEFT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_book_read, "课文阅读") {
                    jxwAppManager.startFixDataPage(requireContext(), "小学阅读指导")
                },
                HomeMenuData(R.mipmap.ic_chinese_book_read_aloud, "课文朗读") {
                    ToastUtil.showToast(requireContext(), "拼音学习2")
                },
                HomeMenuData(R.mipmap.ic_chinese_poem, "诗词朗读") {
                    jxwAppManager.startFixDataPage(requireContext(), "小学古诗词")
                },
                HomeMenuData(R.mipmap.ic_chinese_composition, "作文写作") {
                    jxwAppManager.startFixDataPage(requireContext(), "语文写作")
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseIdiom) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.LEFT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_ancient_poetry, "同步古诗") {
                    jxwAppManager.startFixDataPage(requireContext(), "小学古诗词")
                },
                HomeMenuData(R.mipmap.ic_chinese_anime_idiom, "动漫成语") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.CHINESE_ANIME_IDIOM)
                },
                HomeMenuData(R.mipmap.ic_chinese_anime_guoxue, "动漫学国学") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.CHINESE_ANIME_GUOXUE)
                }
            ))
        }
        // 查词典
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseToolsDict) {
            jxwAppManager.startOtherPage(requireContext(), JxwAppType.CHINESE_DICT)
        }
        // 字词听写
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseToolsWord) {
            jxwAppManager.startOtherPage(requireContext(), JxwAppType.ZICI_LISTEN)
        }
        // 趣味语文
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseToolsInterest) {
            jxwAppManager.startOtherPage(requireContext(), JxwAppType.CHINESE_INTEREST)
        }
        // 错题本
        OnceClickStrategy.onceClick(bindingPageTwo.btnChineseToolsWrongBook) {
            ToastUtil.showToast(requireContext(), "错题本")
        }
    }

    /**
     * 英语
     */
    private fun bindPageThreeView() {
        OnceClickStrategy.onceClick(bindingPageThree.cardEnglishPrepareRead) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_TOP, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "名师课堂") {
                    jxwAppManager.startFamousTeacherClassroom(requireContext(), "英语")
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "课本点读") {
                    jxwAppManager.startBookFingerRead(requireActivity(), "英语");
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "指尖阅读") {
                    jxwAppManager.startFingerRead(requireContext(), "keben")
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageThree.cardEnglishClassic) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "知识点视频") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "知识点归纳") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "记单词") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "背课文") {
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageThree.cardEnglishAfterSchool) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "口语测评") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.ORAL_ENGLISH_TEST)
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "同步测试") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "错题本") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "自我评测") {
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageThree.btnEnglishWrite) {
            jxwAppManager.startFixDataPage(requireContext(), "小学英语写作")
        }
        OnceClickStrategy.onceClick(bindingPageThree.btnEnglishMemorizeWords) {
            jxwAppManager.startOtherPage(requireContext(), JxwAppType.ENGLISH_MEMORIZE_WORDS)
        }
        OnceClickStrategy.onceClick(bindingPageThree.btnEnglishRead) {
            jxwAppManager.startFixDataPage(requireContext(), "小学英语阅读")
        }
        OnceClickStrategy.onceClick(bindingPageThree.btnEnglishGrammar) {
            jxwAppManager.startFixDataPage(requireContext(), "小学英语语法")
        }
    }

    /**
     * 数学
     */
    private fun bindPageFourView() {
        OnceClickStrategy.onceClick(bindingPageFour.cardMathematicsPrepareRead) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_TOP, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "名师课堂") {
                    jxwAppManager.startFamousTeacherClassroom(requireContext(), "数学")
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "课本点读") {
                    jxwAppManager.startBookFingerRead(requireActivity(), "数学");
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "指尖阅读") {
                    jxwAppManager.startFingerRead(requireContext(), "keben")
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageFour.cardMathematicsClassic) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "知识点精讲") {
                },
                HomeMenuData(R.mipmap.ic_math_fundament_graph, "数学图形") {
                    jxwAppManager.startFixDataPage(requireContext(), "小学数学图形")
                },
                HomeMenuData(R.mipmap.ic_math_fundament_formula, "数学公式") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.MATH_FORMULA)
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageFour.cardMathematicsAfterSchool) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.RIGHT_BOTTOM, arrayListOf(
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "例题解析") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "错题本") {
                },
                HomeMenuData(R.mipmap.ic_chinese_pinyin, "同步测试") {
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsFundament) {
            listener?.onItemSelected(it, HomeMenuDialog.Align.LEFT_TOP, arrayListOf(
                HomeMenuData(R.mipmap.ic_math_fundament_know_number, "认识数字") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.MATH_LEARN_NUMBER)
                },
                HomeMenuData(R.mipmap.ic_math_fundament_arithmetic, "算术口诀") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.MATH_ARITHMETIC)
                },
                HomeMenuData(R.mipmap.ic_math_fundament_formula, "数学公式") {
                    jxwAppManager.startOtherPage(requireContext(), JxwAppType.MATH_FORMULA)
                },
                HomeMenuData(R.mipmap.ic_math_fundament_graph, "数学图形") {
                    jxwAppManager.startFixDataPage(requireContext(), "小学数学图形")
                }
            ))
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsKnowledgePoints) {
            ToastUtil.showToast(requireContext(), "知识点图谱")
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsOlympic) {
            jxwAppManager.startFixDataPage(requireContext(), "小学奥数训练")
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsPractise) {
            jxwAppManager.startFixDataPage(requireContext(), "小学应用题训练")
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsToolsFocus) {
            jxwAppManager.startOtherPage(requireContext(), JxwAppType.MATH_FOCUS_PRACTISE)
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsToolsInterest) {
            jxwAppManager.startFixDataPage(requireContext(), "小学趣味数学")
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsToolsArithmetic) {
            jxwAppManager.startFixDataPage(requireContext(), "小学数的运算")
        }
        OnceClickStrategy.onceClick(bindingPageFour.btnMathematicsToolErrorBook) {
            ToastUtil.showToast(requireContext(), "错题本")
        }
    }

    /**
     * AI编程
     */
    private fun bindPageFiveView() {
        OnceClickStrategy.onceClick(bindingPageFive.tvProgramTeachingVideo) {
            val items = ArrayList<VideoItem>()
            items.add(VideoItem("视频1", MockData.video1))
            items.add(VideoItem("视频2", MockData.video2))
            items.add(VideoItem("视频3", MockData.video3))
            items.add(VideoItem("视频4", MockData.video4))
            VideoPlayerActivity.start(requireContext(), items)
        }
        // 少儿编程思维
        OnceClickStrategy.onceClick(bindingPageFive.btnProgramThinking) {
            ToastUtil.showToast(requireContext(), "少儿编程思维")
        }
    }

    interface OnFragmentActionListener {
        fun onItemSelected(v: View, align: HomeMenuDialog.Align, data: ArrayList<HomeMenuData>)
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