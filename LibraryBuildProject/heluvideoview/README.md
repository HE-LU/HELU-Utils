# HeluVideoView 1.1.0
This library provides you very easy to use and intuitive API to create new Video View. You can create your own play, mute, placeholder, seekbar views, which you can then provide to HeluVideoView.

You need to use provided Builder class to create new HeluVideoView.

![Alt text](./extras/HeluVideoView.png?raw=true "HeluVideoView")


## Gradle:
```groovy
compile 'cz.helu.android:heluvideoview:1.1.0'
```


## Parameters

### Builder:
* **withVideoUrl(String)**
  URL of video load.
  
* **withBackupVideoUrl(String)**
  Backup URL that will be loaded in case that loading of the first video failed.
  
* **withPlaceholderView(View)**
  View with some placeholder image set.
  *(Default is ```null```)*
    
* **withErrorView(View)**
  View with some error image set. Will be used in case video wont be loaded, or any error occurs.
  *(Default is ```null```)*
  
* **withPlayView(View)**
  View with some play button image set.
  *(Default is ```null```)*
  
* **withMuteOnView(View)**
  View with some mute on image set. This View will be displayed when the sound is muted.
  *(Default is ```null```)*
  
* **withMuteOffView(View)**
  View with some mute off image set. This View will be displayed when the sound is un-muted.
  *(Default is ```null```)*
  
* **withSeekBarView(SeekBar)**
  SeekBar view which will be used for navigation.
  *(Default is ```null```)*
  
* **withScalingMode(ScaleType)**
  Specify how the view should be resized when prepare is called.
  ```
  enum ScaleType {
  	SCALE_TO_FIT_VIEW,
  	SCALE_TO_FIT_WITH_CROPPING,
  	SCALE_TO_FIT_VIDEO;
  }
  ```
  *(Default is ```SCALE_TO_FIT_VIEW```)*
  
* **withAttachViewPolicy(AttachPolicy)**
  Specify what should happen when the view is re-attached.
  ```
  enum AttachPolicy {
  	CONTINUE,
  	PAUSE,
  	START_OVER;
  }
  ```
  *(Default is ```PAUSE```)*
  
* **withAutoPlay(boolean)**
  If set to ```true```, video will start automaticly playing once prepare is called.
   *(Default is ```false```)*
  
* **withMuteOnStart(boolean)**
  If set to ```true```, video will start automaticly muted once prepare is called.
   *(Default is ```false```)*
  
* **withLooping(boolean)**
  If set to ```true```, video will start from beginning once onCompleteListener is called.
   *(Default is ```false```)*
  
* **withProgressUpdateInterval(int)**
  This values specify how often should be progress updated. (This affects how often is onProgressChange called, how often is seekbar state updated, etc.)
   *(Default is ```1000ms```)*

* **withMaxVideoHeight(int)**
  If ```value > 0 && ScaleType == SCALE_TO_FIT_VIDEO``` then this is the max height on which the view will be scaled.
   *(Default is ```0```)*

### Instance:
* **initFromBuilder(Builder)**
  Use this in case you have the HeluVideoView already created in layout file, and you just want to initialize it.
  
* **destroy()**
  Call this method everytime you want to release mediaPlayer.
  
* **reCreate()**
  In case you called destroy() and you want to use this view again, you can call this method to initialize MediaPlayer again.
  
* **play()**
  Start video playback.
  
* **playFromBeginning()**
  Seek to beginning and start video playback.
  
* **pause()**
  Pause video playback.
  
* **seekToBeginning()**
  Pause and seek to first frame.
    
* **showVolumeButton()**
  Show the volume button.
  
* **hideVolumeButton()**
  Hide the volume button.
  
* **setOnStateChangeListener(PlayerStateChangeInterface)**
  This interface provides you method about the mediaPlayer state change. You can use it for implementing your own logic.
  ```
  interface PlayerStateChangeInterface
	{
		void onPrepare(MediaPlayer mediaPlayer);
		void onPlay();
		void onPause();
		void onComplete(MediaPlayer mediaPlayer);
		void onProgressChange(int i);
	}
  ```

## Usage
You have to use the Builder class for creating or initializing new HeluVideoView.

```java
// Create and prepare all view we want to use.
HeluVideoView videoView = (HeluVideoView) findViewById(R.id.helu_video_view);
ImageView placeholderView = ViewGeneratorUtility.generatePlaceholderView(getContext());
ImageView playView = ViewGeneratorUtility.generatePlayButtonView(getContext());
ImageView muteOnView = ViewGeneratorUtility.generateMuteOnView(getContext());
ImageView muteOffView = ViewGeneratorUtility.generateMuteOffView(getContext());
SeekBar seekBarView = (SeekBar) findViewById(R.id.seek_bar);

// Create builder.
HeluVideoView.Builder builder = new HeluVideoView.Builder(getContext())
		.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO)
		.withVideoUrl("Some URL")
		.withBackupVideoUrl("Some backup URL")
		.withPlaceholderView(placeholderView)
		.withPlayView(playView)
		.withMuteOnView(muteOnView)
		.withMuteOffView(muteOffView)
		.withSeekBarView(seekBarView)
		.withAutoPlay(true)
		.withMuteOnStart(true)
		.withProgressUpdateInterval(75);

// Init already created HeluVideoView from the builder.
videoView.initFromBuilder(builder);

// Set OnStateChangeListener if necessary.
videoView.setOnStateChangeListener(new HeluVideoView.PlayerStateChangeInterface() {
	@Override
	public void onPrepare(MediaPlayer mediaPlayer)
	{}

	@Override
	public void onPlay()
	{
		videoView.hideVolumeButton()
	}

	@Override
	public void onPause()
	{
		videoView.showVolumeButton()
	}

	@Override
	public void onComplete(MediaPlayer mediaPlayer)
	{}

	@Override
	public void onProgressChange(int i)
	{}
});

```