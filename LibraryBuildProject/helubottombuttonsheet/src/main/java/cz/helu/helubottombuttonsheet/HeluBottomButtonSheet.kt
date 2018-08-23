package cz.helu.helubottombuttonsheet


import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cz.helu.helubottombuttonsheet.entity.BaseSheetItem
import cz.helu.helubottombuttonsheet.entity.CustomViewSheetItem
import cz.helu.helubottombuttonsheet.entity.DividerSheetItem
import cz.helu.helubottombuttonsheet.entity.TextSheetItem
import cz.helu.helubottombuttonsheet.utility.DrawableUtility
import java.util.*


@Suppress("unused")
class HeluBottomButtonSheet : BottomSheetDialogFragment() {
	private var titleItemHeight: Int = 0
	private var itemHeight: Int = 0
	private var itemImageSize: Int = 0
	private var titleTextSize: Int = 0
	private var itemTextSize: Int = 0
	private var spacingHorizontal: Int = 0
	private var paddingVertical: Int = 0
	private var itemTouchFeedbackColor: Int = 0
	private var sheetBackgroundColor: Int = 0
	private var sheetTitleColor: Int = 0
	private var itemTextColor: Int = 0
	private var dividerColor: Int = 0
	private var title: String? = null
	private val itemList = ArrayList<BaseSheetItem>()
	private var contentLayout: LinearLayout? = null
	private var savedInstanceStateDone: Boolean = false


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val arguments = arguments
		if (arguments != null) {
			this.titleItemHeight = arguments.getInt(Builder.ARGUMENTS_TITLE_ITEM_HEIGHT)
			this.itemHeight = arguments.getInt(Builder.ARGUMENTS_ITEM_HEIGHT)
			this.itemImageSize = arguments.getInt(Builder.ARGUMENTS_ITEM_IMAGE_SIZE)
			this.titleTextSize = arguments.getInt(Builder.ARGUMENTS_TITLE_TEXT_SIZE)
			this.itemTextSize = arguments.getInt(Builder.ARGUMENTS_ITEM_TEXT_SIZE)
			this.spacingHorizontal = arguments.getInt(Builder.ARGUMENTS_SPACING_HORIZONTAL)
			this.paddingVertical = arguments.getInt(Builder.ARGUMENTS_PADDING_VERTICAL)
			this.itemTouchFeedbackColor = arguments.getInt(Builder.ARGUMENTS_ITEM_TOUCH_FEEDBACK_COLOR)
			this.sheetBackgroundColor = arguments.getInt(Builder.ARGUMENTS_SHEET_BACKGROUND_COLOR)
			this.sheetTitleColor = arguments.getInt(Builder.ARGUMENTS_SHEET_TITLE_COLOR)
			this.itemTextColor = arguments.getInt(Builder.ARGUMENTS_ITEM_TEXT_COLOR)
			this.dividerColor = arguments.getInt(Builder.ARGUMENTS_DIVIDER_COLOR)
			this.title = arguments.getString(Builder.ARGUMENTS_TITLE)
		}
	}


	override fun onResume() {
		super.onResume()
		savedInstanceStateDone = false
	}


	override fun onStart() {
		super.onStart()
		savedInstanceStateDone = false
	}


	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		savedInstanceStateDone = true
	}


	@SuppressLint("RestrictedApi")
	override fun setupDialog(dialog: Dialog, style: Int) {
		super.setupDialog(dialog, style)

		// Setup View
		contentLayout = LinearLayout(context)
		contentLayout?.orientation = LinearLayout.VERTICAL
		contentLayout?.setBackgroundColor(sheetBackgroundColor)

		inflateViews()

		dialog.setContentView(contentLayout)
	}


	fun show(manager: FragmentManager?) {
		if (!savedInstanceStateDone && manager != null)
			show(manager, tag)
	}


	fun addButton(button: TextSheetItem): TextSheetItem {
		itemList.add(button)
		return button
	}


	fun addButton(text: String, clickListener: View.OnClickListener): TextSheetItem {
		val item = TextSheetItem(text, clickListener)
		itemList.add(item)
		return item
	}


	fun addButton(drawable: Drawable, text: String, clickListener: View.OnClickListener): TextSheetItem {
		val item = TextSheetItem(drawable, text, clickListener)
		itemList.add(item)
		return item
	}


	fun addButton(drawableResource: Int, text: String, clickListener: View.OnClickListener): TextSheetItem {
		val item = TextSheetItem(drawableResource, text, clickListener)
		itemList.add(item)
		return item
	}


	fun addDivider(): DividerSheetItem {
		val item = DividerSheetItem()
		itemList.add(item)
		return item
	}


	fun addCustomView(customView: View): CustomViewSheetItem {
		val item = CustomViewSheetItem(customView)
		itemList.add(item)
		return item
	}


	fun addCustomView(customView: View, clickListener: View.OnClickListener): CustomViewSheetItem {
		val item = CustomViewSheetItem(customView, clickListener)
		itemList.add(item)
		return item
	}


	fun getItem(position: Int): BaseSheetItem {
		return itemList[position]
	}


	fun invalidate() {
		contentLayout?.removeAllViews()
		inflateViews()
	}


	private fun convertDpToPx(dp: Int): Int {
		return Math.round(dp * (context?.resources?.displayMetrics?.xdpi ?: 0f) / DisplayMetrics.DENSITY_DEFAULT)
	}


	private fun createTitleView(): View {
		val textViewParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleItemHeight)

		val textView = TextView(context)
		textView.text = title
		textView.setTextColor(sheetTitleColor)
		textView.layoutParams = textViewParams
		textView.gravity = Gravity.CENTER_VERTICAL
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
		textView.setPadding(spacingHorizontal, 0, spacingHorizontal, 0)

		return textView
	}


	private fun createDividerItemView(): View {
		val verticalContentPadding = convertDpToPx(Builder.DEFAULT_CONTENT_VERTICAL_SPACING)
		val dividerParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPx(1))
		dividerParams.setMargins(0, verticalContentPadding, 0, verticalContentPadding)

		val divider = View(context)
		divider.layoutParams = dividerParams
		divider.setBackgroundColor(dividerColor)
		return divider
	}


	private fun createTextItemView(entity: TextSheetItem): LinearLayout {
		val drawable = DrawableUtility.getAdaptiveRippleDrawable(sheetBackgroundColor, itemTouchFeedbackColor)
		val item = LinearLayout(context)
		val params = LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, itemHeight)

		item.layoutParams = params
		item.gravity = Gravity.CENTER_VERTICAL
		item.orientation = LinearLayout.HORIZONTAL
		item.setPadding(spacingHorizontal, 0, spacingHorizontal, 0)
		item.setOnClickListener(entity.clickListener)
		item.background = drawable

		if (entity.drawable != null || entity.drawableResource != -1) {
			val imageView = ImageView(context)
			val imageViewParams = LinearLayout.LayoutParams(itemImageSize, itemImageSize)
			imageViewParams.setMargins(0, 0, spacingHorizontal, 0)
			imageView.layoutParams = imageViewParams
			if (entity.drawable != null)
				imageView.setImageDrawable(entity.drawable)
			else
				imageView.setImageResource(entity.drawableResource)

			item.addView(imageView)
		}

		val textView = TextView(context)
		textView.text = entity.text
		textView.setTextColor(itemTextColor)
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize.toFloat())

		item.addView(textView)

		return item
	}


	private fun inflateViews() {
		if (title?.isNotEmpty() == true) {
			contentLayout?.setPadding(0, 0, 0, paddingVertical)
			contentLayout?.addView(createTitleView())
		} else {
			contentLayout?.setPadding(0, paddingVertical, 0, paddingVertical)
		}

		for (entity in itemList) {
			if (entity is DividerSheetItem) {
				contentLayout?.addView(createDividerItemView())
			} else if (entity is TextSheetItem) {
				contentLayout?.addView(createTextItemView(entity))
			} else if (entity is CustomViewSheetItem) {
				if (entity.customView.parent == null)
					contentLayout?.addView(entity.customView)
			}
		}
	}


	class Builder(private val context: Context) {
		companion object {
			internal const val ARGUMENTS_TITLE_ITEM_HEIGHT = "ARGUMENTS_TITLE_ITEM_HEIGHT"
			internal const val ARGUMENTS_ITEM_HEIGHT = "ARGUMENTS_ITEM_HEIGHT"
			internal const val ARGUMENTS_ITEM_IMAGE_SIZE = "ARGUMENTS_ITEM_IMAGE_SIZE"
			internal const val ARGUMENTS_TITLE_TEXT_SIZE = "ARGUMENTS_TITLE_TEXT_SIZE"
			internal const val ARGUMENTS_ITEM_TEXT_SIZE = "ARGUMENTS_ITEM_TEXT_SIZE"
			internal const val ARGUMENTS_SPACING_HORIZONTAL = "ARGUMENTS_SPACING_HORIZONTAL"
			internal const val ARGUMENTS_PADDING_VERTICAL = "ARGUMENTS_PADDING_VERTICAL"
			internal const val ARGUMENTS_ITEM_TOUCH_FEEDBACK_COLOR = "ARGUMENTS_ITEM_TOUCH_FEEDBACK_COLOR"
			internal const val ARGUMENTS_SHEET_BACKGROUND_COLOR = "ARGUMENTS_SHEET_BACKGROUND_COLOR"
			internal const val ARGUMENTS_SHEET_TITLE_COLOR = "ARGUMENTS_SHEET_TITLE_COLOR"
			internal const val ARGUMENTS_ITEM_TEXT_COLOR = "ARGUMENTS_ITEM_TEXT_COLOR"
			internal const val ARGUMENTS_DIVIDER_COLOR = "ARGUMENTS_DIVIDER_COLOR"
			internal const val ARGUMENTS_TITLE = "ARGUMENTS_TITLE"

			internal const val DEFAULT_TITLE_ITEM_HEIGHT = 56
			internal const val DEFAULT_ITEM_HEIGHT = 48
			internal const val DEFAULT_ITEM_IMAGE_SIZE = 24
			internal const val DEFAULT_TITLE_TEXT_SIZE = 14
			internal const val DEFAULT_ITEM_TEXT_SIZE = 14
			internal const val DEFAULT_HORIZONTAL_SPACING = 16
			internal const val DEFAULT_CONTENT_VERTICAL_SPACING = 8
		}

		private var titleItemHeight: Int = 0
		private var itemHeight: Int = 0
		private var itemImageSize: Int = 0
		private var titleTextSize: Int = 0
		private var itemTextSize: Int = 0
		private var spacingHorizontal: Int = 0
		private var paddingVertical: Int = 0
		private var title = ""
		@ColorInt
		private var itemTouchFeedbackColor = Color.LTGRAY
		@ColorInt
		private var sheetBackgroundColor = Color.parseColor("#FAFAFA")
		@ColorInt
		private var sheetTitleColor = Color.GRAY
		@ColorInt
		private var itemTextColor = Color.BLACK
		@ColorInt
		private var dividerColor = Color.LTGRAY


		init {

			titleItemHeight = convertDpToPx(DEFAULT_TITLE_ITEM_HEIGHT)
			itemHeight = convertDpToPx(DEFAULT_ITEM_HEIGHT)
			itemImageSize = convertDpToPx(DEFAULT_ITEM_IMAGE_SIZE)
			titleTextSize = convertDpToPx(DEFAULT_TITLE_TEXT_SIZE)
			itemTextSize = convertDpToPx(DEFAULT_ITEM_TEXT_SIZE)
			spacingHorizontal = convertDpToPx(DEFAULT_HORIZONTAL_SPACING)
			paddingVertical = convertDpToPx(DEFAULT_CONTENT_VERTICAL_SPACING)
		}


		fun withTitleRes(@StringRes titleResId: Int): Builder {
			this.title = context.resources.getString(titleResId)
			return this
		}


		fun withTitle(title: String): Builder {
			this.title = title
			return this
		}


		@SuppressLint("ResourceType")
		fun withTitleItemHeightRes(@DimenRes dimension: Int): Builder {
			this.titleItemHeight = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withTitleItemHeight(pixelSize: Int): Builder {
			this.titleItemHeight = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withItemHeightRes(@DimenRes dimension: Int): Builder {
			this.itemHeight = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withItemHeight(pixelSize: Int): Builder {
			this.itemHeight = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withImageSizeRes(@DimenRes dimension: Int): Builder {
			this.itemImageSize = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withImageSize(@DimenRes pixelSize: Int): Builder {
			this.itemImageSize = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withTitleTextSizeRes(@DimenRes dimension: Int): Builder {
			this.titleTextSize = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withTitleTextSize(@DimenRes pixelSize: Int): Builder {
			this.titleTextSize = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withItemTextSizeRes(@DimenRes dimension: Int): Builder {
			this.itemTextSize = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withItemTextSize(@DimenRes pixelSize: Int): Builder {
			this.itemTextSize = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withHorizontalSpacingRes(@DimenRes dimension: Int): Builder {
			this.spacingHorizontal = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withHorizontalSpacing(pixelSize: Int): Builder {
			this.spacingHorizontal = pixelSize
			return this
		}


		@SuppressLint("ResourceType")
		fun withVerticalPaddingRes(@DimenRes dimension: Int): Builder {
			this.paddingVertical = context.resources.getDimensionPixelSize(dimension)
			return this
		}


		@SuppressLint("ResourceType")
		fun withVerticalPadding(pixelSize: Int): Builder {
			this.paddingVertical = pixelSize
			return this
		}


		fun withBackgroundColorRes(@ColorRes color: Int): Builder {
			this.sheetBackgroundColor = ContextCompat.getColor(context, color)
			return this
		}


		fun withBackgroundColor(color: Int): Builder {
			this.sheetBackgroundColor = color
			return this
		}


		fun withItemTouchFeedbackColorRes(@ColorRes color: Int): Builder {
			this.itemTouchFeedbackColor = ContextCompat.getColor(context, color)
			return this
		}


		fun withItemTouchFeedbackColor(color: Int): Builder {
			this.itemTouchFeedbackColor = color
			return this
		}


		fun withTitleColorRes(@ColorRes color: Int): Builder {
			this.sheetTitleColor = ContextCompat.getColor(context, color)
			return this
		}


		fun withTitleColor(color: Int): Builder {
			this.sheetTitleColor = color
			return this
		}


		fun withItemTextColorRes(@ColorRes color: Int): Builder {
			this.itemTextColor = ContextCompat.getColor(context, color)
			return this
		}


		fun withItemTextColor(color: Int): Builder {
			this.itemTextColor = color
			return this
		}


		fun withDividerColorRes(@ColorRes color: Int): Builder {
			this.dividerColor = ContextCompat.getColor(context, color)
			return this
		}


		fun withDividerColor(color: Int): Builder {
			this.dividerColor = color
			return this
		}


		fun build(): HeluBottomButtonSheet {
			val fragment = HeluBottomButtonSheet()
			val arguments = Bundle()

			arguments.putInt(ARGUMENTS_TITLE_ITEM_HEIGHT, this.titleItemHeight)
			arguments.putInt(ARGUMENTS_ITEM_HEIGHT, this.itemHeight)
			arguments.putInt(ARGUMENTS_ITEM_IMAGE_SIZE, this.itemImageSize)
			arguments.putInt(ARGUMENTS_TITLE_TEXT_SIZE, this.titleTextSize)
			arguments.putInt(ARGUMENTS_ITEM_TEXT_SIZE, this.itemTextSize)
			arguments.putInt(ARGUMENTS_SPACING_HORIZONTAL, this.spacingHorizontal)
			arguments.putInt(ARGUMENTS_PADDING_VERTICAL, this.paddingVertical)
			arguments.putInt(ARGUMENTS_ITEM_TOUCH_FEEDBACK_COLOR, this.itemTouchFeedbackColor)
			arguments.putInt(ARGUMENTS_SHEET_BACKGROUND_COLOR, this.sheetBackgroundColor)
			arguments.putInt(ARGUMENTS_SHEET_TITLE_COLOR, this.sheetTitleColor)
			arguments.putInt(ARGUMENTS_ITEM_TEXT_COLOR, this.itemTextColor)
			arguments.putInt(ARGUMENTS_DIVIDER_COLOR, this.dividerColor)
			arguments.putString(ARGUMENTS_TITLE, this.title)

			fragment.arguments = arguments
			return fragment
		}


		private fun convertDpToPx(dp: Int): Int {
			return Math.round(dp * context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
		}
	}
}
