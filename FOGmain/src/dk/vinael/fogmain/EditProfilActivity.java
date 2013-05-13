package dk.vinael.fogmain;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

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
import asynctasks.ProfilePictureHandler;
import asynctasks.ProfilePictureHandler.Execute;

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

	public void makeToast(EditText e, String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public boolean checkLength(EditText e, String msg) {
		if (e.getText().length() < 1) {
			makeToast(e, msg);
			return true;
		}
		return false;
	}

	public boolean validateInput() {
		boolean valid = true;
		String[] cal_data = bday.getText().toString().split("-");
		Calendar c_now = Calendar.getInstance();
		Calendar c_select = Calendar.getInstance();
		c_now.set(c_now.get(Calendar.YEAR) - 18, c_now.get(Calendar.MONTH), c_now.get(Calendar.DAY_OF_MONTH));
		c_select.set(Integer.parseInt(cal_data[0]), Integer.parseInt(cal_data[1]), Integer.parseInt(cal_data[2]));

		if (fname.getText().toString().matches(".*\\d.*")) {
			makeToast(fname, fname.getHint() + " error: Contains a number");
			valid = false;
		} else if (checkLength(fname, "To short name")) {
			valid = false;
		} else if (lname.getText().toString().matches(".*\\d.*")) {
			makeToast(lname, lname.getHint() + " error: Contains a number");
			valid = false;
		} else if (checkLength(lname, "To short name")) {
			valid = false;
		} else if (checkLength(street, "Street name is to short")) {
			valid = false;
		} else if (checkLength(zip, "To short zip")) {
			valid = false;
		} else if (city.getText().toString().matches(".*\\d.*")) {
			makeToast(city, city.getHint() + " error: Contains a number");
			valid = false;
		} else if (checkLength(city, "City name to short")) {
			valid = false;
		} else if (country.getText().toString().matches(".*\\d.*")) {
			makeToast(country, country.getHint() + " error: Contains a number");
			valid = false;
		} else if (checkLength(country, "Country name to short")) {
			valid = false;
		} else if (!(email.getText().toString().contains("@") && email.getText().toString().contains("."))) {
			makeToast(email, email.getHint() + " error: Your email contains errors!");
			valid = false;
		} else if (c_select.after(c_now)) {
			Toast.makeText(this, "Your birthday isn't valid", Toast.LENGTH_LONG).show();
			valid = false;
		}
		if (valid) {
			try {

				Integer.parseInt(zip.getText().toString());

			} catch (Exception e) {
				makeToast(zip, zip.getHint() + "Syntax is incorrect");
				valid = false;

			}
		}
		if (valid) {
			try {
				Integer.parseInt(phoneNr.getText().toString());
			} catch (Exception e) {
				makeToast(phoneNr, phoneNr.getHint() + "Syntax is incorrect");
				valid = false;

			}
		}
		makeToast(fname, ""+valid);
		return valid;
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
					
					new ProfilePictureHandler(yourSelectedImage, Execute.SEND, this).execute();

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
		try {
			new ProfilePictureHandler((ImageView) findViewById(R.id.editprof_iv_profil), Execute.RECIEVE).execute(new URL(user.getProfilPic()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
