package dk.vinael.domain;

import java.util.Comparator;

import android.location.Location;

public class PartySortingComparator {
	private Location loc;
	public PartySortingComparator(Location loc) {
		this.loc = loc;
	}
	
	public static Comparator<Party> AgeNameComparator = new Comparator<Party>() {

		public int compare(Party p1, Party p2) {
			Double p1gem = (double) ((p1.getMinAge() + p1.getMaxAge())/2);
			Double p2gem = (double) ((p2.getMinAge() + p2.getMaxAge())/2);
			return p1gem.compareTo(p2gem);		}
	};
	public static Comparator<Party> NameComparator = new Comparator<Party>() {

		public int compare(Party p1, Party p2) {

			return p1.getName().compareTo(p2.getName());
		}
	};
	public Comparator<Party> DistanceComparator = new Comparator<Party>() {

		public int compare(Party p1, Party p2) {
			Location p1loc = new Location("");
			p1loc.setLatitude(p1.getLat());
			p1loc.setLongitude(p1.getLon());
			Location p2loc = new Location("");
			p2loc.setLatitude(p2.getLat());
			p2loc.setLongitude(p2.getLon());
			Float distance1 = loc.distanceTo(p1loc);
			Float distance2 = loc.distanceTo(p2loc);
			return distance1.compareTo(distance2);			
		}
	};

}
