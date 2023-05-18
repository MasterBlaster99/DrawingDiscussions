package com.example.drawingdiscussions.customui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class ZoomClass : AppCompatImageView, View.OnTouchListener,
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    // properties of Markers
    private var markerPoints: MutableList<PointF>? = null
    private var markerInfo: HashMap<Int, String>? = null
    private var onClickListener: OnMarkerAddedListener? = null
    private var onMarkerClickedListener: OnMarkerClickListener? = null
    private var mContext: Context? = null
    private var firstTouch = false
    private var time: Long? = null
    var isImageAdded = false

    // properties for Zooming
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var mMatrix: Matrix? = null
    private var mMatrixValues: FloatArray? = null
    private var mode = NONE

    // Scales
    private var mSaveScale = 1f
    private var mMinScale = 1f
    private var mMaxScale = 4f

    // view dimensions
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

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    companion object {
        // Image States
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }


    private fun sharedConstructing(context: Context) {
        super.setClickable(true)
        markerPoints = ArrayList()
        markerInfo = HashMap()
        mContext = context
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mMatrix = Matrix()
        mMatrixValues = FloatArray(9)
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX
        mGestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    // Methods for Handling Zoom
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
            if (origWidth * mSaveScale <= viewWidth
                || origHeight * mSaveScale <= viewHeight
            ) {
                mMatrix!!.postScale(
                    mScaleFactor, mScaleFactor, viewWidth / 2.toFloat(),
                    viewHeight / 2.toFloat()
                )
            } else {
                mMatrix!!.postScale(
                    mScaleFactor, mScaleFactor,
                    detector.focusX, detector.focusY
                )
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
        mMatrix!!.setScale(scale, scale)

        // Center the image
        var redundantYSpace = (viewHeight.toFloat()
                - scale * imageHeight.toFloat())
        var redundantXSpace = (viewWidth.toFloat()
                - scale * imageWidth.toFloat())
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        mMatrix!!.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = viewWidth - 2 * redundantXSpace
        origHeight = viewHeight - 2 * redundantYSpace
        imageMatrix = mMatrix
    }

    fun fixTranslation() {
        mMatrix!!.getValues(mMatrixValues) //put matrix values into a float array so we can analyze
        val transX =
            mMatrixValues!![Matrix.MTRANS_X] //get the most recent translation in x direction
        val transY =
            mMatrixValues!![Matrix.MTRANS_Y] //get the most recent translation in y direction
        val fixTransX = getFixTranslation(transX, viewWidth.toFloat(), origWidth * mSaveScale)
        val fixTransY = getFixTranslation(transY, viewHeight.toFloat(), origHeight * mSaveScale)
        if (fixTransX != 0f || fixTransY != 0f) mMatrix!!.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) { // case: NOT ZOOMED
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else { //CASE: ZOOMED
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) { // negative x or y translation (down or to the right)
            return -trans + minTrans
        }
        if (trans > maxTrans) { // positive x or y translation (up or to the left)
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
        if (mSaveScale == 1f) {

            // Fit to screen.
            fitToScreen()
        }
    }

    //  GestureListeners
    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        if (mode != NONE)
            fitToScreen()
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return false
    }

    // Methods for Handling Markers
    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (point in markerPoints!!) {
            val marker = BitmapFactory.decodeResource(
                resources,
                com.example.drawingdiscussions.R.drawable.iv_marker_blue
            )
            val bitmap = Bitmap.createScaledBitmap(marker, 120, 120, false)
            canvas.drawBitmap(bitmap, point.x - 40, point.y - 40, null)

        }
    }

    interface OnMarkerAddedListener {
        fun onMarkerAdded(pointF: PointF)
    }

    fun setOnMarkerAddedListener(listener: OnMarkerAddedListener) {
        onClickListener = listener
    }

    interface OnMarkerClickListener {
        fun onMarkerClicked(i: Int)
    }

    fun setOnMarkerClickListener(listener: OnMarkerClickListener) {
        onMarkerClickedListener = listener
    }

    fun addMarkerPoint(point: PointF) {
        markerPoints?.add(point)
        invalidate() // Redraw the markers
    }

    // Major Operations of Markers and Zoom
    override fun onTouch(view: View?, mMouseEvent: MotionEvent?): Boolean {
        mScaleDetector!!.onTouchEvent(mMouseEvent!!)
        mGestureDetector!!.onTouchEvent(mMouseEvent)
        val clickedMarker = findClickedMarker(mMouseEvent!!.x, mMouseEvent.y)
        if (clickedMarker != null && mMouseEvent!!.action == MotionEvent.ACTION_DOWN && !isImageAdded) {
            onMarkerClickedListener!!.onMarkerClicked(clickedMarker)
            invalidate()
            return false // Return true to consume the touch event and prevent further handling
        }
        if (mMouseEvent!!.action == MotionEvent.ACTION_DOWN && mMouseEvent.action != MotionEvent.ACTION_MOVE) {
            if (firstTouch && (System.currentTimeMillis() - time!!) <= 300 && isImageAdded) {
                firstTouch = false
                val newPoint = PointF(mMouseEvent.x, mMouseEvent.y)

                markerPoints!!.add(newPoint)
                // save the info related to the marker
                if (onClickListener != null)
                    onClickListener!!.onMarkerAdded(newPoint)
                // redraw the markers
                invalidate()
            } else {
                firstTouch = true
                time = System.currentTimeMillis();
                return super.onTouchEvent(mMouseEvent);
                //return false;Use this if you dont want to call default onTouchEvent()
            }
        }
//         Zoom
        if (mMouseEvent != null) {
            val currentPoint = PointF(mMouseEvent.x, mMouseEvent.y)
            when (mMouseEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLast.set(currentPoint)
                    mStart.set(mLast)
                    mode = DRAG
                }
                MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                    val dx = currentPoint.x - mLast.x
                    val dy = currentPoint.y - mLast.y
                    val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), origWidth * mSaveScale)
                    val fixTransY =
                        getFixDragTrans(dy, viewHeight.toFloat(), origHeight * mSaveScale)
                    mMatrix!!.postTranslate(fixTransX, fixTransY)
                    fixTranslation()
                    mLast[currentPoint.x] = currentPoint.y
                }
                MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }
            imageMatrix = mMatrix
            return false
        }
        return super.onTouchEvent(mMouseEvent)
    }

    private fun findClickedMarker(x: Float, y: Float): Int? {
        for (i in 0 until markerPoints!!.size) {
            val point = markerPoints!![i]
            val markerRect = RectF(point.x, point.y, point.x + 60, point.y + 60)
            if (markerRect.contains(x, y)) {
                return i // Return the clicked marker
            }
        }
        return null // No marker clicked
    }

}