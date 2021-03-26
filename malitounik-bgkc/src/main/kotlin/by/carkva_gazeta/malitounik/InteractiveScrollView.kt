package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class InteractiveScrollView : ScrollView {
    private var mOnScrollChangedCallback: OnScrollChangedCallback? = null
    private var mListener: OnBottomReachedListener? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun computeScrollDeltaToGetChildRectOnScreen(rect: Rect?): Int {
        return 0
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val view = getChildAt(childCount - 1)
        val diff = view.bottom - (height + scrollY)
        mListener?.onScrollDiff(diff)
        if (diff == 0) {
            mListener?.onBottomReached()
        }
        super.onScrollChanged(l, t, oldl, oldt)
        mOnScrollChangedCallback?.onScroll(t, oldt)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action ?: MotionEvent.ACTION_CANCEL) {
            MotionEvent.ACTION_DOWN -> mListener?.onTouch(true)
            MotionEvent.ACTION_UP -> mListener?.onTouch(false)
        }
        return super.onTouchEvent(event)
    }

    fun setOnScrollChangedCallback(onScrollChangedCallback: OnScrollChangedCallback?) {
        mOnScrollChangedCallback = onScrollChangedCallback
    }

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener?) {
        mListener = onBottomReachedListener
    }

    interface OnScrollChangedCallback {
        fun onScroll(t: Int, oldt: Int)
    }

    interface OnBottomReachedListener {
        fun onBottomReached()
        fun onScrollDiff(diff: Int)
        fun onTouch(action: Boolean)
    }
}