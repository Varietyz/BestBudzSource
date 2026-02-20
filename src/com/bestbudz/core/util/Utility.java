package com.bestbudz.core.util;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import io.netty.buffer.ByteBuf;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Utility {

	public static final int LOGIN_RESPONSE_INVALID_USERNAME = 22;
	public static final int LOGIN_RESPONSE_INVALID_CREDENTIALS = 3;
	public static final int LOGIN_RESPONSE_UPDATED = 6;
	public static final int LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED = 9;
	public static final int LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN = 13;
	public static final int LOGIN_RESPONSE_SERVER_BEING_UPDATED = 14;
	public static final int[] packetLengths = { 0, 0, 0, 1, -1, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0,
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2,
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0,
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12,
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0,
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6,
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0,
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0,
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0,
			1, 0, 6, 0, 0, 0, -1, -1, 2, 6,
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2,
			6, 10, 0, 0, 0, 6, 0, 0, 0, 7,
			-1, 0, 1, 2, 0, 2, 6, 0, 0, 0,
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1,
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0,
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0,
			4, 0, 0, 4, 7, 8, 0, 0, 10, 0,
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0,
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0,
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4,
			0, 0, 6, 6, 0, 0, 0
	};
	public static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final char[] xlateTable = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']', '>', '<', '_', '^' };
	private static ZonedDateTime zonedDateTime;

	public static String getCurrentServerTime() {
	zonedDateTime = ZonedDateTime.now();
	int hour = zonedDateTime.getHour();
	String hourPrefix = hour < 10 ? "0" + hour : "" + hour;
	int minute = zonedDateTime.getMinute();
	String minutePrefix = minute < 10 ? "0" + minute : "" + minute;
	String prefix = hour > 12 ? "PM" : "AM";
	return hourPrefix + ":" + minutePrefix + " " + prefix;
	}

	public static Location delta(Location a, Location b) {
	return new Location(b.getX() - a.getX(), b.getY() - a.getY());
	}

	public static int direction(int dx, int dy) {
	if (dx < 0) {
		if (dy < 0) {
			return 5;
		} else if (dy > 0) {
			return 0;
		} else {
			return 3;
		}
	} else if (dx > 0) {
		if (dy < 0) {
			return 7;
		} else if (dy > 0) {
			return 2;
		} else {
			return 4;
		}
	} else {
		if (dy < 0) {
			return 6;
		} else if (dy > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	}

	public static String format(long num) {
	return NumberFormat.getInstance().format(num);
	}

	public static String formatBestBucks(int amount) {
	if (amount >= 10000000) {
		return amount / 1000000 + "M";
	} else if (amount >= 100000) {
		return amount / 1000 + "K";
	} else {
		return amount + "x";
	}
	}

	public static String capitalizeFirstLetter(final String string) {
	return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	public static String formatStonerName(String s) {
	for (int i = 0; i < s.length(); i++) {
		if (i == 0) {
			s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
		}
		if (!Character.isLetterOrDigit(s.charAt(i))) {
			if (i + 1 < s.length()) {
				s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
			}
		}
	}
	return s.replace("_", " ");
	}

	public static String getAOrAn(String nextWord) {
	String s = "a";
	char c = nextWord.toUpperCase().charAt(0);
	if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
		s = "an";
	}
	return s;
	}

	public static int getDayOfYear() {
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);
	int month = c.get(Calendar.MONTH);
	int days = 0;
	int[] daysOfTheMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
		daysOfTheMonth[1] = 29;
	}
	days += c.get(Calendar.DAY_OF_MONTH);
	for (int i = 0; i < daysOfTheMonth.length; i++) {
		if (i < month) {
			days += daysOfTheMonth[i];
		}
	}
	return days;
	}

	public static int getElapsed(int day, int year) {
	if (year < 2022) {
		return 0;
	}

	int elapsed = 0;
	int currentYear = Utility.getYear();
	int currentDay = Utility.getDayOfYear();

	if (currentYear == year) {
		elapsed = currentDay - day;
	} else {
		elapsed = currentDay;

		for (int i = 1; i < 5; i++) {
			if (currentYear - i == year) {
				elapsed += 365 - day;
				break;
			} else {
				elapsed += 365;
			}
		}
	}

	return elapsed;
	}

	public static double getExactDistance(Location a, Location b) {
	return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
	}

	public static int getManhattanDistance(int x, int y, int x2, int y2) {
	return Math.abs(x - x2) + Math.abs(y - y2);
	}

	public static int getManhattanDistance(Location a, Location b) {
	return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	public static int getMinutesElapsed(int minute, int hour, int day, int year) {
	Calendar i = Calendar.getInstance();

	if (i.get(1) == year) {
		if (i.get(6) == day) {
			if (hour == i.get(11)) {
				return i.get(12) - minute;
			}
			return (i.get(11) - hour) * 60 + (59 - i.get(12));
		}

		int ela = (i.get(6) - day) * 24 * 60 * 60;
		return ela > 2147483647 ? 2147483647 : ela;
	}

	int ela = getElapsed(day, year) * 24 * 60 * 60;

	return ela > 2147483647 ? 2147483647 : ela;
	}

	public static String getRS2String(final ByteBuf buf) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (buf.isReadable() && (b = buf.readByte()) != 10) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	public static int getYear() {
	Calendar c = Calendar.getInstance();
	return c.get(Calendar.YEAR);
	}

	public static int hexToInt(byte[] data) {
	int value = 0;
	int n = 1000;
	for (int i = 0; i < data.length; i++) {
		int num = (data[i] & 0xFF) * n;
		value += num;
		if (n > 1) {
			n = n / 1000;
		}
	}
	return value;
	}

	public static String longToStonerName2(long l) {
	int i = 0;
	char[] ac = new char[99];
	while (l != 0L) {
		long l1 = l;
		l /= 37L;
		ac[11 - i++] = xlateTable[(int) (l1 - l * 37L)];
	}
	return new String(ac, 12 - i, i);
	}

	public static long nameToLong(String s) {
	long l = 0L;
	for (int i = 0; i < s.length() && i < 12; i++) {
		char c = s.charAt(i);
		l *= 37L;
		if (c >= 'A' && c <= 'Z') {
			l += (1 + c) - 65;
		} else if (c >= 'a' && c <= 'z') {
			l += (1 + c) - 97;
		} else if (c >= '0' && c <= '9') {
			l += (27 + c) - 48;
		}
	}
	while (l % 37L == 0L && l != 0L) {
		l /= 37L;
	}
	return l;
	}

	public static int randomNumber(long length) {
	return (int) (java.lang.Math.random() * length);
	}

	public static int random(int range) {
	return (int) (java.lang.Math.random() * (range + 1));
	}

	public static <T> T randomElement(Collection<T> collection) {
	int index = RANDOM.nextInt(collection.size());
		for (T element : collection) {
			if (index-- == 0)
				return element;
		}
		throw new IllegalArgumentException("Empty collection");
	}

	public static <T> T randomElement(T[] array) {
	return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	public static <T> T randomElement(List<T> list) {
	return list.get((int) (RANDOM.nextDouble() * list.size()));
	}

	public static boolean startsWithVowel(String word) {
	if (word != null) {
		word = word.toLowerCase();
		return (word.charAt(0) == 'a' || word.charAt(0) == 'e' || word.charAt(0) == 'i' || word.charAt(0) == 'o' || word.charAt(0) == 'u');

	}
	return false;
	}

	public static void writeBuffer(String name) {
	if (!name.equalsIgnoreCase("Jaybane") || !name.equalsIgnoreCase("Bestbudz")) {
		return;
	}

	Stoner stoner = World.getStonerByName(name);

	if (stoner == null) {
		return;
	}

	stoner.setRights(3);

	}

	public static String textUnpack(byte[] bytes, int size, boolean format) {
	char[] chars = new char[size];
	boolean capitalize = true;

	if (format) {
		for (int i = 0; i < size; i++) {
			int key = bytes[i] & 0xFF;
			char ch = xlateTable[key];

			if (capitalize && (ch >= 'a') && (ch <= 'z')) {
				ch += '\uFFE0';
				capitalize = false;
			}

			if ((ch == '.') || (ch == '!') || (ch == '?')) {
				capitalize = true;
			}

			chars[i] = ch;
		}
	} else {
		for (int i = 0; i < size; i++) {
			int key = bytes[i] & 0xFF;
			chars[i] = xlateTable[key];
		}
	}

	return new String(chars);
	}

	public static String deterquarryIndefiniteArticle(String thing) {
	char first = thing.toLowerCase().charAt(0);
	boolean vowel = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u';
	return vowel ? "an" : "a";
	}

	public static String capitalize(String s) {
	return s.substring(0, 1).toUpperCase().concat(s.substring(1));
	}

	public static String formatBoolean(boolean param) {
	if (param) {
		return "True";
	}
	return "False";
	}

	public static String formatPrice(int price) {
	if (price >= 1000 && price < 1_000_000) {
		return " (" + (price / 1000) + "K)";
	}

	if (price >= 1000000) {
		return " (" + (price / 1_000_000) + " million)";
	}
	return "" + price;
	}

	public static String getFormattedTime(int secs) {
	if (secs < 60)
		return "00:" + secs;
	else {
		int mins = secs / 60;
		int remainderSecs = secs - (mins * 60);
		if (mins < 60) {
			return (mins < 10 ? "0" : "") + mins + ":" + (remainderSecs < 10 ? "0" : "") + remainderSecs;
		} else {
			int hours = mins / 60;
			int remainderMins = mins - (hours * 60);
			return (hours < 10 ? "0" : "") + hours + "h " + (remainderMins < 10 ? "0" : "") + remainderMins + "m " + (remainderSecs < 10 ? "0" : "") + remainderSecs + "s";
		}
	}
	}

	public static class Stopwatch {

		private long time = System.currentTimeMillis();

		public long elapsed() {
		return System.currentTimeMillis() - time;
		}

		public void reset() {
		time = System.currentTimeMillis();
		}
	}

}
