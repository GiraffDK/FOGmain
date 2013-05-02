package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ShowPartyRequestersActivity extends Activity implements FogActivityInterface {

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

}
