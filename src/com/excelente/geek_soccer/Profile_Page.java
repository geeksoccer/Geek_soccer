package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Profile_Page extends Activity implements OnClickListener, ImageChooserListener{
	
	private LinearLayout upBtn;
	private ImageView memberPhoto;
	private EditText memberName;
	private RelativeLayout saveBtn;

	private Bitmap bitmapPhoto;

	private SessionManager cacheImage;

	private ImageChooserManager imageChooserManager;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MemberSession.hasMember()){
			createLayout();
		}else{
			if(NetworkUtils.isNetworkAvailable(this)){
				new doSignTokenTask().execute();
			}else{
				Toast.makeText(this, NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void createLayout() {
		ThemeUtils.setThemeByTeamId(this, MemberSession.getMember().getTeamId());
		setContentView(R.layout.profile_page);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		initView();
	}

	public class doSignTokenTask extends AsyncTask<Void, Void, MemberModel>{
		
		private static final String MEMBER_TOKEN_URL = "http://183.90.171.209/gs_member/member_token.php";
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = ProgressDialog.show(Profile_Page.this, "", "Updating Profile...", true);
			dialog.setCancelable(false); 
		}
		
		@Override
		protected MemberModel doInBackground(Void... params) {
			
			SharedPreferences memberFile = getSharedPreferences(MemberSession.MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
			
			if(!memberFile.getString(MemberModel.MEMBER_TOKEN, "").equals("")){
	
				List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
				
				String token = memberFile.getString(MemberModel.MEMBER_TOKEN, "");
	
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, token));
				
				String dev_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
				
				String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_TOKEN_URL, memberParam);
				if(memberStr.trim().equals("member not yet")){ 
					return null;
				}
				MemberModel memberSignedIn = MemberModel.convertMemberJSONToList(memberStr);
				return memberSignedIn;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MemberModel memberToken) {
			super.onPostExecute(memberToken);
			dialog.dismiss();
			MemberSession.setMember(Profile_Page.this, memberToken);
			if(MemberSession.hasMember())
				createLayout();
			else{
				Toast.makeText(Profile_Page.this, NetworkUtils.getConnectivityStatusString(Profile_Page.this), Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
		}
		
	}
	
	private void initView() {
		imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
		imageChooserManager.setImageChooserListener(this);
		
		cacheImage = new SessionManager(Profile_Page.this);
		bitmapPhoto = null;
		
		upBtn = (LinearLayout) findViewById(R.id.Up_btn);
		upBtn.setOnClickListener(this);
		
		memberPhoto = (ImageView) findViewById(R.id.member_photo);
		memberPhoto.setOnClickListener(this);
		if(cacheImage.hasKey(MemberSession.getMember().getPhoto())){ 
			memberPhoto.setImageBitmap(cacheImage.getImageSession(MemberSession.getMember().getPhoto())); 
		}else{
			doConfigImageLoader(200, 200); 
			ImageLoader.getInstance().displayImage(MemberSession.getMember().getPhoto(), memberPhoto, getOptionImageLoader(MemberSession.getMember().getPhoto()));
		}
		
		memberName = (EditText) findViewById(R.id.member_name);
		memberName.setText(MemberSession.getMember().getNickname());
		memberPhoto.setOnClickListener(this);
		
		saveBtn = (RelativeLayout) findViewById(R.id.Footer_Layout); 
		saveBtn.setOnClickListener(this);
	}
	
	private void doConfigImageLoader(int w, int h) {
		
		File cacheDir = StorageUtils.getCacheDirectory(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		        .memoryCacheExtraOptions(w, h) // default = device screen dimensions
		        .discCacheExtraOptions(w, h, CompressFormat.PNG, 100, null)
		        .threadPoolSize(3) // default
		        .threadPriority(Thread.NORM_PRIORITY - 1) // default
		        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		        .denyCacheImageMultipleSizesInMemory()
		        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		        .memoryCacheSize(2 * 1024 * 1024)
		        .memoryCacheSizePercentage(13) // default
		        .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        .discCacheSize(20 * 1024 * 1024)
		        .discCacheFileCount(100)
		        .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		        .imageDownloader(new CustomImageDownaloder(this))
		        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		        .writeDebugLogs()
		        .build();
		ImageLoader.getInstance().init(config);
	}
	
	private DisplayImageOptions getOptionImageLoader(String url) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "http://localhost");
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	        //.showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
	        //.showImageForEmptyUri(R.drawable.soccer_icon) // resource or drawable
	        //.showImageOnFail(R.drawable.soccer_icon) // resource or drawable
	        .resetViewBeforeLoading(false)  // default
	        //.delayBeforeLoading(500)
	        .cacheInMemory(false)
	        .cacheOnDisc(false)
	        .considerExifParams(false) // default
	        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
	        .bitmapConfig(Bitmap.Config.RGB_565) // default
	        //.decodingOptions()
	        .displayer(new SimpleBitmapDisplayer()) // default
	        .handler(new Handler()) // default
	        .extraForDownloader(headers)
	        .build();
		
		return options;
	}
	
	public class CustomImageDownaloder extends BaseImageDownloader {

	    public CustomImageDownaloder(Context context) {
	        super(context);
	    }

	    public CustomImageDownaloder(Context context, int connectTimeout, int readTimeout) {
	        super(context, connectTimeout, readTimeout);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
	        HttpURLConnection conn = super.createConnection(url, extra);
	        Map<String, String> headers = (Map<String, String>) extra;
	        if (headers != null) {
	            for (Map.Entry<String, String> header : headers.entrySet()) {
	                conn.setRequestProperty(header.getKey(), header.getValue());
	            }
	        }
	        return conn;
	    }
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
			
			case R.id.member_photo:{
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
			if(!memberName.getText().toString().trim().equals(MemberSession.getMember().getNickname()) || bitmapPhoto!=null)
				new PostMember().execute();
			else
				Toast.makeText(Profile_Page.this, getResources().getString(R.string.warn_member_name1), Toast.LENGTH_SHORT).show();
			 
		}else{
			Toast.makeText(Profile_Page.this, getResources().getString(R.string.warn_member_name2), Toast.LENGTH_SHORT).show();
		}
	}
	
	public class PostMember extends AsyncTask<Void, Void, String>{
		
		public static final String MEMBER_UPDATE_URL = "http://183.90.171.209/gs_member/member_update.php";
		public static final String MEMBER_IMAGES_URL = "http://183.90.171.209/gs_member/member_images/";
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(Profile_Page.this, "", "Updating Profile...", true);
			dialog.setCancelable(false);
		}
		 
		@Override
		protected String doInBackground(Void... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("m_uid", String.valueOf(MemberSession.getMember().getUid())));
			paramsPost.add(new BasicNameValuePair("m_nickname", memberName.getText().toString().trim()));
			paramsPost.add(new BasicNameValuePair("m_photo", MemberSession.getMember().getPhoto()));
			
			if(bitmapPhoto!=null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				bitmapPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos); 
				byte[] b = baos.toByteArray();
				paramsPost.add(new BasicNameValuePair("m_photo_base64", Base64.encodeToString(b, Base64.DEFAULT)));
				paramsPost.add(new BasicNameValuePair("m_photo", MEMBER_IMAGES_URL + MemberSession.getMember().getUid() + ".png"));
			}
			
			return HttpConnectUtils.getStrHttpPostConnect(MEMBER_UPDATE_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(Profile_Page.this, result.trim(), Toast.LENGTH_SHORT).show();
			if(result.trim().equals("OK Success")){
				MemberSession.getMember().setNickname(memberName.getText().toString().trim());
				
				if(bitmapPhoto!=null){
					MemberSession.getMember().setPhoto(MEMBER_IMAGES_URL + MemberSession.getMember().getUid() + ".png");
					new Thread(new Runnable() {
						@Override
						public void run() {
							cacheImage.createNewImageSession(MemberSession.getMember().getPhoto(), bitmapPhoto);
						}
					}).start();
				}
				
			}
			dialog.dismiss();
		}

	}

	@Override
	public void onError(final String error) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
 
				Toast.makeText(Profile_Page.this, error, Toast.LENGTH_SHORT).show();
			}

		});
	}

	@Override
	public void onImageChosen(final ChosenImage image) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (image != null) {
					bitmapPhoto = BitmapFactory.decodeFile(image.getFilePathOriginal());
					memberPhoto.setImageBitmap(bitmapPhoto);
				}
			}

		});
	}

}
