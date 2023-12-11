package by.carkva_gazeta.malitounik

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan


class CustomTypefaceSpan(family: String?, type: Typeface?) : TypefaceSpan(family) {
    private var newType: Typeface? = type

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
        val old = paint.typeface
        val oldStyle = old?.style ?: Typeface.NORMAL
        val fake = oldStyle and (tf?.style?.inv() ?: Typeface.NORMAL)
        if (fake and Typeface.BOLD != Typeface.NORMAL) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != Typeface.NORMAL) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = tf
    }
}