package cz.helu.heluvideoview


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Handler
import android.support.annotation.DimenRes
import android.util.AttributeSet
import android.view.*
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.SeekBar
import java.io.IOException


class HeluVideoView : FrameLayout {
	private var textureView: TextureView? = null
	var mediaPlayer: MediaPlayer? = null
	private var surface: Surface? = null
	private var playerState = PlayerState.NOT_INITIALIZED
	private var videoUrl: String? = null
	private var backupVideoUrl: String? = null
	private var viewPlaceholder: View? = null
	private var viewError: View? = null
	private var viewPlay: View? = null
	private var viewPause: View? = null
	private var viewReplay: View? = null
	private var viewMuteOn: View? = null
	private var viewMuteOff: View? = null
	private var seekBarView: SeekBar? = null
	private var scalingMode: ScaleType? = null
	private var attachPolicy: AttachPolicy? = null
	private var autoPlay: Boolean = false
	private var pauseOnVisibilityChange: Boolean = false
	private var isMuted: Boolean = false
	private var looping: Boolean = false
	private var progressUpdateInterval: Int = 1000
	private var maxVideoHeight = -1
	private var playerStateChangeListener: PlayerStateChangeInterface? = null
	private val progressHandler = Handler()
	private val progressRunnable = object : Runnable {
		override fun run() {
			if (playerState != PlayerState.PLAYING)
				return

			val time = mediaPlayer?.currentPosition ?: 0

			seekBarView?.progress = time
			playerStateChangeListener?.onProgressChange(time)
			progressHandler.postDelayed(this, progressUpdateInterval.toLong())
		}
	}


	enum class ScaleType(val value: Int) {
		SCALE_TO_FIT_VIEW(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT),
		SCALE_TO_FIT_WITH_CROPPING(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING),
		SCALE_TO_FIT_VIDEO(3)
	}


	enum class AttachPolicy(val value: Int) {
		CONTINUE(1),
		PAUSE(2),
		START_OVER(3)
	}


	private enum class PlayerState(val value: Int) {
		NOT_INITIALIZED(1),
		DESTROYED(2),
		INITIALIZED(4),
		PREPARED(8),
		PLAYING(16),
		PAUSED(32)
	}


	interface PlayerStateChangeInterface {
		fun onPrepare(mediaPlayer: MediaPlayer?)
		fun onPlay()
		fun onPause()
		fun onComplete(mediaPlayer: MediaPlayer)
		fun onProgressChange(progress: Int)
	}


	constructor(context: Context) : super(context)


	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


	constructor(builder: Builder) : super(builder.context) {
		initFromBuilder(builder)
	}


	override fun onVisibilityChanged(changedView: View, visibility: Int) {
		super.onVisibilityChanged(changedView, visibility)
		if (pauseOnVisibilityChange) {
			if (visibility == View.VISIBLE) {
				if (autoPlay) {
					play()
				}
			} else {
				pause()
			}
		}
	}


	fun initFromBuilder(builder: Builder) {
		if (playerState != PlayerState.NOT_INITIALIZED && playerState != PlayerState.DESTROYED)
			return

		this.videoUrl = builder.videoUrl
		this.backupVideoUrl = builder.backupVideoUrl
		this.viewPlaceholder = builder.viewPlaceholder
		this.viewError = builder.viewError
		this.viewPlay = builder.viewPlay
		this.viewPause = builder.viewPause
		this.viewReplay = builder.viewReplay
		this.viewMuteOn = builder.viewMuteOn
		this.viewMuteOff = builder.viewMuteOff
		this.seekBarView = builder.viewSeekBar
		this.scalingMode = builder.scalingMode
		this.attachPolicy = builder.attachPolicy
		this.autoPlay = builder.autoPlay
		this.pauseOnVisibilityChange = builder.pauseOnVisibilityChange
		this.isMuted = builder.mutedOnStart
		this.looping = builder.looping
		this.progressUpdateInterval = builder.progressUpdateInterval
		this.maxVideoHeight = builder.maxVideoHeight

		// First init views! We need textureView initialized
		setupViews()

		// Now we can finally setup MediaPlayer
		setupMediaPlayer()

		setupControlViews()
		setupAudioViews()
	}


