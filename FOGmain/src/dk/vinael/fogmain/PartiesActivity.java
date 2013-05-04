package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PartiesActivity extends Activity implements FogActivityInterface {
	private ListView owner_parties;
	private ListView att_parties;
	final ArrayList<Party> reqAttList = new ArrayList<Party>();
	final ArrayList<String> stringList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parties);
		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.ic_party);
		bar.setTitle("Your/Requested Parties");

		// Calls
		SqlWrapper.selectAllPartiesByOwnerUserId(this, "owner_parties", ((FOGmain) getApplicationContext()).user.getUserId());
		SqlWrapper.selectAllPartiesImAttending(this, "attending_parties", ((FOGmain) getApplicationContext()).user.getUserId());
		SqlWrapper.selectAllPartiesIveRequested(this, "requested_parties", ((FOGmain) getApplicationContext()).user.getUserId());

		owner_parties = (ListView) findViewById(R.id.parties_lv_owner);
		att_parties = (ListView) findViewById(R.id.parties_lv_attending);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_parties, menu);
		return true;
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("owner_parties")) {
			final ArrayList<Party> ownerList = new ArrayList<Party>();
			try {
				for (int i = 0; i < ja.length(); i++) {
					Party temp = new Party();
					temp.setPartyWithJSON(ja.getJSONObject(i));
					ownerList.add(temp);
				}
				ArrayAdapter<Party> arrayAdapter = new ArrayAdapter<Party>(this, android.R.layout.simple_list_item_1, ownerList);
				owner_parties.setAdapter(arrayAdapter);
				owner_parties.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> l, View v, int position, long id) {
						Intent intent = new Intent(PartiesActivity.this, ViewPartyActivity.class);
						intent.putExtra("party", ownerList.get(position));
						PartiesActivity.this.startActivity(intent);
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (identifier.equals("attending_parties")) {
			JSONObject checker = null;
			try {
				checker = ja.getJSONObject(0);
				if (!checker.has("results")) {
					for (int i = 0; i < ja.length(); i++) {
						Party temp = new Party();
						temp.setPartyWithJSON(ja.getJSONObject(i));
						reqAttList.add(temp);
						stringList.add(temp.getName() + " (Attending)");
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (identifier.equals("requested_parties")) {
			JSONObject checker = null;
			try {
				checker = ja.getJSONObject(0);
				if (!checker.has("results")) {
					for (int i = 0; i < ja.length(); i++) {
						Party temp = new Party();
						temp.setPartyWithJSON(ja.getJSONObject(i));
						reqAttList.add(temp);
						stringList.add(temp.getName() + " (Requested)");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringList);
			att_parties.setAdapter(arrayAdapter);
			att_parties.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> l, View v, int position, long id) {
					Intent intent = new Intent(PartiesActivity.this, ViewPartyActivity.class);
					intent.putExtra("party", reqAttList.get(position));
					PartiesActivity.this.startActivity(intent);
				}
			});
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
