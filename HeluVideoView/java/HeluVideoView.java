package cz.helu.heluvideoview;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Dimension;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
	private ImageView mImageViewPlaceholder;
	private ImageView mImageViewPlay;
	private ImageView mImageViewMuteOn;
	private ImageView mImageViewMuteOff;
	private SeekBar mSeekBarView;
	private ScaleType mScalingMode;
	private AttachPolicy mAttachPolicy;
	private boolean mAutoPlay;
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
			int time = mMediaPlayer.getCurrentPosition();

			if(mPlayerState != PlayerState.PLAYING)
				return;

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
		INITIALIZED(2),
		PREPARED(4),
		PLAYING(8),
		PAUSED(16);

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


	public HeluVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	public HeluVideoView(Builder builder)
	{
		super(builder.context);
		initFromBuilder(builder);
	}


	public void initFromBuilder(Builder builder)
	{
		if(mPlayerState != PlayerState.NOT_INITIALIZED)
			return;

		this.mVideoUrl = builder.videoUrl;
		this.mImageViewPlaceholder = builder.imageViewPlaceholder;
		this.mImageViewPlay = builder.imageViewPlay;
		this.mImageViewMuteOn = builder.imageViewMuteOn;
		this.mImageViewMuteOff = builder.imageViewMuteOff;
		this.mSeekBarView = builder.viewSeekBar;
		this.mScalingMode = builder.scalingMode;
		this.mAttachPolicy = builder.attachPolicy;
		this.mAutoPlay = builder.autoPlay;
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


	public void play()
	{
		if(mSurface == null || mMediaPlayer == null || mPlayerState.getValue() < PlayerState.PREPARED.getValue())
			return;

		if(!mMediaPlayer.isPlaying())
			mMediaPlayer.start();

		if(mPlayerStateChangeListener != null)
			mPlayerStateChangeListener.onPlay();

		if(mSeekBarView != null)
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
		}
	}


	private void setupViews()
	{
		// Create texture video view
		mTextureView = new TextureView(getContext());
		setupSurfaceViewChangeListener();
		addView(mTextureView);

		// Create placeholder view
		if(mImageViewPlaceholder != null)
			addView(mImageViewPlaceholder);

		// Create play view
		if(mImageViewPlay != null)
			addView(mImageViewPlay);

		// Create mute buttons views
		if(mImageViewMuteOn != null && mImageViewMuteOff != null)
		{
			addView(mImageViewMuteOn);
			addView(mImageViewMuteOff);
		}
	}


	private void setupMediaPlayer()
	{
		if(mVideoUrl == null || mVideoUrl.isEmpty())
			return;

		try
		{
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(createOnCompleteListener());
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
		catch(IOException e)
		{
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

				if(mImageViewPlaceholder != null)
					mImageViewPlaceholder.setVisibility(GONE);

				if(mScalingMode == ScaleType.SCALE_TO_FIT_VIDEO)
					recalculateViewSize();

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
				if(mPlayerState.getValue() == PlayerState.PAUSED.getValue())
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
		if(mImageViewPlay != null)
		{
			setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					if(mPlayerState.getValue() >= PlayerState.PAUSED.getValue())
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
		if(mImageViewMuteOn != null && mImageViewMuteOff != null)
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

			mImageViewMuteOn.setOnClickListener(clickListener);
			mImageViewMuteOff.setOnClickListener(clickListener);
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


	private void checkControlsVisibility()
	{
		if(mPlayerState.getValue() < PlayerState.PREPARED.getValue())
		{
			if(mImageViewPlay != null)
				mImageViewPlay.setVisibility(GONE);
			return;
		}

		if(mImageViewPlay != null)
		{
			if(mPlayerState.getValue() >= PlayerState.PAUSED.getValue())
				mImageViewPlay.setVisibility(VISIBLE);
			else
				mImageViewPlay.setVisibility(GONE);
		}
	}


	private void checkVolumeVisibility()
	{
		if(mPlayerState.getValue() < PlayerState.PREPARED.getValue())
		{
			if(mImageViewMuteOn != null && mImageViewMuteOff != null)
			{
				mImageViewMuteOn.setVisibility(GONE);
				mImageViewMuteOff.setVisibility(GONE);
			}
			return;
		}

		if(mImageViewMuteOn != null && mImageViewMuteOff != null)
		{
			if(mIsMuted)
			{
				mImageViewMuteOn.setVisibility(VISIBLE);
				mImageViewMuteOff.setVisibility(GONE);
			}
			else
			{
				mImageViewMuteOn.setVisibility(GONE);
				mImageViewMuteOff.setVisibility(VISIBLE);
			}
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

		FrameLayout.LayoutParams paramsTextureView = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
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


	public static class Builder
	{
		private Context context;
		private String videoUrl;
		private ImageView imageViewPlaceholder;
		private ImageView imageViewPlay;
		private ImageView imageViewMuteOn;
		private ImageView imageViewMuteOff;
		private SeekBar viewSeekBar;
		private AttachPolicy attachPolicy = AttachPolicy.PAUSE;
		private ScaleType scalingMode = ScaleType.SCALE_TO_FIT_VIEW;
		private boolean autoPlay = false;
		private boolean mutedOnStart = false;
		private boolean looping = false;
		private int progressUpdateInterval = 1000;
		private int maxVideoHeight;


		public Builder(Context context)
		{
			this.context = context;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withVideoUrl(String videoUrl)
		{
			this.videoUrl = videoUrl;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withPlaceholderView(ImageView imageViewPlaceholder)
		{
			this.imageViewPlaceholder = imageViewPlaceholder;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withPlayView(ImageView imageViewPlay)
		{
			this.imageViewPlay = imageViewPlay;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withMuteOnView(ImageView imageViewMuteOn)
		{
			this.imageViewMuteOn = imageViewMuteOn;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withMuteOffView(ImageView imageViewMuteOff)
		{
			this.imageViewMuteOff = imageViewMuteOff;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withSeekBarView(SeekBar viewSeekBar)
		{
			this.viewSeekBar = viewSeekBar;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withScalingMode(ScaleType scalingMode)
		{
			this.scalingMode = scalingMode;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withAttachViewPolicy(AttachPolicy attachPolicy)
		{
			this.attachPolicy = attachPolicy;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withAutoPlay(boolean autoPlay)
		{
			this.autoPlay = autoPlay;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withMuteOnStart(boolean mutedOnStart)
		{
			this.mutedOnStart = mutedOnStart;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withLooping(boolean looping)
		{
			this.looping = looping;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withProgressUpdateInterval(int progressUpdateInterval)
		{
			this.progressUpdateInterval = progressUpdateInterval;
			return this;
		}


		@SuppressWarnings("unused")
		public HeluVideoView.Builder withMaxVideoHeight(@Dimension int maxVideoHeight)
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

