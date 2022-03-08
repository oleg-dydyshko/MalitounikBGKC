package by.carkva_gazeta.resources

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView
import kotlinx.coroutines.*

class LinkMovementMethodCheck : LinkMovementMethod() {

    private var initialPosition = 0
    private var scrollJob: Job? = null
    private var scrollY = 0
    private var mListener: LinkMovementMethodCheckListener? = null

    interface LinkMovementMethodCheckListener {
        fun linkMovementMethodCheckOnTouch(onTouch: Boolean)
    }

    fun setLinkMovementMethodCheckListener(listener: LinkMovementMethodCheckListener) {
        mListener = listener
    }

    fun getScrollY(scrollY: Int) {
        this.scrollY = scrollY
    }

    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        when (event?.action ?: MotionEvent.ACTION_CANCEL) {
            MotionEvent.ACTION_DOWN -> {
                mListener?.linkMovementMethodCheckOnTouch(true)
                scrollJob?.cancel()
            }
            MotionEvent.ACTION_UP -> {
                startScrollerTask()
            }
        }
        return super.onTouchEvent(widget, buffer, event)
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
                        mListener?.linkMovementMethodCheckOnTouch(false)
                        run = false
                    } else {
                        initialPosition = scrollY
                    }
                }
            }
        }
    }
}