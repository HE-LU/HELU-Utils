package cz.helu.helucollapsingtabbar;


import android.graphics.drawable.Drawable;
import android.view.View;


public class HeluCollapsingTabBarButton
{
	private Drawable mIcon;
	private Drawable mInActiveIcon;
	private View.OnClickListener mOnClickListener;


	public Drawable getIcon()
	{
		return mIcon;
	}


	public void setIcon(Drawable icon)
	{
		mIcon = icon;
	}


	public Drawable getInActiveIcon()
	{
		return mInActiveIcon;
	}


	public void setInActiveIcon(Drawable inActiveIcon)
	{
		mInActiveIcon = inActiveIcon;
	}


	public View.OnClickListener getOnClickListener()
	{
		return mOnClickListener;
	}


	public void setOnClickListener(View.OnClickListener onClickListener)
	{
		mOnClickListener = onClickListener;
	}
}