	@Suppress("MemberVisibilityCanBePrivate")
	fun reCreate() {
		if (playerState == PlayerState.DESTROYED) {
			viewPlaceholder?.visibility = View.VISIBLE
			viewError?.visibility = View.GONE

			checkControlsVisibility()
			checkVolumeVisibility()

			setupMediaPlayer()
		}
	}


	fun play() {
		if (surface == null || mediaPlayer == null || playerState.value < PlayerState.PREPARED.value)
			return

		if (mediaPlayer?.isPlaying == false)
			mediaPlayer?.start()

		playerStateChangeListener?.onPlay()
		progressHandler.postDelayed(progressRunnable, progressUpdateInterval.toLong())
		playerState = PlayerState.PLAYING

		checkControlsVisibility()
	}


	fun playFromBeginning() {
		if (surface == null || mediaPlayer == null || playerState.value < PlayerState.PREPARED.value)
			return

		seekToBeginning()
		play()

		playerState = PlayerState.PLAYING
	}


	fun pause() {
		if (mediaPlayer == null || playerState.value < PlayerState.PREPARED.value)
			return

		if (mediaPlayer?.isPlaying == true)
			mediaPlayer?.pause()

		playerStateChangeListener?.onPause()
		playerState = PlayerState.PAUSED

		checkControlsVisibility()
	}


	@Suppress("MemberVisibilityCanBePrivate")
	fun seekToBeginning() {
		if (surface == null || mediaPlayer == null || playerState.value < PlayerState.PREPARED.value)
			return

		pause()
		mediaPlayer?.seekTo(1)

		playerState = PlayerState.PAUSED
	}


	@Suppress("unused")
	fun setOnStateChangeListener(playerStateInterface: PlayerStateChangeInterface?) {
		playerStateChangeListener = playerStateInterface
	}


	@Suppress("MemberVisibilityCanBePrivate")
	fun destroy() {
		mediaPlayer?.release()
		mediaPlayer = null
		playerState = PlayerState.DESTROYED
	}


	@Suppress("unused")
	fun showVolumeButton() {
		checkVolumeVisibility()
	}


	@Suppress("unused")
	fun hideVolumeButton() {
		viewMuteOn?.visibility = View.GONE
		viewMuteOff?.visibility = View.GONE
	}


	private fun setupViews() {
		// Remove all views first
		removeAllViews()

		// Create texture video view
		textureView = TextureView(context)
		setupSurfaceViewChangeListener()
		addView(textureView)

		// Create placeholder view
		if (viewPlaceholder != null && viewPlaceholder?.parent == null)
			addView(viewPlaceholder)

		// Create error view
		if (viewError != null) {
			viewError?.visibility = View.GONE
			if (viewError?.parent == null)
				addView(viewError)
		}

		// Create play view
		if (viewPlay != null && viewPlay?.parent == null)
			addView(viewPlay)

		// Create pause view
		if (viewPause != null && viewPause?.parent == null)
			addView(viewPause)

		// Create pause view
		if (viewReplay != null && viewReplay?.parent == null)
			addView(viewReplay)

		// Create mute buttons views
		if (viewMuteOn != null && viewMuteOff != null) {
			if (viewMuteOn?.parent == null)
				addView(viewMuteOn)
			if (viewMuteOff?.parent == null)
				addView(viewMuteOff)
		}
	}


