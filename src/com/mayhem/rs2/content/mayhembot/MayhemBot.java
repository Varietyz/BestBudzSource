package com.mayhem.rs2.content.mayhembot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.mayhem.Constants;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendBanner;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Mayhem Bot - Asks random questions which players can race to answer
 * @author Daniel
 *
 */
public class MayhemBot {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(MayhemBot.class.getSimpleName());
	
	/**
	 * Holds all the bot data
	 */
	private final static Set<MayhemBotData> BOT_DATA = new HashSet<>();
	
	/**
	 * The current question/answer set
	 */
	private static MayhemBotData current = null;
	
	/*
	 * Holds all the MayhemBot attempted answers
	 */
	public final static ArrayList<String> attempts = new ArrayList<String>();
	
	/**
	 * Color of the MayhemBot messages
	 */
	private static final String COLOR = "<col=8814B3>";
	
	/**
	 * Declares the Mayhem data
	 */
	public static void declare() {
		for (MayhemBotData data : MayhemBotData.values()) {
			BOT_DATA.add(data);
		}
		logger.info("Loaded " + BOT_DATA.size() + " Trivia Bot questions.");
	}
	
	/**
	 * Initializes the MayhemBot task
	 */
	public static void initialize() {
		TaskQueue.queue(new Task(650, false) {
			@Override
			public void execute() {
				if (current == null) {
					assign();	
					return;
				}
				sendMessage("[" + COLOR + "Trivia Bot</col>] " + current.getQuestion());
				sendNotification("[" + COLOR + "Trivia Bot</col>] " + current.getQuestion());
			}
			@Override
			public void onStop() {
			}
		});	
	}
	
	/**
	 * Assigns a new question
	 */
	private static void assign() {
		current = Utility.randomElement(BOT_DATA);
		sendMessage("[" + COLOR + "Trivia:</col>] " + current.getQuestion());
	}
	
	/**
	 * Handles player answering the question
	 * @param player
	 * @param answer
	 */
	public static void answer(Player player, String answer) {
		if (current == null) {
			return;
		}
		for (int i = 0; i < Constants.BAD_STRINGS.length; i++) {
			if (answer.contains(Constants.BAD_STRINGS[i])) {
				player.send(new SendMessage("[" + COLOR + "Trivia Bot</col>] That was an offensive answer! Contain yourself or be punished."));
				return;
			}
		}
		for (int i = 0; i < current.getAnswers().length; i++) {
			if (current.getAnswers()[i].equalsIgnoreCase(answer)) {
				answered(player, answer);
				return;
			}
		}
		player.send(new SendMessage("[" + COLOR + "Trivia Bot</col>] Sorry, the answer you have entered is incorrect! Try again!"));
		attempts.add(answer);
	}
	
	/**
	 * Handles player answering the question successfully 
	 * @param player
	 * @param answer
	 */
	private static void answered(Player player, String answer) {
		sendMessage("[" + COLOR + "Trivia Bot</col>] " + COLOR + player.determineIcon(player) + " " + player.getUsername() + "</col> has answered the question correctly! Answer:" + COLOR + " " + Utility.capitalizeFirstLetter(answer) + "</col>.");
		if (attempts.size() > 0) {
			sendMessage("[" + COLOR + "Trivia Bot</col>] Attempted answers: " + COLOR + "" + attempts.toString() + "</col>!");
		}
		int REWARD = Utility.random(25_000);
		player.getInventory().addOrCreateGroundItem(995, REWARD, true);
		player.triviaPoints ++;
		AchievementHandler.activate(player, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY, 1);
		AchievementHandler.activate(player, AchievementList.ANSWER_80_TRIVIABOTS_CORRECTLY, 1);
		reset();
	}
	
	/**
	 * Resets the Trivia Bot
	 */
	private static final void reset() {
		current = null;
		attempts.clear();
	}
	
	/**
	 * Sends message to server
	 * @param message
	 */
	public static void sendMessage(String message) {
		for (Player players : World.getPlayers()) {
			if (players != null && players.isWantTrivia()) {
				players.send(new SendMessage(message));
			}
		}
	}
	
	/**
	 * Sends notification to server
	 * @param message
	 */
	public static void sendNotification(String message) {
		for (Player players : World.getPlayers()) {
			if (players != null && players.isWantTrivia() && players.isTriviaNotification()) {
				players.send(new SendBanner(message, 0x8E9CA3));
			}
		}
	}

}
