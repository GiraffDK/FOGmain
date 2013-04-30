package fragments;

import java.util.Calendar;

import dk.vinael.fogmain.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Activity a = getActivity();
		
		// Format month
		String m = ("0"+(String) Integer.toString(month+1));
		m = m.substring(m.length()-2, m.length());
		
		// Format day
		String d = ("0"+(String) Integer.toString(day));
		d = d.substring(d.length()-2, d.length());
		
		// Set button text
		if (this.getTag().equals("party_start_date_picker")){
			((Button) a.findViewById(R.id.btn_set_start_date)).setText(year + "-" + m + "-" + d);
		}
		else if (this.getTag().equals("party_end_date_picker")){
			((Button) a.findViewById(R.id.btn_set_end_date)).setText(year + "-" + m + "-" + d);
		}
		
	}
}
