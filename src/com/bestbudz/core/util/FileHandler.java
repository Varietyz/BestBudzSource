package com.bestbudz.core.util;

import com.bestbudz.rs2.content.gambling.Gambling;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

public class FileHandler {

	private static final Logger logger = Logger.getLogger(FileHandler.class.getSimpleName());

	public static void load() {
	loadGambling();
	}

	public static void saveGambling() {
	try {
		File file = new File("./data/saves/GAMBLING.txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(String.valueOf(Gambling.MONEY_TRACKER));
		out.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public static void loadGambling() {
	try {
		File file = new File("./data/saves/GAMBLING.txt");
		if (!file.exists()) {
			return;
		}
		BufferedReader in = new BufferedReader(new FileReader(file));
		long money = Long.parseLong(in.readLine());
		Gambling.MONEY_TRACKER = money;
		logger.info("Gambling results " + Utility.format(money));
		in.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

}
