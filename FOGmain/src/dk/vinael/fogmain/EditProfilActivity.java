package dk.vinael.fogmain;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import fragments.DatePickerFragment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfilActivity extends FragmentActivity implements FogActivityInterface {
	private EditText fname;
	private EditText lname;
	private Button bday;
	private EditText street;
	private EditText zip;
	private EditText city;
	private EditText country;
	private EditText phoneNr;
	private EditText email;
	private EditText desc;
	private String oldText = "";
	private String newText = "";
	private User user;
	private ImageView im_profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editprofil);

		user = ((FOGmain) getApplicationContext()).user;
		im_profile = (ImageView) findViewById(R.id.editprof_iv_profil);
		
		fname = (EditText) findViewById(R.id.editprof_et_fname);
		lname = (EditText) findViewById(R.id.editprof_et_lname);
		bday = (Button) findViewById(R.id.editprof_btn_birth);
		street = (EditText) findViewById(R.id.editprof_et_street);
		zip = (EditText) findViewById(R.id.editprof_et_zip);
		city = (EditText) findViewById(R.id.editprof_et_city);
		country = (EditText) findViewById(R.id.editprof_et_country);
		phoneNr = (EditText) findViewById(R.id.editprof_et_phonenumber);
		email = (EditText) findViewById(R.id.editprof_et_email);
		desc = (EditText) findViewById(R.id.editprof_et_desc);

		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.ic_settings);
		bar.setTitle("Edit Profile");
		bar.setHomeButtonEnabled(true);
		setValues(user);
		oldText = takeText();

	}
	public void selectDate(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		if (v.getId() == R.id.editprof_btn_birth){
			newFragment.show(getSupportFragmentManager(), "birthday");
		}
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

	public String takeText() {
		return "" + fname.getText() + lname.getText()  + street.getText() + zip.getText() + country.getText() + phoneNr.getText() + email.getText() + desc.getText();
	}
	public void checkData(EditText et, String str) {
		if (!(str.equals("null"))) {
			et.setText(str);
		} 
	}
	public void changePicture(View v) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case 100:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);

					Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
					ImageView i = (ImageView) findViewById(R.id.editprof_iv_profil);
					i.setImageBitmap(yourSelectedImage);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void onBackPressed() {
		newText = takeText();
		if (oldText.toString().equals(newText.toString())) {
			super.onBackPressed();
		} else {
			confirmSaveOrCancel();
		}
	}

	public void onSaveClick(View v) {
		this.onBackPressed();
	}

	public void setValues(User user) {
		checkData(fname, user.getFirstName());
		checkData(lname,user.getLastName());
		if (!(user.getBirthdate().equals("null"))) bday.setText(user.getBirthdate());
		checkData(street, user.getAddress());
		checkData(zip,user.getZip());
		checkData(city, user.getCity());
		checkData(country,user.getCountry());
		//checkData(phoneNr.setText("88888888");
		checkData(email,user.getEmail());
		checkData(desc,user.getDescription());
	}

	public void confirmSaveOrCancel() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					setValues(user);
					SqlWrapper.updateUser(EditProfilActivity.this, "updateUser", user);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					finish();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Want to save before leaving?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("updateUser")) {
			Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			Toast.makeText(this, "Couldn't save", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void returningAddress(String Address, String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returningLocation(Location location, String identifier) {
		// TODO Auto-generated method stub

	}
}
