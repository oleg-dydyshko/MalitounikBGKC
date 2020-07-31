package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by oleg on 23.12.17
 */
class TextViewRobotoCondensed : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setTypeface(tf: Typeface?, style: Int) {
        typeface = createFont(style)
    }

    companion object {
        fun createFont(style: Int): Typeface {
            return when (style) {
                Typeface.BOLD -> Typeface.create("sans-serif-condensed", Typeface.BOLD)
                Typeface.ITALIC -> Typeface.create("sans-serif-condensed", Typeface.ITALIC)
                Typeface.BOLD_ITALIC -> Typeface.create("sans-serif-condensed", Typeface.BOLD_ITALIC)
                else -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            }
        }
    }
}