package dk.vinael.fogmain;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ViewPartyActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewparty);
		user = ((FOGmain)getApplicationContext()).user;
		TextView tv_data = (TextView) findViewById(R.id.textViewUserdata);
		if (user!=null){
			tv_data.setText("false");
		}
		else{
			tv_data.setText("true");
		}
		tv_data.setText(user.toString());
		
	}
	
	public void addNewParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		this.startActivity(intent);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getUserData")){
			TextView tv_data = (TextView) findViewById(R.id.textViewUserdata);
			tv_data.setText(user.toString());
		}
	}

}
