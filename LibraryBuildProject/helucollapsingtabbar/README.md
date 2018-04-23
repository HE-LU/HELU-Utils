# HeluCollapsingTabBar 1.1.1
![Alt text](./extras/HeluCollapsingTabBar.gif?raw=true "HeluCollapsingTabBar")


## Gradle:
```groovy
implementation 'cz.helu.android:helucollapsingtabbar:1.1.1'
```

## Parameters


### Instance:
* **setSelectedItem(int** position**)**
  Set selected button in tab bar.
  
* **collapseView()**
  Collapse the tab bar.
  
* **expandView()**
  Expand the tab bar.
  
* **isCollapsed()**
  Returns ``boolean`` based on tab bar collapse state.


### Builder:
* **withBackground(Drawable** backgorundDrawable**)**
  Set background drawable.
  
* **withButtonSize(int** dimension**)**
  Set button size.
  *(Default is ```56dp```)*
  
* **withButtonPadding(int** dimension**)**
  Set background drawable.
  *(Default is ```16dp```)*
  
* **withButtonSpacing(int** dimension**)**
  Set background drawable.
  *(Default is ```12dp```)*
    
* **withButtonSizePx(int** pixelSize**)**
  Set background drawable.
  *(Default is ```56dp```)*
  
* **withButtonPaddingPx(int** pixelSize**)**
  Set background drawable.
  *(Default is ```16dp```)*
  
* **withButtonSpacingPx(int** pixelSize**)**
  Set background drawable.
  *(Default is ```12dp```)*
  
* **addButton(HeluCollapsingTabBarButton** button**)**
  Add custom button.
  
* **addButton(Drawable** activeIcon**,OnClickListener** clickListener**)**
  Add button with ``activeIcon`` and ``clickListener``.
  
* **addButton(Drawable** activeIcon**,Drawable** inactiveIcon**,OnClickListener** clickListener**)**
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

### JAVA


### Example of usage with Glide 4.0.0:
```java
private void setupTabBar()
{
	HeluCollapsingTabBar bar = (HeluCollapsingTabBar) findViewById(R.id.helu_tab_bar);
	HeluCollapsingTabBar.Builder builder = new HeluCollapsingTabBar.Builder(this);

	// Setup drawables
	Drawable arrowLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_selected);
	Drawable arrowLeft = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
	Drawable arrowRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_selected);
	Drawable arrowRight= ContextCompat.getDrawable(this, R.drawable.ic_arrow_right);
	Drawable pauseSelected = ContextCompat.getDrawable(this, R.drawable.ic_pause_selected);
	Drawable pause = ContextCompat.getDrawable(this, R.drawable.ic_pause);

	// Setup builder
	builder.withBackground(ContextCompat.getDrawable(this, R.drawable.shape_collapsing_tab_bar));
	builder.withButtonSize(R.dimen.global_spacing_48);
	builder.withButtonPadding(R.dimen.global_spacing_12);
	builder.withButtonSpacing(R.dimen.global_spacing_16);

	// Add buttons
	builder.addButton(arrowLeftSelected, arrowLeft, new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			showToast("Left");
		}
	});
	builder.addButton(pauseSelected, pause, new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			showToast("Pause");
		}
	});
	builder.addButton(arrowRightSelected, arrowRight, new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			showToast("Right");
		}
	});

	// Setup bar
	bar.initFromBuilder(builder);
	bar.setSelectedItem(0);

	// Customize animation using LayoutTransition
	bar.getLayoutTransition().setDuration(150); // Translation duration
	bar.getLayoutTransition().setDuration(LayoutTransition.CHANGE_APPEARING, 200); // Translation duration
	bar.getLayoutTransition().setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125); // Start Delay
	bar.getLayoutTransition().setStartDelay(LayoutTransition.APPEARING, 100); // Start Delay
	bar.getLayoutTransition().setInterpolator(LayoutTransition.CHANGE_APPEARING, new OvershootInterpolator());
}
```