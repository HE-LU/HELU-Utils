package cz.helu.example

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.*
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

		setupTabBar()
		setupImageView()
		setupBottomButtonSheet()
		setupVideoView()
	}


	private fun setupTabBar() {
		val bar = findViewById<HeluCollapsingTabBar>(R.id.helu_tab_bar)

		// Setup drawables
		val alignLeft = ContextCompat.getDrawable(this, R.drawable.ic_align_left)!!
		val alignLeftSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_left_selected)!!
		val alignCenter = ContextCompat.getDrawable(this, R.drawable.ic_align_center)!!
		val alignCenterSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_center_selected)!!
		val alignRight = ContextCompat.getDrawable(this, R.drawable.ic_align_right)!!
		val alignRightSelected = ContextCompat.getDrawable(this, R.drawable.ic_align_right_selected)!!

		// Add buttons
		val builder = HeluCollapsingTabBar.Builder(this)
		builder.addButton(alignLeftSelected, alignLeft, View.OnClickListener { showToast("Left") })
		builder.addButton(alignCenterSelected, alignCenter, View.OnClickListener { showToast("Center") })
		builder.addButton(alignRightSelected, alignRight, View.OnClickListener { showToast("Right") })

		// Setup bar
		bar.initFromBuilder(builder)
		bar.setSelectedPosition(1)

		// Customize animation using LayoutTransition
		bar.layoutTransition.setDuration(150) // Translation duration
		bar.layoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 200) // Translation duration
		bar.layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 125) // Start Delay
		bar.layoutTransition.setStartDelay(LayoutTransition.APPEARING, 100) // Start Delay
		bar.layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, OvershootInterpolator())
	}


	private fun setupImageView() {
		Handler().postDelayed({
			val parallaxDisabled = findViewById<HeluParallaxView>(R.id.parallax_image_disabled)
			parallaxDisabled.scaleType = ImageView.ScaleType.CENTER_INSIDE
			parallaxDisabled.scale = 0.15f
			parallaxDisabled.disableParallax()
		}, 5000)
	}


	private fun setupBottomButtonSheet() {
		val sheetButton = findViewById<Button>(R.id.bottom_button_sheet_button)
		val sheetSimpleButton = findViewById<Button>(R.id.bottom_button_sheet_simple_button)
		val sheetComplexButton = findViewById<Button>(R.id.bottom_button_sheet_complex_button)

		sheetButton.setOnClickListener { showBottomButtonSheet() }
		sheetSimpleButton.setOnClickListener { showBottomButtonSheetSimple() }
		sheetComplexButton.setOnClickListener { showBottomButtonSheetComplex() }
	}


	private fun setupVideoView() {
		setupVideoViewWithControls()
		val videoViewFitVideo = findViewById<HeluVideoView>(R.id.video_view_fit_video)
		val videoViewFitView = findViewById<HeluVideoView>(R.id.video_view_fit_view)
		val videoViewWitchCropping = findViewById<HeluVideoView>(R.id.video_view_with_cropping)
		val videoViewWitchCropping2 = findViewById<HeluVideoView>(R.id.video_view_with_cropping_2)

		val builder = HeluVideoView.Builder(this)
				.withVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
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


	private fun setupVideoViewWithControls() {
		val videoViewWithControls = findViewById<HeluVideoView>(R.id.video_view_with_controls)
		val playView = findViewById<ImageView>(R.id.video_play)
		val pauseView = findViewById<ImageView>(R.id.video_pause)
		val replayView = findViewById<ImageView>(R.id.video_replay)
		val seekBarView = findViewById<SeekBar>(R.id.video_seek_bar)

		val builder = HeluVideoView.Builder(this)
				.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO)
				.withVideoUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
				.withBackupVideoUrl("http://techslides.com/demos/sample-videos/small.mp4")
				.withPlayView(playView)
				.withPauseView(pauseView)
				.withReplayView(replayView)
				.withSeekBarView(seekBarView)
				.withProgressUpdateInterval(50)
				.withAutoPlay(false)
				.withMuteOnStart(true)
				.withLooping(true)
				.withAudioFocusHandling(true)

		videoViewWithControls.initFromBuilder(builder)
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


	@SuppressLint("InflateParams")
	private fun showBottomButtonSheetSimple() {
		val sheet = HeluBottomButtonSheet.Builder(this).build()

		sheet.addButton("First button", View.OnClickListener {
			Log.d("TAG", "First button click")
		})

		sheet.addButton("Second button", View.OnClickListener {
			Log.d("TAG", "First button click")
		})

		sheet.show(supportFragmentManager)
	}


	private fun showBottomButtonSheetComplex() {
		val sheet = HeluBottomButtonSheet.Builder(this)
				.withTitle("Complex sheet")
				.withItemHeightRes(R.dimen.global_size_36)
				.withTitleTextSizeRes(R.dimen.global_text_20)
				.build()

		sheet.addButton(R.drawable.ic_edit, "Edit", View.OnClickListener { /* click */ })
		sheet.addButton(R.drawable.ic_delete, "Delete", View.OnClickListener { /* click */ })
		sheet.addDivider()
		sheet.addCustomView(EditText(this).apply { hint = "Custom EditText view" })

		sheet.show(supportFragmentManager)
	}


	private fun showToast(text: String) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
	}
}
