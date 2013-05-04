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

public class ShowPartyRequestersActivity extends Activity implements FogActivityInterface {

	FogActivityInterface fai;
	private Bundle bundle;
	private User user;
	private Party party;
	
	private ArrayList<User> al_requesters;
	private ArrayList<User> al_denied;
	
	private ListView lv_requesters_showpartyrequesters;
	private ListView lv_denied_showpartyrequesters;
	
	private ArrayAdapter<User> listAdapterRequester;
	private ArrayAdapter<User> listAdapterDenied;
	
	private TextView tv_show_requesters_showpartyrequesters;
	private TextView tv_show_denied_showpartyrequesters;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpartyrequesters);
		
		fai = this;
		user = ((FOGmain)getApplicationContext()).user;
		bundle = getIntent().getExtras();
		
		al_requesters = new ArrayList<User>();
		al_denied = new ArrayList<User>();
		
		lv_requesters_showpartyrequesters = ((ListView)findViewById(R.id.lv_requesters_showpartyrequesters));
		lv_denied_showpartyrequesters = ((ListView)findViewById(R.id.lv_denied_showpartyrequesters));
		
		tv_show_requesters_showpartyrequesters = ((TextView)findViewById(R.id.tv_show_requesters_showpartyrequesters));
		tv_show_denied_showpartyrequesters = ((TextView)findViewById(R.id.tv_show_denied_showpartyrequesters));
		
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			party.getPartyRequesters(this, "getPartyRequesters");
		}
		else{
			// EXIT
		}
		
		OnItemClickListener oicl_user_status = new OnItemClickListener() {
			
			User tmpUser;
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				
				// Go to profile
				// Accept
				// Deny
				
				final Activity aFai = (Activity)fai;
				//final User tmpUser = new User();
				
				//Toast.makeText(aFai, ""+arg2, Toast.LENGTH_LONG).show();
				if (view.getParent() == findViewById(R.id.lv_requesters_showpartyrequesters)){
					tmpUser = al_requesters.get(index);
				}
				else{
					tmpUser = al_denied.get(index);
				}
				
				String[] a_choices = {"See profile","Accept", "Deny"};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(aFai);
				builder.setTitle("What to do with " +tmpUser.getFirstName() + "?");
				
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
		            		   party.userAcceptedToParty((FogActivityInterface)aFai, "userAccepted", tmpUser);
		            	   }
		            	   else if(which==2){
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
		lv_requesters_showpartyrequesters.setOnItemClickListener(oicl_user_status);
		lv_denied_showpartyrequesters.setOnItemClickListener(oicl_user_status);
	}
	
	private void reloadActivity(){
		Intent intent = new Intent(this, ShowPartyRequestersActivity.class);
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
		if (identifier.equals("getPartyRequesters")){
			//Toast.makeText(this, ""+ja.toString(), Toast.LENGTH_LONG).show();
			al_requesters.clear();
			putUsersInArrayList(ja, al_requesters);
			
			//Toast.makeText(this, ""+al_requesters.size(), Toast.LENGTH_LONG).show();
			if (al_requesters.size()>0){
				listAdapterRequester = new ArrayAdapter<User>(this, R.layout.listview_row_requester_partyrequester, al_requesters);
				lv_requesters_showpartyrequesters.setAdapter(listAdapterRequester);
				int c = al_requesters.size();
				tv_show_requesters_showpartyrequesters.setText(tv_show_requesters_showpartyrequesters.getText()+ " ("+c+")");
			}
			party.getPartyDenied(this, "getPartyDenied");
		}
		else if (identifier.equals("getPartyDenied")){
			al_denied.clear();
			putUsersInArrayList(ja, al_denied);
			
			//Toast.makeText(this, ""+al_denied.size(), Toast.LENGTH_LONG).show();
			if (al_denied.size()>0){
				listAdapterDenied = new ArrayAdapter<User>(this, R.layout.listview_row_denied_partyrequester, al_denied);
				lv_denied_showpartyrequesters.setAdapter(listAdapterDenied);
				int c = al_denied.size();
				tv_show_denied_showpartyrequesters.setText(tv_show_denied_showpartyrequesters.getText()+ " ("+c+")");
			}else{lv_denied_showpartyrequesters.setVisibility(View.GONE);}
		}
		else if(identifier.equals("userAccepted")){
			Toast.makeText(this, "User accepted", Toast.LENGTH_LONG).show();
			reloadActivity();
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
