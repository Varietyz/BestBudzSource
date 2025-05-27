package com.bestbudz.rs2.content.pets;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles boss pets
 * 
 * @author Jaybane
 */
public class BossPets {

	/**
	 * Boss Pet data
	 * 
	 * @author Jaybane
	 *
	 */
	public enum PetData {

		KALPHITE_PRINCESS_FLY(12654, 6637),
		KALPHITE_PRINCESS_BUG(12647, 6638),
		SMOKE_DEVIL(12648, 6655),
		DARK_CORE(12816, 318),
		PRINCE_BLACK_DRAGON(12653, 4000),
		GREEN_SNAKELING(12921, 2130),
		RED_SNAKELING(12939, 2131),
		BLUE_SNAKELING(12940, 2132),
		CHAOS_ELEMENT(11995, 5907),
		KREE_ARRA(12649, 4003),
		CALLISTO(13178, 497),
		SCORPIAS_OFFSPRING(13181, 5547),
		VENENATIS(13177, 495),
		VETION_PURPLE(13179, 5559),
		VETION_ORANGE(13180, 5560),
		BABY_MOLE(12646, 6635),
		KRAKEN(12655, 6640),
		DAGANNOTH_SUPRIME(12643, 4006),
		DAGANNOTH_RIME(12644, 4007),
		DAGANNOTH_REX(12645, 4008),
		GENERAL_GRAARDOR(12650, 4001),
		COMMANDER_ZILYANA(12651, 4009),
		KRIL_TSUTSAROTH(12652, 4004);

		private final int itemID;
		private final int npcID;

		private PetData(int itemID, int npcID) {
		this.itemID = itemID;
		this.npcID = npcID;
		}

		public int getItem() {
		return itemID;
		}

		public int getNPC() {
		return npcID;
		}

		public static PetData forItem(int id) {
		for (PetData data : PetData.values())
			if (data.itemID == id)
				return data;
		return null;
		}

		public static PetData forNPC(int id) {
		for (PetData data : PetData.values())
			if (data.npcID == id)
				return data;
		return null;
		}
	}

	/**
	 * Handles spawning the pet
	 * 
	 * @param stoner
	 * @param itemID
	 */
	public static boolean spawnPet(Stoner stoner, int itemID, boolean loot) {
	PetData data = PetData.forItem(itemID);

	if (data == null) {
		return false;
	}

	if (stoner.getBossPet() != null) {
		stoner.send(new SendMessage("I know u love animals, but they dont like eachother!"));
		return true;
	}

	stoner.getBox().remove(new Item(itemID, 1));

	final Mob mob = new Mob(stoner, data.npcID, false, false, true, stoner.getLocation());
	mob.getFollowing().setIgnoreDistance(true);
	mob.getFollowing().setFollow(stoner);

	stoner.setBossPet(mob);
	stoner.setBossID(data.npcID);
	stoner.getUpdateFlags().sendAnimation(new Animation(827));
	stoner.face(stoner.getBossPet());

	if (loot) {
		AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_1_BOSS_PET, 1);
		AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_10_BOSS_PET, 1);
	} else {
		stoner.send(new SendMessage("You took out " + mob.getDefinition().getName() + " for a walk."));
	}
	return true;
	}

	/**
	 * Handles picking up the pet
	 * 
	 * @param stoner
	 * @param npcID
	 * @return
	 */
	public static boolean pickupPet(Stoner stoner, Mob mob) {
	if (mob == null || World.getNpcs()[mob.getIndex()] == null) {
		return false;
	}
	PetData data = PetData.forNPC(mob.getId());

	if (data == null) {
		return false;
	}

	if (stoner.getBossPet() == null || stoner.getBossPet().isDead()) {
		return false;
	}

	if (stoner.getBossPet() != mob || mob.getOwner() != stoner) {
		DialogueManager.sendStatement(stoner, "This is not your pet!");
		return true;
	}

	if (stoner.getBox().hasSpaceFor(new Item(data.getItem()))) {
		stoner.getBox().add(new Item(data.getItem()));
	} else if (stoner.getBank().hasSpaceFor((new Item(data.getItem())))) {
		stoner.getBank().add((new Item(data.getItem())));
		stoner.getClient().queueOutgoingPacket(new SendMessage("Your pet has been added to your bank."));
	} else {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You must free some box space to pick up your pet."));
		return false;
	}

	stoner.getUpdateFlags().sendAnimation(new Animation(827));
	stoner.face(stoner.getBossPet());

	TaskQueue.queue(new Task(stoner, 1, true) {
		@Override
		public void execute() {
		stoner.getBossPet().remove();
		stoner.setBossPet(null);
		stop();
		}

		@Override
		public void onStop() {
		stoner.send(new SendMessage("You have picked up your pet."));
		}
	});

	return true;
	}

	/**
	 * Handles pets on logout
	 * 
	 * @param stoner
	 * @return
	 */
	public static void onLogout(Stoner stoner) {
	if (stoner.getBossPet() != null) {

		PetData data = PetData.forNPC(stoner.getBossPet().getId());

		if (stoner.getBox().hasSpaceFor(new Item(data.getItem()))) {
			stoner.getBox().add(new Item(data.getItem()));
		} else if (stoner.getBank().hasSpaceFor((new Item(data.getItem())))) {
			stoner.getBank().add((new Item(data.getItem())));
		}
	}
	}

	/**
	 * Handles what happens on death
	 * 
	 * @param stoner
	 */
	public static void onDeath(Stoner stoner) {
	if (stoner.getBossPet() != null) {
		stoner.getBossPet().remove();
		stoner.setBossPet(null);
		stoner.send(new SendMessage("You got yourself and your pet killed, irresponsible douch!"));
	}
	}
}