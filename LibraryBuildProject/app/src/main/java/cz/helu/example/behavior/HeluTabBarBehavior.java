package cz.helu.example.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import cz.helu.helucollapsingtabbar.HeluCollapsingTabBar;


public class HeluTabBarBehavior extends VerticalScrollingBehavior<View>
{
	private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
	private ViewPropertyAnimatorCompat mTranslationAnimator;


	public HeluTabBarBehavior()
	{
		super();
	}


	public HeluTabBarBehavior(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	@Override
	void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, View child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll)
	{
	}


	@Override
	void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection)
	{
		if(target instanceof RecyclerView && isRecyclerVerticallyScrollable((RecyclerView) target))
			handleDirection(child, scrollDirection);
		else if(!(target instanceof RecyclerView))
			handleDirection(child, scrollDirection);
	}


	@Override
	boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection)
	{
		if(target instanceof RecyclerView && isRecyclerVerticallyScrollable((RecyclerView) target))
			handleDirection(child, scrollDirection);
		else if(!(target instanceof RecyclerView))
			handleDirection(child, scrollDirection);
		return true;
	}


	public boolean isRecyclerVerticallyScrollable(RecyclerView recyclerView)
	{
		return recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
	}


	private void handleDirection(final View child, int scrollDirection)
	{
		if(!(child instanceof HeluCollapsingTabBar))
			return;

		if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN)
		{
			((HeluCollapsingTabBar) child).expandView();
		}
		else if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP)
		{
			((HeluCollapsingTabBar) child).collapseView();
		}
	}


	private void ensureOrCancelAnimator(View child)
	{
		if(mTranslationAnimator == null)
		{
			mTranslationAnimator = ViewCompat.animate(child);
			mTranslationAnimator.setDuration(300);
			mTranslationAnimator.setInterpolator(INTERPOLATOR);
		}
		else
		{
			mTranslationAnimator.cancel();
		}
	}
}