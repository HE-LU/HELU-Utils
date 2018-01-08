package cz.helu.helubottombuttonsheet;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.helu.helubottombuttonsheet.entity.BaseSheetItem;
import cz.helu.helubottombuttonsheet.entity.CustomViewSheetItem;
import cz.helu.helubottombuttonsheet.entity.DividerSheetItem;
import cz.helu.helubottombuttonsheet.entity.TextSheetItem;
import cz.helu.helubottombuttonsheet.utility.DrawableUtility;


@SuppressLint("ValidFragment")
@SuppressWarnings("unused")
public class HeluBottomButtonSheet extends BottomSheetDialogFragment
{
	private int titleItemHeight;
	private int itemHeight;
	private int itemImageSize;
	private int spacingHorizontal;
	private int paddingVertical;
	private int itemTouchFeedbackColor;
	private int sheetBackgroundColor;
	private int sheetTitleColor;
	private int itemTextColor;
	private int dividerColor;
	private String title;
	private List<BaseSheetItem> mItemList = new ArrayList<>();


	@SuppressLint("ValidFragment")
	public HeluBottomButtonSheet(Builder builder)
	{
		this.titleItemHeight = builder.titleItemHeight;
		this.itemHeight = builder.itemHeight;
		this.itemImageSize = builder.itemImageSize;
		this.spacingHorizontal = builder.spacingHorizontal;
		this.paddingVertical = builder.paddingVertical;
		this.itemTouchFeedbackColor = builder.itemTouchFeedbackColor;
		this.sheetBackgroundColor = builder.sheetBackgroundColor;
		this.sheetTitleColor = builder.sheetTitleColor;
		this.itemTextColor = builder.itemTextColor;
		this.dividerColor = builder.dividerColor;
		this.title = builder.title;
	}


	@SuppressLint("RestrictedApi")
	@Override
	public void setupDialog(final Dialog dialog, int style)
	{
		super.setupDialog(dialog, style);

		// Setup View
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setBackgroundColor(sheetBackgroundColor);

		if(!title.isEmpty())
		{
			contentLayout.setPadding(0, 0, 0, paddingVertical);
			contentLayout.addView(createTitleView());
		}
		else
		{
			contentLayout.setPadding(0, paddingVertical, 0, paddingVertical);
		}

		for(BaseSheetItem entity : mItemList)
		{
			if(entity instanceof DividerSheetItem)
			{
				contentLayout.addView(createDividerItemView());
			}
			else if(entity instanceof TextSheetItem)
			{
				contentLayout.addView(createTextItemView((TextSheetItem) entity));
			}
			else if(entity instanceof CustomViewSheetItem)
			{
				if(((CustomViewSheetItem) entity).customView.getParent() == null)
					contentLayout.addView(((CustomViewSheetItem) entity).customView);
			}
		}

		dialog.setContentView(contentLayout);
	}


	public void show(FragmentManager manager)
	{
		show(manager, getTag());
	}


