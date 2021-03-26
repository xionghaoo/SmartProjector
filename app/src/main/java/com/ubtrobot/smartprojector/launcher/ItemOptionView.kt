package com.ubtrobot.smartprojector.launcher

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.SystemUtil
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator

/**
 * ACTION_DOWN = 0
 * ACTION_UP = 1
 * ACTION_MOVE = 2
 */
class ItemOptionView : FrameLayout {

    private var _dragging = false
    private val _dragLocation = PointF()
    private var _overlayPopup: RecyclerView = RecyclerView(context)
    private val _slideInLeftAnimator = SlideInLeftAnimator(AccelerateDecelerateInterpolator())
    private val _slideInRightAnimator = SlideInRightAnimator(AccelerateDecelerateInterpolator())
    private val _overlayPopupAdapter = FastItemAdapter<PopupIconLabelItem>()
    private var _overlayPopupShowing: Boolean = false

    private var _dragItem: App? = null

    private val uninstallItemIdentifier = 83L
    private val infoItemIdentifier = 84L
    private val editItemIdentifier = 85L
    private val removeItemIdentifier = 86L
    private val resizeItemIdentifier = 87L

    private val _overlayView = OverlayView(context)

    private val uninstallItem = PopupIconLabelItem(R.string.uninstall, R.drawable.ic_delete).withIdentifier(
            uninstallItemIdentifier
    )
    private val infoItem = PopupIconLabelItem(R.string.info, R.drawable.ic_info).withIdentifier(
            infoItemIdentifier
    )
    private val editItem = PopupIconLabelItem(R.string.edit, R.drawable.ic_edit).withIdentifier(
            editItemIdentifier
    )
    private val removeItem = PopupIconLabelItem(R.string.remove, R.drawable.ic_close).withIdentifier(
            removeItemIdentifier
    )

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        _overlayPopup.visibility = View.INVISIBLE
        _overlayPopup.alpha = 0f;
        _overlayPopup.overScrollMode = OVER_SCROLL_NEVER
        _overlayPopup.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        _overlayPopup.itemAnimator = _slideInRightAnimator
        _overlayPopup.adapter = _overlayPopupAdapter
        addView(_overlayView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(_overlayPopup, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        setWillNotDraw(false)

    }

    private inner class OverlayView : View {
        constructor(context: Context) : super(context) {
            setWillNotDraw(false)
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            if (event == null || event.actionMasked != 0 || !_overlayPopupShowing) {
                return super.onTouchEvent(event)
            }
            collapse()
            return true
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null && ev.actionMasked == 1 && _dragging) {
            // 手指抬起
        }
        if (_dragging) {
            return true
        }
        if (ev != null) {
            _dragLocation.set(ev.x, ev.y)
        }
        return super.onInterceptTouchEvent(ev)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (_dragging) {
                _dragLocation.set(event.x, event.y)

            }
        }
        return super.onTouchEvent(event)

    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        _overlayView.bringToFront()
        _overlayPopup.bringToFront()
    }

    private fun showPopupMenuForItem(
            x: Float,
            y: Float,
            popupItem: List<PopupIconLabelItem?>?,
            listener: com.mikepenz.fastadapter.listeners.OnClickListener<PopupIconLabelItem?>?
    ) {
        if (!_overlayPopupShowing) {
            _overlayPopupShowing = true
            _overlayPopup.visibility = VISIBLE
            _overlayPopup.translationX = x
            _overlayPopup.translationY = y
            _overlayPopup.alpha = 1.0f
            _overlayPopupAdapter.add(popupItem)
            _overlayPopupAdapter.withOnClickListener(listener)
        }
    }

    private fun showItemPopup() {
        val itemList = ArrayList<PopupIconLabelItem>()
        itemList.add(uninstallItem)
        itemList.add(infoItem)
//        itemList.add(editItem)
//        itemList.add(removeItem)

        var x = _dragLocation.x + resources.getDimension(R.dimen._20dp)
        var y = _dragLocation.y
        val itemListHeight = itemList.size * resources.getDimension(R.dimen.option_view_height)
        val optionViewWidth = resources.getDimension(R.dimen.option_view_width)
        if (x + optionViewWidth > width) {
            _overlayPopup.itemAnimator = _slideInLeftAnimator
            x -= optionViewWidth
        } else {
            _overlayPopup.itemAnimator = _slideInRightAnimator
        }
        if (y + itemListHeight > height) {
            y -= itemListHeight
        }
        showPopupMenuForItem(
                x,
                y,
                itemList,
                com.mikepenz.fastadapter.listeners.OnClickListener<PopupIconLabelItem?> { v, adapter, item, position ->
                    if (item != null) {
                        when (item.identifier) {
                            uninstallItemIdentifier -> {
                                SystemUtil.uninstallApp(context, _dragItem?._packageName)
//                                Toast.makeText(context, "卸载${_dragItem?._className}", Toast.LENGTH_SHORT).show()

                            }
//                            editItemIdentifier -> itemOption.onEditItem(dragItem)
//                            removeItemIdentifier -> itemOption.onRemoveItem(dragItem)
                            infoItemIdentifier -> {
                                if (_dragItem != null) {
                                    SystemUtil.appInfo(context, _dragItem!!.packageName, _dragItem!!.className)
                                }
                            }
//                            resizeItemIdentifier -> itemOption.onResizeItem(dragItem)
                        }
                    }
                    collapse()
                    true
                }
        )
    }

    fun getLongClickListener(item: App) : OnLongClickListener {
        return OnLongClickListener { _ ->
            _dragItem = item
            showItemPopup()
            true
        }
    }

    fun collapse() {
        if (_overlayPopupShowing) {
            _overlayPopupShowing = false
            _overlayPopup.animate().alpha(0.0f).withEndAction {
                _overlayPopup.visibility = INVISIBLE
                _overlayPopupAdapter.clear()
            }
            if (!_dragging) {
//                _dragView = null
//                _dragItem = null
//                _dragAction = null
            }
        }
    }
}