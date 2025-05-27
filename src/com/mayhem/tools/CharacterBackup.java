package com.mayhem.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles backups of player details/containers/farming/houses data.
 * @author ReverendDread
 * Jul 12, 2018
 */
public class CharacterBackup extends Thread {

	/** 
	 * The logger for printing information.
	 */
	public static Logger logger = Logger.getLogger(CharacterBackup.class.getSimpleName());
	
	/**
	 * Locations of backups to be wrote to.
	 */
	private static final String BACKUP_PATH = "./data/characters/backups/";
	
	/**
	 * Paths for various player details.
	 */
	private static final String CONTAINERS_PATH = "./data/characters/containers/";
	private static final String DETAILS_PATH = "./data/characters/details/";
	private static final String FARMING_PATH = "./data/characters/farming/";
	private static final String HOUSES_PATH = "./data/characters/houses/";
	
	/**
	 * Frequencey of backups in minutes.
	 */
	private static final int MINUTES = 30;
	
	/**
	 * Starts the character backup thread.
	 */
	public static void init() {
		new CharacterBackup().start();
	}
	
	@Override
	public void run() {	
		
		//Create the backup task.
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				backup();
			}
		};
		
		//Create a new timer to schedual the timer task.
		Timer timer = new Timer("Backup");
		
		//Schedual the backup task.
		timer.schedule(task, MINUTES * 60000, MINUTES * 60000);
	}
	
	/**
	 * Backup's all the desired files.
	 */
	private static final void backup() {
		try {
			
			File today = new File(BACKUP_PATH + getDate());
			if (!today.exists()) today.mkdirs();
			
			//Zip containers
			File containers = new File(CONTAINERS_PATH);	
			if (containers.exists()) {
				File directory = new File(today + "\\containers\\");
				if (!directory.exists())
					directory.mkdirs();
				ZipOutputStream z = new ZipOutputStream(
						new FileOutputStream(directory + "\\" + getTime() + ".zip"));
				zip(containers, z);
				z.close();
			}
			
			//Zip details
			File details = new File(DETAILS_PATH);	
			if (details.exists()) {
				File directory = new File(today + "\\details\\");
				if (!directory.exists())
					directory.mkdirs();
				ZipOutputStream z = new ZipOutputStream(
						new FileOutputStream(directory + "\\" + getTime() + ".zip"));
				zip(details, z);
				z.close();
			}
			
			//Zip farming
			File farming = new File(FARMING_PATH);
			if (farming.exists()) {
				File directory = new File(today + "\\farming\\");
				if (!directory.exists())
					directory.mkdirs();
				ZipOutputStream z = new ZipOutputStream(
						new FileOutputStream(directory + "\\" + getTime() + ".zip"));
				zip(farming, z);
				z.close();
			}
			
			//Zip houses
			File houses = new File(HOUSES_PATH);
			if (houses.exists()) {
				File directory = new File(today + "\\houses\\");
				if (!directory.exists())
					directory.mkdirs();
				ZipOutputStream z = new ZipOutputStream(
						new FileOutputStream(directory + "\\" + getTime() + ".zip"));
				zip(houses, z);
				z.close();
			}
			
			logger.info("Successfully backed up characters..");
		
		} catch (Throwable e) {
			logger.info("Failed backing up characters..");
			e.printStackTrace();
		}
	}
	
	/**
	 * Zips the desired directory.
	 * 
	 * @param directory
	 *            the directory
	 * @param zos
	 *            the zos         
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static final void zip(File directory, ZipOutputStream zos)
			throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[Short.MAX_VALUE];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getName());
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}
	
	/**
	 * Gets the date.
	 * @return the date
	 */
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}
	
	/**
	 * Gets the time.
	 * @return the time
	 */
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh-mm");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}
	
}