	@SuppressWarnings("unused")
	public void addButton(@NonNull String text, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(new TextSheetItem(text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addButton(@NonNull Drawable drawable, @NonNull String text, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(new TextSheetItem(drawable, text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addButton(int drawableResource, @NonNull String text, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(new TextSheetItem(drawableResource, text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addDivider()
	{
		mItemList.add(new DividerSheetItem());
	}


	@SuppressWarnings("unused")
	public void addCustomView(@NonNull View customView)
	{
		mItemList.add(new CustomViewSheetItem(customView));
	}


	@SuppressWarnings("unused")
	public void addCustomView(@NonNull View customView, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(new CustomViewSheetItem(customView, clickListener));
	}


	private int convertDpToPx(int dp)
	{
		return (getContext() != null) ? (Math.round(dp * getContext().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT)) : 0;
	}


	private View createTitleView()
	{
		LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleItemHeight);

		TextView textView = new TextView(getContext());
		textView.setText(title);
		textView.setTextColor(sheetTitleColor);
		textView.setLayoutParams(textViewParams);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(spacingHorizontal, 0, spacingHorizontal, 0);

		return textView;
	}


	private View createDividerItemView()
	{
		int verticalContentPadding = convertDpToPx(Builder.DEFAULT_CONTENT_VERTICAL_SPACING);
		LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPx(1));
		dividerParams.setMargins(0, verticalContentPadding, 0, verticalContentPadding);

		View divider = new View(getContext());
		divider.setLayoutParams(dividerParams);
		divider.setBackgroundColor(dividerColor);
		return divider;
	}


	private LinearLayout createTextItemView(TextSheetItem entity)
	{
		Drawable drawable = DrawableUtility.getAdaptiveRippleDrawable(sheetBackgroundColor, itemTouchFeedbackColor);
		LinearLayout item = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, itemHeight);

		item.setLayoutParams(params);
		item.setGravity(Gravity.CENTER_VERTICAL);
		item.setOrientation(LinearLayout.HORIZONTAL);
		item.setPadding(spacingHorizontal, 0, spacingHorizontal, 0);
		item.setOnClickListener(entity.clickListener);
		item.setBackground(drawable);

		if(entity.drawable != null || entity.drawableResource != -1)
		{
			ImageView imageView = new ImageView(getContext());
			LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(itemImageSize, itemImageSize);
			imageViewParams.setMargins(0, 0, spacingHorizontal, 0);
			imageView.setLayoutParams(imageViewParams);
			if(entity.drawable != null)
				imageView.setImageDrawable(entity.drawable);
			else
				imageView.setImageResource(entity.drawableResource);

			item.addView(imageView);
		}

		TextView textView = new TextView(getContext());
		textView.setText(entity.text);
		textView.setTextColor(itemTextColor);

		item.addView(textView);

		return item;
	}


	public static class Builder
	{
		static final int DEFAULT_TITLE_ITEM_HEIGHT = 56;
		static final int DEFAULT_ITEM_HEIGHT = 48;
		static final int DEFAULT_ITEM_IMAGE_SIZE = 24;
		static final int DEFAULT_HORIZONTAL_SPACING = 16;
		static final int DEFAULT_CONTENT_VERTICAL_SPACING = 8;

		private Context mContext;
		private int titleItemHeight;
		private int itemHeight;
		private int itemImageSize;
		private int spacingHorizontal;
		private int paddingVertical;
		private String title = "";
		@ColorInt
		private int itemTouchFeedbackColor = Color.LTGRAY;
		@ColorInt
		private int sheetBackgroundColor = Color.WHITE;
		@ColorInt
		private int sheetTitleColor = Color.GRAY;
		@ColorInt
		private int itemTextColor = Color.BLACK;
		@ColorInt
		private int dividerColor = Color.LTGRAY;


		public Builder(Context context)
		{
			mContext = context;

			titleItemHeight = convertDpToPx(DEFAULT_TITLE_ITEM_HEIGHT);
			itemHeight = convertDpToPx(DEFAULT_ITEM_HEIGHT);
			itemImageSize = convertDpToPx(DEFAULT_ITEM_IMAGE_SIZE);
			spacingHorizontal = convertDpToPx(DEFAULT_HORIZONTAL_SPACING);
			paddingVertical = convertDpToPx(DEFAULT_CONTENT_VERTICAL_SPACING);
		}


		@SuppressWarnings("unused")
		public Builder withTitleRes(@StringRes int titleResId)
		{
			this.title = mContext.getResources().getString(titleResId);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withTitle(String title)
		{
			this.title = title;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withTitleItemHeightRes(@DimenRes int dimension)
		{
			this.titleItemHeight = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withTitleItemHeight(int pixelSize)
		{
			this.titleItemHeight = pixelSize;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withItemHeightRes(@DimenRes int dimension)
		{
			this.itemHeight = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withItemHeight(int pixelSize)
		{
			this.itemHeight = pixelSize;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withImageSizeRes(@DimenRes int dimension)
		{
			this.itemImageSize = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withImageSize(@DimenRes int pixelSize)
		{
			this.itemImageSize = pixelSize;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withHorizontalSpacingRes(@DimenRes int dimension)
		{
			this.spacingHorizontal = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withHorizontalSpacing(int pixelSize)
		{
			this.spacingHorizontal = pixelSize;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withVerticalPaddingRes(@DimenRes int dimension)
		{
			this.paddingVertical = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withVerticalPadding(int pixelSize)
		{
			this.paddingVertical = pixelSize;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withBackgroundColorRes(@ColorRes int color)
		{
			this.sheetBackgroundColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withBackgroundColor(int color)
		{
			this.sheetBackgroundColor = color;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTouchFeedbackColorRes(@ColorRes int color)
		{
			this.itemTouchFeedbackColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTouchFeedbackColor(int color)
		{
			this.itemTouchFeedbackColor = color;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withTitleColorRes(@ColorRes int color)
		{
			this.sheetTitleColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withTitleColor(int color)
		{
			this.sheetTitleColor = color;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTextColorRes(@ColorRes int color)
		{
			this.itemTextColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTextColor(int color)
		{
			this.itemTextColor = color;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withDividerColorRes(@ColorRes int color)
		{
			this.dividerColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withDividerColor(int color)
		{
			this.dividerColor = color;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluBottomButtonSheet build()
		{
			return new HeluBottomButtonSheet(this);
		}


		int convertDpToPx(int dp)
		{
			return Math.round(dp * mContext.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT);
		}
	}
}
