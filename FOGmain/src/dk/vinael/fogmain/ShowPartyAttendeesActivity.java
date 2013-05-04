package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.fogmain.R.id;
import dk.vinael.interfaces.FogActivityInterface;

public class ShowPartyAttendeesActivity extends Activity implements FogActivityInterface {

	FogActivityInterface fai;
	private Bundle bundle;
	private User user;
	private Party party;
	
	int user_status = 0;
	private ArrayList<User> al_attendees;
	
	private ListView lv_attendees_showpartyattendees;
	private ArrayAdapter<User> listAdapterAttendees;
	
	private TextView tv_show_attendees_showpartyattendees;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpartyattendees);
		
		fai = this;
		user = ((FOGmain)getApplicationContext()).user;
		bundle = getIntent().getExtras();
		
		al_attendees = new ArrayList<User>();
		
		lv_attendees_showpartyattendees = ((ListView)findViewById(R.id.lv_attendees_showpartyattendees));
		
		tv_show_attendees_showpartyattendees = ((TextView)findViewById(R.id.tv_show_attendees_showpartyattendees));
		
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			party.getPartyAttendees(this, "getPartyAttendees");
		}
		else{
			// EXIT
		}
		
		if (party.getOwnerId()==user.getUserId()){
			user_status=1;
		}
		
		OnItemClickListener oicl_user_status = new OnItemClickListener() {
			
			User tmpUser;
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				
				// Go to profile
				// Deny (owner only)
				
				final Activity aFai = (Activity)fai;
				//final User tmpUser = new User();
				
				//Toast.makeText(aFai, ""+arg2, Toast.LENGTH_LONG).show();
				tmpUser = al_attendees.get(index);
				
				ArrayList<String> al_choices = new ArrayList<String>();
				al_choices.add("See profile");
				if (user_status==1){
					al_choices.add("Remove from party");
				}
				//String[] a_choices = new String[al_choices.size()];
				String[] a_choices = al_choices.toArray(new String[al_choices.size()]);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(aFai);
				builder.setTitle("I want to..");
				
				builder.setItems(a_choices, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		                   // The 'which' argument contains the index position
		                   // of the selected item
		            	   if (which==0){
		            		Intent intent = new Intent(aFai, ViewProfilActivity.class);
		           			intent.putExtra("user", tmpUser);
		           			
		           			aFai.startActivity(intent);
		            	   }
		            	   else if(which==1){
		            		   party.userDeniedToParty((FogActivityInterface)aFai, "userDenied", tmpUser);
		            	   }
		            	   //Toast.makeText(aFai, ""+which, Toast.LENGTH_LONG).show();
		               };
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			           }
			       });
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
		};
		lv_attendees_showpartyattendees.setOnItemClickListener(oicl_user_status);
	}
	
	private void reloadActivity(){
		Intent intent = new Intent(this, ShowPartyAttendeesActivity.class);
		intent.putExtra("party", party);
		this.finish();
		this.startActivity(intent);
	}
	
	private void putUsersInArrayList(JSONArray ja_results, ArrayList al_data){
		JSONObject result = null;
		try {
			 result = ja_results.getJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (!result.has("results")){
			for (int i = 0; i < ja_results.length(); i++) {
				User tmp_user = new User();
				try {
					tmp_user.setUserByJson(ja_results.getJSONObject(i));
					if (tmp_user.getUserId()!=0){
						al_data.add(tmp_user);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getPartyAttendees")){
			//Toast.makeText(this, ""+ja.toString(), Toast.LENGTH_LONG).show();
			al_attendees.clear();
			putUsersInArrayList(ja, al_attendees);
			
			//Toast.makeText(this, ""+al_requesters.size(), Toast.LENGTH_LONG).show();
			if (al_attendees.size()>0){
				listAdapterAttendees = new ArrayAdapter<User>(this, R.layout.listview_row_requester_partyrequester, al_attendees);
				lv_attendees_showpartyattendees.setAdapter(listAdapterAttendees);
				int c = al_attendees.size();
				tv_show_attendees_showpartyattendees.setText(tv_show_attendees_showpartyattendees.getText()+ " ("+c+")");
			}
			party.getPartyDenied(this, "getPartyDenied");
		}
		else if(identifier.equals("userDenied")){
			Toast.makeText(this, "User denied", Toast.LENGTH_LONG).show();
			reloadActivity();
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
