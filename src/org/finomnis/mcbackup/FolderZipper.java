package org.finomnis.mcbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.finomnis.mcbackup.util.Logger;

// This code is a modified version of http://www.mkyong.com/java/how-to-compress-files-in-zip-format/

public class FolderZipper {

	/**
	 * Zips an entire folder to a single file
	 * 
	 * @param inputFolder
	 *            The input folder
	 * @param outputFile
	 *            The output zip file
	 */
	public static void zip(String inputFolder, String outputFile) {

		// Generate a list of all the files that we want to zip
		List<String> fileList = generateFileList(inputFolder);

		// Get the parent dir
		String parentDir = new File(inputFolder).getAbsoluteFile().getParent();

		// a buffer to speed up file io
		byte[] buffer = new byte[1024];

		try {

			// Open the zip file
			FileOutputStream binaryFile = null;
			try {
				binaryFile = new FileOutputStream(outputFile);
			} catch (FileNotFoundException e) {
				Logger.error("Unable to write to '" + outputFile + "'");
			}
			ZipOutputStream zipFile = new ZipOutputStream(binaryFile);

			// zip every file
			for (String file : fileList) {

				Logger.msg("Adding: " + parentDir + File.separator
						+ file);

				// Create a zip entry for the current file
				ZipEntry zipEntry = new ZipEntry(file);

				// Add the entry to the output file
				zipFile.putNextEntry(zipEntry);

				// Open the input file
				FileInputStream fileStream = new FileInputStream(parentDir
						+ File.separator + file);

				while (true) {

					// read input file to buffer
					int len = fileStream.read(buffer);

					// if we reached the end of the file, stop reading
					if (len <= 0)
						break;

					// write the read data to the zip file
					zipFile.write(buffer, 0, len);

				}

				fileStream.close();

			}

			// Close and save output zip file
			zipFile.closeEntry();
			zipFile.close();

		} catch (IOException ex) {
			Logger.error(ex);
		}
		
		Logger.msg("Done.");

	}

	/**
	 * Generate a list of all the files that are in a given directory or
	 * subdirectory
	 * 
	 * @param basedir
	 *            The base directory.
	 * @return A list of all files inside the base directory or one of its
	 *         subdirectories. This list contains the full path information of
	 *         every file, starting with the name of the base directory.
	 */
	private static List<String> generateFileList(String basedir) {

		// get full basedir path
		File baseDir = new File(basedir).getAbsoluteFile();

		// ensure that the basedir exists
		if (!(baseDir.exists() && baseDir.isDirectory())) {
			Logger.error("Input directory does not exist!");
		}

		// retrieve the parent directory of the base dir
		File baseParentDir = baseDir.getParentFile().getAbsoluteFile();
		String baseParentPath = baseParentDir.toString();

		// This list will hold the result. It gets filled recursively
		List<String> fileList = new ArrayList<String>();

		// write all the files to fileList
		generateFileListRec(baseDir, baseParentPath, fileList);

		// return the result
		return fileList;

	}

	private static void generateFileListRec(File node, String baseDir,
			List<String> fileList) {

		// if current node is a file, add it
		if (node.isFile()) {
			// Get the filename of the current file
			String fileName = node.getAbsolutePath().toString();

			// Make sure we are still in the current directory.
			if (!fileName.startsWith(baseDir)) {
				throw new RuntimeException(
						"Current file is not inside the parent directory. WTF??");
			}

			// Remove the base string
			String relFileName = fileName.substring(baseDir.length() + 1);

			// Add to file list
			fileList.add(relFileName);

		}

		// if it's a directory, recursively traverse it
		if (node.isDirectory()) {
			// Get content of directory
			String[] dirContents = node.list();
			// Cycle through every file and add it to the list
			for (String filename : dirContents) {
				generateFileListRec(new File(node, filename), baseDir, fileList);
			}
		}

	}

}