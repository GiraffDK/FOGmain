package fragments;

import java.util.Calendar;

import dk.vinael.fogmain.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
	
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
	
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		Activity a = getActivity();
		
		// Format hour
		String h = ("0"+(String) Integer.toString(hourOfDay));
		h = h.substring(h.length()-2, h.length());
		
		// Format minute
		String m = ("0"+(String) Integer.toString(minute));
		m = m.substring(m.length()-2, m.length());
		
		// Set button text
		if (this.getTag().equals("party_start_time_picker")){
			((Button) a.findViewById(R.id.btn_set_start_time)).setText(h + ":" + m + ":00");
		}
		else if (this.getTag().equals("party_end_time_picker")){
			((Button) a.findViewById(R.id.btn_set_end_time)).setText(h + ":" + m + ":00");
		}
		
	}
}
