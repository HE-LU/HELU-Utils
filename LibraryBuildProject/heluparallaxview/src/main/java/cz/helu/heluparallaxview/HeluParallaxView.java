package cz.helu.heluparallaxview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;


/*
	This class is usable only on API >= 16
*/
@SuppressWarnings("unused")
public class HeluParallaxView extends ImageView
{
	static final int REVERSE_NONE = 1;
	static final int REVERSE_X = 2;
	static final int REVERSE_Y = 3;
	static final int REVERSE_BOTH = 4;

	public boolean reverseX = false;
	public boolean reverseY = false;
	public boolean updateOnDraw = false;
	public boolean blockParallaxX = false;
	public boolean blockParallaxY = false;

	private int screenWidth;
	private int screenHeight;
	private float scrollSpaceX = 0;
	private float scrollSpaceY = 0;
	private float heightImageView;
	private float widthImageView;

	private Interpolator interpolator = new LinearInterpolator();

	private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = null;
	private ViewTreeObserver.OnDrawListener onDrawListener = null;


	public HeluParallaxView(Context context)
	{
		super(context);
	}


	public HeluParallaxView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		if(!checkScaleType())
			return;

		if(!isInEditMode())
			checkAttributes(attrs);
	}


	public HeluParallaxView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		if(!checkScaleType())
			return;

		if(!isInEditMode())
			checkAttributes(attrs);
	}


	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		if(!checkScaleType())
			return;

		mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener()
		{
			@Override
			public void onScrollChanged()
			{
				applyParallax();
			}
		};

		ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		viewTreeObserver.addOnScrollChangedListener(mOnScrollChangedListener);

		if(updateOnDraw)
		{
			onDrawListener = new ViewTreeObserver.OnDrawListener()
			{
				@Override
				public void onDraw()
				{
					applyParallax();
				}
			};
			viewTreeObserver.addOnDrawListener(onDrawListener);
		}

		applyParallax();
	}


	@Override
	protected void onDetachedFromWindow()
	{
		if(!checkScaleType())
		{
			super.onDetachedFromWindow();
			return;
		}

		getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);

		if(updateOnDraw)
			getViewTreeObserver().removeOnDrawListener(onDrawListener);

		super.onDetachedFromWindow();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		heightImageView = (float) getMeasuredHeight();
		widthImageView = (float) getMeasuredWidth();

		if(getDrawable() == null || !checkScaleType())
			return;

		initSizeScreen();

		int dHeight = getDrawable().getIntrinsicHeight();
		int dWidth = getDrawable().getIntrinsicWidth();
		int vHeight = getMeasuredHeight();
		int vWidth = getMeasuredWidth();
		float dNewHeight;
		float dNewWidth;

		if(dWidth * vHeight > vWidth * dHeight)
		{
			float scale = (float) vHeight / (float) dHeight;
			dNewWidth = dWidth * scale;
			dNewHeight = vHeight;
		}
		else
		{
			float scale = (float) vWidth / (float) dWidth;
			dNewWidth = vWidth;
			dNewHeight = dHeight * scale;
		}

		if(scrollSpaceX == 0) // 0 = Not been initialized yet!
		{
			setScaleX(1.2f);
			scrollSpaceX = dNewWidth * 0.17f;
		}

		if(scrollSpaceY == 0) // 0 = Not been initialized yet!
		{
			setScaleY(1.2f);
			scrollSpaceY = dNewHeight * 0.17f;
		}

		onAttachedToWindow();
	}


	public void applyColorFilter()
	{
		float alpha = 0.4f;
		float contrast = 1.5f;
		int brightness = -60;

		ColorMatrix cm = new ColorMatrix(new float[]
				{
						contrast, 0, 0, 0, brightness,
						0, contrast, 0, 0, brightness,
						0, 0, contrast, 0, brightness,
						0, 0, 0, 1, 0
				});

		setColorFilter(new ColorMatrixColorFilter(cm));
		setAlpha(alpha);
	}


	public void setInterpolator(Interpolator interpol)
	{
		interpolator = interpol;
	}


	public boolean isReverseX()
	{
		return reverseX;
	}


	public void setReverseX(boolean reverseX)
	{
		this.reverseX = reverseX;
	}


	public boolean isReverseY()
	{
		return reverseY;
	}


	public void setReverseY(boolean reverseY)
	{
		this.reverseY = reverseY;
	}


	public boolean isBlockParallaxX()
	{
		return blockParallaxX;
	}


	public void setBlockParallaxX(boolean blockParallaxX)
	{
		this.blockParallaxX = blockParallaxX;
	}


	public boolean isBlockParallaxY()
	{
		return blockParallaxY;
	}


	public void setBlockParallaxY(boolean blockParallaxY)
	{
		this.blockParallaxY = blockParallaxY;
	}


	private boolean checkScaleType()
	{
		switch(getScaleType())
		{
			case CENTER:
			case CENTER_CROP:
			case CENTER_INSIDE:
				return true;
			case FIT_CENTER:
				Log.d("ParallaxImageView", "Scale type firCenter unsupported");
				break;
			case FIT_END:
				Log.d("ParallaxImageView", "Scale type fitEnd unsupported");
				break;
			case FIT_START:
				Log.d("ParallaxImageView", "Scale type fitStart unsupported");
				break;
			case FIT_XY:
				Log.d("ParallaxImageView", "Scale type fitXY unsupported");
				break;
			case MATRIX:
				Log.d("ParallaxImageView", "Scale type matrix unsupported");
				break;
		}
		return false;
	}


	private void checkAttributes(AttributeSet attrs)
	{
		TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.HeluParallaxViewAttrs);
		int reverse = arr.getInt(R.styleable.HeluParallaxViewAttrs_reverse, 1);

		updateOnDraw = arr.getBoolean(R.styleable.HeluParallaxViewAttrs_update_onDraw, false);
		blockParallaxX = arr.getBoolean(R.styleable.HeluParallaxViewAttrs_block_parallax_x, false);
		blockParallaxY = arr.getBoolean(R.styleable.HeluParallaxViewAttrs_block_parallax_y, false);
		interpolator = InterpolatorSelector.interpolatorId(arr.getInt(R.styleable.HeluParallaxViewAttrs_interpolation, 0));

		reverseX = false;
		reverseY = false;
		switch(reverse)
		{
			case REVERSE_NONE:
				break;
			case REVERSE_X:
				reverseX = true;
				break;
			case REVERSE_Y:
				reverseY = true;
				break;
			case REVERSE_BOTH:
				reverseX = true;
				reverseY = true;
				break;
		}

		arr.recycle();
	}


	private void initSizeScreen()
	{
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point size = new Point();
		display.getSize(size);
		screenHeight = size.y;
		screenWidth = size.x;
	}


	private void applyParallax()
	{
		int[] location = new int[2];
		getLocationOnScreen(location);

		if(scrollSpaceY != 0)
		{
			float locationUsableY = location[1] + heightImageView / 2;
			float scrollDeltaY = locationUsableY / screenHeight;
			float interpolatedScrollDeltaY = interpolator.getInterpolation(scrollDeltaY);

			if(reverseY)
				setMyScrollY((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaY), -0.5f), 0.5f) * -scrollSpaceY));
			else
				setMyScrollY((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaY), -0.5f), 0.5f) * scrollSpaceY));
		}

		if(scrollSpaceX != 0)
		{
			float locationUsableX = location[0] + widthImageView / 2;
			float scrollDeltaX = locationUsableX / screenWidth;
			float interpolatedScrollDeltaX = interpolator.getInterpolation(scrollDeltaX);

			if(reverseX)
				setMyScrollX((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaX), -0.5f), 0.5f) * -scrollSpaceX));
			else
				setMyScrollX((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaX), -0.5f), 0.5f) * scrollSpaceX));
		}
	}


	private void setMyScrollX(int value)
	{
		setScrollX(value);
	}


	private void setMyScrollY(int value)
	{
		setScrollY(value);
	}


	private static class InterpolatorSelector
	{
		private static final int LINEAR = 0;
		private static final int ACCELERATE_DECELERATE = 1;
		private static final int ACCELERATE = 2;
		private static final int ANTICIPATE = 3;
		private static final int ANTICIPATE_OVERSHOOT = 4;
		private static final int BOUNCE = 5;
		private static final int DECELERATE = 6;
		private static final int OVERSHOOT = 7;


		private static Interpolator interpolatorId(int interpolationId)
		{
			switch(interpolationId)
			{
				case LINEAR:
				default:
					return new LinearInterpolator();
				case ACCELERATE_DECELERATE:
					return new AccelerateDecelerateInterpolator();
				case ACCELERATE:
					return new AccelerateInterpolator();
				case ANTICIPATE:
					return new AnticipateInterpolator();
				case ANTICIPATE_OVERSHOOT:
					return new AnticipateOvershootInterpolator();
				case BOUNCE:
					return new BounceInterpolator();
				case DECELERATE:
					return new DecelerateInterpolator();
				case OVERSHOOT:
					return new OvershootInterpolator();
				//TODO: this interpolations needs parameters
				//case CYCLE:
				//    return new CycleInterpolator();
				//case PATH:
				//    return new PathInterpolator();
			}
		}
	}
}
