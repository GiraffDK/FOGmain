package dk.vinael.domain;

import dk.vinael.fogmain.MenuActivity;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MenuItem;

public class FOGmain extends Application {
	public User user;
	public Intent notificationService;
	// General to menu method
	public void onOptionsItemSelected(MenuItem item, Activity a) {
		Intent intent = new Intent(a, MenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		a.startActivity(intent);
	}
	
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} 
		else{
			return true;
		}
	 }
}