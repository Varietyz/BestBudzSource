package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DialogueManager {

	public static void sendItem2zoom(
		Stoner stoner, String text1, String text2, int item1, int item2) {
		stoner.send(new SendMessage(text1));
		stoner.send(new SendMessage(text2));
	}

	public static void sendItem1(Stoner stoner, String text, int item) {
		stoner.send(new SendMessage(text));
	}

	public static void sendInformationBox(Stoner stoner, String title, String line1, String line2, String line3, String line4) {
		if (title != null) stoner.send(new SendMessage("[INFO] " + title));
		if (line1 != null) stoner.send(new SendMessage("[INFO] " + line1));
		if (line2 != null) stoner.send(new SendMessage("[INFO] " + line2));
		if (line3 != null) stoner.send(new SendMessage("[INFO] " + line3));
		if (line4 != null) stoner.send(new SendMessage("[INFO] " + line4));
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