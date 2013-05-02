package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfilActivity extends Activity implements FogActivityInterface {
	private EditText fname;
	private EditText lname;
	private EditText bday;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editprofil);
		user = ((FOGmain) getApplicationContext()).user;
		fname = (EditText) findViewById(R.id.editprof_et_fname);
		lname = (EditText) findViewById(R.id.editprof_et_lname);
		bday = (EditText) findViewById(R.id.editprof_et_bday);
		street = (EditText) findViewById(R.id.editprof_et_street);
		zip = (EditText) findViewById(R.id.editprof_et_zip);
		city = (EditText) findViewById(R.id.editprof_et_city);
		country = (EditText) findViewById(R.id.editprof_et_country);
		phoneNr = (EditText) findViewById(R.id.editprof_et_phonenumber);
		email = (EditText) findViewById(R.id.editprof_et_email);
		desc = (EditText) findViewById(R.id.editprof_et_desc);
		
		fname.setText(user.getFirstName());
		lname.setText(user.getLastName());
		bday.setText(user.getBirthdate());
		street.setText(user.getAddress());
		zip.setText(user.getZip());
		city.setText(user.getCity());
		country.setText(user.getCountry());
		phoneNr.setText("88888888");
		email.setText(user.getEmail());
		desc.setText(user.getDescription());
		
		oldText = takeText();

	}
	public String takeText() {
		return "" +fname.getText() + lname.getText() + bday.getText() + street.getText() + zip.getText() + 
				country.getText() + phoneNr.getText() + email.getText() + desc.getText();
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
	public void setValues() {
		user.setFirstName(fname.getText().toString());
		user.setLastName(lname.getText().toString());
		user.setBirthdate(bday.getText().toString());
		user.setAddress(street.getText().toString());
		user.setZip(zip.getText().toString());
		user.setCountry(country.getText().toString());
		//user.setPhoneNr(phoneNr.getText().toString());
		user.setEmail(email.getText().toString());
		user.setDescription(desc.getText().toString());
	}
	public void confirmSaveOrCancel() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					setValues();
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
