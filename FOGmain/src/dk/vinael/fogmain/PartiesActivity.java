package dk.vinael.fogmain;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TextView;
import design.ExpandableGroup;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
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
	private ArrayList<Party> oldParties;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parties);
		oldParties = new ArrayList<Party>();
		
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
		new Party().getPartiesUserAttended(this, "past_parties", ((NumberPicker)findViewById(R.id.np_month)).getValue(), ((NumberPicker)findViewById(R.id.np_year)).getValue());
	}
	public void getData() {
		// Calls
		SqlWrapper.selectAllPartiesByOwnerUserId(this, "owner_parties", ((FOGmain) getApplicationContext()).user.getUserId());
		SqlWrapper.selectAllPartiesImAttending(this, "attending_parties", ((FOGmain) getApplicationContext()).user.getUserId());
		SqlWrapper.selectAllPartiesIveRequested(this, "requested_parties", ((FOGmain) getApplicationContext()).user.getUserId());
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
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));
		((TextView) view.findViewById(R.id.tab_text)).setText(text);
		return view;
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
			ExpandableGroup own = new ExpandableGroup();
			
			ArrayList<Party> ownerList = new ArrayList<Party>();
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
			ExpandableGroup att = new ExpandableGroup();
			ArrayList<Party> attList = new ArrayList<Party>();
			
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
			ExpandableGroup req = new ExpandableGroup();
			ArrayList<Party> reqList = new ArrayList<Party>();
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
			try {
				for (int i = 0; i < ja.length(); i++) {
					Party temp = new Party();
					temp.setPartyWithJSON(ja.getJSONObject(i));
					oldParties.add(temp);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	public void partiesAttended() {
		
	}
}
