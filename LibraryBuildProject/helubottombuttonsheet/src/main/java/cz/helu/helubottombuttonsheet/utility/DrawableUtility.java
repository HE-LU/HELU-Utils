package cz.helu.helubottombuttonsheet.utility;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;

import java.util.Arrays;


public class DrawableUtility
{
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
}
