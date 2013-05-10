package dk.vinael.domain;

import java.util.Calendar;

public class DateAndTimeStringHandler {
	
	public enum RETURN_TYPE {
		DAYS, HOURS, MINUTES
	}
	
	public static String getCurrentDateAndTime(){
		return (DateAndTimeStringHandler.getCurrentDate() + " " + DateAndTimeStringHandler.getCurrentTime());
	}
	
	public static Calendar getDateStringAsCalendar(String dateAndTime){
		Calendar c = Calendar.getInstance();
		int year = DateAndTimeStringHandler.getYearFromDateAndTime(dateAndTime);
		int month = DateAndTimeStringHandler.getMonthFromDateAndTime(dateAndTime);
		int day = DateAndTimeStringHandler.getMonthFromDateAndTime(dateAndTime);
		int hourOfDay = DateAndTimeStringHandler.getHourFromDateAndTime(dateAndTime);
		int minute = DateAndTimeStringHandler.getMinuttesFromDateAndTime(dateAndTime);
		c.set(year, month, day, hourOfDay, minute);
		return c;
	}
	
	public static int dateDiffInMinutes(Calendar a, Calendar b, RETURN_TYPE n){
		int days, hours, minutes;
		long difference = (long)(a.getTimeInMillis() - b.getTimeInMillis());
		
		days = (int) (difference / (1000*60*60*24));  
		hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60)); 
		minutes = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
		
		switch (n){
		case DAYS:
			return days;
		case HOURS:
			return hours;
		case MINUTES:
			return minutes;
		default:
			return hours;
		}
		
	}
	
	public static String getCurrentDate(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Format month
		String m = ("0"+(String) Integer.toString(month+1));
		m = m.substring(m.length()-2, m.length());
		
		// Format day
		String d = ("0"+(String) Integer.toString(day));
		d = d.substring(d.length()-2, d.length());
		
		return ""+year+"-"+m+"-"+d;
	}
	
	public static String getCurrentTime(){
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Format hour
		String h = ("0"+(String) Integer.toString(hour));
		h = h.substring(h.length()-2, h.length());
		
		// Format minute
		String m = ("0"+(String) Integer.toString(minute));
		m = m.substring(m.length()-2, m.length());
		
		return ""+h+":"+m+":00";
	}
	public static int getYearFromDateAndTime(String dateAndTime) {
		return Integer.parseInt(dateAndTime.substring(0, dateAndTime.indexOf("-")));
	}
	public static int getMonthFromDateAndTime(String dateAndTime) {
		return Integer.parseInt(dateAndTime.substring(dateAndTime.indexOf("-")+2, dateAndTime.lastIndexOf("-")));
	}
	public static int getDayOfMonthFromDateAndTime(String dateAndTime) {
		return Integer.parseInt(dateAndTime.substring(dateAndTime.lastIndexOf("-")+1, dateAndTime.indexOf(" ")));
	}
	public static int getHourFromDateAndTime(String dateAndTime) {
		return Integer.parseInt(dateAndTime.substring(dateAndTime.indexOf(" ")+1, dateAndTime.indexOf(":")));
	}
	public static int getMinuttesFromDateAndTime(String dateAndTime) {
		return Integer.parseInt(dateAndTime.substring(dateAndTime.indexOf(":")+1, dateAndTime.lastIndexOf(":")));
	}
	
	public static String getTimeFromDateAndTime(String dateAndTime){
		return dateAndTime.substring(11, dateAndTime.length());
	}
	
	public static String getDateFromDateAndTime(String dateAndTime){
		return dateAndTime.substring(0, 10);
	}
	
	public static String setDateAndTime(String date, String time){
		return date + " " + time; 
	}
}
