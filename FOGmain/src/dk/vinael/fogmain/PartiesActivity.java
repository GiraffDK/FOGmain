package dk.vinael.fogmain;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import design.ExpandableGroup;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import fragments.FutureFragment;
import fragments.PastFragment;


public class PartiesActivity extends FragmentActivity implements FogActivityInterface {
	// new stuff
	/* Tab identifiers */
	private String TAB_A = "Current/Future Parties";
	private String TAB_B = "Past Parties";

	private TabHost mTabHost;

	private FutureFragment fragment1;
	private PastFragment fragment2;
	private ArrayList<Party> oldParties = new ArrayList<Party>();
	private ArrayAdapter<Party> arrayAdapter;
	
	private ExpandableGroup own = new ExpandableGroup();
	private ArrayList<Party> ownerList = new ArrayList<Party>();
	private ExpandableGroup att = new ExpandableGroup();
	private ArrayList<Party> attList = new ArrayList<Party>();			
	private ExpandableGroup req = new ExpandableGroup();
	private ArrayList<Party> reqList = new ArrayList<Party>();
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parties);
		initializaAll();
	}
	public void initializaAll() {
		fragment1 = new FutureFragment();
		fragment1.setActivity(this);
		fragment2 = new PastFragment();
		fragment2.setA(this);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(listener);
		mTabHost.setup();

		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.ic_a_stiff_drink);
		bar.setTitle("Your/Requested Parties");
		bar.setHomeButtonEnabled(true);
		initializeTab();
	}

	/*
	 * Initialize the tabs and set views and identifiers for the tabs
	 */
	public void partiesAttended(View v) {

		int month = ((NumberPicker) findViewById(R.id.np_month)).getValue();
		int year = ((NumberPicker) findViewById(R.id.np_year)).getValue();
		new Party().getPartiesUserAttended(this, "past_parties", month, year);
		((TextView)findViewById(R.id.fragment_tv_label_month_year)).setText("You've selected : " + new DateFormatSymbols().getMonths()[month-1] + " - " + year);	
		new Party().getPartiesUserAttended(this, "past_parties", ((NumberPicker)findViewById(R.id.np_month)).getValue(), ((NumberPicker)findViewById(R.id.np_year)).getValue());

	}

	public void getData() {
		User user = ((FOGmain) getApplicationContext()).user;

		// Calls
		fragment1.clearList();
		ownerList.clear();
		attList.clear();
		reqList.clear();
		user.selectAllPartiesByOwnerUserId(this, "owner_parties");
		user.selectAllPartiesImAttending(this, "attending_parties");
		user.selectAllPartiesIveRequested(this, "requested_parties");		
	}

	public void initializeTab() {

		TabHost.TabSpec spec = mTabHost.newTabSpec(TAB_A);
		mTabHost.setCurrentTab(2);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView(TAB_A, R.drawable.ic_a_stiff_drink));
		mTabHost.addTab(spec);

		spec = mTabHost.newTabSpec(TAB_B);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView(TAB_B, R.drawable.ic_add));
		mTabHost.addTab(spec);

	}

	/*
	 * TabChangeListener for changing the tab when one of the tabs is pressed
	 */
	TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
		public void onTabChanged(String tabId) {
			/* Set current tab.. */
			if (tabId.equals(TAB_A)) {
				getData();
				pushFragments(tabId, fragment1);
			} else if (tabId.equals(TAB_B)) {
				fragment1.clearList();
				pushFragments(tabId, fragment2);
			}
		}
	};

	/*
	 * adds the fragment to the FrameLayout
	 */
	public void pushFragments(String tag, Fragment fragment) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		ft.replace(android.R.id.tabcontent, fragment);
		ft.commit();
	}

	/*
	 * returns the tab view i.e. the tab icon and text
	 */
	private View createTabView(final String text, final int id) {
		/*
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));
		((TextView) view.findViewById(R.id.tab_text)).setText(text);
		return view;
		*/
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_to_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((FOGmain) getApplicationContext()).onOptionsItemSelected(item, this);
		return true;
	}

	@Override
	public void returningAddress(String Address, String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returningLocation(Location location, String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("owner_parties")) {
			try {
				for (int i = 0; i < ja.length(); i++) {
					Party temp = new Party();
					temp.setPartyWithJSON(ja.getJSONObject(i));
					ownerList.add(temp);
				}
			} catch (Exception e) {

			}
			own.setParties(ownerList);
			own.setNameOfGroup("Your parties! (" + ownerList.size() + ")");
			fragment1.addToList(own);
		} else if (identifier.equals("attending_parties")) {


			JSONObject checker = null;
			try {
				checker = ja.getJSONObject(0);
				if (!checker.has("results")) {
					for (int i = 0; i < ja.length(); i++) {
						Party temp = new Party();
						temp.setPartyWithJSON(ja.getJSONObject(i));
						attList.add(temp);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			att.setParties(attList);
			att.setNameOfGroup("Attending Parties! (" + attList.size() + ")");
			fragment1.addToList(att);
		} else if (identifier.equals("requested_parties")) {

			JSONObject checker = null;
			try {
				checker = ja.getJSONObject(0);
				if (!checker.has("results")) {
					for (int i = 0; i < ja.length(); i++) {
						Party temp = new Party();
						temp.setPartyWithJSON(ja.getJSONObject(i));
						reqList.add(temp);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			req.setNameOfGroup("Rquested Parties! (" + reqList.size() + ")");
			req.setParties(reqList);
			fragment1.addToList(req);
		} else if (identifier.equals("past_parties")) {
			oldParties.clear();
			if (arrayAdapter != null) arrayAdapter.clear();
			JSONObject checker = null;
			try {
				checker = ja.getJSONObject(0);
				if (!checker.has("results")) {

					for (int i = 0; i < ja.length(); i++) {
						Party temp = new Party();
						temp.setPartyWithJSON(ja.getJSONObject(i));
						oldParties.add(temp);
					}
					ListView lv = (ListView) findViewById(R.id.fragment_lv_pastparties);
					lv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> l, View v, int position, long id) {
							Intent intent = new Intent(PartiesActivity.this, ViewPartyActivity.class);
							intent.putExtra("party", oldParties.get(position));
							PartiesActivity.this.startActivity(intent);
						}
					});
					arrayAdapter = new ArrayAdapter<Party>(this, android.R.layout.simple_list_item_1, oldParties);

					lv.setAdapter(arrayAdapter);
					lv.setTextFilterEnabled(true);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
	public void seeInMap(View v) {
		if (oldParties.size() != 0) { 
			Intent in = new Intent(this, MapsActivity.class);
			in.putExtra("Location", "");
			in.putExtra("List", oldParties);
			startActivity(in);
		}
	}
}
