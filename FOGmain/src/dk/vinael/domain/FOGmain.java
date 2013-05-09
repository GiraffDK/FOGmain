package dk.vinael.domain;

import dk.vinael.fogmain.MenuActivity;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.MenuItem;

public class FOGmain extends Application {
	public User user;
	// General to menu method
	public void onOptionsItemSelected(MenuItem item, Activity a) {
		Intent intent = new Intent(a, MenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		a.startActivity(intent);
	}
}