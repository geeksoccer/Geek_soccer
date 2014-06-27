package com.excelente.geek_soccer.pic_download;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.R;

public class DownLiveScorePic {
	private Handler handler = new Handler(Looper.getMainLooper());
	public static Bitmap loadImageFromUrl(String url) {
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		Bitmap bitmap = null;
		try {
			final HttpGet getRequest = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 5000;

			httpParameters.setParameter(CoreProtocolPNames.USER_AGENT,
					System.getProperty("http.agent"));
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(getRequest);

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
			}

			final HttpEntity entity = response.getEntity();

			i = entity.getContent();// connection.getInputStream();//(InputStream)
									// m.getContent();//

			bis = new BufferedInputStream(i, 1024 * 8);
			out = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = new FlushedInputStream(bis).read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bis.close();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
		}
		// double image_size = lenghtOfFile;
		if (out != null) {
			byte[] data = out.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, options);

			double screenWidth = options.outWidth / 2;
			double screenHeight = options.outHeight / 2;

			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inDither = false; // Disable Dithering mode
			options.inPurgeable = true; // Tell to gc that whether it needs free
										// memory, the Bitmap can be cleared
			options.inInputShareable = true; // Which kind of reference will be
												// used to recover the Bitmap
												// data after being clear, when
												// it will be used in the future
			options.inTempStorage = new byte[32 * 1024];
			options.inSampleSize = calculateInSampleSize(options,
					(int) screenWidth, (int) screenHeight);

			options.inJustDecodeBounds = false;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		}
		return bitmap;
	}

	public void startDownload_Home(final String Url, final ImageView img_H
			, final String saveMode, final ControllParameter data) {

		Runnable runnable = new Runnable() {
			public void run() {
				if (saveMode.equals("true")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							img_H.setImageResource(R.drawable.ic_menu_view);
							img_H.setFocusable(false);
							img_H.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									startDownload_Home(Url, img_H, "false", data);
								}
							});
						}
					});
				}else if(saveMode.equals("false")||saveMode.equals("null")){
					if (Url.length() > 0) {

						if (data.get_HomeMap(Url) != null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									img_H.setImageBitmap(data.get_HomeMap(Url));
								}
							});
						} else {
							if (!Url.contains("/images/placeholder-64x64.png")) {
								final Bitmap pic;
								pic = loadImageFromUrl(Url);
								data.set_HomeMap(Url, pic);
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (pic == null) {
											img_H.setImageResource(R.drawable.soccer_icon);
										} else {
											img_H.setImageBitmap(pic);
										}
									}
								});
							}
						}
					}
				}
				
			}
		};

		new Thread(runnable).start();
	}

	public void startDownload_Away(final String Url, final ImageView img_A
			, final String saveMode, final ControllParameter data) {

		Runnable runnable = new Runnable() {
			public void run() {
				if (saveMode.equals("true")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							img_A.setImageResource(R.drawable.ic_menu_view);
							img_A.setFocusable(false);
							img_A.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									startDownload_Away(Url, img_A, "false", data);
								}
							});
						}
					});
				}else if(saveMode.equals("false")||saveMode.equals("null")){

					if (Url.length() > 0) {

						if (data.get_AwayMap(Url) != null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									img_A.setImageBitmap(data.get_AwayMap(Url));
								}
							});
						} else {
							if (!Url.contains("/images/placeholder-64x64.png")) {
								final Bitmap pic;
								pic = loadImageFromUrl(Url);
								data.set_AwayMap(Url, pic);

								handler.post(new Runnable() {
									@Override
									public void run() {
										if (pic == null) {
											img_A.setImageResource(R.drawable.soccer_icon);
										} else {
											img_A.setImageBitmap(pic);
										}
									}
								});
							}
						}

					}
				}
			}
		};

		new Thread(runnable).start();
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
}
