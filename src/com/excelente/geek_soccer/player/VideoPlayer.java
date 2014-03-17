package com.excelente.geek_soccer.player;

import com.excelente.geek_soccer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity{
	
	public static final String VDO_URL = "VDO_URL";
	
	private VideoView videoPlayerView;
	private MediaController mediaController;

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
        mediaController = new MediaController(this);
        videoPlayerView.setMediaController(mediaController);
        videoPlayerView.setVideoPath(videoURL);
        videoPlayerView.start();
        
        /*videoPlayerView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				AlertDialog.Builder buildDialog = new AlertDialog.Builder(getApplicationContext());
				buildDialog.setTitle(getResources().getString(R.string.video_title_error));
				buildDialog.setMessage(getResources().getString(R.string.video_message_error));
				buildDialog.setCancelable(false);
				buildDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				buildDialog.create();
				finish();
				return true;
			}
			
			
		});*/
    }
	
	@Override
	public void onBackPressed() {
		finish();
	}

}
