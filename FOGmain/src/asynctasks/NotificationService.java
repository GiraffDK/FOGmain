package asynctasks;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.domain.DateAndTimeStringHandler;
import dk.vinael.domain.DateAndTimeStringHandler.RETURN_TYPE;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.fogmain.SearchForPartyActivity;
import dk.vinael.fogmain.ViewPartyActivity;
import dk.vinael.interfaces.FogServiceInterface;
import android.R;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

public class NotificationService extends Service implements FogServiceInterface {

	private ArrayList<Party> al_requested_parties; /* check if you have been accepted */
	private ArrayList<Party> al_attending_parties; /* check if status has changed, alert when it's time to party */
	private User user;
	
	private Runnable r;
	private Handler handler;
	
	private NotificationManager notificationManager;
	
	//private final static int INTERVAL = (60*1000)*10; // 10 min (production)
	private final static int INTERVAL = (60*1000)*2; // 2 min (exams)
	//private final static int INTERVAL = (10000); // 10 sek (testing)
	int counter;
	
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	
	@Override
	public void onCreate() {
		counter = 0;
		user = ((FOGmain)getApplicationContext()).user;
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NotificationService");
		wl.acquire();
		
		handler = new Handler();
		r = new Runnable()
		{
		    public void run() 
		    {
		    	checkForChanges();
		    	/*
		    	if (counter==3){ // every 30 min. start over
		    		
		    		counter=0;
		    	}
		    	*/
		        handler.postDelayed(this, INTERVAL);
		    }
		};
		handler.postDelayed(r, INTERVAL);
	}
	
	@Override
	public void onDestroy(){
		handler.removeCallbacks(r);
		if (notificationManager!=null){
			notificationManager.cancelAll();
		}
		wl.release();
		super.onDestroy();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		return START_STICKY;
    }
	
	public void startKevService(){
		
		if (user.getToken()!=null){
			al_requested_parties = new ArrayList<Party>();
			al_attending_parties = new ArrayList<Party>();
			
			addToRequestedParties(user);
			addToAttendingParties(user);
		}
		else{
			al_requested_parties=null;
			al_attending_parties=null;
			this.stopSelf();
			user = new User();
		}
	}
	
