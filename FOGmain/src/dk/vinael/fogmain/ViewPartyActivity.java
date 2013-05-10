package dk.vinael.fogmain;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import dk.vinael.domain.DateAndTimeStringHandler;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ViewPartyActivity extends Activity implements FogActivityInterface, LocationListener {

	private ViewPartyActivity fai = this;
	
	private Location loc;
	private LocationManager locationManager;
	private User user;
	private User OwnerUser = new User();
	private Party party;
	private Bundle bundle;
	
	private View viewbyall;
	private View viewbyrequesterattendee;
	private View viewbyowner;
	private View viewbyattendee;
	
	private int user_status = -1; /* -2 = denied, -1 = looker, 0=requester, 1=owner, 2=attending guest */
	private TextView viewparty_tv_ownertext;
	private TextView tv_partyname_viewparty;
	private TextView tv_description_viewparty;
	private TextView tv_start_date_time_viewparty;
	private TextView tv_end_date_time_viewparty;
	private TextView tv_age_level_viewparty;
	private TextView tv_party_address_viewparty;
	private TextView tv_doorcode_viewparty;
	private Button btn_requestcancelunsub_viewparty;
	private Button btn_editparty_viewparty;
	private Button btn_gotoattendingguests_viewparty;
	
	private String action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewparty);
		
		bundle = getIntent().getExtras();
		
		action = getIntent().getAction();
		
		user = ((FOGmain)getApplicationContext()).user;
		party = (Party) bundle.getSerializable("party");
		
		OwnerUser.setUserId(party.getOwnerId());
		OwnerUser.selectUserByUserID(this, "owner_user");
		
		/* Set elements */
		viewparty_tv_ownertext = (TextView) findViewById(R.id.viewparty_tv_ownertext);
		tv_partyname_viewparty = ((TextView) findViewById(R.id.tv_partyname_viewparty));
		tv_description_viewparty = ((TextView) findViewById(R.id.tv_description_viewparty));
		tv_start_date_time_viewparty = ((TextView) findViewById(R.id.tv_start_date_time_viewparty));
		tv_end_date_time_viewparty = ((TextView) findViewById(R.id.tv_end_date_time_viewparty));
		tv_age_level_viewparty = ((TextView) findViewById(R.id.tv_age_level_viewparty));
		tv_party_address_viewparty = ((TextView) findViewById(R.id.tv_party_address_viewparty));
		tv_doorcode_viewparty = ((TextView) findViewById(R.id.tv_doorcode_viewparty));
		btn_requestcancelunsub_viewparty = ((Button) findViewById(R.id.btn_requestcancelunsub_viewparty));
		btn_editparty_viewparty = ((Button) findViewById(R.id.btn_editparty_viewparty));
		btn_gotoattendingguests_viewparty = ((Button) findViewById(R.id.btn_gotoattendingguests_viewparty));
		
		/* Determing user status - start */
		
		viewbyall = ((View) findViewById(R.id.ll_party_viewbyall_viewparty));
		viewbyrequesterattendee = ((View) findViewById(R.id.ll_party_viewbyrequesterattendees_viewparty));
		viewbyattendee = ((View) findViewById(R.id.ll_party_viewbyattendees_viewparty));
		viewbyowner = ((View) findViewById(R.id.ll_party_viewbyowner_viewparty));
		
		viewbyall.setVisibility(View.GONE);
		viewbyrequesterattendee.setVisibility(View.GONE);
		viewbyattendee.setVisibility(View.GONE);
		viewbyowner.setVisibility(View.GONE);
		
		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.ic_a_stiff_drink);
		bar.setTitle("View Party");
		bar.setHomeButtonEnabled(true);
		
		if (user.getUserId() == party.getOwnerId()){
			user_status=1;
			showLayouts();
		}
		else{
			party.selectUserInParty(this, "showPartyInfo", user);
		}
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		
		/* Determing user status - end */
	}
	
	public void showLayouts(){
		//Toast.makeText(this, "user status: "+user_status, Toast.LENGTH_LONG).show();
		if (user_status>=-2){ /* all */
			// show only layout for all
			viewbyall.setVisibility(View.VISIBLE);
		}
		if (user_status==-2 || user_status==-1 || user_status==0 || user_status==2){ /* requester || attending guest */
			// handle request button
			viewbyrequesterattendee.setVisibility(View.VISIBLE);
			if (user_status==-2){
				btn_requestcancelunsub_viewparty.setText("Requested");
			
				btn_requestcancelunsub_viewparty.setEnabled(false);
				btn_requestcancelunsub_viewparty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//party.userRequestParty(fai, "userRequestParty", user);
					}
				});
				
			}
			else if(user_status==-1){
				btn_requestcancelunsub_viewparty.setText("Request");
				btn_requestcancelunsub_viewparty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						party.userRequestParty(fai, "userRequestParty", user);
					}
				});
			}
			else if (user_status==0 || user_status==2){
				if (user_status==0){btn_requestcancelunsub_viewparty.setText("Cancel request");}
				else if(user_status==2){btn_requestcancelunsub_viewparty.setText("Unsubscribe party");}
				btn_requestcancelunsub_viewparty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						party.userCancelRequestParty(fai, "userCancelRequestParty", user);
					}
				});
			}
			else{
				btn_requestcancelunsub_viewparty.setVisibility(View.GONE);
			}
		}
		if (user_status>=1){ /* owner || attending guest */
			viewbyattendee.setVisibility(View.VISIBLE);
			
			btn_gotoattendingguests_viewparty.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(fai, ShowPartyAttendeesActivity.class);
					intent.putExtra("party", party);
					//this.finish();
					fai.startActivity(intent);
					
				}
			});
			
		}
		if (user_status==1){ /* owner */
			viewbyowner.setVisibility(View.VISIBLE);
			btn_editparty_viewparty.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(fai, AddEditPartyActivity.class);
					intent.putExtra("party", party);
					//this.finish();
					fai.startActivity(intent);
				}
			});
		}
		showPartyData();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_to_main_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((FOGmain) getApplicationContext()).onOptionsItemSelected(item,this);
		return true;
	}
	
	public void showPartyData(){
		tv_partyname_viewparty.setText(party.getName());
		tv_description_viewparty.setText(party.getDescription());
		tv_start_date_time_viewparty.setText(party.getStartDateAndTime());
		tv_end_date_time_viewparty.setText(party.getEndDateAndTime());
		tv_age_level_viewparty.setText(party.getMinAge() + " - " + party.getMaxAge());
		tv_party_address_viewparty.setText(party.getAddress() + "\n" + party.getZip() + ", " + party.getCity() + "\n" + party.getCountry());
		tv_doorcode_viewparty.setText(party.getDoorCode());
		

		// if notification wants to open Google Map:
		if (action.equals("map")){
			ImageView v = (ImageView) findViewById(R.id.iv_showdirections_viewparty);
			showDirections(v);
		}
		
	}
	
	public void seeRequesters(View v){
		Intent intent = new Intent(this, ShowPartyRequestersActivity.class);
		intent.putExtra("party", party);
		//this.finish();
		this.startActivity(intent);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("showPartyInfo")){
			//Toast.makeText(this, "JSONArray: "+ja.toString(), Toast.LENGTH_LONG).show();
			if (ja != null){
				JSONObject jo = null;
				try {
					jo = ja.getJSONObject(0);
					if (jo.has("attending_status_id")){
						try {
							if (jo.getString("attending_status_id").equals("1")){ // Requested
								user_status = 0;
							}
							else if(jo.getString("attending_status_id").equals("2")){ // Attending
								user_status = 2;
							}
							else if (jo.getString("attending_status_id").equals("3")){ // Rejected
								user_status = -2;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			showLayouts();

		} else if (identifier.equals("owner_user")) {
			try {
				OwnerUser.setUserByJson(ja.getJSONObject(0));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewparty_tv_ownertext.setText(OwnerUser.getFirstName() + "(" + OwnerUser.getFbUserId()+ ")");
		}
		else if (identifier.equals("userRequestParty") || identifier.equals("userCancelRequestParty")){
			Intent intent = new Intent(this, ViewPartyActivity.class);
			intent.putExtra("party", party);
			this.finish();
			this.startActivity(intent);
		}
	}
	public void showDirections(View v) {
		String sAddr = ""+loc.getLatitude() + "," +loc.getLongitude();
		String dAddr = tv_party_address_viewparty.getText().toString();
		String url = "http://maps.google.com/maps?saddr=" + sAddr + "&daddr=" +  dAddr;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
		startActivity(intent);
	}
	public void makeAppointment(View v) {
		Calendar beginTime = Calendar.getInstance();
		String startDate = party.getStartDateAndTime();
		String endDate = party.getEndDateAndTime();
		beginTime.set(
				DateAndTimeStringHandler.getYearFromDateAndTime(startDate), 
				DateAndTimeStringHandler.getMonthFromDateAndTime(startDate),
				DateAndTimeStringHandler.getDayOfMonthFromDateAndTime(startDate),
				DateAndTimeStringHandler.getHourFromDateAndTime(startDate),
				DateAndTimeStringHandler.getMinuttesFromDateAndTime(startDate));
		Toast.makeText(this, ""+DateAndTimeStringHandler.getYearFromDateAndTime(startDate) + "-" +
				DateAndTimeStringHandler.getMonthFromDateAndTime(startDate) + "-" +
				DateAndTimeStringHandler.getDayOfMonthFromDateAndTime(startDate) + " @ " + 
				DateAndTimeStringHandler.getHourFromDateAndTime(startDate) + " : " +
				DateAndTimeStringHandler.getMinuttesFromDateAndTime(startDate), Toast.LENGTH_LONG).show();
		Calendar endTime = Calendar.getInstance();
		endTime.set(
				DateAndTimeStringHandler.getYearFromDateAndTime(endDate), 
				DateAndTimeStringHandler.getMonthFromDateAndTime(endDate),
				DateAndTimeStringHandler.getDayOfMonthFromDateAndTime(endDate),
				DateAndTimeStringHandler.getHourFromDateAndTime(endDate),
				DateAndTimeStringHandler.getMinuttesFromDateAndTime(endDate));
		Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
		        .putExtra(Events.TITLE, party.getName())
		        .putExtra(Events.DESCRIPTION, "Group class")
		        .putExtra(Events.EVENT_LOCATION, party.getAddress() + "\n" + party.getZip() + ", " + party.getCity())
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
		        .putExtra(Intent.EXTRA_EMAIL, "");
		startActivity(intent);
	}

	@Override
	public void returningAddress(String Address, String identifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returningLocation(Location location, String identifier) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location location) {
		loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
