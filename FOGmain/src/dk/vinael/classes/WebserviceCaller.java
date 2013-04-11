package dk.vinael.classes;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.fogmain.LoginActivity;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class WebserviceCaller extends AsyncTask<String, Void, String> {

	private FogActivityInterface callingActivity;
	private User user;
	private String identifier;
	private List<NameValuePair> nameValuePairs;
	
	
	// Create a new HttpClient and Post Header
    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost = new HttpPost("http://bluerocketmedia.dk/fog/index.php");
	
    // Constructor (setting calling activity and user)
    public WebserviceCaller(FogActivityInterface callingActivity, User user, String identifier){
    	this.callingActivity = callingActivity;
		this.user = user;
		this.identifier = identifier;
	}
    
	@Override
	protected String doInBackground(String... sql) {
		if (user==null){
			return "[{\"access\":\"denied\"}]";
		}
		else{
			if (this.user.getToken()==null){ // Login post (sends username and password)
				nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("username", user.getUsername()));
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
		        String responseBody = EntityUtils.toString(response.getEntity());
		        return responseBody;
		        
		        
		    } catch (ClientProtocolException e) {
		    	return null;
		    } catch (IOException e) {
		    	return null;
		    }
		}
		
	}
	
	@Override
    protected void onPostExecute(String result) {
		JSONArray jsonArray;
		//Toast.makeText((Activity) callingActivity, "test", Toast.LENGTH_LONG).show();
		try {
			// Create JSONArray out of result string
			jsonArray = new JSONArray(result);

			// Checks for a JSONObject "access".
			// If access exist and is equal "denied", finish current
			// running activity and go to LoginActivity
			

			JSONObject jo = jsonArray.getJSONObject(0);
			if (jo.has("access")){
				// If username or password is wrong
				if (jo.getString("access").toString().equals("denied")){
					Toast.makeText((Activity) callingActivity, "DENIED", Toast.LENGTH_LONG).show();
					Intent intent = new Intent((Activity) callingActivity, LoginActivity.class);
					((Activity) callingActivity).startActivity(intent);
					((Activity) callingActivity).finish();
				}
				// If username and password is right
				else{ 
					user.setToken(jo.getString("access").toString());
					callingActivity.jsonArrayHandler(jsonArray, identifier);
				}
				//Toast.makeText((Activity) callingActivity, jo.getString("access").toString(), Toast.LENGTH_LONG).show();
			}
			else{
				// Return resultset as JSONArray
				callingActivity.jsonArrayHandler(jsonArray, identifier);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//Toast.makeText((Activity) callingActivity, e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
    	
    	
    }

}
