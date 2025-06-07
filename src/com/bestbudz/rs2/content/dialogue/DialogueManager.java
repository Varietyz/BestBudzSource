package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DialogueManager {

	public static void sendItem2(Stoner stoner, String text1, String text2, int item1, int item2) {
		stoner.send(new SendMessage(text1));
		stoner.send(new SendMessage(text2));
	}

	public static void sendItem2zoom(
		Stoner stoner, String text1, String text2, int item1, int item2) {
		stoner.send(new SendMessage(text1));
		stoner.send(new SendMessage(text2));
	}

	public static void sendItem1(Stoner stoner, String text, int item) {
		stoner.send(new SendMessage(text));
	}

	public static void makeItem3(
		Stoner stoner,
		int itemId1,
		String itemName1,
		int itemId2,
		String itemName2,
		int itemId3,
		String itemName3) {
		stoner.send(new SendMessage(itemName1));
		stoner.send(new SendMessage(itemName2));
		stoner.send(new SendMessage(itemName3));
	}

	public static void sendInformationBox(Stoner stoner, String title, String line1, String line2, String line3, String line4) {
		if (title != null) stoner.send(new SendMessage("[INFO] " + title));
		if (line1 != null) stoner.send(new SendMessage("[INFO] " + line1));
		if (line2 != null) stoner.send(new SendMessage("[INFO] " + line2));
		if (line3 != null) stoner.send(new SendMessage("[INFO] " + line3));
		if (line4 != null) stoner.send(new SendMessage("[INFO] " + line4));
	}

	public static void sendNpcChat(Stoner stoner, int npcId, Emotion emotion, String... lines) {
		String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
		switch (lines.length) {
			case 1:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				break;
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[3]));
		}
	}

	public static void sendOption(Stoner stoner, String... options) {
		if (options.length < 2) {
			return;
		}
		switch (options.length) {
			case 1:
				throw new IllegalArgumentException("1 option is not possible! (DialogueManager.java)");
			case 2:
				stoner.send(new SendMessage("Option 1: " + options[0]));
				stoner.send(new SendMessage("Option 2: " + options[1]));
				break;
			case 3:
				stoner.send(new SendMessage("Option 1: " + options[0]));
				stoner.send(new SendMessage("Option 2: " + options[1]));
				stoner.send(new SendMessage("Option 3: " + options[2]));
				break;
			case 4:
				stoner.send(new SendMessage("Option 1: " + options[0]));
				stoner.send(new SendMessage("Option 2: " + options[1]));
				stoner.send(new SendMessage("Option 3: " + options[2]));
				stoner.send(new SendMessage("Option 4: " + options[3]));
				break;
			case 5:
				stoner.send(new SendMessage("Option 1: " + options[0]));
				stoner.send(new SendMessage("Option 2: " + options[1]));
				stoner.send(new SendMessage("Option 3: " + options[2]));
				stoner.send(new SendMessage("Option 4: " + options[3]));
				stoner.send(new SendMessage("Option 5: " + options[4]));
		}
	}

	public static void sendStonerChat(Stoner stoner, Emotion emotion, String... lines) {
		switch (lines.length) {
			case 1:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				break;
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[3]));
		}
	}

	public static void sendStatement(Stoner stoner, String... lines) {
		switch (lines.length) {
			case 1:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				break;
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage(lines[3]));
				break;
			case 5:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage(lines[3]));
				if (lines[4] != null) stoner.send(new SendMessage(lines[4]));
		}
	}

	public static void sendTimedNpcChat(Stoner stoner, int npcId, Emotion emotion, String... lines) {
		String npcName = GameDefinitionLoader.getNpcDefinition(npcId).getName();
		switch (lines.length) {
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage("[" + npcName + "] " + lines[3]));
		}
	}

	public static void sendTimedStonerChat(Stoner stoner, Emotion emotion, String... lines) {
		switch (lines.length) {
			case 1:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				break;
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage("[" + stoner.getUsername() + "] " + lines[3]));
		}
	}

	public static void sendTimedStatement(Stoner stoner, String... lines) {
		switch (lines.length) {
			case 1:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				break;
			case 2:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				break;
			case 3:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				break;
			case 4:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage(lines[3]));
				break;
			case 5:
				if (lines[0] != null) stoner.send(new SendMessage(lines[0]));
				if (lines[1] != null) stoner.send(new SendMessage(lines[1]));
				if (lines[2] != null) stoner.send(new SendMessage(lines[2]));
				if (lines[3] != null) stoner.send(new SendMessage(lines[3]));
				if (lines[4] != null) stoner.send(new SendMessage(lines[4]));
		}
	}
}