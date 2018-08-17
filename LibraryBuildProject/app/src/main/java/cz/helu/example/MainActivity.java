package cz.helu.example;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import cz.helu.helubottombuttonsheet.HeluBottomButtonSheet;
import cz.helu.helubottombuttonsheet.entity.TextSheetItem;
import cz.helu.helucollapsingtabbar.HeluCollapsingTabBar;
import cz.helu.heluparallaxview.HeluParallaxView;
import cz.helu.heluvideoview.HeluVideoView;


public class MainActivity extends AppCompatActivity
{
	private int counter = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupVideoView();
		setupImageView();
		setupTabBar();
	}


	private void setupVideoView()
	{
		HeluVideoView videoViewFitVideo = findViewById(R.id.video_view_fit_video);
		HeluVideoView videoViewFitView = findViewById(R.id.video_view_fit_view);
		HeluVideoView videoViewWitchCropping = findViewById(R.id.video_view_with_cropping);
		HeluVideoView videoViewWitchCropping2 = findViewById(R.id.video_view_with_cropping_2);

		HeluVideoView.Builder builder = new HeluVideoView.Builder(this)
				.withVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withBackupVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withAutoPlay(true)
				.withMuteOnStart(true)
				.withLooping(true);


		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO);
		videoViewFitVideo.initFromBuilder(builder);

		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIEW);
		videoViewFitView.initFromBuilder(builder);

		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_WITH_CROPPING);
		videoViewWitchCropping.initFromBuilder(builder);
		videoViewWitchCropping2.initFromBuilder(builder);
	}


	private void setupImageView()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				HeluParallaxView parallaxDisabled = findViewById(R.id.parallax_image_disabled);
				parallaxDisabled.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				parallaxDisabled.setScale(0.15f);
				parallaxDisabled.disableParallax();
			}
		}, 5000);
	}


	private void setupTabBar()
	{
		HeluCollapsingTabBar bar = findViewById(R.id.helu_tab_bar);
		HeluCollapsingTabBar.Builder builder = new HeluCollapsingTabBar.Builder(this);

		// Setup drawables
		Drawable arrowLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_selected);
		Drawable arrowLeft = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left);
		Drawable arrowRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_selected);
		Drawable arrowRight = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right);
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
				showBottomButtonSheet();
			}
		});
		builder.addButton(pauseSelected, pause, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showBottomButtonSheet();
			}
		});
		builder.addButton(arrowRightSelected, arrowRight, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showBottomButtonSheet();
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


	private void showBottomButtonSheet()
	{
		// Crete Bottom Button Sheet
		final HeluBottomButtonSheet sheet = new HeluBottomButtonSheet.Builder(this).withTitle("Bottom button sheet title").build();
		final TextSheetItem button = new TextSheetItem("Test Button value: " + counter, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showToast("Test Button clicked!");
			}
		});

		// Create custom view
		View customView = getLayoutInflater().inflate(R.layout.bottom_button_sheet_custom_view, null);
		customView.findViewById(R.id.button_decrease).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				counter--;
				button.setText("Test Button value: " + counter);
				sheet.invalidate();
			}
		});
		customView.findViewById(R.id.button_increase).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				counter++;
				button.setText("Test Button value: " + counter);
				sheet.invalidate();
			}
		});

		sheet.setRetainInstance(true);
		sheet.addCustomView(customView);
		sheet.addDivider();
		sheet.addButton(button);

		sheet.show(getSupportFragmentManager());
	}


	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
