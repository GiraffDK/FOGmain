package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class ViewPartyActivity extends Activity implements FogActivityInterface {

	private ViewPartyActivity fai = this;
	
	private User user;
	private Party party;
	private Bundle bundle;
	
	private View viewbyall;
	private View viewbyrequesterattendee;
	private View viewbyowner;
	private View viewbyattendee;
	
	private int user_status = -1; /* -1 = looker, 0=requester, 1=owner, 2=attending guest */
	
	private TextView tv_partyname_viewparty;
	private TextView tv_description_viewparty;
	private TextView tv_start_date_time_viewparty;
	private TextView tv_end_date_time_viewparty;
	private TextView tv_age_level_viewparty;
	private TextView tv_party_address_viewparty;
	private TextView tv_doorcode_viewparty;
	private Button btn_requestcancelunsub_viewparty;
	private Button btn_editparty_viewparty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewparty);
		
		bundle = getIntent().getExtras();
		
		user = ((FOGmain)getApplicationContext()).user;
		party = (Party) bundle.getSerializable("party");
		
		
		/* Set elements */
		tv_partyname_viewparty = ((TextView) findViewById(R.id.tv_partyname_viewparty));
		tv_description_viewparty = ((TextView) findViewById(R.id.tv_description_viewparty));
		tv_start_date_time_viewparty = ((TextView) findViewById(R.id.tv_start_date_time_viewparty));
		tv_end_date_time_viewparty = ((TextView) findViewById(R.id.tv_end_date_time_viewparty));
		tv_age_level_viewparty = ((TextView) findViewById(R.id.tv_age_level_viewparty));
		tv_party_address_viewparty = ((TextView) findViewById(R.id.tv_party_address_viewparty));
		tv_doorcode_viewparty = ((TextView) findViewById(R.id.tv_doorcode_viewparty));
		btn_requestcancelunsub_viewparty = ((Button) findViewById(R.id.btn_requestcancelunsub_viewparty));
		btn_editparty_viewparty = ((Button) findViewById(R.id.btn_editparty_viewparty));
		
		/* Determing user status - start */
		
		viewbyall = ((View) findViewById(R.id.ll_party_viewbyall_viewparty));
		viewbyrequesterattendee = ((View) findViewById(R.id.ll_party_viewbyrequesterattendees_viewparty));
		viewbyattendee = ((View) findViewById(R.id.ll_party_viewbyattendees_viewparty));
		viewbyowner = ((View) findViewById(R.id.ll_party_viewbyowner_viewparty));
		
		viewbyall.setVisibility(View.GONE);
		viewbyrequesterattendee.setVisibility(View.GONE);
		viewbyattendee.setVisibility(View.GONE);
		viewbyowner.setVisibility(View.GONE);
		
		if (user.getUserId() == party.getOwnerId()){
			user_status=1;
			showLayouts();
		}
		else{
			party.selectUserInParty(this, "showPartyInfo", user);
		}
		
		/* Determing uset status - end */
	}
	
	public void showLayouts(){
		Toast.makeText(this, "user status: "+user_status, Toast.LENGTH_LONG).show();
		if (user_status>=-1){ /* all */
			// show only layout for all
			viewbyall.setVisibility(View.VISIBLE);
		}
		if (user_status==-1 || user_status==0 || user_status==2){ /* requester || attending guest */
			// handle request button
			viewbyrequesterattendee.setVisibility(View.VISIBLE);
			if (user_status==-1){
				btn_requestcancelunsub_viewparty.setText("Request");
				btn_requestcancelunsub_viewparty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						party.userRequestParty(fai, "userRequestParty", user);
					}
				});
			}
			else if (user_status==0 || user_status==2){
				if (user_status==0){btn_requestcancelunsub_viewparty.setText("Cancel request");}
				else if(user_status==2){btn_requestcancelunsub_viewparty.setText("Unsubscribe party");}
				btn_requestcancelunsub_viewparty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						party.userCancelRequestParty(fai, "userCancelRequestParty", user);
					}
				});
			}
			else{
				btn_requestcancelunsub_viewparty.setVisibility(View.GONE);
			}
		}
		if (user_status>=1){ /* owner || attending guest */
			viewbyattendee.setVisibility(View.VISIBLE);
		}
		if (user_status==1){ /* owner */
			viewbyowner.setVisibility(View.VISIBLE);
			btn_editparty_viewparty.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(fai, AddEditPartyActivity.class);
					intent.putExtra("party", party);
					//this.finish();
					fai.startActivity(intent);
				}
			});
		}
		showPartyData();
	}
	
	public void showPartyData(){
		tv_partyname_viewparty.setText(party.getName());
		tv_description_viewparty.setText(party.getDescription());
		tv_start_date_time_viewparty.setText(party.getStartDateAndTime());
		tv_end_date_time_viewparty.setText(party.getEndDateAndTime());
		tv_age_level_viewparty.setText(party.getMinAge() + " - " + party.getMaxAge());
		tv_party_address_viewparty.setText(party.getAddress() + "\n" + party.getZip() + ", " + party.getCity() + "\n" + party.getCountry());
		tv_doorcode_viewparty.setText(party.getDoorCode());
	}
	
	public void seeRequesters(View v){
		Intent intent = new Intent(this, ShowPartyRequestersActivity.class);
		intent.putExtra("party", party);
		//this.finish();
		this.startActivity(intent);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("showPartyInfo")){
			Toast.makeText(this, "JSONArray: "+ja.toString(), Toast.LENGTH_LONG).show();
			if (ja != null){
				JSONObject jo = null;
				try {
					jo = ja.getJSONObject(0);
					if (jo.has("attending_status_id")){
						try {
							if (jo.getString("attending_status_id").equals("1")){ // Requested
								user_status = 0;
							}
							else if(jo.getString("attending_status_id").equals("2")){ // Attending
								user_status = 2;
							}
							else if (jo.getString("attending_status_id").equals("3")){ // Rejected
								user_status = -1;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			showLayouts();

		}
		else if (identifier.equals("userRequestParty") || identifier.equals("userCancelRequestParty")){
			Intent intent = new Intent(this, ViewPartyActivity.class);
			intent.putExtra("party", party);
			this.finish();
			this.startActivity(intent);
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
