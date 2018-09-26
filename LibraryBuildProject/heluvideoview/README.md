# HeluVideoView 2.1.1
This library provides you very easy to use and intuitive API to create new Video View. You can create your own play, mute, placeholder, seekbar views, which you can then provide to HeluVideoView.

You need to use provided Builder class to create new HeluVideoView.

![Alt text](./extras/HeluVideoView.jpg?raw=true "HeluVideoView")
![Alt text](./extras/HeluVideoView.gif?raw=true "HeluVideoView")


## Gradle:
```groovy
implementation 'cz.helu.android:heluvideoview:2.1.1'
```


## Methods

### Builder:
* **withVideoUrl(url: String)**
  URL of video load.
  
* **withBackupVideoUrl(url: String)**
  Backup URL that will be loaded in case that loading of the first video failed.
  
* **withPlaceholderView(placeholder: View)**
  View with some placeholder image set.
  *(Default is ```null```)*
    
* **withErrorView(error: View)**
  View with some error image set. Will be used in case video wont be loaded, or any error occurs.
  *(Default is ```null```)*
  
* **withPlayView(play: View)**
  View with some play button image set.
  *(Default is ```null```)*
  
* **withPauseView(play: View)**
  View with some pause button image set.
  *(Default is ```null```)*
  
* **withReplayView(play: View)**
  View with some replay button image set.
  *(Default is ```null```)*
  
* **withMuteOnView(muteOn: View)**
  View with some mute on image set. This View will be displayed when the sound is muted.
  *(Default is ```null```)*
  
* **withMuteOffView(muteOff: View)**
  View with some mute off image set. This View will be displayed when the sound is un-muted.
  *(Default is ```null```)*
  
* **withSeekBarView(seekbar: SeekBar)**
  SeekBar view which will be used for navigation.
  *(Default is ```null```)*
  
* **withScalingMode(scaleType: ScaleType)**
  Specify how the view should be resized when prepare is called.
  ```
  enum ScaleType {
  	SCALE_TO_FIT_VIEW,
  	SCALE_TO_FIT_WITH_CROPPING,
  	SCALE_TO_FIT_VIDEO;
  }
  ```
  *(Default is ```SCALE_TO_FIT_VIEW```)*
  
* **withAttachViewPolicy(attachPolicy: AttachPolicy)**
  Specify what should happen when the view is re-attached.
  ```
  enum AttachPolicy {
  	CONTINUE,
  	PAUSE,
  	START_OVER;
  }
  ```
  *(Default is ```PAUSE```)*
  
* **withAutoPlay(autoplay: Boolean)**
  If set to ```true```, video will start automatically playing once prepare is called.
   *(Default is ```false```)*
  
* **withPauseOnVisibilityChange(pause: Boolean)**
  If set to ```true```, video will automatically pause and play once View change its visibility. For example, when app goes to background, or another activity is started above the current one.
   *(Default is ```true```)*
  
* **withMuteOnStart(muteOnStart: Boolean)**
  If set to ```true```, video will start automaticly muted once prepare is called.
   *(Default is ```false```)*
  
* **withLooping(looping: Boolean)**
  If set to ```true```, video will start from beginning once onCompleteListener is called.
   *(Default is ```false```)*
  
* **withProgressUpdateInterval(interval: Int)**
  This values specify how often should be progress updated. (This affects how often is onProgressChange called, how often is seekbar state updated, etc.)
   *(Default is ```1000ms```)*

* **withMaxVideoHeight(maxHeight: Int)**
  If ```value > 0 && ScaleType == SCALE_TO_FIT_VIDEO``` then this is the max height on which the view will be scaled.
   *(Default is ```0```)*

### Instance:
* **initFromBuilder(builder: Builder)**
  Use this in case you have the HeluVideoView already created in layout file, and you just want to initialize it.
  
* **destroy()**
  Call this method everytime you want to release mediaPlayer.
  
* **reCreate()**
  In case you called destroy() and you want to use this view again, you can call this method to initialize MediaPlayer again.
  
* **recomputeLayout(newMaxWidth: Int?, newMaxHeight: Int?)**
  You can call this method to recompute the size of the HeluVideoView.
  
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
  
* **setOnStateChangeListener(interface: PlayerStateChangeInterface)**
  This interface provides you method about the mediaPlayer state change. You can use it for implementing your own logic.
  ```
  interface PlayerStateChangeInterface {
		fun onPrepare(mediaPlayer: MediaPlayer?)
		fun onPlay()
		fun onPause()
		fun onComplete(mediaPlayer: MediaPlayer)
		fun onProgressChange(i: Int)
	}
  ```

## Usage
You have to use the Builder class for creating or initializing new HeluVideoView.

```java
// Create and prepare all view we want to use
val videoView: HeluVideoView = findViewById(R.id.helu_video_view)
val placeholderView: ImageView = ViewGeneratorUtility.generatePlaceholderView(context)
val playView: ImageView = ViewGeneratorUtility.generatePlayButtonView(context)
val pauseView: ImageView = ViewGeneratorUtility.generatePauseButtonView(context)
val muteOnView: ImageView = ViewGeneratorUtility.generateMuteOnView(context)
val muteOffView: ImageView = ViewGeneratorUtility.generateMuteOffView(context)
val seekBarView: SeekBar = findViewById(R.id.seek_bar)

// Create builder
val builder = HeluVideoView.Builder(context)
		.withScalingMode(HeluVideoView.ScaleType.SCALE_TO_FIT_VIDEO)
		.withVideoUrl("Some URL")
		.withBackupVideoUrl("Some backup URL")
		.withPlaceholderView(placeholderView)
		.withPlayView(playView)
		.withPauseView(pauseView)
		.withMuteOnView(muteOnView)
		.withMuteOffView(muteOffView)
		.withSeekBarView(seekBarView)
		.withAutoPlay(true)
		.withMuteOnStart(true)
		.withProgressUpdateInterval(75)

// Init already created HeluVideoView from the builder
videoView.initFromBuilder(builder)

// Set OnStateChangeListener if necessary
videoView.setOnStateChangeListener(object: HeluVideoView.PlayerStateChangeInterface(){
	override fun onPrepare(mediaPlayer: MediaPlayer?) {}

	override fun onPlay() {
		videoView.hideVolumeButton()
	}

	override fun onPause() {
		videoView.showVolumeButton()
	}

	override fun onComplete(mediaPlayer: MediaPlayer) {}

	override fun onProgressChange(progress: Int) {}
})

```