package cz.helu.example

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import cz.helu.helubottombuttonsheet.HeluBottomButtonSheet
import cz.helu.helubottombuttonsheet.entity.TextSheetItem
import cz.helu.helucollapsingtabbar.HeluCollapsingTabBar
import cz.helu.heluparallaxview.HeluParallaxView
import cz.helu.heluvideoview.HeluVideoView


class MainActivity : AppCompatActivity() {
	private var counter = 0


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setupVideoView()
		setupImageView()
		setupTabBar()
	}


	private fun setupVideoView() {
		val videoViewFitVideo = findViewById<HeluVideoView>(R.id.video_view_fit_video)
		val videoViewFitView = findViewById<HeluVideoView>(R.id.video_view_fit_view)
		val videoViewWitchCropping = findViewById<HeluVideoView>(R.id.video_view_with_cropping)
		val videoViewWitchCropping2 = findViewById<HeluVideoView>(R.id.video_view_with_cropping_2)

		val builder = HeluVideoView.Builder(this)
				.withVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withBackupVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withAutoPlay(true)
				.withMuteOnStart(true)
				.withLooping(true)


		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO)
		videoViewFitVideo.initFromBuilder(builder)

		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIEW)
		videoViewFitView.initFromBuilder(builder)

		builder.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_WITH_CROPPING)
		videoViewWitchCropping.initFromBuilder(builder)
		videoViewWitchCropping2.initFromBuilder(builder)
	}


	private fun setupImageView() {
		Handler().postDelayed({
			val parallaxDisabled = findViewById<HeluParallaxView>(R.id.parallax_image_disabled)
			parallaxDisabled.scaleType = ImageView.ScaleType.CENTER_INSIDE
			parallaxDisabled.scale = 0.15f
			parallaxDisabled.disableParallax()
		}, 5000)
	}


	private fun setupTabBar() {
		val bar = findViewById<HeluCollapsingTabBar>(R.id.helu_tab_bar)
		val builder = HeluCollapsingTabBar.Builder(this)

		// Setup drawables
		val arrowLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_selected)
		val arrowLeft = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
		val arrowRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_selected)
		val arrowRight = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right)
		val pauseSelected = ContextCompat.getDrawable(this, R.drawable.ic_pause_selected)
		val pause = ContextCompat.getDrawable(this, R.drawable.ic_pause)

		// Setup builder
		builder.withBackground(ContextCompat.getDrawable(this, R.drawable.shape_collapsing_tab_bar)!!)
		builder.withButtonSize(R.dimen.global_spacing_48)
		builder.withButtonPadding(R.dimen.global_spacing_12)
		builder.withButtonSpacing(R.dimen.global_spacing_16)

		// Add buttons
		builder.addButton(arrowLeftSelected!!, arrowLeft!!, View.OnClickListener { showBottomButtonSheet() })
		builder.addButton(pauseSelected!!, pause!!, View.OnClickListener { showBottomButtonSheet() })
		builder.addButton(arrowRightSelected!!, arrowRight!!, View.OnClickListener { showBottomButtonSheet() })

		// Setup bar
		bar.initFromBuilder(builder)
		bar.setSelectedItem(0)

		// Customize animation using LayoutTransition
		bar.layoutTransition.setDuration(150) // Translation duration
		bar.layoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 200) // Translation duration
		bar.layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125) // Start Delay
		bar.layoutTransition.setStartDelay(LayoutTransition.APPEARING, 100) // Start Delay
		bar.layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, OvershootInterpolator())
	}


	@SuppressLint("InflateParams")
	private fun showBottomButtonSheet() {
		// Crete Bottom Button Sheet
		val sheet = HeluBottomButtonSheet.Builder(this).withTitle("Bottom button sheet title").build()
		val button = TextSheetItem("Test Button value: $counter", View.OnClickListener { showToast("Test Button clicked!") })

		// Create custom view
		val customView = layoutInflater.inflate(R.layout.bottom_button_sheet_custom_view, null)
		customView.findViewById<View>(R.id.button_decrease).setOnClickListener {
			counter--
			button.text = "Test Button value: $counter"
			sheet.invalidate()
		}
		customView.findViewById<View>(R.id.button_increase).setOnClickListener {
			counter++
			button.text = "Test Button value: $counter"
			sheet.invalidate()
		}

		sheet.retainInstance = true
		sheet.addCustomView(customView)
		sheet.addDivider()
		sheet.addButton(button)

		sheet.show(supportFragmentManager)
	}


	private fun showToast(text: String) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
	}
}
