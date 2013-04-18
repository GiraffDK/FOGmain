package dk.vinael.fogmain;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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

public class SearchResultActivity extends Activity implements OnClickListener {
	
	private ArrayList<String> items = new ArrayList<String>();
	private Bundle bundle;
	private double radius;
	private SQLiteDatabase db2;
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
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				moveToParty(items.get(position));
			}
		});
		ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		lv.setAdapter(myarrayAdapter);
		lv.setTextFilterEnabled(true);	
	}
	public void getPartiesByRadius() {
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
		in.putExtra("list", items);
		startActivity(in);
	}
}
