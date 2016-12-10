package cz.helu.helubottombuttonsheet;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
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
import java.util.Arrays;
import java.util.List;


@SuppressLint("ValidFragment")
@SuppressWarnings("unused")
public class HeluBottomButtonSheet extends BottomSheetDialogFragment
{
	private final int DEFAULT_CONTENT_VERTICAL_SPACING = 8;

	private int titleItemHeight;
	private int itemHeight;
	private int itemImageSize;
	private int spacingHorizontal;
	private int itemTouchFeedbackColor;
	private int sheetBackgroundColor;
	private int sheetTitleColor;
	private int itemTextColor;
	private int dividerColor;
	private String title;
	private List<SheetItem> mItemList = new ArrayList<>();


	@SuppressLint("ValidFragment")
	public HeluBottomButtonSheet(Builder builder)
	{
		this.titleItemHeight = builder.titleItemHeight;
		this.itemHeight = builder.itemHeight;
		this.itemImageSize = builder.itemImageSize;
		this.spacingHorizontal = builder.spacingHorizontal;
		this.itemTouchFeedbackColor = builder.itemTouchFeedbackColor;
		this.sheetBackgroundColor = builder.sheetBackgroundColor;
		this.sheetTitleColor = builder.sheetTitleColor;
		this.itemTextColor = builder.itemTextColor;
		this.dividerColor = builder.dividerColor;
		this.title = builder.title;
	}


	public static Drawable getAdaptiveRippleDrawable(int normalColor, int pressedColor)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			return new RippleDrawable(ColorStateList.valueOf(pressedColor), null, getRippleMask(normalColor));
		}
		else
		{
			StateListDrawable states = new StateListDrawable();
			states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
			states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(pressedColor));
			states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
			states.addState(new int[]{}, new ColorDrawable(normalColor));
			return states;
		}
	}


	private static Drawable getRippleMask(int color)
	{
		float[] outerRadii = new float[8];
		Arrays.fill(outerRadii, 3);// 3 is radius of final ripple, instead of 3 you can give required final radius

		ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
		shapeDrawable.getPaint().setColor(color);
		return shapeDrawable;
	}


	@Override
	public void setupDialog(final Dialog dialog, int style)
	{
		super.setupDialog(dialog, style);
		int verticalContentPadding = convertDpToPx(DEFAULT_CONTENT_VERTICAL_SPACING);

		// Setup View
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setBackgroundColor(sheetBackgroundColor);

		if(!title.isEmpty())
		{
			contentLayout.setPadding(0, 0, 0, verticalContentPadding);
			contentLayout.addView(createTitleView());
		}
		else
		{
			contentLayout.setPadding(0, verticalContentPadding, 0, verticalContentPadding);
		}

		for(SheetItem entity : mItemList)
		{
			if(entity.type == SheetItem.SheetItemType.DIVIDER)
			{
				contentLayout.addView(createDividerItemView());
			}
			else
			{
				contentLayout.addView(createTextItemView(entity));
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
		mItemList.add(SheetItem.createTextSheetItem(text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addButton(@NonNull Drawable drawable, @NonNull String text, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(SheetItem.createTextSheetItem(drawable, text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addButton(int drawableResource, @NonNull String text, @NonNull View.OnClickListener clickListener)
	{
		mItemList.add(SheetItem.createTextSheetItem(drawableResource, text, clickListener));
	}


	@SuppressWarnings("unused")
	public void addDivider()
	{
		mItemList.add(SheetItem.createDividerSheetItem());
	}


	int convertDpToPx(int dp)
	{
		return Math.round(dp * getContext().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT);
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
		int verticalContentPadding = convertDpToPx(DEFAULT_CONTENT_VERTICAL_SPACING);
		LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPx(1));
		dividerParams.setMargins(0, verticalContentPadding, 0, verticalContentPadding);

		View divider = new View(getContext());
		divider.setLayoutParams(dividerParams);
		divider.setBackgroundColor(dividerColor);
		return divider;
	}


	private LinearLayout createTextItemView(SheetItem entity)
	{
		Drawable drawable = getAdaptiveRippleDrawable(sheetBackgroundColor, itemTouchFeedbackColor);
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
		private final int DEFAULT_TITLE_ITEM_HEIGHT = 56;
		private final int DEFAULT_ITEM_HEIGHT = 48;
		private final int DEFAULT_ITEM_IMAGE_SIZE = 24;
		private final int DEFAULT_HORIZONTAL_SPACING = 16;

		private Context mContext;
		private int titleItemHeight;
		private int itemHeight;
		private int itemImageSize;
		private int spacingHorizontal;
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
		}


		@SuppressWarnings("unused")
		public Builder withTitle(@StringRes int titleResId)
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
		public Builder withTitleItemHeight(@Dimension int dimension)
		{
			this.titleItemHeight = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemHeight(@Dimension int dimension)
		{
			this.itemHeight = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withImageSize(@Dimension int dimension)
		{
			this.itemImageSize = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withHorizontalSpacing(@Dimension int dimension)
		{
			this.spacingHorizontal = mContext.getResources().getDimensionPixelSize(dimension);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withBackgroundColor(@ColorRes int color)
		{
			this.sheetBackgroundColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTouchFeedbackColor(@ColorRes int color)
		{
			this.itemTouchFeedbackColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withTitleColor(@ColorRes int color)
		{
			this.sheetTitleColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withItemTextColor(@ColorRes int color)
		{
			this.itemTextColor = ContextCompat.getColor(mContext, color);
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withDividerColor(@ColorRes int color)
		{
			this.dividerColor = ContextCompat.getColor(mContext, color);
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


	private static class SheetItem
	{
		private SheetItemType type;
		private Drawable drawable;
		private int drawableResource = -1;
		private String text;
		private View.OnClickListener clickListener;


		enum SheetItemType
		{
			DIVIDER, TEXT
		}


		static SheetItem createTextSheetItem(Drawable drawable, String text, View.OnClickListener clickListener)
		{
			SheetItem item = new SheetItem();
			item.type = SheetItemType.TEXT;
			item.drawable = drawable;
			item.text = text;
			item.clickListener = clickListener;
			return item;
		}


		static SheetItem createTextSheetItem(int drawableResource, String text, View.OnClickListener clickListener)
		{
			SheetItem item = new SheetItem();
			item.type = SheetItemType.TEXT;
			item.drawableResource = drawableResource;
			item.text = text;
			item.clickListener = clickListener;
			return item;
		}


		static SheetItem createTextSheetItem(String text, View.OnClickListener clickListener)
		{
			SheetItem item = new SheetItem();
			item.type = SheetItemType.TEXT;
			item.text = text;
			item.clickListener = clickListener;
			return item;
		}


		static SheetItem createDividerSheetItem()
		{
			SheetItem item = new SheetItem();
			item.type = SheetItemType.DIVIDER;
			return item;
		}
	}
}

