package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EditTextRobotoCondensed : AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setTypeface(tf: Typeface?, style: Int) {
        typeface = TextViewRobotoCondensed.createFont(style)
    }
}