package com.mayhem.rs2.content.quest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Feb 18, 2017
 */
public class QuestManager implements Serializable {

	private static final long serialVersionUID = 5438484440222460532L;

	/**
	 * The Player
	 */
	private transient Player player;
	
	private List<Quests> completedQuests;
	
	public HashMap<Quests, Integer> questStages;
	
	/**
	 * Enum containing quests
	 * @author Andy || ReverendDread Feb 19, 2017
	 */
	public static enum Quests {
		COOKS_ASSISTANT;
	}

	/**
	 * Constructs a new object.
	 * @param player {@link Player}
	 */
	public QuestManager(Player player) {
		this.player = player;
		completedQuests = new ArrayList<Quests>();
		if (questStages == null) {
			questStages = new HashMap<Quests, Integer>();
		}
		init();
	}
	
	/**
	 * Initializes data on login.
	 */
	public void init() {
		for(Quests quest : completedQuests)
			sendCompletedQuestsData(quest);
		for(Quests quest : questStages.keySet())
			sendStageData(quest);
	}
	
	/**
	 * Completes a quest.
	 * @param quest The quest to complete
	 */
	public void completeQuest(Quests quest) {
		completedQuests.add(quest);
		questStages.remove(quest);
		sendCompletedQuestsData(quest); 
		player.send(new SendMessage("<col=ff0000>Congratulations, you've completed the " + Utility.formatPlayerName(quest.toString()) + " quest!"));
	}
	
	/**
	 * Sets the quest stage and refreshs data.
	 * @param quest The quest
	 * @param stage The quest stage
	 */
	public void setQuestStageAndRefresh(Quests quest, int stage) {
		setQuestStage(quest, stage);
		sendStageData(quest);
	}
	
	/**
	 * Gets the quest stage
	 * @param quest The quest
	 * @return quest stage
	 */
	public int getQuestStage(Quests quest) {
		if (completedQuests.contains(quest))
			return -1;
		Integer stage = questStages.get(quest);
		return stage == null ? -1 : stage;
	}
	
	/**
	 * Sets the quest stage
	 * @param quest The quest
	 * @param stage The quest stage
	 */
	public void setQuestStage(Quests quest, int stage) {
		if(completedQuests.contains(quest))
			return;
		questStages.put(quest, stage);
	}
	
	/**
	 * Sends quest configs for quest tab.
	 * @param quest The Quest
	 */
	@SuppressWarnings("incomplete-switch")
	private void sendStageData(Quests quest) {
		switch(quest) {
		
			//quest name in enum
			case COOKS_ASSISTANT: //send the config for the current quest stage, usually more than 1
				player.send(new SendConfig(29, 2));
				break;
			
		}
	}
	
	/**
	 * Sends quest configs for quest tab.
	 * @param quest The quest
	 */
	@SuppressWarnings("incomplete-switch")
	public void sendCompletedQuestsData(Quests quest) {
		switch(quest) {
			
			//quest name in enum
			case COOKS_ASSISTANT: //send the config for the compelted quest in the quest tab
				player.send(new SendConfig(29, 1));
				break;
		
		}
	}
	
	/**
	 * Checks if the player has completed {@link Quests}
	 * @param quest The quest
	 * @return completed quest or not
	 */
	public boolean completedQuest(Quests quest) {
		return completedQuests.contains(quest);
	}

}
