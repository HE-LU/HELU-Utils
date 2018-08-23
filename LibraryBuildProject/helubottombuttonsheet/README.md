# HeluBottomButtonSheet 2.1.0
Create Bottom Sheet acording to Google Material guidelines simply: https://material.google.com/components/bottom-sheets.html

![Alt text](./extras/HeluBottomButtonSheet.png?raw=true "HeluBottomButtonSheet")


## Gradle:
```groovy
implementation 'cz.helu.android:helubottombuttonsheet:2.1.0'
```


## Methods


### Builder:

* **withTitle(text: String)** or **withTitleRes(res: Int)**
  Set **string** or **string resource** as a title text to the Bottom sheet. 
  *Note: Sheet wont contain any title if the title is not set.*
  
* **withTitleColor(color: Int)** or **withTitleColorRes(res: Int)**
  Set **color** or **color resource** to change default title color. 
  *(Default is ```Color.GRAY```)*

* **withTitleItemHeight(size: Int)** or **withTitleItemHeightRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default title item height. 
  *(Default is ```56dp```)*
  
* **withTitleTextSize(size: Int)** or **withTitleTextSizeRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default title text size. 
  *(Default is ```14sp```)*

* **withBackgroundColor(color: Int)** or **withBackgroundColorRes(res: Int)**
  Set **color** or **color resource** to change default background sheet color. 
  *(Default is ```Color.WHITE```)*
  
* **withItemHeight(height: Int)** or **withItemHeightRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default item height. 
  *(Default is ```48dp```)*
  
* **withItemTextColor(color: Int)** or **withItemTextColorRes(res: Int)**
  Set **color** or **color resource** to change default item text color. 
  *(Default is ```Color.BLACK```)*
  
* **withItemTextSize(size: Int)** or **withItemTextSize(res: Int)**
  Set **pixel size** or **dimension resource** to change default item text size. 
  *(Default is ```14sp```)*

* **withDividerColor(color: Int)** or **withDividerColorRes(res: Int)**
  Set **color** or **color resource** to change default divider color. 
  *(Default is ```Color.LTGRAY```)*

* **withItemTouchFeedbackColor(color: Int)** or **withItemTouchFeedbackColorRes(res: Int)**
  Set **color** or **color resource** to change default ripple color. 
  *(Default is ```Color.LTGRAY```)*
  
* **withHorizontalSpacing(spacing: Int)** or **withHorizontalSpacingRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default horizontal spacing. 
  *(Default is ```16dp```)*
  
* **withVerticalPadding(padding: Int)** or **withVerticalPaddingRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default vertical padding of the content view. 
  *(Default is ```8dp```)*
  
* **withImageSize(size: Int)** or **withImageSizeRes(res: Int)**
  Set **pixel size** or **dimension resource** to change default item icon size. 
  *(Default is ```24dp```)*
  
  
### Instance:


* **addButton(item: TextSheetItem)**
  Add custom created ```TextSheetItem```.

* **addButton(text: String, listener: View.OnClickListener())**
  Add button with custom ```text``` and ```OnClickListener```.

* **addButton(image: Drawable, text: String, listener: View.OnClickListener)**
  Add button with custom ```drawable```,  ```text``` and ```OnClickListener```.

* **addButton(drawableResourceId: Int, text: String, listener: View.OnClickListener)**
  Add button with custom ```drawableResourceId```,  ```text``` and ```OnClickListener```.

* **addDivider()**
  Add divider line.

* **addCustomView(customView: View)**
  Add custom view.

* **addCustomView(customView: View, listener: View.OnClickListener)**
  Add custom view with ```OnClickListener```.

* **getItem(position: Int)** 
  Return ```BaseSheetItem``` on specified position. You can make some changes above that item, and redraw the sheet using ```invalidate(```) method.

* **show()**
  Render and show the Sheet.

* **invalidate()**
  Redraw all sheet views. Need to be called after item change.


## Usage
You can create new HeluBottomButtonSheet using the Builder class. Then you can add buttons and dividers.

```java
// Create new bottom sheet using Builder class.
val sheet = HeluBottomButtonSheet.Builder(context)
		.withTitle("Some title")
		.withTitleColor(R.color.some_title_color)
		.withTitleItemHeight(R.dimen.some_title_item_height)
		.withBackgroundColor(R.color.some_background_color)
		.withItemHeight(R.dimen.some_item_height)
		.withItemTextColor(R.color.some_text_color)
		.withDividerColor(R.color.some_divider_color)
		.withItemTouchFeedbackColor(R.color.some_ripple_color)
		.withHorizontalSpacing(R.dimen.some_horizontal_spacing)
		.withImageSize(R.dimen.some_image_size)
		.build()

// Add two buttons and one divider into the bottom sheet.
sheet.addButton("First Button without image!", View.OnClickListener {
	Timber.d("First Item clicked!")
})

// Here we add the divider
sheet.addDivider()

sheet.addButton(R.drawable.some_image_drawable, "Second button with drawable image!", View.OnClickListener {
	Timber.d("Second Item clicked!")
	sheet.dismiss() // Call dismiss to hide the bottom sheet
})

// Finally show the bottom sheet
sheet.show(activity.getSupportFragmentManager())
```






You can also create a button first and change it content later:
```java
// Create new bottom sheet using Builder class.
val sheet = HeluBottomButtonSheet.Builder(context)
		.withTitle("Some title")
		.build()

// Create first button
val button = TextSheetItem("HELLO", View.OnClickListener{
	showToast("Click")
})

// Add Views inside sheet
sheet.addButton(button)
sheet.addDivider()
sheet.addButton("Click me to change text of first button!", View.OnClickListener {
	button.text = "WORLD"
	sheet.invalidate()
})

// Finally show the bottom sheet
sheet.show(activity.getSupportFragmentManager())
```
