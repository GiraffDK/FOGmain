package dk.vinael.fogmain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.cb;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.RememberMe;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

public class LoginActivity extends Activity implements FogActivityInterface {

	private User user;
	
	private EditText txt_email;
	private EditText txt_password;
	private CheckBox cb_remember_me;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		((FOGmain)getApplicationContext()).user = new User();
		this.user = ((FOGmain)getApplicationContext()).user;
		
		txt_email = (EditText) findViewById(R.id.txt_email);
		txt_password = (EditText) findViewById(R.id.txt_password);
		cb_remember_me = (CheckBox) findViewById(R.id.remember_loginactivity);
		
		getMe();
	}

	public void login(View view){
		user.setEmail(txt_email.getText().toString());
		user.setPassword(txt_password.getText().toString());
		//Log.i(user.getPassword(), "PASS:");
		user.selectUserByEmailAndPassword(this, "getUserToken");
		
		
		// remember me
		RememberMe rm = new RememberMe();
		if (cb_remember_me.isChecked()){
			rm.email=txt_email.getText().toString();
			rm.password=txt_password.getText().toString();
		}
		
		Context c = this.getBaseContext();
		FileOutputStream fos;
		ObjectOutputStream os;
		
		try {
			fos = c.openFileOutput("usr.fog", Context.MODE_PRIVATE);
			os = new ObjectOutputStream(fos);
			os.writeObject(rm);
			os.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void getMe(){
		RememberMe me = new RememberMe();
		try {
			Context c = this.getBaseContext();
			FileInputStream fis = c.openFileInput("usr.fog");
			ObjectInputStream is = new ObjectInputStream(fis);
			me = (RememberMe) is.readObject();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (me.email!=null && me.password!=null){
			txt_email.setText(me.email);
			txt_password.setText(me.password);
			cb_remember_me.setChecked(true);
		}
		//Toast.makeText(this, me.toString(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getUserToken")){
			try {
				JSONObject jo = ja.getJSONObject(0);
				if (jo.has("access")){
					if (jo.getString("access")!="granted" && jo.getString("access")!="denied"){
						user.setToken(jo.getString("access"));
						user.selectUserByToken(this, "setUser");
					}
				}
			} catch (JSONException e) {
				user = null;
				//e.printStackTrace();
			}
		}
		else if (identifier.equals("setUser")){
			try {
				JSONObject jo = ja.getJSONObject(0);
				if (jo.has("firstname")){
					user.setUserByJson(jo);
					
					Intent intent = new Intent(this, MenuActivity.class);
					this.finish();
					this.startActivity(intent);
					overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);
					
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void returningAddress(String Address, String identifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returningLocation(Location location, String identifier) {
		// TODO Auto-generated method stub
		
	}
	

	
}