	private fun setupMediaPlayer() {
		if (videoUrl?.isEmpty() == true)
			return

		try {
			mediaPlayer?.reset()
			mediaPlayer?.release()
			mediaPlayer = MediaPlayer()
			mediaPlayer?.setOnCompletionListener(createOnCompleteListener())
			mediaPlayer?.setOnErrorListener(createOnErrorListener())
			mediaPlayer?.isLooping = looping
			mediaPlayer?.setDataSource(videoUrl)

			if (surface != null)
				mediaPlayer?.setSurface(surface)

			if (scalingMode != ScaleType.SCALE_TO_FIT_VIDEO)
				scalingMode?.value?.let { mediaPlayer?.setVideoScalingMode(it) }

			// VideoView prepare listener
			setupMediaPlayerPrepareListener()

			mediaPlayer?.prepareAsync()

			playerState = PlayerState.INITIALIZED
		} catch (e: IOException) {
			if (backupVideoUrl != null) {
				videoUrl = backupVideoUrl
				backupVideoUrl = null
				setupMediaPlayer()
				return
			}
			e.printStackTrace()
		} catch (e: IllegalStateException) {
			if (backupVideoUrl != null) {
				videoUrl = backupVideoUrl
				backupVideoUrl = null
				setupMediaPlayer()
				return
			}
			e.printStackTrace()
		}
	}


	private fun setupMediaPlayerPrepareListener() {
		mediaPlayer?.setOnPreparedListener { mediaPlayer ->
			playerState = PlayerState.PREPARED
			viewPlaceholder?.visibility = View.GONE

			if (scalingMode == ScaleType.SCALE_TO_FIT_VIDEO)
				recalculateViewSize()
			else if (scalingMode == ScaleType.SCALE_TO_FIT_WITH_CROPPING)
				recalculateSurfaceSize()

			if (autoPlay)
				playFromBeginning()
			else
				seekToBeginning()

			checkSeekBar()
			checkVolumeMute()
			checkControlsVisibility()
			checkVolumeVisibility()

			playerStateChangeListener?.onPrepare(mediaPlayer)
		}
	}


	private fun checkSeekBar() {
		seekBarView?.max = mediaPlayer?.duration ?: 0
		seekBarView?.progress = mediaPlayer?.currentPosition ?: 0
		seekBarView?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			private var wasRunning = false


			override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
				if (playerState == PlayerState.PAUSED)
					mediaPlayer?.seekTo(i)
			}


			override fun onStartTrackingTouch(seekBar: SeekBar) {
				wasRunning = mediaPlayer?.isPlaying ?: false
				pause()
			}


