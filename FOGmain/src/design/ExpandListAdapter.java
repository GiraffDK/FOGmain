package design;

import java.util.ArrayList;

import dk.vinael.domain.Party;
import dk.vinael.fogmain.R;
import dk.vinael.fogmain.ViewPartyActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ExpandListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ExpandableGroup> groups;
	private ExpandableListView listView;
	
	public ExpandListAdapter(Context context, ArrayList<ExpandableGroup> groups, ExpandableListView listView) {
		this.context = context;
		this.groups = groups;
		this.listView = listView;
	}
	
	public void addItem(Party item, ExpandableGroup group) {
		if (!groups.contains(group)) {
			groups.add(group);
		}
		int index = groups.indexOf(group);
		ArrayList<Party> ch = groups.get(index).getParties();
		ch.add(item);
		groups.get(index).setParties(ch);
	}
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		ArrayList<Party> chList = groups.get(groupPosition).getParties();
		return chList.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		Party child = (Party) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.tvChild);
		tv.setText(child.getName().toString());
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] s = v.getTag().toString().split("-");
				startParty(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
			}
		});
		tv.setTag(""+groupPosition+"-"+childPosition);
		// TODO Auto-generated method stub
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		ArrayList<Party> chList = groups.get(groupPosition).getParties();

		return chList.size();

	}

	public ExpandableGroup getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		ExpandableGroup group = (ExpandableGroup) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.tvGroup);
		tv.setText(group.getNameOfGroup());
		// TODO Auto-generated method stub
		return view;
	}
	
	@Override
	public void onGroupExpanded(int groupPosition) {
		for (int i = 0; i < groups.size(); i++) {
			if (groupPosition != i) listView.collapseGroup(i);
		}
		super.onGroupExpanded(groupPosition);
	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}
	public void startParty(int groupPosition, int childPosition) {
		Intent intent = new Intent(context, ViewPartyActivity.class);
		intent.putExtra("party", groups.get(groupPosition).getParties().get(childPosition));
		context.startActivity(intent);
	}

}



