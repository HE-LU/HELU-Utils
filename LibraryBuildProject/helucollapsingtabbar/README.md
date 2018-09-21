# HeluCollapsingTabBar 2.1.0
![Alt text](./extras/HeluCollapsingTabBar.gif?raw=true "HeluCollapsingTabBar")


## Gradle:
```groovy
implementation 'cz.helu.android:helucollapsingtabbar:2.1.0'
```

## Methods


### Instance:
* **setSelectedPosition(position: Int)**
  Set selected button in tab bar.
  
* **getSelectedPosition()**
  Get currently selected position of tab bar.
  
* **collapseView()**
  Collapse the tab bar.
  
* **expandView()**
  Expand the tab bar.
  
* **isCollapsed()**
  Returns ``boolean`` based on tab bar collapse state.


### Builder:
* **withBackground(backgorundDrawable: Drawable)**
  Set background drawable.
  
* **withButtonSize(dimension: Int)**
  Set button size.
  *(Default is ```48dp```)*
  
* **withButtonPadding(dimension: Int)**
  Set background drawable.
  *(Default is ```12dp```)*
  
* **withButtonSpacing(dimension: Int)**
  Set background drawable.
  *(Default is ```16dp```)*
    
* **withButtonSizePx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```48dp```)*
  
* **withButtonPaddingPx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```12dp```)*
  
* **withButtonSpacingPx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```16dp```)*
  
* **addButton(button: HeluCollapsingTabBarButton)**
  Add custom button.
  
* **addButton(activeIcon: Drawable, clickListener: View.OnClickListener)**
  Add button with ``activeIcon`` and ``clickListener``.
  
* **addButton(activeIcon: Drawable, inactiveIcon: Drawable, clickListener: View.OnClickListener)**
  Add button with ``activeIcon``, ``inactiveIcon`` and ``clickListener``.



## Usage

### XML
```xml
<cz.helu.helucollapsingtabbar.HeluCollapsingTabBar
	android:id="@+id/helu_tab_bar"
	android:layout_width="wrap_content"
	android:layout_height="@dimen/global_spacing_48"
	android:layout_marginBottom="@dimen/global_spacing_12"
	android:layout_gravity="center_horizontal|bottom"
	android:orientation="horizontal"
	app:layout_behavior="cz.helu.example.behavior.HeluTabBarBehavior" />
```

### Kotlin
```java
fun setupTabBar() {
	val bar = findViewById<HeluCollapsingTabBar>(R.id.helu_tab_bar)

	// Setup drawables
	val alignLeft = ContextCompat.getDrawable(this, R.drawable.ic_align_left)!!
	val alignLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_left_selected)!!
	val alignCenter = ContextCompat.getDrawable(this, R.drawable.ic_align_center)!!
	val alignCenterSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_center_selected)!!
	val alignRight = ContextCompat.getDrawable(this, R.drawable.ic_align_right)!!
	val alignRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_right_selected)!!

	// Add buttons
	val builder = HeluCollapsingTabBar.Builder(this)
	builder.addButton(alignLeftSelected, alignLeft, View.OnClickListener { showToast("Left") })
	builder.addButton(alignCenterSelected, alignCenter, View.OnClickListener { showToast("Center") })
	builder.addButton(alignRightSelected, alignRight, View.OnClickListener { showToast("Right") })

	// Setup bar
	bar.initFromBuilder(builder)
	bar.setSelectedPosition(1)

	// Customize animation using LayoutTransition
	bar.layoutTransition.setDuration(150) // Translation duration
	bar.layoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 200) // Translation duration
	bar.layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125) // Start Delay
	bar.layoutTransition.setStartDelay(LayoutTransition.APPEARING, 100) // Start Delay
	bar.layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, OvershootInterpolator())
}
```