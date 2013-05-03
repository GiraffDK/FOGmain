package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.widget.Toast;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ShowPartyRequestersActivity extends Activity implements FogActivityInterface {

	FogActivityInterface fai;
	private Bundle bundle;
	private User user;
	private Party party;
	
	private ArrayList<User> al_requesters;
	
	private ListView lv_requesters_showpartyrequesters;
	private ArrayAdapter<User> listAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpartyrequesters);
		
		fai = this;
		user = ((FOGmain)getApplicationContext()).user;
		bundle = getIntent().getExtras();
		al_requesters = new ArrayList<User>();
		lv_requesters_showpartyrequesters = ((ListView)findViewById(R.id.lv_requesters_showpartyrequesters));
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			party.getPartyRequesters(this, "getPartyRequesters");
		}
		else{
			// EXIT
		}
		
		lv_requesters_showpartyrequesters.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				// Go to profile
				// Accept
				// Deny
				
				final Activity aFai = (Activity)fai;
				
				Toast.makeText(aFai, ""+arg2, Toast.LENGTH_LONG).show();
				User tmpUser = al_requesters.get(arg2);
				String[] a_choices = {"See profile","Accept", "Deny"};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(aFai);
				builder.setTitle("What to do?");
				
				builder.setItems(a_choices, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		                   // The 'which' argument contains the index position
		                   // of the selected item
		            	   Toast.makeText(aFai, ""+which, Toast.LENGTH_LONG).show();
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
		});
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getPartyRequesters")){
			for (int i = 0; i < ja.length(); i++) {
				User tmp_user = new User();
				try {
					tmp_user.setUserByJson(ja.getJSONObject(i));
					al_requesters.add(tmp_user);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (al_requesters.size()>0){
				listAdapter = new ArrayAdapter<User>(this, R.layout.listview_row_partyrequester, al_requesters);
				lv_requesters_showpartyrequesters.setAdapter(listAdapter);
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
