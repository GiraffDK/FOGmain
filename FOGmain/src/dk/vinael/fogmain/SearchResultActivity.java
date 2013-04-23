package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
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
import asynctasks.WebserviceCaller;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.interfaces.FogActivityInterface;

public class SearchResultActivity extends Activity implements OnClickListener, FogActivityInterface {
	
	private ArrayList<Party> ps = new ArrayList<Party>();
	private Bundle bun;
	
	private Location loc;
	private double radius;
	private int min_age;
	private int max_age;
	private String start_date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultview);
		
		// Getting incoming data.
		bun = getIntent().getExtras();
		loc = new Location("");
		loc.set((Location) bun.get("Location"));
		radius = (double) bun.getInt("Radius");
		min_age = bun.getInt("Min_age");
		max_age = bun.getInt("Max_age");
		//start_date = bun.getString("Start_date");
		getPartiesByRadius();
		
	}
	public void getPartiesByRadius() {
		Double loclat = loc.getLatitude();
		Double loclon = loc.getLongitude();
		
		/*
		new WebserviceCaller(this, "getParties")
			.execute("select", "SELECT * FROM party WHERE lat > '"+(loclat - radius)+"' AND " +
					"lat < '"+(loclat + radius)+"' AND " +
					"lon > '"+(loclon - radius)+"' AND " +
					"lon < '"+(loclon + radius)+"';");
		*/
		
		SqlWrapper.selectParties(this, "getParties", loclat, loclon, radius, min_age, max_age);
	}

	@Override
	public void onClick(View v) {
		Intent in = new Intent(this, MapsActivity.class);
		in.putExtra("Location", loc);
		in.putExtra("List", ps);
		startActivity(in);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		Toast.makeText(this, ""+ja.length(), Toast.LENGTH_LONG).show();
		if (identifier.equals("getParties")) {
			Toast.makeText(this, ""+ja.length(), Toast.LENGTH_LONG).show();
			try {
				for (int i = 0; i < ja.length(); i++) {
					Party temp = new Party();
					temp.setPartyWithJSON(ja.getJSONObject(i));
					ps.add(temp); 
				}
				ListView lv = (ListView) findViewById(R.id.list_search_result);
				lv.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> l, View v, int position, long id) {
						Intent intent = new Intent(SearchResultActivity.this, ViewPartyActivity.class);
						intent.putExtra("party", ps.get(position));
						SearchResultActivity.this.startActivity(intent);
					}
				});
				ArrayAdapter<Party> myarrayAdapter = new ArrayAdapter<Party>(this, android.R.layout.simple_list_item_1, ps);
				lv.setAdapter(myarrayAdapter);
				lv.setTextFilterEnabled(true);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
