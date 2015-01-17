package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.utils.asynctask.GetImageUriTask;
import com.excelente.geek_soccer.view.SoftKeyboardHandledLinearLayout;
import com.excelente.geek_soccer.view.SoftKeyboardHandledLinearLayout.OnSoftKeyboardListener;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout; 
import android.widget.TextView;
import android.widget.Toast;

public class Profile_Page extends Activity implements OnClickListener, ImageChooserListener, OnSoftKeyboardListener{
	
	public static final int MAX_IMAGE = 512;
	
	private LinearLayout upBtn;
	private ImageView memberPhoto;
	private EditText memberName;
	private RelativeLayout saveBtn;

	private Bitmap bitmapPhoto;

	private ImageChooserManager imageChooserManager;
	private TextView memberEmail;
	private TextView memberFT;

	private SoftKeyboardHandledLinearLayout layoutProfile;

	private LinearLayout layoutPhoto;

	private RelativeLayout Header_Layout;

	private TextView Title_bar;

	private TextView saveBtnTxt;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		Log.e("onCreate", "onCreate");
		 
		imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, false);
		imageChooserManager.setImageChooserListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume", "onResume");
		if(SessionManager.hasMember(Profile_Page.this)){
			createLayout();
		}
	}
	
	@Override
	protected void onDestroy() { 
		super.onDestroy();
		Log.e("onDestroy", "onDestroy");
	}
	
	private void createLayout() {
		setContentView(R.layout.profile_page);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		initView();
	}
	
	private void initView() {
		
		bitmapPhoto = null;
		
		Header_Layout = (RelativeLayout) findViewById(R.id.Header_Layout);
		Title_bar = (TextView) findViewById(R.id.Title_bar); 
		
		layoutProfile = (SoftKeyboardHandledLinearLayout) findViewById(R.id.Layout_PROFILE);
		layoutProfile.setOnSoftKeyboardListener(this);
		
		upBtn = (LinearLayout) findViewById(R.id.Up_btn);
		upBtn.setOnClickListener(this);  
		
		layoutPhoto = (LinearLayout) findViewById(R.id.layout_photo_);
		layoutPhoto.setVisibility(View.VISIBLE); 
		layoutPhoto.setOnClickListener(this);
		  
		memberPhoto = (ImageView) findViewById(R.id.member_photo);
		memberPhoto.setVisibility(View.VISIBLE);
		
		//memberPhoto.getLayoutParams().height = (int) ConvertUtil.convertPixelsToDp(MAX_IMAGE, this);
		if(SessionManager.hasKey(Profile_Page.this, SessionManager.getMember(Profile_Page.this).getPhoto())){ 
			Bitmap bitmapPhoto = SessionManager.getImageSession(Profile_Page.this, SessionManager.getMember(Profile_Page.this).getPhoto());
			memberPhoto.setImageBitmap(resizeBitMap(bitmapPhoto));
		}else{
			new GetImageUriTask(this, memberPhoto, SessionManager.getMember(Profile_Page.this).getPhoto()).doLoadImage(true);
		} 
		
		memberName = (EditText) findViewById(R.id.member_name);
		memberName.setText(SessionManager.getMember(Profile_Page.this).getNickname());
		memberName.setSelection(memberName.getText().length());
		
		memberEmail = (TextView) findViewById(R.id.profile_email);
		memberEmail.setText(SessionManager.getMember(Profile_Page.this).getEmail());
		
		memberFT = (TextView) findViewById(R.id.profile_favorit_team);
		
		if(SessionManager.getMember(Profile_Page.this).getTeam().getTeamId() == 0){
			memberFT.setText(getResources().getString(R.string.no_favorite_team));
		}else{
			if(SessionManager.getLang(getApplicationContext()).equals("en")){
				memberFT.setText(SessionManager.getMember(getApplicationContext()).getTeam().getTeamName());
			}else if(SessionManager.getLang(getApplicationContext()).equals("th")){
				memberFT.setText(SessionManager.getMember(getApplicationContext()).getTeam().getTeamNameTH());
			}
		}
		
		saveBtn = (RelativeLayout) findViewById(R.id.Footer_Layout); 
		saveBtn.setOnClickListener(this);
		
		saveBtnTxt = (TextView) findViewById(R.id.save_btn_txt);
		
		setThemeToView();
	}

	private void setThemeToView() {
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_BACKGROUND_COLOR, Header_Layout);
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_TEXT_COLOR, Title_bar);
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_BACKGROUND_COLOR, saveBtn);
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_TEXT_COLOR, saveBtnTxt);
	}

	public static Bitmap resizeBitMap(Bitmap bitmapPhoto) {
		
		float scale = (bitmapPhoto.getWidth()*1.0f)/(1.0f*bitmapPhoto.getHeight());
		int width = MAX_IMAGE;
		int height = MAX_IMAGE;
		if(width > height){
			height = (int) (height * scale);
		}else{
			width = (int) (width * scale);
		}
		bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, width, height, false);
		return bitmapPhoto;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.Up_btn:{
				onBackPressed();
				break;
			}
			
			case R.id.layout_photo_:{
				onSelectPhoto();
				break;
			}
			
			case R.id.Footer_Layout:{
				if(NetworkUtils.isNetworkAvailable(this)){
					onSaveProfile();
				}else{
					Toast.makeText(this, NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
				}
				break;
			}
			
		}
	}

	private void onSelectPhoto() {
		try {
			imageChooserManager.choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
               imageChooserManager.submit(requestCode, data);
        }
    }

	private void onSaveProfile() {
		if(memberName.getText().toString().trim().length() > 2 || bitmapPhoto!=null){ 
			if(!memberName.getText().toString().trim().equals(SessionManager.getMember(Profile_Page.this).getNickname()) || bitmapPhoto!=null)
				new PostMember().execute();
			else
				Toast.makeText(Profile_Page.this, getResources().getString(R.string.warn_member_name1), Toast.LENGTH_SHORT).show();
			 
		}else{
			Toast.makeText(Profile_Page.this, getResources().getString(R.string.warn_member_name2), Toast.LENGTH_SHORT).show();
		}
	}
	
	public class PostMember extends AsyncTask<Void, Void, String>{
		
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(Profile_Page.this, "", getResources().getString(R.string.update_profile), true);
			dialog.setCancelable(false);
		}
		 
		@Override
		protected String doInBackground(Void... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("m_uid", String.valueOf(SessionManager.getMember(Profile_Page.this).getUid())));
			paramsPost.add(new BasicNameValuePair("m_nickname", memberName.getText().toString().trim()));
			paramsPost.add(new BasicNameValuePair("m_photo", SessionManager.getMember(Profile_Page.this).getPhoto()));
			
			if(bitmapPhoto!=null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				bitmapPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos); 
				byte[] b = baos.toByteArray();
				paramsPost.add(new BasicNameValuePair("m_photo_base64", Base64.encodeToString(b, Base64.DEFAULT)));
				paramsPost.add(new BasicNameValuePair("m_photo", ControllParameter.MEMBER_IMAGES_URL + SessionManager.getMember(Profile_Page.this).getUid() + ".png"));
			}
			
			return HttpConnectUtils.getStrHttpPostConnect(ControllParameter.MEMBER_UPDATE_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if(result.trim().equals("OK Success")){
				MemberModel member = SessionManager.getMember(Profile_Page.this);
				member.setNickname(memberName.getText().toString().trim());
				SessionManager.setMember(Profile_Page.this, member);
				
				if(bitmapPhoto!=null){
					member.setPhoto(ControllParameter.MEMBER_IMAGES_URL + SessionManager.getMember(Profile_Page.this).getUid() + ".png");
					SessionManager.setMember(Profile_Page.this, member);
					new Thread(new Runnable() {
						@Override
						public void run() {
							SessionManager.createNewImageSession(Profile_Page.this, SessionManager.getMember(Profile_Page.this).getPhoto(), bitmapPhoto);
						}
					}).start();
				}
				
				Toast.makeText(Profile_Page.this, getResources().getString(R.string.profile_save_success), Toast.LENGTH_SHORT).show(); 
			}else{
				Toast.makeText(Profile_Page.this, getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onError(final String error) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Profile_Page.this, getResources().getString(R.string.profile_pick_image), Toast.LENGTH_SHORT).show();
			}
		}); 
	}

	@Override
	public void onImageChosen(final ChosenImage image) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (image != null) {
					if(image.getExtension().equalsIgnoreCase("png") || image.getExtension().equalsIgnoreCase("jpg") || image.getExtension().equalsIgnoreCase("jpeg")){
						bitmapPhoto = BitmapFactory.decodeFile(image.getFilePathOriginal());
						float scale = (bitmapPhoto.getWidth()*1.0f)/(1.0f*bitmapPhoto.getHeight());
						int width = MAX_IMAGE;
						int height = MAX_IMAGE;
						if(bitmapPhoto.getWidth() >= MAX_IMAGE || bitmapPhoto.getHeight() >= MAX_IMAGE){
							
							if(width > height){
								height = (int) (height * scale);
							}else{
								width = (int) (width * scale);
							}
							
						}else{
						
							if(bitmapPhoto.getWidth() < MAX_IMAGE){
								width = bitmapPhoto.getWidth();
							}
							
							if(bitmapPhoto.getHeight() < MAX_IMAGE){
								height = bitmapPhoto.getHeight();
							}
							
						}
						
						try{
							
							bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, width, height, false);
							memberPhoto.setImageBitmap(bitmapPhoto);
							memberPhoto.setVisibility(View.VISIBLE);
							
						}catch(OutOfMemoryError out){
							Toast.makeText(Profile_Page.this, "Please Pick Image Less Size.", Toast.LENGTH_SHORT).show();
						}
						
					}else{
						Toast.makeText(Profile_Page.this, getResources().getString(R.string.profile_pick_image), Toast.LENGTH_SHORT).show();
					}
					
					File thumbnailFile = new File(image.getFileThumbnail());
					if(thumbnailFile.isFile()){
						try {
							delete(new File(thumbnailFile.getParent()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		
		});
	}
	
	public static void delete(File file) throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	    		   file.delete();
	    		}else{
	    		   //list all the directory contents
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	 
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
	        	   }
	    		}
	 
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    	}
	}

	@Override
	public void onShown() { 
		layoutPhoto.setVisibility(View.GONE);
		memberPhoto.setVisibility(View.GONE);
	}

	@Override
	public void onHidden() {
		layoutPhoto.setVisibility(View.VISIBLE);
		memberPhoto.setVisibility(View.VISIBLE);
	}

	@Override
	public void onNoEvent() {}

}
