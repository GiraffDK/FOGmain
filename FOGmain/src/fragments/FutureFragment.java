package fragments;

import java.util.ArrayList;

import design.ExpandListAdapter;
import design.ExpandableGroup;
import dk.vinael.fogmain.R;
import dk.vinael.fogmain.R.id;
import dk.vinael.fogmain.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class FutureFragment extends Fragment {
	private Activity a;
	private ExpandListAdapter ExpAdapter;
	private ExpandableListView ExpandList;
	private ArrayList<ExpandableGroup> own_att_req;
	
	public FutureFragment() {
		own_att_req = new ArrayList<ExpandableGroup>();
	}
	public void setActivity(Activity a) {
		this.a = a;
	}
	@Override
	public void onStart() {
		try {
			ExpandList = (ExpandableListView) a.findViewById(R.id.expandableListView1); 
			ExpAdapter = new ExpandListAdapter(a, own_att_req, ExpandList);
			ExpandList.setAdapter(ExpAdapter);
		} catch (Exception e) {
			
		}
		super.onStart();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		onStart();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_future,
		        container, false);
		return view;
	}
	public void clearList() {
		own_att_req.clear();
	}
	public void addToList(ExpandableGroup group) {
		if (!(own_att_req.contains(group))) {
			own_att_req.add(group);
			ExpAdapter.notifyDataSetChanged();
		}
	}

}
