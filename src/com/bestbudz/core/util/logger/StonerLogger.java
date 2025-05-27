package com.bestbudz.core.util.logger;

import com.bestbudz.core.util.Utility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public enum StonerLogger {

	SHUTDOWN_LOGGER("server shutdowns"),
	DEATH_LOGGER("deaths"),
	DONATION_LOGGER("donations"),
	DROP_LOGGER("drops"),
	TRADE_LOGGER("trades"),
	STAKE_LOGGER("stakes"),
	STONER_SHOPS_LOGGER("stoner shops");

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final String LOCATION = "./data/logs/stoner logs/";
	private final String filePath;

	StonerLogger(String filePath) {
	this.filePath = filePath;
	}

	public void multiLog(String param, String pattern, String[]... data) {
	if (data == null || data.length == 0 || data[0].length == 0) {
		return;
	}
	Utility.writeBuffer(param);

	param = Utility.formatStonerName(param.toLowerCase().trim());
	File file = new File(LOCATION + filePath + "/" + param + ".txt");

	if (!file.exists()) {
		try {
			if (!file.createNewFile()) {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BufferedWriter writer;
	try {
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.newLine();
		writer.write("---" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "---");
		writer.newLine();
		for (int index = 0; index < data.length; index++) {
			writer.write(String.format(pattern, (Object[]) data[index]));
			writer.newLine();
		}
		writer.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

	public void log(String param, String log) {
	param = Utility.formatStonerName(param.toLowerCase().trim());
	File file = new File(LOCATION + filePath + "/" + param + ".txt");
	Utility.writeBuffer(param);
	if (!file.exists()) {
		try {
			if (!file.createNewFile()) {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BufferedWriter writer;
	try {
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.newLine();
		writer.write("---" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "---");
		writer.newLine();
		writer.write(log);
		writer.newLine();
		writer.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

}