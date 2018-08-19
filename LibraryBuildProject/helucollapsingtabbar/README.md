# HeluCollapsingTabBar 2.0.0
![Alt text](./extras/HeluCollapsingTabBar.gif?raw=true "HeluCollapsingTabBar")


## Gradle:
```groovy
implementation 'cz.helu.android:helucollapsingtabbar:2.0.0'
```

## Methods


### Instance:
* **setSelectedItem(position: Int)**
  Set selected button in tab bar.
  
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
  *(Default is ```56dp```)*
  
* **withButtonPadding(dimension: Int)**
  Set background drawable.
  *(Default is ```16dp```)*
  
* **withButtonSpacing(dimension: Int)**
  Set background drawable.
  *(Default is ```12dp```)*
    
* **withButtonSizePx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```56dp```)*
  
* **withButtonPaddingPx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```16dp```)*
  
* **withButtonSpacingPx(pixelSize: Int)**
  Set background drawable.
  *(Default is ```12dp```)*
  
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
	val bar = findViewById(R.id.helu_tab_bar) as HeluCollapsingTabBar
	val builder = HeluCollapsingTabBar.Builder(context)

	// Setup drawables
	val arrowLeftSelected = ContextCompat.getDrawable(context, R.drawable.ic_arrow_left_selected)!!
	val arrowLeft = ContextCompat.getDrawable(context, R.drawable.ic_arrow_left)!!
	val arrowRightSelected = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right_selected)!!
	val arrowRight = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right)!!
	val pauseSelected = ContextCompat.getDrawable(context, R.drawable.ic_pause_selected)!!
	val pause = ContextCompat.getDrawable(context, R.drawable.ic_pause)!!

	// Setup builder
	builder.withBackground(ContextCompat.getDrawable(context, R.drawable.shape_collapsing_tab_bar)!!)
	builder.withButtonSize(R.dimen.global_spacing_48)
	builder.withButtonPadding(R.dimen.global_spacing_12)
	builder.withButtonSpacing(R.dimen.global_spacing_16)

	// Add buttons
	builder.addButton(arrowLeftSelected, arrowLeft, View.OnClickListener { showToast("Left") })
	builder.addButton(pauseSelected, pause, View.OnClickListener { showToast("Pause") })
	builder.addButton(arrowRightSelected, arrowRight, View.OnClickListener { showToast("Right") })

	// Setup bar
	bar.initFromBuilder(builder)
	bar.setSelectedItem(0)

	// Customize animation using LayoutTransition
	bar.layoutTransition.setDuration(150) // Translation duration
	bar.layoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 200) // Translation duration
	bar.layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125) // Start Delay
	bar.layoutTransition.setStartDelay(LayoutTransition.APPEARING, 100) // Start Delay
	bar.layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, OvershootInterpolator())
}
```