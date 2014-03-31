package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
 
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Sharedpref file name
    private static final String PREF_NAME = "Excelente";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }
    public void createNewImageSession(String key, Bitmap value){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			value.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

			editor.putString(key, encodedImage);
			editor.commit();
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
		}
    }
    
    public Bitmap getImageSession(String key){
		try {
			if (pref.getString(key, null) != null) {
				byte[] imageAsBytes = Base64.decode(pref.getString(key, null)
						.getBytes(), PRIVATE_MODE);
				return BitmapFactory.decodeByteArray(imageAsBytes, 0,
						imageAsBytes.length);// pref.getString(key, null);
			} else {
				return null;
			}
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
			return null;
		}
    }
    
    public void createNewJsonSession(String key, String value){
    	editor.putString(key, value);
		editor.commit();
    }
    
    public String getJsonSession(String key){
    	return pref.getString(key, null);
    }
}