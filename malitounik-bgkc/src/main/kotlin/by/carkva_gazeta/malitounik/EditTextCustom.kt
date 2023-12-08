package by.carkva_gazeta.malitounik

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

class EditTextCustom : AppCompatEditText {
    constructor(context: Context) : super(context) {
        focusAndShowKeyboard(this)
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        focusAndShowKeyboard(this)
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        focusAndShowKeyboard(this)
        setFontInterface(context)
    }

    private fun setFontInterface(context: Context) {
        val sp = (context as BaseActivity).setFontInterface(textSize)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
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