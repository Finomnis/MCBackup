package org.finomnis.mcbackup;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.finomnis.mcbackup.util.Logger;
import org.finomnis.mcbackup.util.Timestamp;

public class Main {

	
	private static void printHelp(){
		
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("\tmcbackup <inputfolder> <backupfolder>");
		System.out.println("");

	}
	
	private static String[] parseCmdLineArgs(String[] args){
		
		List<String> res = new ArrayList<String>();
		
		for(String arg : args){
			if(arg.startsWith("-")){
				switch(arg){
					case "-v":
						Logger.setVerbose();
						break;
					case "-h":
						printHelp();
						System.exit(2);
					default:
						Logger.warn("unknown flag '" + arg + "'");
						break;
				}
			} else {
				res.add(arg);
			}
		}
		
		if(res.size() != 2){
			printHelp();
			System.exit(2);
		}
		
		return res.toArray(new String[0]);
		
	}
	
	public static void main(String[] args) {

		// parse command line
		String[] inputArguments = parseCmdLineArgs(args);
		String inputFolderStr = inputArguments[0];
		String backupFolderStr = inputArguments[1];
		File backupFolder = new File(backupFolderStr);
		Logger.msg("Input folder: " + inputFolderStr);
		Logger.msg("Backup folder: " + backupFolder.toString());
		
		// ensure that backup folder exists, otherwise try to create it
		backupFolder.mkdirs();
		if(!backupFolder.exists())
		{
			Logger.error("Unable to create directory '" + backupFolder.getPath() + "'!");
		}
		
		// get Timestamp
		Timestamp timeStamp = Timestamp.now();
		Logger.msg("Current timestamp: " + timeStamp.getStamp());
		
		// create current backup
		FolderZipper.zip(inputFolderStr, backupFolder.getAbsolutePath() + File.separator + "backup." + timeStamp + ".zip");
		
		// delete unneeded backups
		BackupManager.cleanup(backupFolder);
		
	}

}
