package org.finomnis.mcbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finomnis.mcbackup.util.Logger;
import org.finomnis.mcbackup.util.Timestamp;

public class BackupManager {

	private static List<Timestamp> readBackupStamps(File backupFolder){
		List<Timestamp> res = new ArrayList<Timestamp>();
		
		if(!backupFolder.isDirectory()){
			throw new RuntimeException("backupFolder '" + backupFolder.getPath() + "' is not a directory.");
		}
		
		for(String file : backupFolder.list()){
			
			if(! file.startsWith("backup.") || ! file.endsWith(".zip"))
				continue;
			
			String stampStr = file.substring(7, file.length() - 4);
			
			Timestamp stamp = Timestamp.fromStamp(stampStr);
			
			if(stamp == null)
				continue;
			
			res.add(stamp);
			
		}
		
		Collections.sort(res);
		
		return res;
	}
	
	private static long getBackupInterval(long age){
		
		// all time units are in seconds
		
		final long ONE_MINUTE = 60;
		final long ONE_HOUR = 60 * ONE_MINUTE;
		final long ONE_DAY = 24 * ONE_HOUR;
		final long ONE_WEEK = 7 * ONE_DAY;
		final long ONE_MONTH = 30 * ONE_DAY;
		final long ONE_YEAR = 12 * ONE_MONTH;
		
		if(age < ONE_DAY)
			return 0;	// keep everything within the last day
		else if (age < ONE_WEEK)
			return 55*ONE_MINUTE; // within the last week, keep one per hour
		else if (age < 2 * ONE_WEEK)
			return 55*ONE_MINUTE + ONE_HOUR; // within the last two weeks, keep one per two hours;
		else if (age < 2 * ONE_MONTH)
			return ONE_DAY - 30*ONE_MINUTE; // within the last two months, keep one per day
		else if (age < ONE_YEAR)
			return ONE_WEEK - 4*ONE_HOUR;	// within the last year, keep one per week
		else
			return ONE_MONTH - ONE_DAY; // otherwise, keep one per month
		
	}
	
	private static boolean shouldBeDeleted(long age, long diffToOlder){
		//Logger.msg("Age: " + age + "\tOlder: " + (age + diffToOlder) + "\tDiff: " + diffToOlder + "\tIntervall: " + getBackupInterval(age));
		
		if(diffToOlder < getBackupInterval(age))
			return true;
		
		return false;
	}
	
	public static void cleanup(File backupFolder) {
		
		Logger.msg("Searching for old and obsolete log files ...");
		
		List<Timestamp> backupStamps = readBackupStamps(backupFolder);
		if(backupStamps.size() < 3) 
			return;

		List<Timestamp> toKeep = new ArrayList<Timestamp>(backupStamps);
		
		Timestamp newest = toKeep.get(toKeep.size() - 1); 
		for(int i = 1; i < toKeep.size() - 1; i++){
			// TODO this is bullshit. make it better.
			Timestamp older = toKeep.get(i - 1);
			Timestamp curr = toKeep.get(i);
			
			long diffToOlder = curr.getTimeDiff(older);
			long age = newest.getTimeDiff(curr);
			
			if(shouldBeDeleted(age, diffToOlder)){
				toKeep.remove(i);
				i--;
			}
		}
		
		for(Timestamp s : backupStamps){
			if(!toKeep.contains(s)){
				File toDelete = new File(backupFolder, "backup." + s.getStamp() + ".zip");
				Logger.msg("Deleting '" + toDelete.getAbsolutePath() + "' ...");
				if(!toDelete.delete()){
					Logger.warn("Unable to delete '" + toDelete.getAbsolutePath() + "'!");
				}
			}
		}
		
	}

}
