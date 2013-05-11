package asynctasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.fogmain.LoginActivity;
import dk.vinael.fogmain.NoInternetActivity;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class WebserviceCaller extends AsyncTask<String, Void, String> {

	private FogActivityInterface callingActivity;
	private User user;
	private String identifier;
	private List<NameValuePair> nameValuePairs;
	
	
	// Create a new HttpClient and Post Header
    private HttpClient httpclient = new DefaultHttpClient();
    //private HttpPost httppost = new HttpPost("http://bluerocketmedia.dk/fog/index.php");
    private HttpPost httppost = new HttpPost("http://supportmand.dk:81/index.php");
	
    // Constructor (setting calling activity and user)
    public WebserviceCaller(FogActivityInterface callingActivity, String identifier){
    	this.callingActivity = callingActivity;
    	
		//this.user = ((FOGmain)callingActivity).user;
		this.user = ((FOGmain)((Activity)callingActivity).getApplicationContext()).user;
		this.identifier = identifier;
		
	}
    
	@Override
	protected String doInBackground(String... sql) {
		
		if (((FOGmain)((Activity)callingActivity).getApplicationContext()).isNetworkConnected()==false){
			return "no_connection";
		}
		else{
			if (user==null){
				return "[{\"access\":\"denied\"}]";
			}
			else{
				if (this.user.getToken()==null){ // Login post (sends username and password)
					// check for username / password OR facebook
					// 
					nameValuePairs = new ArrayList<NameValuePair>();
			        nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
			        nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
				}
				else{ // SQL post (sends token, mode and query)
					nameValuePairs = new ArrayList<NameValuePair>();
			        nameValuePairs.add(new BasicNameValuePair("token", user.getToken()));
			        nameValuePairs.add(new BasicNameValuePair("mode", sql[0]));
			        nameValuePairs.add(new BasicNameValuePair("query", sql[1]));
				}
				
			    try {
			        
			    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        
			        // Execute HTTP Post Request and return response to "onPostExecute" method
			        HttpResponse response = httpclient.execute(httppost);
			        String responseBody = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			        
			        if (responseBody.equals("[]")){
			        	return null;
			        }
			        return responseBody;
			        
			    } catch (ClientProtocolException e) {
			    	return "no_connection";
			    	//return null;
			    } catch (IOException e) {
			    	return "no_connection";
			    	//return null;
			    }
			}
		}
	}
	
	@Override
    protected void onPostExecute(String result) {
		JSONArray jsonArray;
		if (result.equals("no_connection")){
			Intent intent = new Intent((Activity) callingActivity, NoInternetActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			((Activity) callingActivity).startActivity(intent);
			((Activity) callingActivity).finish();
		}
		else{
			try {
				// Create JSONArray out of result string
				jsonArray = new JSONArray(result);
	
				// Checks for a JSONObject "access".
				// If access exist and is equal "denied", finish current
				// running activity and go to LoginActivity
	
				JSONObject jo = jsonArray.getJSONObject(0);
				//Toast.makeText((Activity) callingActivity, jo.toString(), Toast.LENGTH_LONG).show();
				
				if (jo.has("access")){
					// If username or password is wrong
					if (jo.getString("access").toString().equals("denied")){
						//Toast.makeText((Activity) callingActivity, "user + pass = not ok", Toast.LENGTH_LONG).show();
						Intent intent = new Intent((Activity) callingActivity, LoginActivity.class);
						((Activity) callingActivity).finish();
						((Activity) callingActivity).startActivity(intent);
					}
					// If username and password is right
					else{ 
						user.setToken(jo.getString("access").toString());
						callingActivity.jsonArrayHandler(jsonArray, identifier);
					}
				}
				else{
					callingActivity.jsonArrayHandler(jsonArray, identifier);
				}
				
			} catch (JSONException e) {
				Intent intent = new Intent((Activity) callingActivity, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				((Activity) callingActivity).finish();
				((Activity) callingActivity).startActivity(intent);
				//e.printStackTrace();
			}
		}
    }
}