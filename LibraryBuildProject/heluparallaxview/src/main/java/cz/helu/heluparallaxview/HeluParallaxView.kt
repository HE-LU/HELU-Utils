package cz.helu.heluparallaxview

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView

/**
 * This class is usable only on API >= 16
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class HeluParallaxView : android.support.v7.widget.AppCompatImageView {
    companion object {
        const val DEFAULT_SCALE = 1.3f
        const val REVERSE_NONE = 1
        const val REVERSE_X = 2
        const val REVERSE_Y = 3
        const val REVERSE_BOTH = 4
    }

    var scale = DEFAULT_SCALE
    var isReverseX = false
    var isReverseY = false
    var isBlockParallaxX = false
    var isBlockParallaxY = false
    private var normalize = true
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var matrixScaleToFit = 1f
    private var matrixTranslateX = 0f
    private var matrixTranslateY = 0f
    private var scrollSpaceX = 0f
    private var scrollSpaceY = 0f
    private var widthImageView = -1f
    private var heightImageView = -1f
    private var interpolator: Interpolator = LinearInterpolator()
    private var onDrawListener: ViewTreeObserver.OnDrawListener? = null

    constructor(context: Context) : super(context) {
        initSizeScreen()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        if (!isInEditMode)
            checkAttributes(attrs)

        initSizeScreen()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        if (!isInEditMode)
            checkAttributes(attrs)

        initSizeScreen()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Remove old listener before trying to add a new one.
        // Looks like onAttachedToWindow can get called multiple times. This could lead to memmory leaks.
        viewTreeObserver.removeOnDrawListener(onDrawListener)

        onDrawListener = ViewTreeObserver.OnDrawListener { applyParallax() }
        viewTreeObserver.addOnDrawListener(onDrawListener)

        scaleType = ImageView.ScaleType.CENTER
        applyMatrix()

        applyParallax()
    }

    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnDrawListener(onDrawListener)

        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (drawable == null)
            return

        widthImageView = measuredWidth.toFloat()
        heightImageView = measuredHeight.toFloat()

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        val drawableNewWidth: Float
        val drawableNewHeight: Float

        if (drawableWidth * heightImageView > drawableHeight * widthImageView) {
            val scale = heightImageView / drawableHeight.toFloat()
            drawableNewWidth = drawableWidth * scale
            drawableNewHeight = heightImageView

            matrixScaleToFit = scale
            matrixTranslateX = (drawableNewWidth * this.scale - widthImageView) / 2 * -1
            matrixTranslateY = heightImageView * (this.scale - 1) / 2 * -1
        } else {
            val scale = widthImageView / drawableWidth.toFloat()
            drawableNewWidth = widthImageView
            drawableNewHeight = drawableHeight * scale

            matrixScaleToFit = scale
            matrixTranslateX = widthImageView * (this.scale - 1) / 2 * -1
            matrixTranslateY = (drawableNewHeight * this.scale - heightImageView) / 2 * -1 // OK
        }

        // 0 = Not been initialized yet!
        if (scrollSpaceX == 0f)
            scrollSpaceX = drawableNewWidth * scale - widthImageView

        // 0 = Not been initialized yet!
        if (scrollSpaceY == 0f)
            scrollSpaceY = drawableNewHeight * scale - heightImageView

        if (normalize) {
            if (scrollSpaceX < scrollSpaceY)
                scrollSpaceY = scrollSpaceX
            else
                scrollSpaceX = scrollSpaceY
        }

        onAttachedToWindow()
    }

    fun applyColorFilter(brightness: Int, contrast: Float, alpha: Float) {
        val cm = ColorMatrix(floatArrayOf(contrast, 0f, 0f, 0f, brightness.toFloat(), 0f, contrast, 0f, 0f, brightness.toFloat(), 0f, 0f, contrast, 0f, brightness.toFloat(), 0f, 0f, 0f, 1f, 0f))

        colorFilter = ColorMatrixColorFilter(cm)
        setAlpha(alpha)
    }

    fun setInterpolator(interpol: Interpolator) {
        interpolator = interpol
    }

    fun resetParallax() {
        setMyScrollX(0)
        setMyScrollY(0)
        scrollSpaceX = 0f
        scrollSpaceY = 0f
        requestLayout()
    }

    fun disableParallax() {
        isBlockParallaxX = true
        isBlockParallaxY = true
        setMyScrollX(0)
        setMyScrollY(0)
    }

    fun enableParallax() {
        isBlockParallaxX = false
        isBlockParallaxY = false
        resetParallax()
    }

    protected fun applyMatrix() {
        if (scrollSpaceX == 0f && scrollSpaceY == 0f)
            return

        scaleType = ImageView.ScaleType.MATRIX

        val imageMatrix = Matrix(imageMatrix)
        imageMatrix.setScale(matrixScaleToFit * scale, matrixScaleToFit * scale)
        imageMatrix.postTranslate(matrixTranslateX, matrixTranslateY)
        setImageMatrix(imageMatrix)
    }

    protected fun applyParallax() {
        val location = IntArray(2)
        getLocationOnScreen(location)

        if (scrollSpaceY != 0f && !isBlockParallaxY) {
            val locationUsableY = location[1] + heightImageView / 2
            val scrollDeltaY = locationUsableY / screenHeight
            val interpolatedScrollDeltaY = interpolator.getInterpolation(scrollDeltaY)

            if (isReverseY)
                setMyScrollY((Math.min(Math.max(0.5f - interpolatedScrollDeltaY, -0.5f), 0.5f) * -scrollSpaceY).toInt())
            else
                setMyScrollY((Math.min(Math.max(0.5f - interpolatedScrollDeltaY, -0.5f), 0.5f) * scrollSpaceY).toInt())
        }

        if (scrollSpaceX != 0f && !isBlockParallaxX) {
            val locationUsableX = location[0] + widthImageView / 2
            val scrollDeltaX = locationUsableX / screenWidth
            val interpolatedScrollDeltaX = interpolator.getInterpolation(scrollDeltaX)

            if (isReverseX)
                setMyScrollX((Math.min(Math.max(0.5f - interpolatedScrollDeltaX, -0.5f), 0.5f) * -scrollSpaceX).toInt())
            else
                setMyScrollX((Math.min(Math.max(0.5f - interpolatedScrollDeltaX, -0.5f), 0.5f) * scrollSpaceX).toInt())
        }
    }

    private fun checkAttributes(attrs: AttributeSet) {
        val arr = context.obtainStyledAttributes(attrs, cz.helu.heluparallaxview.R.styleable.HeluParallaxView)
        val reverse = arr.getInt(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_reverse, 1)

        scale = arr.getFloat(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_scale, DEFAULT_SCALE)
        isBlockParallaxX = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_blockParallaxX, false)
        isBlockParallaxY = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_blockParallaxY, false)
        normalize = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_normalize, true)
        interpolator = InterpolatorSelector.interpolatorId(arr.getInt(cz.helu.heluparallaxview.R.styleable.HeluParallaxView_interpolation, 0))

        when (reverse) {
            REVERSE_NONE -> {
                isReverseX = false
                isReverseY = false
            }
            REVERSE_X -> {
                isReverseX = true
                isReverseY = false
            }
            REVERSE_Y -> {
                isReverseX = false
                isReverseY = true
            }
            REVERSE_BOTH -> {
                isReverseX = true
                isReverseY = true
            }
        }

        arr.recycle()
    }

    private fun initSizeScreen() {
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
    }

    private fun setMyScrollX(value: Int) {
        scrollX = value
    }

    private fun setMyScrollY(value: Int) {
        scrollY = value
    }

    private object InterpolatorSelector {
        const val LINEAR = 0
        const val ACCELERATE_DECELERATE = 1
        const val ACCELERATE = 2
        const val ANTICIPATE = 3
        const val ANTICIPATE_OVERSHOOT = 4
        const val BOUNCE = 5
        const val DECELERATE = 6
        const val OVERSHOOT = 7

        fun interpolatorId(interpolationId: Int): Interpolator {
            return when (interpolationId) {
                LINEAR -> LinearInterpolator()
                ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
                ACCELERATE -> AccelerateInterpolator()
                ANTICIPATE -> AnticipateInterpolator()
                ANTICIPATE_OVERSHOOT -> AnticipateOvershootInterpolator()
                BOUNCE -> BounceInterpolator()
                DECELERATE -> DecelerateInterpolator()
                OVERSHOOT -> OvershootInterpolator()
                else -> LinearInterpolator()
            } //TODO: this interpolations needs parameters
            //case CYCLE:
            //    return new CycleInterpolator();
            //case PATH:
            //    return new PathInterpolator();
        }
    }

}

