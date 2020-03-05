package cz.helu.helucollapsingtabbar

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cz.helu.helucollapsingtabbar.entity.HeluCollapsingTabBarButton
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class HeluCollapsingTabBar : LinearLayout {
    private var buttonsList: List<HeluCollapsingTabBarButton> = ArrayList()
    private var buttonSize = 0
    private var buttonPadding = 0
    private var buttonSpacing = 0
    private var bgDrawable: Drawable? = null
    var isCollapsed = false
        private set
    private var selectedPosition = 0

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(builder: Builder) : super(builder.context) {
        initFromBuilder(builder)
    }

    fun initFromBuilder(builder: Builder) {
        this.bgDrawable = builder.background
        this.buttonsList = builder.buttonsList
        this.buttonSize = builder.buttonSize
        this.buttonPadding = builder.buttonPadding
        this.buttonSpacing = builder.buttonSpacing

        initView()
    }

    fun setSelectedPosition(position: Int) {
        if (position >= childCount)
            return

        selectedPosition = position

        collapseView()
    }

    fun getSelectedPosition(): Int = selectedPosition

    fun collapseView() {
        if (getChildAt(selectedPosition) !is ImageView)
            return

        val view = getChildAt(selectedPosition) as ImageView

        for (i in 0 until childCount) {
            if (getChildAt(i) !is ImageView)
                continue

            val child = getChildAt(i) as ImageView
            if (child != view) {
                child.visibility = View.GONE
            } else {
                child.setImageDrawable(buttonsList[i].icon)
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.gravity = Gravity.CENTER
                clearViewMargins(params, view)
                selectedPosition = i
            }
        }
        isCollapsed = true
    }

    fun expandView() {
        if (getChildAt(selectedPosition) !is ImageView)
            return

        val view = getChildAt(selectedPosition) as ImageView
        val params = view.layoutParams as LinearLayout.LayoutParams
        params.gravity = Gravity.CENTER
        setupViewMargins(params, view)

        for (i in 0 until childCount) {
            if (getChildAt(i) !is ImageView)
                continue

            val child = getChildAt(i) as ImageView
            child.visibility = View.VISIBLE

            if (i != selectedPosition)
                child.setImageDrawable(buttonsList[i].inactiveIcon)
        }

        isCollapsed = false
    }

    private fun initView() {
        // Set Parent layout background drawable
        background = bgDrawable
        layoutTransition = LayoutTransition()

        // Setup all buttons view and add them into layout
        for (button in buttonsList) {
            val buttonImageView = ImageView(context)

            val params = LinearLayout.LayoutParams(buttonSize, buttonSize)
            params.gravity = Gravity.CENTER
            setupViewMargins(params, buttonImageView)

            buttonImageView.layoutParams = params
            buttonImageView.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding)
            buttonImageView.setImageDrawable(button.icon)
            buttonImageView.setOnClickListener { view ->
                selectedPosition = indexOfChild(view)
                onButtonClicked()
            }

            addView(buttonImageView)
        }
    }

    private fun onButtonClicked() {
        if (isCollapsed) {
            expandView()
        } else {
            val view = getChildAt(selectedPosition) as ImageView
            buttonsList[selectedPosition].onClickListener?.onClick(view)
            collapseView()
        }
    }

    private fun setupViewMargins(params: LinearLayout.LayoutParams, view: ImageView) {
        // Set margin for view, depending if the layout is vertical or horizontal
        if (orientation == LinearLayout.HORIZONTAL)
            params.setMargins(buttonSpacing, 0, buttonSpacing, 0)
        else
            params.setMargins(0, buttonSpacing, 0, buttonSpacing)

        view.layoutParams = params
    }

    private fun clearViewMargins(params: LinearLayout.LayoutParams, view: ImageView) {
        params.setMargins(0, 0, 0, 0)
        view.layoutParams = params
    }

    class Builder(val context: Context) {
        companion object {
            private const val DEFAULT_BUTTON_SIZE = 48
            private const val DEFAULT_BUTTON_PADDING = 12
            private const val DEFAULT_BUTTON_SPACING = 16
        }

        val buttonsList = ArrayList<HeluCollapsingTabBarButton>()
        var buttonSize = 0
        var buttonPadding = 0
        var buttonSpacing = 0
        var background: Drawable? = null

        init {
            buttonSize = convertDpToPx(DEFAULT_BUTTON_SIZE)
            buttonPadding = convertDpToPx(DEFAULT_BUTTON_PADDING)
            buttonSpacing = convertDpToPx(DEFAULT_BUTTON_SPACING)
            background = ContextCompat.getDrawable(context, R.drawable.shape_collapsing_tab_bar)
        }

        fun withBackground(background: Drawable): Builder {
            this.background = background
            return this
        }

        fun withButtonSize(dimension: Int): Builder {
            this.buttonSize = context.resources.getDimensionPixelSize(dimension)
            return this
        }

        fun withButtonPadding(dimension: Int): Builder {
            this.buttonPadding = context.resources.getDimensionPixelSize(dimension)
            return this
        }

        fun withButtonSpacing(dimension: Int): Builder {
            this.buttonSpacing = context.resources.getDimensionPixelSize(dimension)
            return this
        }

        fun withButtonSizePx(dimension: Int): Builder {
            this.buttonSize = dimension
            return this
        }

        fun withButtonPaddingPx(dimension: Int): Builder {
            this.buttonPadding = dimension
            return this
        }

        fun withButtonSpacingPx(dimension: Int): Builder {
            this.buttonSpacing = dimension
            return this
        }

        fun addButton(button: HeluCollapsingTabBarButton): Builder {
            this.buttonsList.add(button)
            return this
        }

        fun addButton(activeIcon: Drawable, clickListener: View.OnClickListener): Builder {
            val button = HeluCollapsingTabBarButton()
            button.icon = activeIcon
            button.inactiveIcon = activeIcon
            button.onClickListener = clickListener
            this.buttonsList.add(button)
            return this
        }

        fun addButton(activeIcon: Drawable, inactiveIcon: Drawable, clickListener: View.OnClickListener): Builder {
            val button = HeluCollapsingTabBarButton()
            button.icon = activeIcon
            button.inactiveIcon = inactiveIcon
            button.onClickListener = clickListener
            this.buttonsList.add(button)
            return this
        }

        fun build(): HeluCollapsingTabBar {
            return HeluCollapsingTabBar(this)
        }

        internal fun convertDpToPx(dp: Int): Int {
            return Math.round(dp * context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}