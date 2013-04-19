package dk.vinael.fogmain;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.interfaces.FogActivityInterface;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

public class SearchResultActivity extends Activity implements OnClickListener, FogActivityInterface {
	
	private ArrayList<Party> ps = new ArrayList<Party>();
	private Bundle bundle;
	private double radius;
	private Location loc = new Location("");
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultview);
		
		bundle = getIntent().getExtras();
		radius = (double) bundle.getInt("Radius");
		loc.set((Location) bundle.get("Location"));
		
		getPartiesByRadius();
		
	}
	public void getPartiesByRadius() {
		Double loclat = loc.getLatitude();
		Double loclon = loc.getLongitude();
		
		new WebserviceCaller(this, ((FOGmain)getApplicationContext()).user, "getParties")
			.execute("select", "SELECT * FROM party WHERE lat > '"+(loclat - radius)+"' AND " +
					"lat < '"+(loclat + radius)+"' AND " +
					"lon > '"+(loclon - radius)+"' AND " +
					"lon < '"+(loclon + radius)+"';");
		Toast.makeText(this,"SELECT * FROM party WHERE lat > "+(loclat - radius)+" AND " +
				"lat < "+(loclat + radius)+" AND " +
				"lon > "+(loclon - radius)+" AND " +
				"lon < "+(loclon + radius)+"", Toast.LENGTH_LONG).show();
		
		/*MySQLiteHelper db = new MySQLiteHelper(this);
		db2 = db.getWritableDatabase();
		Cursor cur = db2.rawQuery("SELECT * FROM locations", null);
		
		if (cur.moveToFirst()) {
			do {
				double lan = cur.getDouble(1);
				double lon = cur.getDouble(2);
				Location temp = new Location("Point");
				temp.setLatitude(lan);
				temp.setLongitude(lon);

				if ((temp.distanceTo(loc) / 1000) <= radius) {
					items.add(lan + "-" + lon);
				}
			} while (cur.moveToNext());
		}*/
	}
	public void moveToParty(String picked) {
//		Intent in = new Intent(this, PartyView.class);
//		String[] t = picked.split("-");
//		double lan = Double.parseDouble(t[0]);
//		double lon = Double.parseDouble(t[1]);
//		Location temp = new Location("Point");
//		temp.setLatitude(lan);
//		temp.setLongitude(lon);
//		in.putExtra("party", temp);
//		startActivity(in);
	}
	@Override
	public void onClick(View v) {
		Intent in = new Intent(this, MapsActivity.class);
		in.putExtra("Location", loc);
		in.putExtra("list", ps);
		startActivity(in);
	}
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		
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
						//moveToParty(ps.get(position));
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
