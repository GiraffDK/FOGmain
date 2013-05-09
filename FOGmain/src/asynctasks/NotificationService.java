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
import dk.vinael.interfaces.FogServiceInterface;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service implements FogServiceInterface {

	private ArrayList<Party> al_requested_parties; /* check if you have been accepted */
	private ArrayList<Party> al_attending_parties; /* check if status has changed, alert when it's time to party */
	private User user;
	
	Runnable r;
	
	private Handler handler;
	
	@Override
	public void onCreate() {
		user = ((FOGmain)getApplicationContext()).user;
		handler = new Handler();
		r = new Runnable()
		{
		    public void run() 
		    {
		    	checkForChanges();
		        handler.postDelayed(this, 10000);
		    }
		};
		handler.postDelayed(r, 10000);
	}
	
	
	public void startKevService(){
		user = ((FOGmain)getApplicationContext()).user;
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
		}
	}
	
	public void checkForChanges(){
		if (user.getToken()!=null){
			//Toast.makeText(this, ""+al_requested_parties.size()+", "+al_attending_parties.size(), Toast.LENGTH_LONG).show();
			if (al_requested_parties==null || al_attending_parties==null){
				startKevService();
			}else{
				Toast.makeText(this, ""+al_requested_parties.size()+", "+al_attending_parties.size(), Toast.LENGTH_LONG).show();
				if (al_requested_parties.size()>0){checkRequestedParties();}
				if (al_attending_parties.size()>0){checkAttendingParties();}
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
		}
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		return START_STICKY;
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
				"AND user_in_party.attending_status_id = 2;";
		new ServiceHandler(user, "attendingParties", this).execute("select", sqlString);
	}
	
	public void addToRequestedParties(User u){
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id " +
				"WHERE user_in_party.user_id = "+u.getUserId()+" " +
				"AND (user_in_party.attending_status_id = 1 OR user_in_party.attending_status_id = 3);";
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
				String sqlString = "SELECT party.start_time, party.status_id, user_in_party.start_time_notified, user_in_party.attending_status_id, user_in_party.party_id FROM user_in_party INNER JOIN party ON user_in_party.party_id = party.id WHERE user_in_party.user_id="+user.getUserId()+";";
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
						Toast.makeText(this, identifier, Toast.LENGTH_LONG).show();
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
								startKevService();
							}
							else{
								int attending_status=Integer.parseInt(jo.getString("attending_status_id"));
								int party_id=Integer.parseInt(jo.getString("party_id"));
								
								if (attending_status==2){ // user accepted to party
									for (Party p : al_requested_parties){
										if (p.getId()==party_id){
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
								startKevService();
							}
							else{
								String start_date_time = jo.getString("start_time");
								int party_status = Integer.parseInt(jo.getString("status_id"));
								int attending_status=Integer.parseInt(jo.getString("attending_status_id"));
								int party_id=Integer.parseInt(jo.getString("party_id"));
								int party_start_time_notified = Integer.parseInt(jo.getString("start_time_notified"));
								
								Toast.makeText(this, "party_start_time_notified: "+party_start_time_notified, Toast.LENGTH_LONG).show();
								
								if (party_status==0){ // party has been cancelled
									for (Party p : al_attending_parties){
										if (p.getId()==party_id){
											showNotification("party_cancelled", p);
										}
									}
								}
								else{
									if (attending_status==1 || attending_status==3){ // user been declined from party
										showNotification("declined", new Party());
										for (Party p : al_attending_parties){
											if (p.getId()==party_id){
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
													
													double diff = DateAndTimeStringHandler.dateDiffInMinutes(party_c, now_c, RETURN_TYPE.MINUTES);
													if (diff<60){
														showNotification("partytime: "+diff, p);
													}
													Toast.makeText(this, "minutes: "+diff+"", Toast.LENGTH_LONG).show();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				this.stopSelf();
				//e.printStackTrace();
			}
		}
	}

	public void showNotification(String identifier, Party p){
		
		Toast.makeText(this, identifier, Toast.LENGTH_LONG).show();
		/*
		// Prepare intent which is triggered if the
		// notification is selected
		
		Intent intendo = new Intent(this, NotificationResultActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intendo, 0);
		
		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(this)
		    .setContentTitle("New mail from " + "test@gmail.com")
		    .setContentText("Subject").setSmallIcon(R.drawable.greenbutton)
		    .setContentIntent(pIntent)
		    .addAction(R.drawable.ic_a_stiff_drink, "Call", pIntent)
		    .addAction(R.drawable.ic_away_girl, "More", pIntent)
		    .addAction(R.drawable.ic_busy, "And more", pIntent).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notificationManager.notify(0, noti);
		
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        */
	}
	
}
