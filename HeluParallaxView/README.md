# HeluParallaxView 1.0.3 (API 16+)
Inspired by **ParallaxEverywhere**:  https://github.com/Narfss/ParallaxEverywhere

![Alt text](./extras/HeluParallaxView.gif?raw=true "HeluParallaxView")


## Gradle:
```groovy
compile 'cz.helu.android:heluparallaxview:1.0.3'
```


## Attributes
* **block_parallax_x** and **block_parallax_y**  = "boolean"
  Blocks parallax effect. Default value false.
  
* **reverse**  = ["none", "reverseX", "reverseY", "reverseBoth"]
  Change the direction of parallax effect. Default value "none".

* **interpolation** = ["linear", "accelerate_decelerate", "accelerate", "anticipate", "anticipate_overshoot", "bounce", "decelerate", "overshoot"]
  Animation interpolation. Default value "linear".

* **update_onDraw**  = "boolean"
  Experimental attribute: update the parallax effect on draw event. Try if the parents don't has scroll. Now only works on +API:16 (Jelly bean). Default value false.


## Usage

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