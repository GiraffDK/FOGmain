package dk.vinael.fogmain;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewProfilActivity extends Activity {

	private Button btn_editProfile;
	private TextView et_name;
	private TextView et_lastname;
	private TextView et_description;

	private User user;

	@Override
	protected void onResume() {
		btn_editProfile.setClickable(true);
		try {
			addInfo(user);
		} catch (Exception e) {
			// Do nothing.
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profil);
		btn_editProfile = (Button) findViewById(R.id.btn_edit_profile);
		et_name = (TextView) findViewById(R.id.profil_tv_firstname);
		et_lastname = (TextView) findViewById(R.id.profil_tv_lastname);
		et_description = (TextView) findViewById(R.id.profil_tv_description);

		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.get("user");
		checkUser(user);
		addInfo(user);
	}

	private void addInfo(User u) {
		et_name.setText("Name : " + u.getFirstName());
		et_lastname.setText("Lastname : " + u.getLastName());
		et_description.setText("Description : " + u.getDescription());
	}

	public void checkUser(User u) {
		if (u.getUserId() == ((FOGmain) getApplicationContext()).user.getUserId()) {
			btn_editProfile.setVisibility(View.VISIBLE);
		} else {
			btn_editProfile.setVisibility(View.GONE);
		}
	}

	public void onClick(View v) {
		btn_editProfile.setClickable(false);
		user = ((FOGmain) getApplicationContext()).user;
		Intent intent = new Intent(this, EditProfilActivity.class);
		this.startActivity(intent);
	}
}
