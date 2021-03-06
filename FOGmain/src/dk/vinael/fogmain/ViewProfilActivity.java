package dk.vinael.fogmain;

import java.net.MalformedURLException;
import java.net.URL;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.ProfilePictureHandler;
import asynctasks.ProfilePictureHandler.Execute;

public class ViewProfilActivity extends Activity {

	private ImageView im_profile;
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
		im_profile = (ImageView) findViewById(R.id.profileImages);
		btn_editProfile = (Button) findViewById(R.id.btn_edit_profile);
		et_name = (TextView) findViewById(R.id.profil_tv_firstname);
		et_lastname = (TextView) findViewById(R.id.profil_tv_lastname);
		et_description = (TextView) findViewById(R.id.profil_tv_description);

		ActionBar bar = getActionBar();
		//bar.setIcon(R.drawable.ic_settings);
		//bar.setTitle("View Profile");
		bar.setHomeButtonEnabled(true);
		
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.get("user");
		
		if (user!=null){
			checkUser(user);
		}
		

	}
	@Override
	public void onAttachedToWindow() {
		//checkUser(user);
		addInfo(user);
		super.onAttachedToWindow();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_to_main_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((FOGmain) getApplicationContext()).onOptionsItemSelected(item,this);
		return true;
	}

	private void addInfo(User u) {
		try {
			new ProfilePictureHandler((ImageView) findViewById(R.id.profileImages),Execute.RECIEVE).execute(new URL(u.getProfilPic()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
