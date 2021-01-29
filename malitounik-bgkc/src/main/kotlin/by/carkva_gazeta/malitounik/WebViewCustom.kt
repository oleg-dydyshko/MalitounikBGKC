package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView

class WebViewCustom : WebView {
    private var mOnScrollChangedCallback: OnScrollChangedCallback? = null
    private var mListener: OnBottomListener? = null

    constructor(context: Context) : super(getFixedContext(context))
    constructor(context: Context, attrs: AttributeSet?) : super(getFixedContext(context), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(getFixedContext(context), attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val diff = contentHeight - ((height + scrollY) / resources.displayMetrics.density).toInt()
        mListener?.onScrollDiff(diff)
        if (diff == 0) {
            mListener?.onBottom()
        }
        super.onScrollChanged(l, t, oldl, oldt)
        mOnScrollChangedCallback?.onScroll(t)
    }

    fun setOnScrollChangedCallback(onScrollChangedCallback: OnScrollChangedCallback?) {
        mOnScrollChangedCallback = onScrollChangedCallback
    }

    fun setOnBottomListener(onBottomListener: OnBottomListener?) {
        mListener = onBottomListener
    }

    interface OnScrollChangedCallback {
        fun onScroll(t: Int)
    }

    interface OnBottomListener {
        fun onBottom()
        fun onScrollDiff(diff: Int)
    }

    companion object {
        @SuppressLint("NewApi")
        private fun getFixedContext(context: Context): Context {
            return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                context.createConfigurationContext(Configuration())
            } else {
                context
            }
        }
    }
}