# Android-Utils

## ParallaxImageView (API 16+)

Inspired by **ParallaxEverywhere**:  https://github.com/Narfss/ParallaxEverywhere

![Alt text](assets/ParallaxImageView.gif?raw=true "ParallaxImageView")

### Attributes

* **block_parallax_x** and **block_parallax_y**  = "boolean"
  Blocks parallax effect. Default value false.
  
* **reverse**  = ["none", "reverseX", "reverseY", "reverseBoth"]
  Change the direction of parallax effect. Default value "none".

* **interpolation** = ["linear", "accelerate_decelerate", "accelerate", "anticipate", "anticipate_overshoot", "bounce", "decelerate", "overshoot"]
  Animation interpolation. Default value "linear".

* **update_onDraw**  = "boolean"
  Experimental attribute: update the parallax effect on draw event. Try if the parents don't has scroll. Now only works on +API:16 (Jelly bean). Default value false.

### Usage ###

```xml
<com.example.widget.ParallaxImageView
	android:layout_width="200dp"
	android:layout_height="200dp"
	android:scaleType="centerCrop"
	app:imageUrlParallax="@{data.articleImg}"
	app:withColorFilter="@{true}" />
	
<com.example.widget.ParallaxImageView
	android:layout_width="200dp"
	android:layout_height="200dp"
	android:scaleType="centerCrop"
	app:imageUrlParallax="@{data.articleImg}"
	app:block_parallax_x="true"
	app:block_parallax_y="false"
	app:interpolation="linear"
	app:reverse="reverseY" />
```

## BottomButtonSheet

Create Bottom Sheet acording to Google Material guidelines simply: https://material.google.com/components/bottom-sheets.html

![Alt text](assets/BottomButtonSheet.png?raw=true "BottomButtonSheet")

### Parameters

* **withTitle(String)** or **withTitle(int)**
  Set **string** or **string resource** as a title text to the Bottom sheet. 
  *Note: Sheet wont contain any title if the title is not set.*
  
* **withTitleColor(int)**
  Set **color resource** to change default title color. (Default is Color.GRAY)

* **withTitleItemHeight(int)**
  Set **dimension resource** to change default title item height. (Default is 56dp)

* **withBackgroundColor(int)**
  Set **color resource** to change default background sheet color. (Default is Color.WHITE)
  
* **withItemHeight(int)**
  Set **dimension resource** to change default item height. (Default is 48dp)
  
* **withItemTextColor(int)**
  Set **color resource** to change default item text color. (Default is Color.BLACK)

* **withDividerColor(int)**
  Set **color resource** to change default divider color. (Default is Color.LTGRAY)

* **withItemTouchFeedbackColor(int)**
  Set **color resource** to change default ripple color. (Default is Color.LTGRAY)
  
* **withHorizontalSpacing(int)**
  Set **dimension resource** to change default horizontal spacing. (Default is 16dp)
  
* **withImageSize(int)**
  Set **dimension resource** to change default item icon size. (Default is 24dp)
  
### Usage ###

You can create new BottomButtonSheet using  the Builder class. Then you can add buttons and dividers.

```java
// Create new bottom sheet using Builder class.
BottomButtonSheet sheet = new BottomButtonSheet.Builder(getContext())
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



