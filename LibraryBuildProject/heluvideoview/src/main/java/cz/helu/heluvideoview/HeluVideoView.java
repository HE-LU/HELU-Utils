package cz.helu.heluvideoview;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import java.io.IOException;


@SuppressWarnings("unused")
public class HeluVideoView extends FrameLayout
{
	private TextureView mTextureView;
	private MediaPlayer mMediaPlayer;
	private Surface mSurface;
	private PlayerState mPlayerState = PlayerState.NOT_INITIALIZED;
	private String mVideoUrl;
	private String mBackupVideoUrl;
	private View mViewPlaceholder;
	private View mViewError;
	private View mViewPlay;
	private View mViewMuteOn;
	private View mViewMuteOff;
	private SeekBar mSeekBarView;
	private ScaleType mScalingMode;
	private AttachPolicy mAttachPolicy;
	private boolean mAutoPlay;
	private boolean mPauseOnVisibilityChange;
	private boolean mIsMuted;
	private boolean mLooping;
	private int mProgressUpdateInterval;
	private int mMaxVideoHeight = -1;
	private PlayerStateChangeInterface mPlayerStateChangeListener;
	private Handler mProgressHandler = new Handler();
	private Runnable mProgressRunnable = new Runnable()
	{
		public void run()
		{
			if(mMediaPlayer == null || mPlayerState != PlayerState.PLAYING)
				return;

			int time = mMediaPlayer.getCurrentPosition();

			if(mSeekBarView != null)
				mSeekBarView.setProgress(time / mProgressUpdateInterval);

			if(mPlayerStateChangeListener != null)
				mPlayerStateChangeListener.onProgressChange(time);

			mProgressHandler.postDelayed(this, mProgressUpdateInterval);
		}
	};


	public enum ScaleType
	{
		SCALE_TO_FIT_VIEW(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT),
		SCALE_TO_FIT_WITH_CROPPING(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING),
		SCALE_TO_FIT_VIDEO(3);

		private final int value;


		ScaleType(final int newValue)
		{
			value = newValue;
		}


		public int getValue() { return value; }
	}


	public enum AttachPolicy
	{
		CONTINUE(1),
		PAUSE(2),
		START_OVER(3);


		private final int value;


		AttachPolicy(final int newValue)
		{
			value = newValue;
		}


		public int getValue() { return value; }
	}


	private enum PlayerState
	{
		NOT_INITIALIZED(1),
		DESTROYED(2),
		INITIALIZED(4),
		PREPARED(8),
		PLAYING(16),
		PAUSED(32);

		private final int value;


		PlayerState(final int newValue)
		{
			value = newValue;
		}


		public int getValue() { return value; }
	}


	public interface PlayerStateChangeInterface
	{
		void onPrepare(MediaPlayer mediaPlayer);
		void onPlay();
		void onPause();
		void onComplete(MediaPlayer mediaPlayer);
		void onProgressChange(int i);
	}


	public HeluVideoView(Context context)
	{
		super(context);
	}


