package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

class EditTextCustom : AppCompatEditText {
    constructor(context: Context) : super(context) {
        focusAndShowKeyboard(this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        focusAndShowKeyboard(this)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        focusAndShowKeyboard(this)
    }

    companion object {
        fun focusAndShowKeyboard(view: View) {
            fun showTheKeyboardNow() {
                if (view.isFocused) {
                    view.post {
                        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(view, 0)
                    }
                }
            }
            if (view.hasWindowFocus()) {
                showTheKeyboardNow()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    view.viewTreeObserver.addOnWindowFocusChangeListener(object : ViewTreeObserver.OnWindowFocusChangeListener {
                        override fun onWindowFocusChanged(hasFocus: Boolean) {
                            if (hasFocus) {
                                showTheKeyboardNow()
                                view.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                            }
                        }
                    })
                }
            }
        }
    }
}