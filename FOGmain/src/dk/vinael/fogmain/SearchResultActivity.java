package dk.vinael.fogmain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import design.RowAdapter;
import dk.vinael.domain.Party;
import dk.vinael.domain.PartySortingComparator;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.interfaces.FogActivityInterface;

public class SearchResultActivity extends Activity implements OnClickListener, FogActivityInterface {

	private ArrayList<Party> partyList = new ArrayList<Party>();
	private Bundle bun;
	private RowAdapter adap;

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
		// start_date = bun.getString("Start_date");
		getPartiesByRadius();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sort, menu);
		return true;
	}

	public void getPartiesByRadius() {
		Double loclat = loc.getLatitude();
		Double loclon = loc.getLongitude();
		SqlWrapper.selectParties(this, "getParties", loclat, loclon, radius, min_age, max_age);
	}

	@Override
	public void onClick(View v) {
		Intent in = new Intent(this, MapsActivity.class);
		in.putExtra("Location", loc);
		in.putExtra("List", partyList);
		startActivity(in);
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getParties")) {

			try {
				for (int i = 0; i < ja.length(); i++) {
					Party temp = new Party();
					temp.setPartyWithJSON(ja.getJSONObject(i));
					partyList.add(temp);
				}
				sortByName(partyList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	public void onMenuClicked(MenuItem item) {
		if (item.getItemId() == R.id.menu_sort_by_age) {
			sortByAvgAge(partyList);
		} else if (item.getItemId() == R.id.menu_sort_by_name) {
			sortByName(partyList);
		} else if (item.getItemId() == R.id.menu_sort_by_distance) {
			sortByDistanceToLocation(partyList, loc);
		} else {
		}
			
	}

	public void sortByName(ArrayList<Party> array) {
		addParties(array, PartySortingComparator.NameComparator);
	}

	public void sortByAvgAge(ArrayList<Party> array) {
		addParties(array, PartySortingComparator.AgeNameComparator);
	}

	public void sortByDistanceToLocation(ArrayList<Party> array, Location loc) {
		addParties(array, new PartySortingComparator(loc).DistanceComparator);
	}

	private void addParties(final ArrayList<Party> array, Comparator<Party> c) {
		Collections.sort(array, c);
		if (adap == null) {
			adap = new RowAdapter(this, array, loc);
			ListView lv = (ListView) findViewById(R.id.list_search_result);
			lv.setAdapter(adap);
			lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				Intent intent = new Intent(SearchResultActivity.this, ViewPartyActivity.class);
				intent.putExtra("party", array.get(position));
				SearchResultActivity.this.startActivity(intent);
			}
		});
		
		lv.setTextFilterEnabled(true);
		} else {
			adap.notifyDataSetChanged();
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
