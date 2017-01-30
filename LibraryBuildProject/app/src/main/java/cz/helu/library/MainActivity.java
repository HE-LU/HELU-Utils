package cz.helu.library;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cz.helu.heluvideoview.HeluVideoView;


public class MainActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupVideoView();
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
}
