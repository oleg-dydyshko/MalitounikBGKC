package by.carkva_gazeta.malitounik

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.SwitchCompat

class SwitchCompatCustom : SwitchCompat {
    constructor(context: Context) : super(context) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFontInterface(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setFontInterface(context)
    }

    private fun setFontInterface(context: Context) {
        val sp = (context as? BaseActivity)?.setFontInterface(textSize) ?: SettingsActivity.GET_FONT_SIZE_DEFAULT
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    }
}