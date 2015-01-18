package org.finomnis.mcbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
	
	private static boolean shouldBeDeleted(long age, long diffToNewer){
		return false;
	}
	
	public static void cleanup(File backupFolder) {
		
		List<Timestamp> backupStamps = readBackupStamps(backupFolder);
		if(backupStamps.size() < 3) 
			return;

		List<Timestamp> toKeep = new ArrayList<Timestamp>(backupStamps);
		
		Timestamp newest = toKeep.get(toKeep.size() - 1); 
		for(int i = 1; i < toKeep.size() - 1; i++){
			// TODO this is bullshit. make it better.
			Timestamp newer = toKeep.get(i + 1);
			Timestamp curr = toKeep.get(i);
			
			long diffToNewer = curr.getTimeDiff(newer);
			long age = curr.getTimeDiff(newest);
			
			if(shouldBeDeleted(age, diffToNewer)){
				//TODO delete
				

			}
		}
		
	}

}