	public void checkForChanges(){
		if (user!=null){
			Log.i("NotificationService", "checkForChanges(), " + user.getFirstName());
			if (al_requested_parties!=null){
				Log.i("NotificationService", "al_requested_parties: " + al_requested_parties.size());
			}
			if (al_attending_parties!=null){
				Log.i("NotificationService", "al_attending_parties: " + al_attending_parties.size());
			}
		}
		if (((FOGmain)this.getApplicationContext()).isNetworkConnected()==true && user!=null){
			//Toast.makeText(this, "checkForChanges()", Toast.LENGTH_LONG).show();
			if (user.getToken()!=null){
				//Toast.makeText(this, ""+al_requested_parties.size()+", "+al_attending_parties.size(), Toast.LENGTH_LONG).show();
				if (al_requested_parties==null && al_attending_parties==null){
					startKevService();
				}
				else{
					//Toast.makeText(this, ""+al_requested_parties.size()+", "+al_attending_parties.size(), Toast.LENGTH_LONG).show();
					if (al_requested_parties.size()>0){checkRequestedParties();}
					if (al_attending_parties.size()>0){checkAttendingParties();}
					counter++;
					/*
					if (al_requested_parties.size()==0 && al_requested_parties.size()==0){
						startKevService();
					}
					else{
						
					}
					*/
				}
			}
			else{
				this.stopSelf();
				user = new User();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addToAttendingParties(User u){
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id " +
				"WHERE user_in_party.user_id = "+u.getUserId()+" " +
				"AND user_in_party.attending_status_id = 2 " +
				"AND STR_TO_DATE(party.start_time, '%Y-%m-%d %H:%i:%s')>NOW();";
		new ServiceHandler(user, "attendingParties", this).execute("select", sqlString);
	}
	
	public void addToRequestedParties(User u){
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id " +
				"WHERE user_in_party.user_id = "+u.getUserId()+" " +
				"AND (user_in_party.attending_status_id = 1 OR user_in_party.attending_status_id = 3) " +
				"AND STR_TO_DATE(party.start_time, '%Y-%m-%d %H:%i:%s')>NOW();";
		new ServiceHandler(user, "requestedParties", this).execute("select", sqlString);
	}
	
	public void checkRequestedParties(){ // check if user has been accepted to party
		if (al_requested_parties!=null){
			for (Party p : al_requested_parties){
				String sqlString = "SELECT user_in_party.attending_status_id, user_in_party.party_id " +
						"FROM user_in_party WHERE party_id=" + p.getId() + " AND user_id="+user.getUserId()+";";
				new ServiceHandler(user, "requestedParty", this).execute("select", sqlString);
			}
		}
	}
	
	public void checkAttendingParties(){ // check if party has been cancelled or it's time to party
		if (al_attending_parties!=null){
			for (Party p : al_attending_parties){
				String sqlString = "SELECT party.start_time, party.status_id, user_in_party.start_time_notified, " +
						"user_in_party.attending_status_id, user_in_party.party_id FROM user_in_party " +
						"INNER JOIN party ON user_in_party.party_id = party.id " +
						"WHERE user_in_party.user_id="+user.getUserId()+" AND party.id="+p.getId()+";";
				new ServiceHandler(user, "attendingParty", this).execute("select", sqlString);
			}
		}
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {	
		//Toast.makeText(this, "result: "+ja.toString(), Toast.LENGTH_LONG).show();
		if (ja!=null){
			JSONObject jo = null;
			try {
				jo = ja.getJSONObject(0);
				// Toast.makeText(this, jo.getString("access")+", "+jo.getString("results"), Toast.LENGTH_LONG).show();
				// Toast.makeText(this, identifier + " " + jo.has("results") + "", Toast.LENGTH_LONG).show();
				if (jo!=null){
					if (!jo.has("access")){
						//Toast.makeText(this, identifier, Toast.LENGTH_LONG).show();
						if (identifier.equals("requestedParties")){
							if(!jo.has("results")){
								for (int i=0; i<ja.length();i++){
									Party tempParty = new Party();
									tempParty.setPartyWithJSON(ja.getJSONObject(i));
									if (tempParty.getId()>0){
										al_requested_parties.add(tempParty);
									}
								}
							}
						}
						else if (identifier.equals("attendingParties")){
							if(!jo.has("results")){
								for (int i=0; i<ja.length();i++){
									Party tempParty = new Party();
									tempParty.setPartyWithJSON(ja.getJSONObject(i));
									if (tempParty.getId()>0){
										al_attending_parties.add(tempParty);
									}
								}
							}
						}
						else if(identifier.equals("requestedParty")){
							if(jo.has("results")){
								//startKevService();
							}
							else{
								
								int attending_status=Integer.parseInt(jo.getString("attending_status_id"));
								int party_id=Integer.parseInt(jo.getString("party_id"));
								
								if (attending_status==2){ // user accepted to party
									for (Party p : al_requested_parties){
										if (p.getId()==party_id){
											Log.i("NotificationService", "requestedParty - attending: " + p.getName());
											al_attending_parties.add(p);
											al_requested_parties.remove(p);
											showNotification("accepted", p);
										}
									}
								}
							}
						}
						else if(identifier.equals("attendingParty")){
							if(jo.has("results")){
								//startKevService();
							}
							else{
								String start_date_time = jo.getString("start_time");
								int party_status = Integer.parseInt(jo.getString("status_id"));
								int attending_status=Integer.parseInt(jo.getString("attending_status_id"));
								int party_id=Integer.parseInt(jo.getString("party_id"));
								int party_start_time_notified = Integer.parseInt(jo.getString("start_time_notified"));
								
								Log.i("NotificationService", "status: " + attending_status + ", " + party_id);
								//Toast.makeText(this, "party_start_time_notified: "+party_start_time_notified, Toast.LENGTH_LONG).show();
								
								if (party_status==0){ // party has been cancelled
									for (Party p : al_attending_parties){
										if (p.getId()==party_id){
											Log.i("NotificationService", "attendingParty - cancelled: " + p.getName());
											showNotification("party_cancelled", p);
										}
									}
								}
								else{
									if (attending_status==1 || attending_status==3){ // user been declined from party
										//showNotification("declined", new Party());
										for (Party p : al_attending_parties){
											if (p.getId()==party_id){
												Log.i("NotificationService", "attendingParty - declined: " + p.getName());
												al_requested_parties.add(p);
												al_attending_parties.remove(p);
												showNotification("declined", p);
											}
										}
									}
									else{
										if (party_start_time_notified==0){
											for (Party p : al_attending_parties){
												if (p.getId()==party_id){
													Calendar party_c = Calendar.getInstance();
													Calendar now_c = Calendar.getInstance();
													party_c.setTime(DateAndTimeStringHandler.getDateStringAsCalendar(start_date_time).getTime());
													now_c.setTime(DateAndTimeStringHandler.getDateStringAsCalendar(DateAndTimeStringHandler.getCurrentDateAndTime()).getTime());
													
													int diff = DateAndTimeStringHandler.dateDifference(party_c, now_c, RETURN_TYPE.MINUTES);
													if (diff<60 && diff>0){
														Log.i("NotificationService", "attendingParty - partytime: " + p.getName());
														String sqlString = "UPDATE user_in_party SET start_time_notified=1 WHERE user_in_party.user_id="+user.getUserId()+" AND user_in_party.party_id="+p.getId()+";";
														new ServiceHandler(user, "userPartyTimeNotified", this).execute("update", sqlString);
														showNotification("partytime", p);
													}
													//Toast.makeText(this, "diff: " + diff +"\n" +
													//		"party: "+party_c.getTime().toString()+"\n" +
													//		"now: "+now_c.getTime().toString(), Toast.LENGTH_LONG).show();
												}
											}
										}
									}
								}
							}
						}
					}
					else{
						this.stopSelf();
						user = new User();
					}
				}
			} catch (JSONException e) {
				this.stopSelf();
				user = new User();
				//e.printStackTrace();
			}
		}
	}

	public void showNotification(String identifier, Party p){
		
		//Toast.makeText(this, identifier, Toast.LENGTH_LONG).show();
		//Intent intendo = new Intent(this, NotificationResultActivity.class);
		//PendingIntent pIntent = PendingIntent.getActivity(this, 0, intendo, 0);
		
		Intent view_party_activity = new Intent(this, ViewPartyActivity.class);
		view_party_activity.putExtra("party", p);
		//view_party_activity.putExtra("openMap","no");
		view_party_activity.setAction("view");
		PendingIntent p_view_party_activity = PendingIntent.getActivity(this, 0, view_party_activity, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent show_directions_activity = new Intent(this, ViewPartyActivity.class);
		show_directions_activity.putExtra("party", p);
		//show_directions_activity.putExtra("openMap", "yes");
		show_directions_activity.setAction("map");
		PendingIntent p_show_directions_activity = PendingIntent.getActivity(this, 0, show_directions_activity, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent search_party_activity = new Intent(this, SearchForPartyActivity.class);
		PendingIntent p_search_party_activity = PendingIntent.getActivity(this, 0, search_party_activity, PendingIntent.FLAG_UPDATE_CURRENT);
		search_party_activity.setAction("search");
		
		Builder notificationbuilder = new NotificationCompat.Builder(this);
		int icon = R.drawable.ic_lock_idle_alarm;
		
		if (identifier.equals("accepted")){
			notificationbuilder
				.setContentTitle("Accepted to party!")
				.setContentText("Accepted to: " + p.getName())
				.setSmallIcon(icon)
				.setContentIntent(p_view_party_activity)
				.addAction(0, "View party", p_view_party_activity)
				.addAction(0, "Directions", p_show_directions_activity);
		}
		if (identifier.equals("party_cancelled")){
			notificationbuilder
				.setContentTitle("Party is cancelled!")
				.setContentText(p.getName()+" is cancelled")
				.setSmallIcon(icon)
				.setContentIntent(p_search_party_activity)
				.addAction(0, "Search for party", p_search_party_activity);
		}
		if (identifier.equals("declined")){
			notificationbuilder
				.setContentTitle("Bad news")
				.setContentText(p.getName()+" is already full")
				.setSmallIcon(icon)
				.setContentIntent(p_search_party_activity)
				.addAction(0, "Search for party", p_search_party_activity);
		}
		if (identifier.equals("partytime")){
			notificationbuilder
				.setContentTitle("PARTY TIME!")
				.setContentText("Party at: " + p.getName())
				.setSmallIcon(icon)
				.setContentIntent(p_view_party_activity)
				.addAction(0, "View party", p_view_party_activity)
				.addAction(0, "Directions", p_show_directions_activity);
		}
		
		// Sound when notification arrives
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notificationbuilder.setSound(alarmSound);
		
		Notification n = notificationbuilder.build();
		
		long[] vibrate = {0,100,200,300};
        n.vibrate = vibrate;
		
		n.ledARGB = Color.BLUE;
		n.ledOnMS = 300; 
		n.ledOffMS = 1000;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		n.flags |= Notification.FLAG_AUTO_CANCEL + Notification.FLAG_SHOW_LIGHTS;
		notificationManager.notify(0, n);
		
	}
}
