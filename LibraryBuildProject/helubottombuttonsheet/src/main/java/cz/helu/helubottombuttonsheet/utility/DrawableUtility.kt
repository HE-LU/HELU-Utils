package cz.helu.helubottombuttonsheet.utility

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import java.util.*

object DrawableUtility {
    fun getAdaptiveRippleDrawable(normalColor: Int, pressedColor: Int): Drawable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return RippleDrawable(ColorStateList.valueOf(pressedColor), null, getRippleMask(normalColor))
        } else {
            val states = StateListDrawable()
            states.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(pressedColor))
            states.addState(intArrayOf(android.R.attr.state_focused), ColorDrawable(pressedColor))
            states.addState(intArrayOf(android.R.attr.state_activated), ColorDrawable(pressedColor))
            states.addState(intArrayOf(), ColorDrawable(normalColor))
            return states
        }
    }

    private fun getRippleMask(color: Int): Drawable {
        val outerRadii = FloatArray(8)
        Arrays.fill(outerRadii, 3f) // 3 is radius of final ripple, instead of 3 you can give required final radius

        val shapeDrawable = ShapeDrawable(RoundRectShape(outerRadii, null, null))
        shapeDrawable.paint.color = color
        return shapeDrawable
    }
}
