package dk.vinael.fogmain;

import java.util.Calendar;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract.CalendarAlerts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.NotificationService;

public class MenuActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		user = ((FOGmain)getApplicationContext()).user;
		((TextView)findViewById(R.id.tv_username_menu)).setText("Logged in as: " + user.getFirstName() + " " + user.getLastName());

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	public void onLogOut(MenuItem item) {
		if (item.getItemId() == R.id.menu_log_out) {
			((FOGmain)getApplicationContext()).user = null;
			Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
		}
	}
	
	public void gotoAddParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoSearch(View view){
		Intent intent = new Intent(this, SearchForPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoParties(View view){
		Intent intent = new Intent(this, PartiesActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoProfile(View view){
		/*
		Intent intent = new Intent(this, ViewProfilActivity.class);
		intent.putExtra("user", ((FOGmain)getApplicationContext()).user);
		this.startActivity(intent);
		*/
		
		/* Starting service */
		Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(this.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10*1000, pintent);
		
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		
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
