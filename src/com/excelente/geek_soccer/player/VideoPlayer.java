package com.excelente.geek_soccer.player;

import com.excelente.geek_soccer.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoPlayer extends Activity implements OnPreparedListener, OnCompletionListener{
	
	public static final String VDO_URL = "VDO_URL";
	
	private VideoView videoPlayerView;
	private MediaController mediaController;

	private ProgressBar videoProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		setContentView(R.layout.video_player);
		
		Intent vdoIntent = getIntent();
		String videoURL = vdoIntent.getStringExtra(VDO_URL);
		
		getInit(videoURL);
	}
	
	public void getInit(String videoURL) {
        videoPlayerView = (VideoView) findViewById(R.id.video_player_view);
        videoPlayerView.setOnPreparedListener(this);
        videoPlayerView.setOnCompletionListener(this);
        mediaController = new MediaController(this);
        videoPlayerView.setMediaController(mediaController);
        videoPlayerView.setVideoPath(videoURL);
        videoPlayerView.start();
        
        videoProgressBar = (ProgressBar) findViewById(R.id.video_progressBar);
    }
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		videoProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mediaController.show();
		videoProgressBar.setVisibility(View.GONE);
	}
}
