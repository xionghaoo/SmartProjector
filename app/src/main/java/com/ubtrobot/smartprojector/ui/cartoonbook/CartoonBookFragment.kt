package com.ubtrobot.smartprojector.ui.cartoonbook

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.eschao.android.widget.pageflip.PageFlip
import com.ubtrobot.smartprojector.R
import java.lang.Exception

/**
 * 绘本
 */
class CartoonBookFragment : Fragment()/*, GestureDetector.OnGestureListener*/ {

    private lateinit var gestureDetector: GestureDetector
//    private var listener: OnFragmentActionListener? = null

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentActionListener) {
//            listener = context
//        } else {
//            throw IllegalStateException("Activity must implement CartoonBookFragment.OnFragmentActionListener")
//        }
//    }
//
//    override fun onDetach() {
//        listener = null
//        super.onDetach()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cartoon_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        gestureDetector = GestureDetector(requireContext(), this)

//        container_flip_page.setOnTouchListener { _, event -> onTouchEvent(event) }
    }

    override fun onResume() {
        super.onResume()
//        page_flip_view.onResume()
    }

    override fun onPause() {
        super.onPause()
//        page_flip_view.onPause()
    }

//    private fun onTouchEvent(e: MotionEvent) : Boolean {
//        if (e.action == MotionEvent.ACTION_UP) {
//            page_flip_view.onFingerUp(e.x, e.y)
//        }
//        return gestureDetector.onTouchEvent(e)
//    }
//
//    override fun onDown(e: MotionEvent): Boolean {
//        page_flip_view.onFingerDown(e.x, e.y)
//        return true
//    }
//
//    override fun onScroll(
//        e1: MotionEvent,
//        e2: MotionEvent,
//        distanceX: Float,
//        distanceY: Float
//    ): Boolean {
//        page_flip_view.onFingerMove(e2.x, e2.y)
//        return true
//    }
//
//    override fun onShowPress(e: MotionEvent?) {
//
//    }
//
//    override fun onSingleTapUp(e: MotionEvent?): Boolean {
//        return false
//    }
//
//    override fun onLongPress(e: MotionEvent?) {
//    }
//
//    override fun onFling(
//        e1: MotionEvent?,
//        e2: MotionEvent?,
//        velocityX: Float,
//        velocityY: Float
//    ): Boolean {
//        return false
//    }

    interface OnFragmentActionListener {
//        fun onTouchEvent(e: MotionEvent)
    }

    companion object {
        fun newInstance() = CartoonBookFragment()
    }
}