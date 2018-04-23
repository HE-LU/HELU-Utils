# HeluBottomButtonSheet 1.4.0
Create Bottom Sheet acording to Google Material guidelines simply: https://material.google.com/components/bottom-sheets.html

![Alt text](./extras/HeluBottomButtonSheet.png?raw=true "HeluBottomButtonSheet")


## Gradle:
```groovy
implementation 'cz.helu.android:helubottombuttonsheet:1.4.0'
```


## Builder Parameters
* **withTitle(String)** or **withTitleRes(int)**
  Set **string** or **string resource** as a title text to the Bottom sheet. 
  *Note: Sheet wont contain any title if the title is not set.*
  
* **withTitleColor(int)** or **withTitleColorRes(int)**
  Set **color** or **color resource** to change default title color. 
  *(Default is ```Color.GRAY```)*

* **withTitleItemHeight(int)** or **withTitleItemHeightRes(int)**
  Set **pixel size** or **dimension resource** to change default title item height. 
  *(Default is ```56dp```)*

* **withBackgroundColor(int)** or **withBackgroundColorRes(int)**
  Set **color** or **color resource** to change default background sheet color. 
  *(Default is ```Color.WHITE```)*
  
* **withItemHeight(int)** or **withItemHeightRes(int)**
  Set **pixel size** or **dimension resource** to change default item height. 
  *(Default is ```48dp```)*
  
* **withItemTextColor(int)** or **withItemTextColorRes(int)**
  Set **color** or **color resource** to change default item text color. 
  *(Default is ```Color.BLACK```)*

* **withDividerColor(int)** or **withDividerColorRes(int)**
  Set **color** or **color resource** to change default divider color. 
  *(Default is ```Color.LTGRAY```)*

* **withItemTouchFeedbackColor(int)** or **withItemTouchFeedbackColorRes(int)**
  Set **color** or **color resource** to change default ripple color. 
  *(Default is ```Color.LTGRAY```)*
  
* **withHorizontalSpacing(int)** or **withHorizontalSpacingRes(int)**
  Set **pixel size** or **dimension resource** to change default horizontal spacing. 
  *(Default is ```16dp```)*
  
* **withVerticalPadding(int)** or **withVerticalPaddingRes(int)**
  Set **pixel size** or **dimension resource** to change default vertical padding of the content view. 
  *(Default is ```8dp```)*
  
* **withImageSize(int)** or **withImageSizeRes(int)**
  Set **pixel size** or **dimension resource** to change default item icon size. 
  *(Default is ```24dp```)*
  
  
## BottomButtonSheet Methods
* ```TextSheetItem addButton(TextSheetItem item)```
  Add custom created ```TextSheetItem```.

* ```TextSheetItem addButton(String text, View.OnClickListener())```
  Add button with custom ```text``` and ```OnClickListener```.

* ```TextSheetItem addButton(Drawable image, String text, View.OnClickListener listener)```
  Add button with custom ```drawable```,  ```text``` and ```OnClickListener```.

* ```TextSheetItem addButton(drawableResourceId int, String text, View.OnClickListener listener)```
  Add button with custom ```drawableResourceId```,  ```text``` and ```OnClickListener```.

* ```DividerSheetItem addDivider()```
  Add divider line.

* ```CustomViewSheetItem addCustomView(View customView)```
  Add custom view.

* ```CustomViewSheetItem addCustomView(View customView, View.OnClickListener listener)```
  Add custom view with ```OnClickListener```.

* ```BaseSheetItem getItem(int position)``` 
  Return ```BaseSheetItem``` on specified position. You can make some changes above that item, and redraw the sheet using ```invalidate(```) method.

* ```void show()```
  Render and show the Sheet.

* ```void invalidate()```
  Redraw all sheet views. Need to be called after item change.


## Usage
You can create new HeluBottomButtonSheet using the Builder class. Then you can add buttons and dividers.

```java
// Create new bottom sheet using Builder class.
HeluBottomButtonSheet sheet = new HeluBottomButtonSheet.Builder(getContext())
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
		.build();

// Add two buttons and one divider into the bottom sheet.
sheet.addButton("First Button without image!", new OnClickListener() {
	@Override
	public void onClick(View view)
	{
		Timber.d("First Item clicked!");
	}
});

// Here we add the divider
sheet.addDivider();

sheet.addButton(R.drawable.some_image_drawable, "Second button with drawable image!", new OnClickListener() {
	@Override
	public void onClick(View view)
	{
		Timber.d("Second Item clicked!");
		sheet.dismiss(); // Call dismiss to hide the bottom sheet
	}
});

// Finally show the bottom sheet
sheet.show(getActivity().getSupportFragmentManager());
```






You can also create a button first and change it content later:
```java
// Create new bottom sheet using Builder class.
HeluBottomButtonSheet sheet = new HeluBottomButtonSheet.Builder(getContext())
		.withTitle("Some title")
		.build();

// Craete first button
final TextSheetItem button = new TextSheetItem("HELLO", new View.OnClickListener(){
	@Override
	public void onClick(View v)
	{
		showToast("Click");
	}
});

// Add Views inside sheet
sheet.addButton(button);
sheet.addDivider();
sheet.addButton("Click me to change text of first button!", new OnClickListener() {
	@Override
	public void onClick(View view)
	{
		button.text = "WORLD"
		sheet.invalidate();
	}
});

// Finally show the bottom sheet
sheet.show(getActivity().getSupportFragmentManager());
```
