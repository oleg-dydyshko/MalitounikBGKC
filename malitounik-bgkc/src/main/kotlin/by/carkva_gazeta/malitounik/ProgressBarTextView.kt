package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ProgressBarTextView : AppCompatTextView {
    private var mMaxValue = 100

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setTypeface(tf: Typeface?, style: Int) {
        typeface = TextViewRobotoCondensed.createFont(context, style)
    }

    @Synchronized
    fun setValue(value: Int) {
        val background = this.background as LayerDrawable
        val barValue = background.getDrawable(1) as ClipDrawable
        val newClipLevel = (value * 10000 / mMaxValue)
        barValue.level = newClipLevel
        drawableStateChanged()
    }
}