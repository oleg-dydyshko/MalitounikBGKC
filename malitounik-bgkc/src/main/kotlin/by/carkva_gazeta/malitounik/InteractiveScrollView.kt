package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import kotlinx.coroutines.*

class InteractiveScrollView : ScrollView {
    private var mOnInteractiveScrollChangedCallback: OnInteractiveScrollChangedCallback? = null
    private var mListener: OnBottomReachedListener? = null
    private var initialPosition = 0
    private var scrollJob: Job? = null
    private var checkDiff = true

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    interface OnInteractiveScrollChangedCallback {
        fun onScroll(t: Int, oldt: Int)
    }

    interface OnBottomReachedListener {
        fun onBottomReached(checkDiff: Boolean)
        fun onTouch(action: Boolean)
    }

    fun setOnScrollChangedCallback(onInteractiveScrollChangedCallback: OnInteractiveScrollChangedCallback?) {
        mOnInteractiveScrollChangedCallback = onInteractiveScrollChangedCallback
    }

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener?) {
        mListener = onBottomReachedListener
    }

    override fun computeScrollDeltaToGetChildRectOnScreen(rect: Rect?): Int {
        return 0
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val view = getChildAt(childCount - 1)
        val diff = view.bottom - (height + scrollY)
        checkDiff = diff <= 0
        mListener?.onBottomReached(checkDiff)
        mOnInteractiveScrollChangedCallback?.onScroll(t, oldt)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action ?: MotionEvent.ACTION_CANCEL) {
            MotionEvent.ACTION_DOWN -> {
                mListener?.onTouch(true)
                scrollJob?.cancel()
            }

            MotionEvent.ACTION_UP -> {
                startScrollerTask()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startScrollerTask() {
        initialPosition = scrollY
        if (scrollJob?.isActive != true) {
            scrollJob = CoroutineScope(Dispatchers.IO).launch {
                var run = true
                while (run) {
                    delay(100L)
                    val newPosition = scrollY
                    if (initialPosition - newPosition == 0) {
                        mListener?.onTouch(false)
                        run = false
                    } else {
                        initialPosition = scrollY
                    }
                }
            }
        }
    }
}