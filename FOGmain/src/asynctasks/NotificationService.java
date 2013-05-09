package asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogServiceInterface;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service implements FogServiceInterface {

	private ArrayList<Party> al_requested_parties; /* check if you have been accepted */
	private ArrayList<Party> al_attending_parties; /* check if status has changed, alert when it's time to party */
	private User user;
	
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Notification Service has been started!", Toast.LENGTH_LONG).show();
		user = ((FOGmain)getApplicationContext()).user;
		if (user!=null){
			al_requested_parties = new ArrayList<Party>();
			al_attending_parties = new ArrayList<Party>();
			addToRequestedParties(user);
			addToAttendingParties(user);
		}
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		Toast.makeText(this, ""+al_requested_parties.size()+", "+al_attending_parties.size(), Toast.LENGTH_LONG).show();
		
		if (user!=null){
			return START_STICKY;
		}
		else{
			return START_NOT_STICKY;
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
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {	
		//Toast.makeText(this, "result: "+ja.toString(), Toast.LENGTH_LONG).show();
		if (ja!=null){
			JSONObject jo = null;
			try {
				jo = ja.getJSONObject(0);
				//Toast.makeText(this, jo.getString("access"), Toast.LENGTH_LONG).show();
				if (jo!=null && !jo.getString("access").equals("denied")){
					if (!jo.has("access") || !jo.has("results")){
						if (identifier.equals("requestedParties")){
							for (int i=0; i<ja.length();i++){
								Party tempParty = new Party();
								tempParty.setPartyWithJSON(ja.getJSONObject(i));
								if (tempParty.getId()>0){
									al_requested_parties.add(tempParty);
								}
							}
						}
						else if (identifier.equals("attendingParties")){
							for (int i=0; i<ja.length();i++){
								Party tempParty = new Party();
								tempParty.setPartyWithJSON(ja.getJSONObject(i));
								if (tempParty.getId()>0){
									al_attending_parties.add(tempParty);
								}
							}
						}
						else if(identifier.equals("requestedParty")){
							int attending_status=Integer.parseInt(jo.getString("attending_status_id"));
							int party_id=Integer.parseInt(jo.getString("party_id"));
							
							if (attending_status==2){
								for (Party p : al_requested_parties){
									if (p.getId()==party_id){
										al_attending_parties.add(p);
										al_requested_parties.remove(p);
										showRequestBeenAcceptedNotification(p);
									}
								}
							}
						}
					}
				}
				else{
					this.stopSelf();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		JSONObject jo;
		try {
			jo = ja.getJSONObject(0);
			if (jo.has("results") || jo.has("access")){
				// No results
			}
			else{
				if (identifier.equals("requestedParties")){
					for (int i=0; i>ja.length(); i++){
						Party tempParty = new Party();
						tempParty.setPartyWithJSON(ja.getJSONObject(i));
						al_requested_parties.add(tempParty);
					}
				}
				else if(identifier.equals("attendingParties")){
					for (int i=0; i>ja.length(); i++){
						Party tempParty = new Party();
						tempParty.setPartyWithJSON(ja.getJSONObject(i));
						al_attending_parties.add(tempParty);
					}
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public void checkAttendingParties(){
		if (al_attending_parties!=null){
			
		}
	}
	*/
	public void showRequestBeenAcceptedNotification(Party p){
		
		Toast.makeText(this, "You have been accepted to party: "+p.getName(), Toast.LENGTH_LONG).show();
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
