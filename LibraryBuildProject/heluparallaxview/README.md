# HeluParallaxView 2.0.0 (API 16+)
![Alt text](./extras/HeluParallaxView.gif?raw=true "HeluParallaxView")


## Gradle:
```groovy
implementation 'cz.helu.android:heluparallaxview:2.0.0'
```


## Attributes
  
  * **scale**  = ``float``
  Set the scale of how much the image will resize. Default value ``1.3``.
  
* **blockParallaxX** and **blockParallaxY**  = ``boolean``
  Blocks parallax effect. Default value ``false``.
  
* **reverse**  = ``["none", "reverseX", "reverseY", "reverseBoth"]``
  Change the direction of parallax effect. Default value ``none``.

* **interpolation** = ``["linear", "accelerateDecelerate", "accelerate", "anticipate", "anticipateOvershoot", "bounce", "decelerate", "overshoot"]``
  Animation interpolation. Default value ``linear``.

  * **normalize**  = ``boolean``
  If set to true, the horizontal and vertical scroll size will be the same, no matter how much image width and height differ. Default value ``true``.

## Methods

### Instance:
* **applyColorFilter(brightness: Int, contrast: Float, alpha: Float)**
  You can use this method to apply collor filter on the ImageView.
  
* **setInterpolator(interpolator: Interpolator)**
  Can be one of ``["linear", "accelerateDecelerate", "accelerate", "anticipate", "anticipateOvershoot", "bounce", "decelerate", "overshoot"]``
  
* **setReverseX()** and **setReverseY()**
  If reversion is set, the parallax will move to oposite of scrolling.
  
* **setBlockParallaxX()** and **setBlockParallaxY()**
  This will block ImageView from applying X or Y parallax effect.
  
* **resetParallax()**
  This will reset parallax X and Y to 0.
  
* **disableParallax()**
  This will block parallax for both X and Y, and also reset parallax position.
  
* **enableParallax()**
  This will enable parallax for both X and Y, and also reset parallax position.
  
  
## Note
HeluParallaxView is using matrix for scaling images. Be aware of that it automatically using ``ScaleType.MATRIX``!
  
  
## Usage

```xml
<cz.helu.heluparallaxview.HeluParallaxView
	android:layout_width="200dp"
	android:layout_height="200dp"
	app:imageUrlParallax="@{data.articleImg}" />
	
<cz.helu.heluparallaxview.HeluParallaxView
	android:layout_width="200dp"
	android:layout_height="200dp"
	app:imageUrlParallax="@{data.articleImg}"
	app:scale="1.8"
	app:blockParallaxX="true"
	app:blockParallaxY="false"
	app:interpolation="linear"
	app:reverse="reverseY" />
```


### Example of usage with Glide 4.0.0:
```java
Glide.with(imageView.context).load(url)
	// We need to set overrideOf SIZE_ORIGINAL, otherwise Glide will crop the image during the caching process, and the parallax won’t work. 
	// We do not need to do this in case of setting:
	// .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
	.apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
	
	.apply(RequestOptions.centerCropTransform())
	// If we want to specify some loading error image, we may also like to change the scale type of it
	// or if there should be parallax effect for the error image.
	// We can use following listener for this:
	.apply(RequestOptions.errorOf(R.drawable.ic_error))
	.listener(object : RequestListener<Drawable> {
		override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
			imageView.enableParallax()
			imageView.scale = 1.35f
			return false
		}

		override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
			imageView.disableParallax()
			imageView.scale = 0.35f
			return false
		}
	})
	.into(imageView)
```