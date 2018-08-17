package cz.helu.helubottombuttonsheet.entity

import android.graphics.drawable.Drawable
import android.view.View


open class BaseSheetItem


class DividerSheetItem : BaseSheetItem()


class TextSheetItem : BaseSheetItem {
	var drawable: Drawable? = null
	var drawableResource = -1
	var text: String
	var clickListener: View.OnClickListener


	constructor(drawable: Drawable, text: String, clickListener: View.OnClickListener) {
		this.drawable = drawable
		this.text = text
		this.clickListener = clickListener
	}


	constructor(drawableResource: Int, text: String, clickListener: View.OnClickListener) {
		this.drawableResource = drawableResource
		this.text = text
		this.clickListener = clickListener
	}


	constructor(text: String, clickListener: View.OnClickListener) {
		this.text = text
		this.clickListener = clickListener
	}
}


class CustomViewSheetItem : BaseSheetItem {
	var customView: View


	constructor(customView: View) {
		this.customView = customView
	}


	constructor(customView: View, clickListener: View.OnClickListener) {
		this.customView = customView
		customView.setOnClickListener(clickListener)
	}
}