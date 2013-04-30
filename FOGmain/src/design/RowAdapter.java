package design;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import dk.vinael.domain.Party;
import dk.vinael.fogmain.R;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RowAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private ArrayList<Party> data;
	private Location center;

	public RowAdapter(Activity a, ArrayList<Party> data, Location center) {
		this.center = center;
		this.data = data;
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		v = inflater.inflate(R.layout.listview_row_expand, null);
		TextView name = (TextView) v.findViewById(R.id.tv_name);
		TextView description = (TextView) v.findViewById(R.id.tv_description);
		TextView distance = (TextView) v.findViewById(R.id.tv_distance);
		TextView age = (TextView) v.findViewById(R.id.tv_age);

		Party p = data.get(position);
		name.setText(p.getName());
		description.setText(p.getDescription());
		age.setText("Age: " +p.getMinAge() + "-" + p.getMaxAge());
		Location temp = new Location("Temp");
		temp.setLatitude(p.getLat());
		temp.setLongitude(p.getLon());
		Float dis = center.distanceTo(temp);
		distance.setText("~ (" + (dis/1000) + " km)");
		
		return v;
	}

}
