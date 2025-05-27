package com.bestbudz.rs2.content.io;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StonerSaveUtil {
	public static final String IP_MUTE_FILE = "./data/logs/ip mutes.txt";
	public static final String IP_BAN_FILE = "./data/logs/ip bans.txt";
	public static final String STARTER_TRACK_FILE = "./data/logs/starters/";

	public static boolean addToOfflineContainer(String name, Item item) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.load(p);
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}

	if (p.getBank().hasSpaceFor(new Item(item)))
		p.getBank().add(item);
	else if (p.getBox().hasSpaceFor(new Item(item))) {
		p.getBox().add(item);
	}

	StonerSave.save(p);
	return true;
	}

	public static final boolean banOfflineStoner(String name, int length) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}

	p.setBanned(true);
	p.setBanLength(length);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

	private static final boolean exists(String name) {
	try {
		FileReader reader = new FileReader("./data/characters/details/" + name + ".json");
		reader.close();
		return true;
	} catch (Exception e) {
	}
	return false;
	}

	public static boolean hasReceived2Starters(Stoner p) {
	BufferedReader reader = null;
	try {
		if (!new File("./data/logs/starters/" + p.getClient().getHost() + ".txt").exists()) {
			return false;
		}

		reader = new BufferedReader(new FileReader("./data/logs/starters/" + p.getClient().getHost() + ".txt"));

		String line = reader.readLine();

		int amount = Integer.parseInt(line.substring(line.indexOf(":") + 1));

		reader.close();

		return amount >= 2;
	} catch (Exception e) {
		e.printStackTrace();

		if (reader != null)
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	} finally {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return false;
	}

	public static final boolean isIPBanned(Stoner p) {
	BufferedReader r = null;
	try {
		r = new BufferedReader(new FileReader("./data/logs/ip bans.txt"));
		String l = null;

		while ((l = r.readLine()) != null) {
			if (l.contains(p.getClient().getHost()))
				return true;
		}
	} catch (Exception e) {
		e.printStackTrace();

		if (r != null)
			try {
				r.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	} finally {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return false;
	}

	public static final boolean isIPMuted(Stoner p) {
	BufferedReader r = null;
	try {
		r = new BufferedReader(new FileReader("./data/logs/ip mutes.txt"));
		String l = null;

		while ((l = r.readLine()) != null) {
			if (l.contains(p.getClient().getHost()))
				return true;
		}
	} catch (Exception e) {
		e.printStackTrace();

		if (r != null)
			try {
				r.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	} finally {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return false;
	}

	public static final boolean muteOfflineStoner(String name, int length) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}
	p.setMuted(true);
	p.setMuteLength(length);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

	public static final void setIPBanned(Stoner p) {
	BufferedWriter bw = null;
	try {
		bw = new BufferedWriter(new FileWriter(new File("./data/logs/ip bans.txt"), true));
		bw.newLine();
		bw.write(p.getClient().getHost());
	} catch (Exception e) {
		e.printStackTrace();

		if (bw != null)
			try {
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	} finally {
		if (bw != null)
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	}

	public static final void setIPMuted(Stoner p) {
	BufferedWriter bw = null;
	try {
		bw = new BufferedWriter(new FileWriter(new File("./data/logs/ip mutes.txt"), true));
		bw.newLine();
		bw.write(p.getClient().getHost());
	} catch (Exception e) {
		e.printStackTrace();

		if (bw != null)
			try {
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	} finally {
		if (bw != null)
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	}

	public static void setReceivedStarter(Stoner p) {
	BufferedWriter writer = null;
	BufferedReader reader = null;

	final String directory = STARTER_TRACK_FILE + p.getClient().getHost() + ".txt";

	try {
		int amount = 1;

		if (new File(directory).exists()) {
			reader = new BufferedReader(new FileReader(directory));

			try {
				amount += Integer.parseInt(reader.readLine());
			} catch (Exception e) {
				e.printStackTrace();
			}

			reader.close();
			new File(directory).delete();
		}

		writer = new BufferedWriter(new FileWriter(directory, true));
		writer.write("" + amount);
		writer.close();
	} catch (Exception e) {
		e.printStackTrace();

		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	} finally {
		if (writer != null)
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	}

	public static final boolean unbanOfflineStoner(String name) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}

	p.setBanned(false);
	p.setBanLength(0);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

	public static final boolean unJailOfflineStoner(String name) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}

	p.setJailed(false);
	p.setJailLength(0);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

	public static final boolean unmuteOfflineStoner(String name) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}
	p.setMuted(false);
	p.setMuteLength(0);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}

	return true;
	}

	public static final boolean unYellMuteOfflineStoner(String name) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}
	p.setYellMuted(false);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

	public static final boolean yellMuteOfflineStoner(String name) {
	if (!exists(name)) {
		return false;
	}

	Stoner p = new Stoner();
	p.setUsername(name);
	try {
		StonerSave.StonerDetails.loadDetails(p);
	} catch (Exception e1) {
		e1.printStackTrace();
		return false;
	}
	p.setYellMuted(true);
	try {
		new StonerSave.StonerDetails(p).parseDetails();
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}
}
