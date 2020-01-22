package by.carkva_gazeta.resources

import android.animation.ObjectAnimator
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {
    private var onLinkListenner: OnLinkListenner? = null
    fun setOnLinkListenner(onLinkListenner: OnLinkListenner?) {
        this.onLinkListenner = onLinkListenner
    }

    interface OnLinkListenner {
        fun onActivityStart()
        fun onDialogStart(message: String?)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.contains("https://m.carkva-gazeta.by/index.php?toUp=1")) {
            val anim = ObjectAnimator.ofInt(view, "scrollY", view.scrollY, 0)
            anim.setDuration(1500).start()
        }
        if (url.contains("https://m.carkva-gazeta.by/index.php?Alert=")) {
            val t1 = url.lastIndexOf("=")
            val message = url.substring(t1 + 1)
            onLinkListenner?.onDialogStart(message)
        }
        if (url.contains("https://m.carkva-gazeta.by/index.php?Activity=1")) {
            onLinkListenner?.onActivityStart()
        }
        return true
    }
}