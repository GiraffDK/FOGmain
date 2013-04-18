package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class AddEditPartyActivity extends Activity implements FogActivityInterface {

	private User user;
	public ArrayList<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addeditparty);
		
		user = ((FOGmain)getApplicationContext()).user;
		list = new ArrayList<String>();
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		
		list.add("Active");
		list.add("Cancel");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		
	}

	public void createParty(View view){
		int userid = user.getUserId();
		int statusid =  ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition();
		String name = ((EditText) findViewById(R.id.party_name)).getText().toString();
		String description = ((EditText) findViewById(R.id.party_description)).getText().toString();
		
		
		String query = "INSERT INTO party " +
				"(owner_user_id, status_id, name, description) VALUES " +
				"("+userid+", "+statusid+", '"+name+"', '"+description+"');";
		
		new WebserviceCaller(this, user, "partyCreated").execute("insert", query);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("partyCreated")){
			String s = "created";
			try {
				JSONObject jo = ja.getJSONObject(0);
				s = Integer.toString(jo.getInt("affected_rows"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(this.getBaseContext(), s, Toast.LENGTH_LONG).show();
		}
	}

}
