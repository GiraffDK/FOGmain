package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;
import dk.vinael.classes.FOGmain;
import dk.vinael.classes.User;
import dk.vinael.classes.WebserviceCaller;
import dk.vinael.interfaces.FogActivityInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements FogActivityInterface {

	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ## Set user
		// ## Set user to global variable
		// ## Handle resume, pause, etc.
		
		// ## only testing below 
		user = ((FOGmain)getApplicationContext()).user;
		if (user==null){
			user = new User();
			//user.setToken("test");
			user.setUsername("kevin");
			user.setPassword("test");
		}
		new WebserviceCaller(this, user, "checkSelect").execute("select", "SELECT * FROM user;");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getAllParties(View view){
		// there should be an identifier so there can be different handlers inside of jsonArrayHandler
		new WebserviceCaller(this, user, "selectAllParties").execute("select", "SELECT * FROM party;");
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		TextView tv_hello = (TextView)findViewById(R.id.helloworld);
		//tv_hello.setText(ja.toString());
		
		// Print out username from all users
		if (identifier.equals("checkSelect")){
			String s = "";
			for (int i=0; i<ja.length(); i++){
				try {
					s += ja.getJSONObject(i).getString("username")+"\n";
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			tv_hello.setText(s);
		}
		if (identifier.equals("selectAllParties")){
			String s = "";
			for (int i=0; i<ja.length(); i++){
				try {
					s += ja.getJSONObject(i).getString("name")+"\n"+ja.getJSONObject(i).getString("description")+"\n\n";
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			tv_hello.setText(s);
		}
	}

}
