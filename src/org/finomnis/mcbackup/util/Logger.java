package org.finomnis.mcbackup.util;

public class Logger {

	private static boolean verbose = false;
	
	public static void setVerbose(){
		verbose = true;
	}
	
	public static void error(String message){
		System.err.println("ERROR: " + message);
		System.exit(1);		
	}

	public static void error(Exception ex) {
		System.err.println("ERROR: " + ex);
		System.exit(1);
	}

	public static void msg(String string) {
		if(verbose)
			System.out.println(string);
	}

	public static void warn(String string) {
		System.out.println("WARNING: " + string);
	}
	
}
