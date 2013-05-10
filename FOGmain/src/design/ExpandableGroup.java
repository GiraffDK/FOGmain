package design;

import java.util.ArrayList;

import dk.vinael.domain.Party;

public class ExpandableGroup {

	private String nameOfGroup;
	private ArrayList<Party> parties = new ArrayList<Party>();
	
	public ArrayList<Party> getParties() {
		return parties;
	}
	public void setParties(ArrayList<Party> parties) {
		this.parties = parties;
	}
	public String getNameOfGroup() {
		return nameOfGroup;
	}
	public void setNameOfGroup(String nameOfGroup) {
		this.nameOfGroup = nameOfGroup;
	}

}
