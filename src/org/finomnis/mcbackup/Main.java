package org.finomnis.mcbackup;

import java.io.File;
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

		//// Uncomment to generate a lot of test backups
		//test_main(args);
		
		// parse command line
		String[] inputArguments = parseCmdLineArgs(args);
		String inputFolderStr = inputArguments[0];
		File inputFolder = new File(inputFolderStr);
		String backupFolderStr = inputArguments[1];
		File backupFolder = new File(backupFolderStr);
		Logger.msg("Input folder: " + inputFolder.getAbsolutePath());
		Logger.msg("Backup folder: " + backupFolder.getAbsolutePath());
		
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
		FolderZipper.zip(inputFolder, new File(backupFolder, "backup." + timeStamp + ".zip"));
		
		// delete unneeded backups
		BackupManager.cleanup(backupFolder);
		
	}
	
	public static void test_main(String[] args) {
		
		// parse command line
		String[] inputArguments = parseCmdLineArgs(args);
		String inputFolderStr = inputArguments[0];
		File inputFolder = new File(inputFolderStr);
		String backupFolderStr = inputArguments[1];
		File backupFolder = new File(backupFolderStr);
		Logger.msg("Input folder: " + inputFolder.getAbsolutePath());
		Logger.msg("Backup folder: " + backupFolder.getAbsolutePath());
		
		// ensure that backup folder exists, otherwise try to create it
		backupFolder.mkdirs();
		if(!backupFolder.exists())
		{
			Logger.error("Unable to create directory '" + backupFolder.getPath() + "'!");
		}
		

		// get Timestamp
		Timestamp timeStamp = Timestamp.now();
		Logger.msg("Current timestamp: " + timeStamp.getStamp());
		
		// Define backup frequency
		long deltaT = 60 * 60; // 1 hour 
		
		// create 4 years of artificial backups
		for(long i = 0; i < 24*365*4; i++){
			FolderZipper.zip(inputFolder, new File(backupFolder, "backup." + timeStamp + ".zip"));
			timeStamp = timeStamp.add(-deltaT);
		}
		
		// Delete all unnecessary backups
		BackupManager.cleanup(backupFolder);
		
		System.exit(0);
		
	}

}
