package com.bestbudz.rs2.util;

public class Cooldown {

	/**
	 * EXAMPLE USAGE = Cooldown.cooldown(3);
	 * Pauses execution for the specified number of seconds
	 * @param seconds Duration to wait in seconds
	 */
	public static void cooldown(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// Restore interrupted status
		}
	}

	/**
	 * Pauses execution for the specified number of milliseconds
	 * @param milliseconds Duration to wait in milliseconds
	 */
	public static void cooldownMs(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// Restore interrupted status
		}
	}

	/**
	 * Pauses execution for the specified number of seconds (supports decimals)
	 * @param seconds Duration to wait in seconds (e.g., 1.5 for 1.5 seconds)
	 */
	public static void cooldown(double seconds) {
		try {
			Thread.sleep((long)(seconds * 1000));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// Restore interrupted status
		}
	}
}