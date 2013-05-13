package asynctasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfilePictureHandler extends AsyncTask<URL, Void, Bitmap> {
	private ImageView iv;
	private Bitmap bmp;
	private Execute ex;
	private Activity AC;
	private InputStream inputStream;
	private HttpResponse response;
	
	public ProfilePictureHandler(ImageView iv, Execute ex) {
		this.iv = iv;
		this.ex = ex;
	}

	public ProfilePictureHandler(Bitmap bmp, Execute ex, Activity ac) {
		this.bmp = bmp;
		this.ex = ex;
		this.AC = ac;
	}

	@Override
	protected Bitmap doInBackground(URL... params) {
		if (ex.equals(Execute.RECIEVE)) {
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
				return bmp;
			} catch (Exception ex) {
				
			}
		} else if (ex.equals(Execute.SEND)) {

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); // compress to
																	// which
																	// format
																	// you want.
			byte[] byte_arr = stream.toByteArray();
			String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			User user = ((FOGmain)((Activity)AC).getApplicationContext()).user;
			nameValuePairs.add(new BasicNameValuePair("image", image_str));
			nameValuePairs.add(new BasicNameValuePair("imagename",  ""+user.getUserId()));

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://neger.dyndns.info/bluerocketmedia.dk/profile/picturehandler.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(httppost);
				
			} catch (Exception e) {
			}
		}
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (ex.equals(Execute.RECIEVE)) {
			iv.setImageBitmap(result);
		} else if (ex.equals(Execute.SEND)) {
			String the_string_response = "";
			try {
				the_string_response = convertResponseToString(response);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(AC, "Response " + the_string_response, Toast.LENGTH_LONG).show();
		}
		super.onPostExecute(result);
	}

	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException {

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); // getting
																			// content
																			// length…..
		Toast.makeText(AC, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
		if (contentLength < 0) {
		} else {
			byte[] data = new byte[512];
			int len = 0;
			try {
				while (-1 != (len = inputStream.read(data))) {
					buffer.append(new String(data, 0, len)); // converting to
																// string and
																// appending to
																// stringbuffer…..
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close(); // closing the stream…..
			} catch (IOException e) {
				e.printStackTrace();
			}
			res = buffer.toString(); // converting stringbuffer to string…..

			Toast.makeText(AC, "Result : " + res, Toast.LENGTH_LONG).show();
			// System.out.println("Response => " +
			// EntityUtils.toString(response.getEntity()));
		}
		return res;
	}

	public enum Execute {
		SEND, RECIEVE;
	}

}
