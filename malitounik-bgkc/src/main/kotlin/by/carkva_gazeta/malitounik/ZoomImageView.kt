package by.carkva_gazeta.malitounik

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView : AppCompatImageView, View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private lateinit var mScaleDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetector
    private var mMatrix = Matrix()
    private var mMatrixValues = FloatArray(9)
    private var mode = NONE
    private var mSaveScale = 1f
    private var mMinScale = 1f
    private var mMaxScale = 4f
    private var origWidth = 0f
    private var origHeight = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var mLast = PointF()
    private var mStart = PointF()

    constructor(context: Context) : super(context) {
        sharedConstructing(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        sharedConstructing(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        sharedConstructing(context)
    }

    private fun sharedConstructing(context: Context) {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX
        mGestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val prevScale = mSaveScale
            mSaveScale *= mScaleFactor
            if (mSaveScale > mMaxScale) {
                mSaveScale = mMaxScale
                mScaleFactor = mMaxScale / prevScale
            } else if (mSaveScale < mMinScale) {
                mSaveScale = mMinScale
                mScaleFactor = mMinScale / prevScale
            }
            if (origWidth * mSaveScale <= viewWidth || origHeight * mSaveScale <= viewHeight) {
                mMatrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2.toFloat(), viewHeight / 2.toFloat())
            } else {
                mMatrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
            }
            fixTranslation()
            return true
        }
    }

    private fun fitToScreen() {
        mSaveScale = 1f
        val scale: Float
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) return
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        val scaleX = viewWidth.toFloat() / imageWidth.toFloat()
        val scaleY = viewHeight.toFloat() / imageHeight.toFloat()
        scale = scaleX.coerceAtMost(scaleY)
        mMatrix.setScale(scale, scale)
        var redundantYSpace = (viewHeight.toFloat() - scale * imageHeight.toFloat())
        var redundantXSpace = (viewWidth.toFloat() - scale * imageWidth.toFloat())
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        mMatrix.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = viewWidth - 2 * redundantXSpace
        origHeight = viewHeight - 2 * redundantYSpace
        imageMatrix = mMatrix
    }

    private fun fixTranslation() {
        mMatrix.getValues(mMatrixValues)
        val transX = mMatrixValues[Matrix.MTRANS_X]
        val transY = mMatrixValues[Matrix.MTRANS_Y]
        val fixTransX = getFixTranslation(transX, viewWidth.toFloat(), origWidth * mSaveScale)
        val fixTransY = getFixTranslation(transY, viewHeight.toFloat(), origHeight * mSaveScale)
        if (fixTransX != 0f || fixTransY != 0f) mMatrix.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) {
            return -trans + minTrans
        }
        if (trans > maxTrans) {
            return -trans + maxTrans
        }
        return 0F
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0F
        } else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        fitToScreen()
    }

    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        mGestureDetector.onTouchEvent(event)
        val currentPoint = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLast.set(currentPoint)
                mStart.set(mLast)
                mode = DRAG
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val dx = currentPoint.x - mLast.x
                val dy = currentPoint.y - mLast.y
                val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), origWidth * mSaveScale)
                val fixTransY = getFixDragTrans(dy, viewHeight.toFloat(), origHeight * mSaveScale)
                mMatrix.postTranslate(fixTransX, fixTransY)
                fixTranslation()
                mLast[currentPoint.x] = currentPoint.y
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        imageMatrix = mMatrix
        return false
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        fitToScreen()
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return false
    }

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}