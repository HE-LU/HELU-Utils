# HeluParallaxView 1.0.5 (API 16+)
Inspired by **ParallaxEverywhere**:  https://github.com/Narfss/ParallaxEverywhere

![Alt text](./extras/HeluParallaxView.gif?raw=true "HeluParallaxView")


## Gradle:
```groovy
compile 'cz.helu.android:heluparallaxview:1.0.5'
```


## Attributes
  
  * **scale**  = ``float``
  Set the scale of how much the image will resize. Default value ``1.2``.
  
* **block_parallax_x** and **block_parallax_y**  = ``boolean``
  Blocks parallax effect. Default value ``false``.
  
* **reverse**  = ``["none", "reverseX", "reverseY", "reverseBoth"]``
  Change the direction of parallax effect. Default value ``none``.

* **interpolation** = ``["linear", "accelerate_decelerate", "accelerate", "anticipate", "anticipate_overshoot", "bounce", "decelerate", "overshoot"]``
  Animation interpolation. Default value ``linear``.


## Parameters

### Instance:
* **applyColorFilter(int** brightness, **float** contrast, **float** alpha**)**
  You can use this method to apply collor filter on the ImageView.
  
* **setInterpolator(Interpolator** interpol**)**
  Can be one of ``["linear", "accelerate_decelerate", "accelerate", "anticipate", "anticipate_overshoot", "bounce", "decelerate", "overshoot"]``
  
* **setReverseX()** and **setReverseY()**
  If reversion is set, the parallax will move to oposite of scrolling.
  
* **setBlockParallaxX()** and **setBlockParallaxY()**
  This will block ImageView from applying X or Y parallax effect.
  
  
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
	app:block_parallax_x="true"
	app:block_parallax_y="false"
	app:interpolation="linear"
	app:reverse="reverseY" />
```


### Example of usage with Glide 4.0.0:
```java
Glide.with(imageView.getContext()).load(url)
		.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
		.apply(RequestOptions.centerCropTransform(imageView.getContext()))
		// We need specify that glide should keep the SIZE_ORIGINAL, otherwise glide will crop the image
		// and we will only see cropped image with black borders in our parallax view.
		.apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
		// If we want to specify some loading error image, we may also like to change the scale type of it
		// or if there should be parallax effect for the error image. 
		// We can use following listener for this:
		.apply(RequestOptions.errorOf(R.drawable.ic_error))
		.listener(new RequestListener<Drawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
			{
				imageView.setScaleType(ImageView.ScaleType.CENTER);
				imageView.setBlockParallaxX(true);
				return false;
			}


			@Override
			public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
			{
				imageView.setScaleType(ImageView.ScaleType.MATRIX);
				imageView.setBlockParallaxX(false);
				return false;
			}
		})
		.into(imageView);
```