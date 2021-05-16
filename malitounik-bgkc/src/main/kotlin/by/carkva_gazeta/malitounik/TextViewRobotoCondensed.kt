package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat

class TextViewRobotoCondensed : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setTypeface(tf: Typeface?, style: Int) {
        typeface = createFont(context, style)
    }

    companion object {
        fun createFont(context: Context, style: Int): Typeface? {
            return when (style) {
                Typeface.BOLD -> ResourcesCompat.getFont(context, R.font.robotocondensedbold)
                Typeface.ITALIC -> ResourcesCompat.getFont(context, R.font.robotocondenseditalic)
                Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(context, R.font.robotocondensedbolditalic)
                else -> ResourcesCompat.getFont(context, R.font.robotocondensed)
            }
        }
    }
}