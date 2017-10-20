package cz.helu.heluparallaxview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;


/*
	This class is usable only on API >= 16
*/
@SuppressWarnings("unused")
public class HeluParallaxView extends android.support.v7.widget.AppCompatImageView
{
	static final float DEFAULT_SCALE = 1.3f;

	static final int REVERSE_NONE = 1;
	static final int REVERSE_X = 2;
	static final int REVERSE_Y = 3;
	static final int REVERSE_BOTH = 4;

	private boolean reverseX = false;
	private boolean reverseY = false;
	private boolean blockParallaxX = false;
	private boolean blockParallaxY = false;
	private boolean normalize = true;

	private int screenWidth;
	private int screenHeight;
	private float imageScale = DEFAULT_SCALE;
	private float matrixScaleToFit = 1f;
	private float matrixTranslateX = 0f;
	private float matrixTranslateY = 0f;
	private float scrollSpaceX = 0;
	private float scrollSpaceY = 0;
	private float widthImageView = -1f;
	private float heightImageView = -1f;
	private Interpolator interpolator = new LinearInterpolator();

	private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = null;
	private ViewTreeObserver.OnDrawListener onDrawListener = null;


	public HeluParallaxView(Context context)
	{
		super(context);
		initSizeScreen();
	}


	public HeluParallaxView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		if(!isInEditMode())
			checkAttributes(attrs);

		initSizeScreen();
	}


	public HeluParallaxView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		if(!isInEditMode())
			checkAttributes(attrs);

		initSizeScreen();
	}


	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		onDrawListener = new ViewTreeObserver.OnDrawListener()
		{
			@Override
			public void onDraw()
			{
				applyParallax();
			}
		};
		getViewTreeObserver().addOnDrawListener(onDrawListener);

		setScaleType(ScaleType.CENTER);
		applyMatrix();

		applyParallax();
	}


	@Override
	protected void onDetachedFromWindow()
	{
		getViewTreeObserver().removeOnDrawListener(onDrawListener);

		super.onDetachedFromWindow();
	}


	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if(getDrawable() == null)
			return;

		widthImageView = (float) getMeasuredWidth();
		heightImageView = (float) getMeasuredHeight();

		int drawableWidth = getDrawable().getIntrinsicWidth();
		int drawableHeight = getDrawable().getIntrinsicHeight();

		float drawableNewWidth;
		float drawableNewHeight;

		if(drawableWidth * heightImageView > drawableHeight * widthImageView)
		{
			float scale = heightImageView / (float) drawableHeight;
			drawableNewWidth = drawableWidth * scale;
			drawableNewHeight = heightImageView;

			matrixScaleToFit = scale;
			matrixTranslateX = (drawableNewWidth * (imageScale) - widthImageView) / 2 * -1;
			matrixTranslateY = (heightImageView * (imageScale - 1)) / 2 * -1;
		}
		else
		{
			float scale = widthImageView / (float) drawableWidth;
			drawableNewWidth = widthImageView;
			drawableNewHeight = drawableHeight * scale;

			matrixScaleToFit = scale;
			matrixTranslateX = (widthImageView * (imageScale - 1)) / 2 * -1;
			matrixTranslateY = (drawableNewHeight * (imageScale) - heightImageView) / 2 * -1; // OK
		}

		if(scrollSpaceX == 0) // 0 = Not been initialized yet!
			scrollSpaceX = (drawableNewWidth * imageScale - widthImageView);

		if(scrollSpaceY == 0) // 0 = Not been initialized yet!
			scrollSpaceY = (drawableNewHeight * imageScale - heightImageView);

		if(normalize)
		{
			if(scrollSpaceX < scrollSpaceY)
				scrollSpaceY = scrollSpaceX;
			else
				scrollSpaceX = scrollSpaceY;
		}

		onAttachedToWindow();
	}


	public void applyColorFilter(int brightness, float contrast, float alpha)
	{
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


	public float getScale()
	{
		return imageScale;
	}


	public void setScale(float imageScale)
	{
		this.imageScale = imageScale;
	}


	public void resetParallax()
	{
		setMyScrollX(0);
		setMyScrollY(0);
	}


	public void disableParallax()
	{
		blockParallaxX = true;
		blockParallaxY = true;
		setMyScrollX(0);
		setMyScrollY(0);
	}


	protected void applyMatrix()
	{
		if((scrollSpaceX == 0 && scrollSpaceY == 0))
			return;

		setScaleType(ScaleType.MATRIX);

		Matrix imageMatrix = new Matrix(getImageMatrix());
		imageMatrix.setScale(matrixScaleToFit * imageScale, matrixScaleToFit * imageScale);
		imageMatrix.postTranslate(matrixTranslateX, matrixTranslateY);
		setImageMatrix(imageMatrix);
	}


	protected void applyParallax()
	{
		int[] location = new int[2];
		getLocationOnScreen(location);

		if(scrollSpaceY != 0 && !blockParallaxY)
		{
			float locationUsableY = location[1] + heightImageView / 2;
			float scrollDeltaY = locationUsableY / screenHeight;
			float interpolatedScrollDeltaY = interpolator.getInterpolation(scrollDeltaY);

			if(reverseY)
				setMyScrollY((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaY), -0.5f), 0.5f) * -scrollSpaceY));
			else
				setMyScrollY((int) (Math.min(Math.max((0.5f - interpolatedScrollDeltaY), -0.5f), 0.5f) * scrollSpaceY));
		}

		if(scrollSpaceX != 0 && !blockParallaxX)
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


	private void checkAttributes(AttributeSet attrs)
	{
		TypedArray arr = getContext().obtainStyledAttributes(attrs, cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs);
		int reverse = arr.getInt(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_reverse, 1);

		imageScale = arr.getFloat(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_scale, DEFAULT_SCALE);
		blockParallaxX = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_blockParallaxX, false);
		blockParallaxY = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_blockParallaxY, false);
		normalize = arr.getBoolean(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_normalize, true);
		interpolator = InterpolatorSelector.interpolatorId(arr.getInt(cz.helu.heluparallaxview.R.styleable.HeluParallaxViewAttrs_interpolation, 0));

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
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
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

