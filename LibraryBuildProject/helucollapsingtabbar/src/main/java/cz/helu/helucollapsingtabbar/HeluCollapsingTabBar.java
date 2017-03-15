package cz.helu.helucollapsingtabbar;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class HeluCollapsingTabBar extends LinearLayout
{
	private List<HeluCollapsingTabBarButton> mButtonsList;
	private int mButtonSize = 0;
	private int mButtonPadding = 0;
	private int mButtonSpacing = 0;
	private Drawable mBackground;
	private boolean mIsCollapsed = false;
	private int mSelectedItem = 0;


	public HeluCollapsingTabBar(Context context)
	{
		this(context, null);
	}


	public HeluCollapsingTabBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	public HeluCollapsingTabBar(Builder builder)
	{
		super(builder.context);
		initFromBuilder(builder);
	}


	public void initFromBuilder(Builder builder)
	{
		this.mBackground = builder.background;
		this.mButtonsList = builder.buttonsList;
		this.mButtonSize = builder.buttonSize;
		this.mButtonPadding = builder.buttonPadding;
		this.mButtonSpacing = builder.buttonSpacing;

		initView();
	}


	public void setSelectedItem(int position)
	{
		if(position >= getChildCount())
			return;

		mSelectedItem = position;

		collapseView();
	}


	public void collapseView()
	{
		if(!(getChildAt(mSelectedItem) instanceof ImageView))
			return;

		ImageView view = (ImageView) getChildAt(mSelectedItem);

		for(int i = 0; i < getChildCount(); i++)
		{
			if(!(getChildAt(i) instanceof ImageView))
				continue;

			ImageView child = (ImageView) getChildAt(i);
			if(!child.equals(view))
			{
				child.setVisibility(View.GONE);
			}
			else
			{
				child.setImageDrawable(mButtonsList.get(i).getIcon());
				LayoutParams params = (LayoutParams) view.getLayoutParams();
				params.gravity = Gravity.CENTER;
				clearViewMargins(params, view);
				mSelectedItem = i;
			}
		}
		mIsCollapsed = true;
	}


	public void expandView()
	{
		if(!(getChildAt(mSelectedItem) instanceof ImageView))
			return;

		ImageView view = (ImageView) getChildAt(mSelectedItem);
		LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.gravity = Gravity.CENTER;
		setupViewMargins(params, view);

		for(int i = 0; i < getChildCount(); i++)
		{
			if(!(getChildAt(i) instanceof ImageView))
				continue;

			ImageView child = (ImageView) getChildAt(i);
			child.setVisibility(View.VISIBLE);

			if(i != mSelectedItem)
				child.setImageDrawable(mButtonsList.get(i).getInActiveIcon());
		}

		mIsCollapsed = false;
	}


	@SuppressWarnings("unused")
	public boolean isCollapsed()
	{
		return mIsCollapsed;
	}


	private void initView()
	{
		// Set Parent layout background drawable
		setBackground(mBackground);
		setLayoutTransition(new LayoutTransition());

		// Setup all buttons view and add them into layout
		for(final HeluCollapsingTabBarButton button : mButtonsList)
		{
			final ImageView buttonImageView = new ImageView(getContext());

			LayoutParams params = new LayoutParams(mButtonSize, mButtonSize);
			params.gravity = Gravity.CENTER;
			setupViewMargins(params, buttonImageView);

			buttonImageView.setLayoutParams(params);
			buttonImageView.setPadding(mButtonPadding, mButtonPadding, mButtonPadding, mButtonPadding);
			buttonImageView.setImageDrawable(button.getIcon());
			buttonImageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					mSelectedItem = indexOfChild(view);
					onButtonClicked();
				}
			});

			addView(buttonImageView);
		}
	}


	private void onButtonClicked()
	{
		if(mIsCollapsed)
		{
			expandView();
		}
		else
		{
			ImageView view = (ImageView) getChildAt(mSelectedItem);
			mButtonsList.get(mSelectedItem).getOnClickListener().onClick(view);
			collapseView();
		}
	}


	private void setupViewMargins(LayoutParams params, ImageView view)
	{
		// Set margin for view, depending if the layout is vertical or horizontal
		if(getOrientation() == HORIZONTAL)
			params.setMargins(mButtonSpacing, 0, mButtonSpacing, 0);
		else
			params.setMargins(0, mButtonSpacing, 0, mButtonSpacing);

		view.setLayoutParams(params);
	}


	private void clearViewMargins(LayoutParams params, ImageView view)
	{
		params.setMargins(0, 0, 0, 0);
		view.setLayoutParams(params);
	}


	public static class Builder
	{
		private final int DEFAULT_BUTTON_SIZE = 56;
		private final int DEFAULT_BUTTON_PADDING = 16;
		private final int DEFAULT_BUTTON_SPACING = 12;

		private Context context;
		private List<HeluCollapsingTabBarButton> buttonsList = new ArrayList<>();
		private int buttonSize = 0;
		private int buttonPadding = 0;
		private int buttonSpacing = 0;
		private Drawable background;


		public Builder(Context context)
		{
			this.context = context;

			buttonSize = convertDpToPx(DEFAULT_BUTTON_SIZE);
			buttonPadding = convertDpToPx(DEFAULT_BUTTON_PADDING);
			buttonSpacing = convertDpToPx(DEFAULT_BUTTON_SPACING);
		}


		@SuppressWarnings("unused")
		public Builder withBackground(Drawable background)
		{
			this.background = background;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonSize(int dimension)
		{
			this.buttonSize = context.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonPadding(int dimension)
		{
			this.buttonPadding = context.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonSpacing(int dimension)
		{
			this.buttonSpacing = context.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonSizePx(int dimension)
		{
			this.buttonSize = dimension;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonPaddingPx(int dimension)
		{
			this.buttonPadding = dimension;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withButtonSpacingPx(int dimension)
		{
			this.buttonSpacing = dimension;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder addButton(HeluCollapsingTabBarButton button)
		{
			this.buttonsList.add(button);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder addButton(Drawable activeIcon, OnClickListener clickListener)
		{
			HeluCollapsingTabBarButton button = new HeluCollapsingTabBarButton();
			button.setIcon(activeIcon);
			button.setInActiveIcon(activeIcon);
			button.setOnClickListener(clickListener);
			this.buttonsList.add(button);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder addButton(Drawable activeIcon, Drawable inactiveIcon, OnClickListener clickListener)
		{
			HeluCollapsingTabBarButton button = new HeluCollapsingTabBarButton();
			button.setIcon(activeIcon);
			button.setInActiveIcon(inactiveIcon);
			button.setOnClickListener(clickListener);
			this.buttonsList.add(button);
			return this;
		}


		@SuppressWarnings("unused")
		public HeluCollapsingTabBar build()
		{
			return new HeluCollapsingTabBar(this);
		}


		int convertDpToPx(int dp)
		{
			return Math.round(dp * context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT);
		}
	}
}