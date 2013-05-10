package fragments;

import java.util.Calendar;

import dk.vinael.fogmain.PartiesActivity;
import dk.vinael.fogmain.R;
import dk.vinael.fogmain.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.NumberPicker;

public class PastFragment extends Fragment {
	private NumberPicker month;
	private NumberPicker year;
	private Activity a;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_past, container, false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		try {
			month = (NumberPicker) a.findViewById(R.id.np_month);
			year = (NumberPicker) a.findViewById(R.id.np_year);
			month.setMaxValue(12);
			month.setMinValue(1);
			year.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
			year.setMinValue(2012);
		} catch (Exception e ){
			
		}

		super.onActivityCreated(savedInstanceState);
	}
	public void setA(Activity a) {
		this.a = a;
	}
	
}
