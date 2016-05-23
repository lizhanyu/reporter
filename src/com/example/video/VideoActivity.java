package com.example.video;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends Activity implements
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	public static final String TAG = "VideoPlayer";
	private VideoView mVideoView;
	private Uri mUri;
	private int mPositionWhenPaused = -1;
	private Handler handler;
	private MediaController mMediaController;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private Intent inten;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		inten = getIntent();
		mVideoView = (VideoView) findViewById(R.id.videoview);
		
		String path = inten.getStringExtra("path");
		 
			mUri = Uri.parse(path);
			// mUri = Uri.parse(Environment.getExternalStorageDirectory() +
			// "/ziranyouli.3gp");
			// mUri =
			// Uri.parse("http://111.13.144.80/m3u8/hz_xhs1_800/desc.m3u8?stream_id=hz_xhs1_800&path=122.72.1.184,111.13.144.100&platid=10&splatid=1001&keyitem=platid,splatid,stream_id&ntm=1433597654&nkey=f0dfd2908bd94012271f53f3076efa10&nkey2=63f2c80a1b86f33487d779ce3c37c09b&tag=live&video_type=m3u8&useloc=0&mslice=3&uidx=0&errc=0&buss=10101&qos=5&cips=202.99.59.32&geo=CN-1-0-4&tmn=1433579654&pnl=322,126,322,431&rson=1&ext=m3u8&sign=live_web");
			// Create media controller
			mMediaController = new MediaController(VideoActivity.this);
	
			// 设置MediaController
			mVideoView.setMediaController(mMediaController);
			mVideoView.setVideoURI(mUri);
		 
		 
		 
	/*	executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					String serverURL = "http://live.gslb.letv.com/gslb?stream_id=hz_xhs2_800&tag=live&ext=m3u8&sign=live_web&format=0&expect=2&platid=10&splatid=1001";
					SpaceRedirectHandler customRedirectHandler = new SpaceRedirectHandler();
					HttpGet httpRequest = new HttpGet(serverURL);// 建立http get联机
					AbstractHttpClient httpclient = new DefaultHttpClient();
					HttpContext httpContext = new BasicHttpContext();
					HttpResponse httpResponse = null;
					Message msg = new Message();
					httpclient.setRedirectHandler(customRedirectHandler);
					httpResponse = httpclient.execute(httpRequest, httpContext);
					Header[] header = httpResponse.getHeaders("Location"); // 获取Location
																			// Header
					if (header != null && header.length > 0) {
						Header locHeader = header[0];
						System.out.println("Name:" + locHeader.getName()
								+ "|Value:" + locHeader.getValue());
						msg.obj = locHeader.getValue();
					}

					msg.obj = customRedirectHandler.getUrl();

					msg.setTarget(handler);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/

	}

	// 监听MediaPlayer上报的错误信息

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	// Video播完的时候得到通知

	@Override
	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	// 开始
	public void onStart() {
		// Play Video
		mVideoView.setVideoURI(mUri);
		mVideoView.start();

		super.onStart();
	}

	// 暂停

	public void onPause() {
		// Stop video when the activity is pause.
		mPositionWhenPaused = mVideoView.getCurrentPosition();
		mVideoView.stopPlayback();
		Log.d(TAG, "OnStop: mPositionWhenPaused = " + mPositionWhenPaused);
		Log.d(TAG, "OnStop: getDuration  = " + mVideoView.getDuration());

		super.onPause();
	}

	public void onResume() {
		// Resume video player
		if (mPositionWhenPaused >= 0) {
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}

		super.onResume();
	}

}

 
 
