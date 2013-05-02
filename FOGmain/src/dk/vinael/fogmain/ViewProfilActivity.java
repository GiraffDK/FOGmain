package dk.vinael.fogmain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewProfilActivity extends Activity {
	
	private Button btn_editProfile;
	
	@Override
	protected void onResume() {
		btn_editProfile.setClickable(true);
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profil);
		btn_editProfile = (Button) findViewById(R.id.btn_edit_profile);
		
	}
	public void onClick(View v) {
		btn_editProfile.setClickable(false);
		Intent intent = new Intent(this, EditProfilActivity.class);
		this.startActivity(intent);
	}
}
