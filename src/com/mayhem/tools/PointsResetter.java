package com.mayhem.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Andy || ReverendDread Jun 14, 2017
 */
public class PointsResetter {

	/**
	 * The path of character details.
	 */
	public static final String PATH = "./data/characters/details/";
	
	
	public static void main(String[] args) throws IOException {
		
		File directory = new File(PATH);
		File[] directoryListing = directory.listFiles();
		
		if (directoryListing != null) {
			
			//Loop through all files in the directory
			for (File file : directoryListing) {		
				
				//Create a new BufferedReader for this file.
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				for (int i = 0; i < 1000; i++) {
					
					String line = reader.readLine();
					
					switch (line) {
					
						case "\"votePoints\":":
							line.replace(line, "\"votePoints\": 0,");
							break;
							
						case "\"triviaPoints\":":
							line.replace(line, "\"triviaPoints\": 0,");
							break;
							
						case "\"pvmPoints\":":
							line.replace(line, "\"pvmPoints\": 0,");
							break;
							
						case "\"bossPoints\":":
							line.replace(line, "\"bossPoints\": 0,");
							break;
							
						case "\"thievePoints\":":
							line.replace(line, "\"thievePoints\": 0,");
							break;
							
						case "\"farmingPoints\":":
							line.replace(line, "\"farmingPoints\": 0,");
							break;
							
						case "\"woodcuttingPoints\":":
							line.replace(line, "\"woodcuttingPoints\": 0,");
							break;
							
						case "\"firemakingPoints\":":
							line.replace(line, "\"firemakingPoints\": 0,");
							break;
							
						case "\"fishingPoints\":":
							line.replace(line, "\"fishingPoints\": 0,");
							break;
							
						case "\"smithingPoints\":":
							line.replace(line, "\"smithingPoints\": 0,");
							break;
							
						case "\"miningPoints\":":
							line.replace(line, "\"miningPoints\": 0,");
							break;
							
						case "\"fletchingPoints\":":
							line.replace(line, "\"fletchingPoints\": 0,");
							break;
							
						case "\"craftingPoints\":":
							line.replace(line, "\"craftingPoints\": 0,");
							break;
							
						case "\"herblorePoints\":":
							line.replace(line, "\"herblorePoints\": 0,");
							break;
							
						case "\"puroPoints\":":
							line.replace(line, "\"puroPoints\": 0,");
							break;
							
						case "\"runecraftingPoints\":":
							line.replace(line, "\"runecraftingPoints\": 0,");
							break;
							
						case "\"prayerPoints\":":
							line.replace(line, "\"prayerPoints\": 0,");
							break;
							
						case "\"slayerPoints\":":
							line.replace(line, "\"slayerPoints\": 0,");
							break;
							
						case "\"pestPoints\":":
							line.replace(line, "\"pestPoints\": 0,");
							break;
							
						case "\"arenaPoints\":":
							line.replace(line, "\"arenaPoints\": 0,");
							break;
					}			
					
				}
				reader.close();
			}
			
		}
		
	}
	
}