			override fun onStopTrackingTouch(seekBar: SeekBar) {
				if (playerStateChangeListener != null)
					playerStateChangeListener?.onProgressChange(mediaPlayer?.currentPosition ?: 0)

				if (wasRunning)
					play()
			}
		})
	}


	private fun setupControlViews() {
		val playPauseClickListener = OnClickListener {
			if (playerState == PlayerState.PAUSED)
				play()
			else
				pause()
		}

		setOnClickListener(playPauseClickListener)
		viewPlay?.setOnClickListener(playPauseClickListener)
		viewPause?.setOnClickListener(playPauseClickListener)

		viewReplay?.setOnClickListener {
			seekToBeginning()
			play()
		}

		checkControlsVisibility()
	}


	private fun setupAudioViews() {
		val clickListener = OnClickListener {
			isMuted = !isMuted
			checkVolumeMute()
			checkVolumeVisibility()
		}

		viewMuteOn?.setOnClickListener(clickListener)
		viewMuteOff?.setOnClickListener(clickListener)

		checkVolumeVisibility()
	}


	private fun checkVolumeMute() {
		if (isMuted)
			mediaPlayer?.setVolume(0f, 0f)
		else
			mediaPlayer?.setVolume(1f, 1f)
	}


	private fun createOnCompleteListener(): MediaPlayer.OnCompletionListener {
		return MediaPlayer.OnCompletionListener { mediaPlayer ->
			if (looping) {
				playFromBeginning()
			} else {
				seekToBeginning()
				checkControlsVisibility()

				if (seekBarView != null)
					seekBarView?.progress = 0

				if (playerStateChangeListener != null)
					playerStateChangeListener?.onComplete(mediaPlayer)
			}
		}
	}


	private fun createOnErrorListener(): MediaPlayer.OnErrorListener {
		return MediaPlayer.OnErrorListener { _, _, _ ->
			destroy()

			if (backupVideoUrl != null) {
				videoUrl = backupVideoUrl
				backupVideoUrl = null
				reCreate()
				return@OnErrorListener true
			}

			if (viewPlaceholder != null)
				viewPlaceholder?.visibility = View.GONE

			if (viewError != null)
				viewError?.visibility = View.VISIBLE

			false
		}
	}


	private fun checkControlsVisibility() {
		if (playerState.value < PlayerState.PREPARED.value) {
			viewPlay?.visibility = View.GONE
			viewPause?.visibility = View.GONE
			viewReplay?.visibility = View.GONE
			return
		} else {
			viewReplay?.visibility = View.VISIBLE
		}

		if (playerState == PlayerState.PAUSED) {
			viewPlay?.visibility = View.VISIBLE
			viewPause?.visibility = View.GONE
		} else {
			viewPlay?.visibility = View.GONE
			viewPause?.visibility = View.VISIBLE
		}
	}


	private fun checkVolumeVisibility() {
		if (playerState.value < PlayerState.PREPARED.value) {
			viewMuteOn?.visibility = View.GONE
			viewMuteOff?.visibility = View.GONE
			return
		}

		if (isMuted) {
			viewMuteOn?.visibility = View.VISIBLE
			viewMuteOff?.visibility = View.GONE
		} else {
			viewMuteOn?.visibility = View.GONE
			viewMuteOff?.visibility = View.VISIBLE
		}
	}


	private fun setupSurfaceViewChangeListener() {
		textureView?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
			override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
				if (mediaPlayer == null)
					return

				this@HeluVideoView.surface = Surface(surface)
				mediaPlayer?.setSurface(this@HeluVideoView.surface)

				if (attachPolicy == AttachPolicy.CONTINUE)
					play()
				else if (attachPolicy == AttachPolicy.START_OVER)
					playFromBeginning()
			}


			override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
				if (mediaPlayer == null)
					return true

				if (this@HeluVideoView.surface != null)
					this@HeluVideoView.surface?.release()

				this@HeluVideoView.surface = null
				mediaPlayer?.setSurface(null)
				pause()
				return true
			}


			override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}


			override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
		}
	}


	private fun recalculateViewSize() {
		if (mediaPlayer == null || textureView == null)
			return

		val maxWidth = width
		var maxHeight = maxVideoHeight
		if (maxVideoHeight <= 0)
			maxHeight = -1

		var scale = maxWidth.toDouble() / (mediaPlayer?.videoWidth ?: 1).toDouble()
		var newWidth = maxWidth
		var newHeight: Int = ((mediaPlayer?.videoHeight ?: 1) * scale).toInt()

		if (maxHeight <= newHeight && maxHeight != -1) {
			scale = maxHeight.toDouble() / (mediaPlayer?.videoHeight ?: 1).toDouble()
			newWidth = ((mediaPlayer?.videoWidth ?: 1) * scale).toInt()
			newHeight = maxHeight
		}

		val paramsTextureView = textureView?.layoutParams as FrameLayout.LayoutParams
		paramsTextureView.width = newWidth
		paramsTextureView.height = newHeight
		paramsTextureView.gravity = Gravity.CENTER
		textureView?.layoutParams = paramsTextureView

		val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
		layoutParams.width = newWidth
		layoutParams.height = newHeight
		setLayoutParams(layoutParams)

		invalidate()
		textureView?.invalidate()
	}


	private fun recalculateSurfaceSize() {
		if (mediaPlayer == null || textureView == null)
			return

		val maxWidth = width
		var maxHeight = maxVideoHeight
		if (maxVideoHeight <= 0)
			maxHeight = measuredHeight

		var scale = maxWidth.toFloat() / (mediaPlayer?.videoWidth ?: 1).toFloat()
		val newHeight: Int = ((mediaPlayer?.videoHeight ?: 1) * scale).toInt()

		if (maxHeight >= newHeight)
			scale = maxHeight.toFloat() / (mediaPlayer?.videoHeight ?: 1).toFloat()

		val paramsTextureView = textureView?.layoutParams as FrameLayout.LayoutParams
		paramsTextureView.width = ((mediaPlayer?.videoWidth ?: 1) * scale).toInt()
		paramsTextureView.height = ((mediaPlayer?.videoHeight ?: 1) * scale).toInt()
		paramsTextureView.gravity = Gravity.CENTER
		textureView?.layoutParams = paramsTextureView

		invalidate()
		textureView?.invalidate()
	}


	@Suppress("unused")
	class Builder(internal val context: Context) {
		internal var videoUrl: String? = null
		internal var backupVideoUrl: String? = null
		internal var viewPlaceholder: View? = null
		internal var viewError: View? = null
		internal var viewPlay: View? = null
		internal var viewPause: View? = null
		internal var viewReplay: View? = null
		internal var viewMuteOn: View? = null
		internal var viewMuteOff: View? = null
		internal var viewSeekBar: SeekBar? = null
		internal var attachPolicy = AttachPolicy.PAUSE
		internal var scalingMode = ScaleType.SCALE_TO_FIT_VIEW
		internal var autoPlay = false
		internal var mutedOnStart = false
		internal var pauseOnVisibilityChange = true
		internal var looping = false
		internal var progressUpdateInterval = 1000
		internal var maxVideoHeight: Int = 0


		fun withVideoUrl(videoUrl: String): Builder {
			this.videoUrl = videoUrl
			return this
		}


		fun withBackupVideoUrl(backupVideoUrl: String): Builder {
			this.backupVideoUrl = backupVideoUrl
			return this
		}


		fun withPlaceholderView(ViewPlaceholder: View): Builder {
			this.viewPlaceholder = ViewPlaceholder
			return this
		}


		fun withErrorView(ViewError: View): Builder {
			this.viewError = ViewError
			return this
		}


		fun withPlayView(ViewPlay: View): Builder {
			this.viewPlay = ViewPlay
			return this
		}


		fun withPauseView(ViewPause: View): Builder {
			this.viewPause = ViewPause
			return this
		}


		fun withReplayView(ViewReplay: View): Builder {
			this.viewReplay = ViewReplay
			return this
		}


		fun withMuteOnView(ViewMuteOn: View): Builder {
			this.viewMuteOn = ViewMuteOn
			return this
		}


		fun withMuteOffView(ViewMuteOff: View): Builder {
			this.viewMuteOff = ViewMuteOff
			return this
		}


		fun withSeekBarView(viewSeekBar: SeekBar): Builder {
			this.viewSeekBar = viewSeekBar
			return this
		}


		fun withScalingMode(scalingMode: ScaleType): Builder {
			this.scalingMode = scalingMode
			return this
		}


		fun withAttachViewPolicy(attachPolicy: AttachPolicy): Builder {
			this.attachPolicy = attachPolicy
			return this
		}


		fun withAutoPlay(autoPlay: Boolean): Builder {
			this.autoPlay = autoPlay
			return this
		}


		fun withMuteOnStart(mutedOnStart: Boolean): Builder {
			this.mutedOnStart = mutedOnStart
			return this
		}


		fun withPauseOnVisibilityChange(pauseOnVisibilityChange: Boolean): Builder {
			this.pauseOnVisibilityChange = pauseOnVisibilityChange
			return this
		}


		fun withLooping(looping: Boolean): Builder {
			this.looping = looping
			return this
		}


		fun withProgressUpdateInterval(progressUpdateIntervalMs: Int): Builder {
			this.progressUpdateInterval = progressUpdateIntervalMs
			return this
		}


		@SuppressLint("ResourceType")
		fun withMaxVideoHeight(@DimenRes maxVideoHeight: Int): Builder {
			this.maxVideoHeight = context.resources.getDimensionPixelSize(maxVideoHeight)
			return this
		}


		fun build(): HeluVideoView {
			return HeluVideoView(this)
		}
	}
}
