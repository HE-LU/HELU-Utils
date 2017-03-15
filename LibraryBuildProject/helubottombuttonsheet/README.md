# HeluBottomButtonSheet 1.1.0
Create Bottom Sheet acording to Google Material guidelines simply: https://material.google.com/components/bottom-sheets.html

![Alt text](./extras/HeluBottomButtonSheet.png?raw=true "HeluBottomButtonSheet")


## Gradle:
```groovy
compile 'cz.helu.android:helubottombuttonsheet:1.1.0'
```


## Parameters
* **withTitle(String)** or **withTitle(int)**
  Set **string** or **string resource** as a title text to the Bottom sheet. 
  *Note: Sheet wont contain any title if the title is not set.*
  
* **withTitleColor(int)**
  Set **color resource** to change default title color. 
  *(Default is ```Color.GRAY```)*

* **withTitleItemHeight(int)**
  Set **dimension resource** to change default title item height. 
  *(Default is ```56dp```)*

* **withBackgroundColor(int)**
  Set **color resource** to change default background sheet color. 
  *(Default is ```Color.WHITE```)*
  
* **withItemHeight(int)**
  Set **dimension resource** to change default item height. 
  *(Default is ```48dp```)*
  
* **withItemTextColor(int)**
  Set **color resource** to change default item text color. 
  *(Default is ```Color.BLACK```)*

* **withDividerColor(int)**
  Set **color resource** to change default divider color. 
  *(Default is ```Color.LTGRAY```)*

* **withItemTouchFeedbackColor(int)**
  Set **color resource** to change default ripple color. 
  *(Default is ```Color.LTGRAY```)*
  
* **withHorizontalSpacing(int)**
  Set **dimension resource** to change default horizontal spacing. 
  *(Default is ```16dp```)*
  
* **withImageSize(int)**
  Set **dimension resource** to change default item icon size. 
  *(Default is ```24dp```)*


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
