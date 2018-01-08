package cz.helu.helubottombuttonsheet.entity;


import android.view.View;


public class CustomViewSheetItem extends BaseSheetItem
{
	public View customView;


	public CustomViewSheetItem(View customView)
	{
		this.customView = customView;
	}


	public CustomViewSheetItem(View customView, View.OnClickListener clickListener)
	{
		this.customView = customView;
		customView.setOnClickListener(clickListener);
	}
}