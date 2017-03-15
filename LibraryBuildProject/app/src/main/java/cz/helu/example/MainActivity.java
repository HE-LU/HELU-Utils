package cz.helu.example;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import cz.helu.helucollapsingtabbar.HeluCollapsingTabBar;
import cz.helu.heluvideoview.HeluVideoView;


public class MainActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupVideoView();
		setupTabBar();
	}


	private void setupVideoView()
	{
		HeluVideoView videoView = (HeluVideoView) findViewById(R.id.video_view);

		HeluVideoView.Builder builder = new HeluVideoView.Builder(this)
				.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO)
				.withVideoUrl("http://techslides.com/demos/sample-videos/small.mp44")
				.withBackupVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withAutoPlay(true)
				.withMuteOnStart(true)
				.withLooping(true);

		videoView.initFromBuilder(builder);
	}


	private void setupTabBar()
	{
		HeluCollapsingTabBar bar = (HeluCollapsingTabBar) findViewById(R.id.helu_tab_bar);
		HeluCollapsingTabBar.Builder builder = new HeluCollapsingTabBar.Builder(this);

		// Setup drawables
		Drawable arrowLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_selected);
		Drawable arrowLeft = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
		Drawable arrowRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_selected);
		Drawable arrowRight= ContextCompat.getDrawable(this, R.drawable.ic_arrow_right);
		Drawable pauseSelected = ContextCompat.getDrawable(this, R.drawable.ic_pause_selected);
		Drawable pause = ContextCompat.getDrawable(this, R.drawable.ic_pause);

		// Setup builder
		builder.withBackground(ContextCompat.getDrawable(this, R.drawable.shape_collapsing_tab_bar));
		builder.withButtonSize(R.dimen.global_spacing_48);
		builder.withButtonPadding(R.dimen.global_spacing_12);
		builder.withButtonSpacing(R.dimen.global_spacing_16);

		// Add buttons
		builder.addButton(arrowLeftSelected, arrowLeft, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showToast("Left");
			}
		});
		builder.addButton(pauseSelected, pause, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showToast("Pause");
			}
		});
		builder.addButton(arrowRightSelected, arrowRight, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showToast("Right");
			}
		});

		// Setup bar
		bar.initFromBuilder(builder);
		bar.setSelectedItem(0);

		// Customize animation using LayoutTransition
		bar.getLayoutTransition().setDuration(150); // Translation duration
		bar.getLayoutTransition().setDuration(LayoutTransition.CHANGE_APPEARING, 200); // Translation duration
		bar.getLayoutTransition().setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125); // Start Delay
		bar.getLayoutTransition().setStartDelay(LayoutTransition.APPEARING, 100); // Start Delay
		bar.getLayoutTransition().setInterpolator(LayoutTransition.CHANGE_APPEARING, new OvershootInterpolator());
	}


	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
