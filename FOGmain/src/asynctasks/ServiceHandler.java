package asynctasks;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import dk.vinael.interfaces.FogServiceInterface;
import android.app.Activity;
import android.app.Service;
import android.os.AsyncTask;

public class ServiceHandler extends AsyncTask<String, Void, String>{

	private User user;
	private String identifier;
	private Service service;
	
	private ArrayList<NameValuePair> nameValuePairs;
	
	// Create a new HttpClient and Post Header
    private HttpClient httpclient = new DefaultHttpClient();
    //private HttpPost httppost = new HttpPost("http://bluerocketmedia.dk/fog/index.php");
    private HttpPost httppost = new HttpPost("http://supportmand.dk:81/index.php");
	
    public ServiceHandler(User u, String i, Service s){
    	this.user = u;
    	this.identifier = i;
    	this.service = s;
    }
    
	@Override
	protected String doInBackground(String... sql) {
		if (((FOGmain)((Service)service).getApplicationContext()).isNetworkConnected()==false){
			return "no_connection";
		}
		else{
			if (user==null || identifier==null || service==null){
				return "[{\"access\":\"denied\"}]";
			}
			else{
				if (user.getToken()!=null){
					nameValuePairs = new ArrayList<NameValuePair>();
			        nameValuePairs.add(new BasicNameValuePair("token", user.getToken()));
			        nameValuePairs.add(new BasicNameValuePair("mode", sql[0]));
			        nameValuePairs.add(new BasicNameValuePair("query", sql[1]));
			        
			        try {
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
				        String responseBody = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				        return responseBody;
				        
					} catch (Exception e) {
						return "no_connection";
						//e.printStackTrace();
					}
				}
				return "[{\"access\":\"denied\"}]";
			}
		}
	}
	
	
	@Override
    protected void onPostExecute(String result) {
		if (!result.equals("no_connection")){
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(result);
				((FogServiceInterface)service).jsonArrayHandler(jsonArray, identifier);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}