package dk.vinael.fogmain;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ViewPartyActivity extends Activity implements FogActivityInterface {

	private User user;
	private Party party;
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewparty);
		
		bundle = getIntent().getExtras();
		
		user = ((FOGmain)getApplicationContext()).user;
		party = (Party) bundle.getSerializable("party");
		
		TextView tv_data = (TextView) findViewById(R.id.textViewUserdata);
		if (party!=null){
			tv_data.setText(party.getId() + ", " + party.getName() + "\n" + party.getDescription());
		}
		
		//Toast.makeText(this.getBaseContext(), user.getUserId()+" == "+party.getOwnerId(), Toast.LENGTH_LONG).show();
		if (!(user.getUserId()==party.getOwnerId())){
			((Button) findViewById(R.id.btn_edit_party)).setEnabled(false);
			
		}
	}
	
	public void addNewParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void editParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		intent.putExtra("party", party);
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
