package org.finomnis.mcbackup.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Timestamp implements Comparable<Timestamp>{

	private Calendar cal;
	
	private Timestamp(Calendar cal){
		this.cal = cal;
	}
	
	public String getStamp(){
		return 			cal.get(Calendar.YEAR)
				+ "-" + String.format("%02d", cal.get(Calendar.MONTH) + 1)
				+ "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
				+ "_" + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
				+ "-" + String.format("%02d", cal.get(Calendar.MINUTE))
				+ "-" + String.format("%02d", cal.get(Calendar.SECOND));
	}
	
	public Timestamp add(long seconds){
		Calendar newCal = Calendar.getInstance();
		newCal.setTimeInMillis(cal.getTimeInMillis() + seconds * 1000);
		return new Timestamp(newCal);
	}
	
	public static Timestamp now(){
		return new Timestamp(Calendar.getInstance());
	}
	
	private static List<String> splitString(String str, char delim){
		List<String> res = new ArrayList<String>();
		
		while(true){
			int pos = str.indexOf(delim);
			if(pos < 0) break;
			res.add(str.substring(0, pos));
			str = str.substring(pos + 1);
		}
		
		res.add(str);
		
		return res;
	}
	
	public static Timestamp fromStamp(String stamp){
		// Split to time and day
		List<String> split1 = splitString(stamp, '_');
		
		// Ensure that we have both a time and a day
		if(split1.size() != 2){
			return null;
		}
		
		String dateStr = split1.get(0);
		String timeStr = split1.get(1);
		
		List<String> date = splitString(dateStr, '-');
		List<String> time = splitString(timeStr, '-');
		
		// Ensure that the formats are correct
		if(date.size() != 3 || time.size() != 3){
			return null;
		}
		
		Calendar cal = Calendar.getInstance();

		try{
			cal.set( Integer.parseInt(date.get(0)),
					 Integer.parseInt(date.get(1)) - 1,
					 Integer.parseInt(date.get(2)),
					 Integer.parseInt(time.get(0)),
					 Integer.parseInt(time.get(1)),
					 Integer.parseInt(time.get(2)));
		} catch (Exception e) {
			return null;
		}
		return new Timestamp(cal);
		
	}
	
	@Override
	public String toString(){
		return getStamp();
	}

	@Override
	public int compareTo(Timestamp other) {
		return this.cal.compareTo(other.cal);
	}

	public long getTimeDiff(Timestamp other) {
		return (long) Math.round((this.cal.getTimeInMillis() - other.cal.getTimeInMillis()) / 1000.0);
	}
	
	@Override
	public boolean equals(Object other){
		if(! (other instanceof Timestamp))
			return false;
		Timestamp otherts = (Timestamp) other;
		
		return otherts.getStamp().equals(this.getStamp());		
	}
		
}
