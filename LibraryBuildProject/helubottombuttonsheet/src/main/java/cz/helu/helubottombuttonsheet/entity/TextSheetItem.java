package cz.helu.helubottombuttonsheet.entity;


import android.graphics.drawable.Drawable;
import android.view.View;


public class TextSheetItem extends BaseSheetItem
{
	public Drawable drawable;
	public int drawableResource = -1;
	public String text;
	public View.OnClickListener clickListener;


	public TextSheetItem(Drawable drawable, String text, View.OnClickListener clickListener)
	{
		this.drawable = drawable;
		this.text = text;
		this.clickListener = clickListener;
	}


	public TextSheetItem(int drawableResource, String text, View.OnClickListener clickListener)
	{
		this.drawableResource = drawableResource;
		this.text = text;
		this.clickListener = clickListener;
	}


	public TextSheetItem(String text, View.OnClickListener clickListener)
	{
		this.text = text;
		this.clickListener = clickListener;
	}
}