	public HeluVideoView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	public HeluVideoView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}


	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public HeluVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	public HeluVideoView(Builder builder)
	{
		super(builder.context);
		initFromBuilder(builder);
	}


	@Override
	protected void onVisibilityChanged(@NonNull View changedView, int visibility)
	{
		super.onVisibilityChanged(changedView, visibility);
		if(mPauseOnVisibilityChange)
		{
			if(visibility == View.VISIBLE)
			{
				if(mAutoPlay)
				{
					play();
				}
			}
			else
			{
				pause();
			}
		}
	}


	public void initFromBuilder(Builder builder)
	{
		if(mPlayerState != PlayerState.NOT_INITIALIZED && mPlayerState != PlayerState.DESTROYED)
			return;

		this.mVideoUrl = builder.videoUrl;
		this.mBackupVideoUrl = builder.backupVideoUrl;
		this.mViewPlaceholder = builder.ViewPlaceholder;
		this.mViewError = builder.ViewError;
		this.mViewPlay = builder.ViewPlay;
		this.mViewMuteOn = builder.ViewMuteOn;
		this.mViewMuteOff = builder.ViewMuteOff;
		this.mSeekBarView = builder.viewSeekBar;
		this.mScalingMode = builder.scalingMode;
		this.mAttachPolicy = builder.attachPolicy;
		this.mAutoPlay = builder.autoPlay;
		this.mPauseOnVisibilityChange = builder.pauseOnVisibilityChange;
		this.mIsMuted = builder.mutedOnStart;
		this.mLooping = builder.looping;
		this.mProgressUpdateInterval = builder.progressUpdateInterval;
		this.mMaxVideoHeight = builder.maxVideoHeight;

		// First init views! We need mTextureView initialized!
		setupViews();

		// Now we can finally setup MediaPlayer.
		setupMediaPlayer();

		setupControlViews();
		setupAudioViews();
	}


	public void reCreate()
	{
		if(mPlayerState == PlayerState.DESTROYED)
		{
			if(mViewPlaceholder != null)
				mViewPlaceholder.setVisibility(VISIBLE);

			if(mViewError != null)
				mViewError.setVisibility(GONE);

			checkControlsVisibility();
			checkVolumeVisibility();

			setupMediaPlayer();
		}
	}


	public void play()
	{
		if(mSurface == null || mMediaPlayer == null || mPlayerState.getValue() < PlayerState.PREPARED.getValue())
			return;

		if(!mMediaPlayer.isPlaying())
			mMediaPlayer.start();

		if(mPlayerStateChangeListener != null)
			mPlayerStateChangeListener.onPlay();

		mProgressHandler.postDelayed(mProgressRunnable, mProgressUpdateInterval);

		mPlayerState = PlayerState.PLAYING;

		checkControlsVisibility();
	}


	public void playFromBeginning()
	{
		if(mSurface == null || mMediaPlayer == null || mPlayerState.getValue() < PlayerState.PREPARED.getValue())
			return;

		seekToBeginning();
		play();

		mPlayerState = PlayerState.PLAYING;
	}


	public void pause()
	{
		if(mMediaPlayer == null || mPlayerState.getValue() < PlayerState.PREPARED.getValue())
			return;

		if(mMediaPlayer.isPlaying())
			mMediaPlayer.pause();

		if(mPlayerStateChangeListener != null)
			mPlayerStateChangeListener.onPause();

		mPlayerState = PlayerState.PAUSED;

		checkControlsVisibility();
	}


	public void seekToBeginning()
	{
		if(mSurface == null || mMediaPlayer == null || mPlayerState.getValue() < PlayerState.PREPARED.getValue())
			return;

		pause();
		mMediaPlayer.seekTo(1);

		mPlayerState = PlayerState.PAUSED;
	}


	public void setOnStateChangeListener(PlayerStateChangeInterface playerStateInterface)
	{
		mPlayerStateChangeListener = playerStateInterface;
	}


	public void destroy()
	{
		if(mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
			mPlayerState = PlayerState.DESTROYED;
		}
	}


	public void showVolumeButton()
	{
		checkVolumeVisibility();
	}


	public void hideVolumeButton()
	{
		if(mViewMuteOn != null)
			mViewMuteOn.setVisibility(GONE);

		if(mViewMuteOff != null)
			mViewMuteOff.setVisibility(GONE);
	}


	public MediaPlayer getMediaPlayer()
	{
		return mMediaPlayer;
	}


	private void setupViews()
	{
		// Remove all views first!
		removeAllViews();

		// Create texture video view
		mTextureView = new TextureView(getContext());
		setupSurfaceViewChangeListener();
		addView(mTextureView);

		// Create placeholder view
		if(mViewPlaceholder != null && mViewPlaceholder.getParent() == null)
			addView(mViewPlaceholder);

		// Create error view
		if(mViewError != null)
		{
			mViewError.setVisibility(GONE);
			if(mViewError.getParent() == null)
				addView(mViewError);
		}

		// Create play view
		if(mViewPlay != null && mViewPlay.getParent() == null)
			addView(mViewPlay);

		// Create mute buttons views
		if(mViewMuteOn != null && mViewMuteOff != null)
		{
			if(mViewMuteOn.getParent() == null)
				addView(mViewMuteOn);
			if(mViewMuteOff.getParent() == null)
				addView(mViewMuteOff);
		}
	}


	private void setupMediaPlayer()
	{
		if(mVideoUrl == null || mVideoUrl.isEmpty())
			return;

		try
		{
			if(mMediaPlayer != null)
			{
				mMediaPlayer.reset();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(createOnCompleteListener());
			mMediaPlayer.setOnErrorListener(createOnErrorListener());
			mMediaPlayer.setLooping(mLooping);
			mMediaPlayer.setDataSource(mVideoUrl);

			if(mSurface != null)
				mMediaPlayer.setSurface(mSurface);

			if(mScalingMode != ScaleType.SCALE_TO_FIT_VIDEO)
				mMediaPlayer.setVideoScalingMode(mScalingMode.getValue());

			// VideoView prepare listener
			setupMediaPlayerPrepareListener();

			mMediaPlayer.prepareAsync();

			mPlayerState = PlayerState.INITIALIZED;
		}
		catch(IOException | IllegalStateException e)
		{
			if(mBackupVideoUrl != null)
			{
				mVideoUrl = mBackupVideoUrl;
				mBackupVideoUrl = null;
				setupMediaPlayer();
				return;
			}
			e.printStackTrace();
		}
	}


	private void setupMediaPlayerPrepareListener()
	{
		mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared(final MediaPlayer mediaPlayer)
			{
				mPlayerState = PlayerState.PREPARED;

				if(mViewPlaceholder != null)
					mViewPlaceholder.setVisibility(GONE);

				if(mScalingMode == ScaleType.SCALE_TO_FIT_VIDEO)
					recalculateViewSize();
				else if(mScalingMode == ScaleType.SCALE_TO_FIT_WITH_CROPPING)
					recalculateSurfaceSize();

				if(mAutoPlay)
					playFromBeginning();
				else
					seekToBeginning();

				checkSeekBar();
				checkVolumeMute();
				checkControlsVisibility();
				checkVolumeVisibility();

				if(mPlayerStateChangeListener != null)
					mPlayerStateChangeListener.onPrepare(mMediaPlayer);
			}
		});
	}


	private void checkSeekBar()
	{
		if(mSeekBarView == null)
			return;

		mSeekBarView.setMax(mMediaPlayer.getDuration() / mProgressUpdateInterval);
		mSeekBarView.setProgress(mMediaPlayer.getCurrentPosition() / mProgressUpdateInterval);
		mSeekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			private boolean mWasRunning = false;


			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b)
			{
				if(mPlayerState == PlayerState.PAUSED)
					mMediaPlayer.seekTo(i * mProgressUpdateInterval);
			}


			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				mWasRunning = mMediaPlayer.isPlaying();
				pause();
			}


			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if(mPlayerStateChangeListener != null)
					mPlayerStateChangeListener.onProgressChange(mMediaPlayer.getCurrentPosition());

				if(mWasRunning)
					play();
			}
		});
	}


	private void setupControlViews()
	{
		if(mViewPlay != null)
		{
			setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					if(mPlayerState == PlayerState.PAUSED)
						play();
					else
						pause();
				}
			});
		}

		checkControlsVisibility();
	}


	private void setupAudioViews()
	{
		if(mViewMuteOn != null && mViewMuteOff != null)
		{
			OnClickListener clickListener = new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					mIsMuted = !mIsMuted;
					checkVolumeMute();
					checkVolumeVisibility();
				}
			};

			mViewMuteOn.setOnClickListener(clickListener);
			mViewMuteOff.setOnClickListener(clickListener);
		}

		checkVolumeVisibility();
	}


	private void checkVolumeMute()
	{
		if(mIsMuted)
			mMediaPlayer.setVolume(0f, 0f);
		else
			mMediaPlayer.setVolume(1f, 1f);
	}


	private MediaPlayer.OnCompletionListener createOnCompleteListener()
	{
		return new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mediaPlayer)
			{
				if(mLooping)
				{
					playFromBeginning();
				}
				else
				{
					seekToBeginning();
					checkControlsVisibility();

					if(mSeekBarView != null)
						mSeekBarView.setProgress(0);

					if(mPlayerStateChangeListener != null)
						mPlayerStateChangeListener.onComplete(mediaPlayer);
				}
			}
		};
	}


	private MediaPlayer.OnErrorListener createOnErrorListener()
	{
		return new MediaPlayer.OnErrorListener()
		{
			@Override
			public boolean onError(MediaPlayer mediaPlayer, int what, int extra)
			{
				destroy();

				if(mBackupVideoUrl != null)
				{
					mVideoUrl = mBackupVideoUrl;
					mBackupVideoUrl = null;
					reCreate();
					return true;
				}

				if(mViewPlaceholder != null)
					mViewPlaceholder.setVisibility(GONE);

				if(mViewError != null)
					mViewError.setVisibility(VISIBLE);

				return false;
			}
		};
	}


	private void checkControlsVisibility()
	{
		if(mPlayerState.getValue() < PlayerState.PREPARED.getValue())
		{
			if(mViewPlay != null)
				mViewPlay.setVisibility(GONE);
			return;
		}

		if(mViewPlay != null)
		{
			if(mPlayerState == PlayerState.PAUSED)
				mViewPlay.setVisibility(VISIBLE);
			else
				mViewPlay.setVisibility(GONE);
		}
	}


	private void checkVolumeVisibility()
	{
		if(mPlayerState.getValue() < PlayerState.PREPARED.getValue())
		{
			if(mViewMuteOn != null && mViewMuteOff != null)
			{
				mViewMuteOn.setVisibility(GONE);
				mViewMuteOff.setVisibility(GONE);
			}
			return;
		}

		if(mIsMuted)
		{
			if(mViewMuteOn != null)
				mViewMuteOn.setVisibility(VISIBLE);
			if(mViewMuteOff != null)
				mViewMuteOff.setVisibility(GONE);
		}
		else
		{
			if(mViewMuteOn != null)
				mViewMuteOn.setVisibility(GONE);
			if(mViewMuteOff != null)
				mViewMuteOff.setVisibility(VISIBLE);
		}
	}


	private void setupSurfaceViewChangeListener()
	{
		mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
		{
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
			{
				if(mMediaPlayer == null)
					return;

				mSurface = new Surface((surface));
				mMediaPlayer.setSurface(mSurface);

				if(mAttachPolicy == AttachPolicy.CONTINUE)
					play();
				else if(mAttachPolicy == AttachPolicy.START_OVER)
					playFromBeginning();
			}


			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
			{
				if(mMediaPlayer == null)
					return true;

				if(mSurface != null)
					mSurface.release();

				mSurface = null;
				mMediaPlayer.setSurface(null);
				pause();
				return true;
			}


			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
			{
			}


			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface)
			{
			}
		});
	}


	private void recalculateViewSize()
	{
		if(mMediaPlayer == null || mTextureView == null)
			return;

		int maxWidth = getWidth();
		int maxHeight = mMaxVideoHeight;
		if(mMaxVideoHeight <= 0)
			maxHeight = getMeasuredHeight();

		double scale = (maxWidth * 1.0) / (mMediaPlayer.getVideoWidth() * 1.0);
		int newWidth = maxWidth;
		int newHeight = ((int) (mMediaPlayer.getVideoHeight() * scale));

		if(maxHeight <= newHeight)
		{
			scale = (maxHeight * 1.0) / (mMediaPlayer.getVideoHeight() * 1.0);
			newWidth = ((int) (mMediaPlayer.getVideoWidth() * scale));
			newHeight = maxHeight;
		}

		LayoutParams paramsTextureView = (LayoutParams) mTextureView.getLayoutParams();
		paramsTextureView.width = newWidth;
		paramsTextureView.height = newHeight;
		paramsTextureView.gravity = Gravity.CENTER;
		mTextureView.setLayoutParams(paramsTextureView);

		MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
		layoutParams.width = newWidth;
		layoutParams.height = newHeight;
		setLayoutParams(layoutParams);

		invalidate();
		mTextureView.invalidate();
	}


	private void recalculateSurfaceSize()
	{
		if(mMediaPlayer == null || mTextureView == null)
			return;

		int maxWidth = getWidth();
		int maxHeight = mMaxVideoHeight;
		if(mMaxVideoHeight <= 0)
			maxHeight = getMeasuredHeight();

		double scale = (maxWidth * 1.0) / (mMediaPlayer.getVideoWidth() * 1.0);
		int newHeight = ((int) (mMediaPlayer.getVideoHeight() * scale));

		if(maxHeight >= newHeight)
			scale = (maxHeight * 1.0) / (mMediaPlayer.getVideoHeight() * 1.0);

		LayoutParams paramsTextureView = (LayoutParams) mTextureView.getLayoutParams();
		paramsTextureView.width = (int) (mMediaPlayer.getVideoWidth() * scale);
		paramsTextureView.height = (int) (mMediaPlayer.getVideoHeight() * scale);
		paramsTextureView.gravity = Gravity.CENTER;
		mTextureView.setLayoutParams(paramsTextureView);

		invalidate();
		mTextureView.invalidate();
	}


	public static class Builder
	{
		private Context context;
		private String videoUrl;
		private String backupVideoUrl;
		private View ViewPlaceholder;
		private View ViewError;
		private View ViewPlay;
		private View ViewMuteOn;
		private View ViewMuteOff;
		private SeekBar viewSeekBar;
		private AttachPolicy attachPolicy = AttachPolicy.PAUSE;
		private ScaleType scalingMode = ScaleType.SCALE_TO_FIT_VIEW;
		private boolean autoPlay = false;
		private boolean mutedOnStart = false;
		private boolean pauseOnVisibilityChange = true;
		private boolean looping = false;
		private int progressUpdateInterval = 1000;
		private int maxVideoHeight;


		public Builder(Context context)
		{
			this.context = context;
		}


		@SuppressWarnings("unused")
		public Builder withVideoUrl(String videoUrl)
		{
			this.videoUrl = videoUrl;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withBackupVideoUrl(String backupVideoUrl)
		{
			this.backupVideoUrl = backupVideoUrl;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withPlaceholderView(View ViewPlaceholder)
		{
			this.ViewPlaceholder = ViewPlaceholder;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withErrorView(View ViewError)
		{
			this.ViewError = ViewError;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withPlayView(View ViewPlay)
		{
			this.ViewPlay = ViewPlay;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withMuteOnView(View ViewMuteOn)
		{
			this.ViewMuteOn = ViewMuteOn;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withMuteOffView(View ViewMuteOff)
		{
			this.ViewMuteOff = ViewMuteOff;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withSeekBarView(SeekBar viewSeekBar)
		{
			this.viewSeekBar = viewSeekBar;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withScalingMode(ScaleType scalingMode)
		{
			this.scalingMode = scalingMode;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withAttachViewPolicy(AttachPolicy attachPolicy)
		{
			this.attachPolicy = attachPolicy;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withAutoPlay(boolean autoPlay)
		{
			this.autoPlay = autoPlay;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withMuteOnStart(boolean mutedOnStart)
		{
			this.mutedOnStart = mutedOnStart;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withPauseOnVisibilityChange(boolean pauseOnVisibilityChange)
		{
			this.pauseOnVisibilityChange = pauseOnVisibilityChange;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withLooping(boolean looping)
		{
			this.looping = looping;
			return this;
		}


		@SuppressWarnings("unused")
		public Builder withProgressUpdateInterval(int progressUpdateInterval)
		{
			this.progressUpdateInterval = progressUpdateInterval;
			return this;
		}


		@SuppressWarnings("unused")
		@SuppressLint("ResourceType")
		public Builder withMaxVideoHeight(@DimenRes int maxVideoHeight)
		{
			this.maxVideoHeight = context.getResources().getDimensionPixelSize(maxVideoHeight);
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView build()
		{
			return new HeluVideoView(this);
		}
	}
}
