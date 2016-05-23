package com.example.video;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends Activity implements
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	public static final String TAG = "VideoPlayer";
	private VideoView mVideoView;
	private Uri mUri;
	private int mPositionWhenPaused = -1;
	private Handler handler;
	private String newPaht;
	private MediaController mMediaController;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		newPaht = getIntent().getStringExtra("path");
		mVideoView = (VideoView) findViewById(R.id.videoview);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// HashMap<String, Object> map = (HashMap<String, Object>)
				// msg.obj;
				String path = (String) msg.obj;
				mUri = Uri.parse(path);
				// mUri = Uri.parse(Environment.getExternalStorageDirectory() +
				// "/ziranyouli.3gp");
				// mUri =
				// Uri.parse("http://111.13.144.80/m3u8/hz_xhs1_800/desc.m3u8?stream_id=hz_xhs1_800&path=122.72.1.184,111.13.144.100&platid=10&splatid=1001&keyitem=platid,splatid,stream_id&ntm=1433597654&nkey=f0dfd2908bd94012271f53f3076efa10&nkey2=63f2c80a1b86f33487d779ce3c37c09b&tag=live&video_type=m3u8&useloc=0&mslice=3&uidx=0&errc=0&buss=10101&qos=5&cips=202.99.59.32&geo=CN-1-0-4&tmn=1433579654&pnl=322,126,322,431&rson=1&ext=m3u8&sign=live_web");
				// Create media controller
				mMediaController = new MediaController(MainActivity.this);

				// 设置MediaController
				mVideoView.setMediaController(mMediaController);
				mVideoView.setVideoURI(mUri);
			}
		};
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					String serverURL =  newPaht ;  //"http://live.gslb.letv.com/gslb?stream_id=hz_xhs2_800&tag=live&ext=m3u8&sign=live_web&format=0&expect=2&platid=10&splatid=1001";
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
		});

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

class SpaceRedirectHandler extends DefaultRedirectHandler {

	private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
	private String url;
	private URI uri;

	public SpaceRedirectHandler() {
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public boolean isRedirectRequested(final HttpResponse response,
			final HttpContext context) {
		if (response == null) {
			throw new IllegalArgumentException("HTTP response may not be null");
		}
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case HttpStatus.SC_MOVED_TEMPORARILY:
		case HttpStatus.SC_MOVED_PERMANENTLY:
		case HttpStatus.SC_SEE_OTHER:
		case HttpStatus.SC_TEMPORARY_REDIRECT:
			return true;
		default:
			return false;
		} 
	}

	public URI getLocationURI(final HttpResponse response,
			final HttpContext context) throws ProtocolException {
		if (response == null) {
			throw new IllegalArgumentException("HTTP response may not be null");
		}
		// get the location header to find out where to redirect to
		Header locationHeader = response.getFirstHeader("location");
		if (locationHeader == null) {
			// got a redirect response, but no location header
			throw new ProtocolException("Received redirect response "
					+ response.getStatusLine() + " but no location header");
		}
		// HERE IS THE MODIFIED LINE OF CODE
		String location = locationHeader.getValue().replaceAll(" ", "%20");

		URI uri;
		try {
			uri = new URI(location);
		} catch (URISyntaxException ex) {
			throw new ProtocolException("Invalid redirect URI: " + location, ex);
		}

		HttpParams params = response.getParams();
		// rfc2616 demands the location value be a complete URI
		// Location = "Location" ":" absoluteURI
		if (!uri.isAbsolute()) {
			if (params.isParameterTrue(ClientPNames.REJECT_RELATIVE_REDIRECT)) {
				throw new ProtocolException("Relative redirect location '"
						+ uri + "' not allowed");
			}
			// Adjust location URI
			HttpHost target = (HttpHost) context
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			if (target == null) {
				throw new IllegalStateException("Target host not available "
						+ "in the HTTP context");
			}

			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);

			try {
				URI requestURI = new URI(request.getRequestLine().getUri());
				URI absoluteRequestURI = URIUtils.rewriteURI(requestURI,
						target, true);
				uri = URIUtils.resolve(absoluteRequestURI, uri);
			} catch (URISyntaxException ex) {
				throw new ProtocolException(ex.getMessage(), ex);
			}
		}

		if (params.isParameterFalse(ClientPNames.ALLOW_CIRCULAR_REDIRECTS)) {

			RedirectLocations redirectLocations = (RedirectLocations) context
					.getAttribute(REDIRECT_LOCATIONS);

			if (redirectLocations == null) {
				redirectLocations = new RedirectLocations();
				context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
			}

			URI redirectURI;
			if (uri.getFragment() != null) {
				try {
					HttpHost target = new HttpHost(uri.getHost(),
							uri.getPort(), uri.getScheme());
					redirectURI = URIUtils.rewriteURI(uri, target, true);
				} catch (URISyntaxException ex) {
					throw new ProtocolException(ex.getMessage(), ex);
				}
			} else {
				redirectURI = uri;
			}

			if (redirectLocations.contains(redirectURI)) {
				throw new CircularRedirectException("Circular redirect to '"
						+ redirectURI + "'");
			} else {
				redirectLocations.add(redirectURI);
			}
		}
		setUrl(location);
		setUri(uri);
		return uri;
	}
}
