package com.mayhem.rs2.content.skill.prayer;

import java.util.HashMap;
import java.util.Map;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.Task.BreakType;
import com.mayhem.core.task.Task.StackType;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendSound;

public class BoneBurying {
	
	public static enum Bones {
		NORMAL_BONES(526, 7.5D),
		WOLF_BONES(2859, 7.5D),
		BAT_BONES(530, 7.5D),
		BIG_BONES(532, 25.0D),
		BABYDRAGON_BONES(534, 38.0D),
		DRAGON_BONES(536, 103.0D),
		DAGG_BONES(6729, 85.0D),
		OURG_BONES(4834, 115.0D),
		LONG_BONE(10976, 150.0D),
		SKELETAL_WYVERN_BONES(6812, 107.0D),
		LAVA_DRAGON_BONES(11943, 125.0D);

		private int id;
		private double experience;
		private static Map<Integer, Bones> bones = new HashMap<Integer, Bones>();

		public static final void declare() {
			for (Bones b : values())
				bones.put(Integer.valueOf(b.getId()), b);
		}

		public static Bones forId(int id) {
			return bones.get(Integer.valueOf(id));
		}
		
		public static Bones forBoneId(int id) {
			for (Bones bone : Bones.values()) {
				if (bone.getId() == id)
					return bone;
			}
			return null;
		}

		private Bones(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public int getId() {
			return id;
		}
		
		public double getExp() {
			return experience;
		}
	}

	public static final String USING_ON_ALTAR_KEY = "boneonaltarkey";

	public static final int BURYING_ANIMATION = 827;

	public static boolean bury(Player player, int id, int slot) {
		Bones bones = Bones.forId(id);

		if (bones == null) {
			return false;
		}

		if (player.getSkill().locked()) {
			return true;
		}

		player.getSkill().lock(4);
		player.getCombat().reset();
		player.getUpdateFlags().sendAnimation(827, 0);
		player.getClient().queueOutgoingPacket(new SendMessage("You bury the " + Item.getDefinition(bones.id).getName() + ". You now have @blu@" + player.skillPoints + "</col> Skill points."));
		player.getInventory().clear(slot);
		player.getSkill().addExperience(5, bones.experience);
		player.skillPoints += 25;
		AchievementHandler.activate(player, AchievementList.BURY_150_BONES, 1);
		AchievementHandler.activate(player, AchievementList.BURY_1000_BONES, 1);

		return true;
	}

	public static void finishOnAltar(Player player, int amount) {
		if (player.getAttributes().get("boneonaltarkey") == null) {
			return;
		}

		int item = player.getAttributes().getInt("boneonaltarkey");

		Bones bones = Bones.forId(item);

		if (bones == null) {
			return;
		}

		int invAmount = player.getInventory().getItemAmount(item);

		if (invAmount == 0)
			return;
		if (invAmount < amount) {
			amount = invAmount;
		}

		player.getSkill().lock(2);

		player.getClient().queueOutgoingPacket(new SendMessage("You sacrifice the " + Item.getDefinition(bones.id).getName() + " at the altar. You now have @blu@" + player.skillPoints + " Skill points."));

		player.getUpdateFlags().sendAnimation(645, 5);
		player.getInventory().remove(new Item(item, amount));
		player.getSkill().addExperience(5, (bones.experience * 2.0D) * amount);
		player.skillPoints += 20;
		handleFallyShield1(player);
		handleFallyShield2(player);
		handleFallyShield3(player);
		AchievementHandler.activate(player, AchievementList.BURY_150_BONES, 1);
		AchievementHandler.activate(player, AchievementList.BURY_1000_BONES, 1);

	}

	public static boolean useBonesOnAltar(Player p, int item, int object) {
		if (object == 2640 || object == 409) {
			Bones bones = Bones.forId(item);
			p.skillPoints += 20;

			if (bones == null) {
				return false;
			}
			
			TaskQueue.queue(new Task(p, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.BONES_ON_ALTER) {
				@Override
				public void execute() {
					if (!p.getInventory().hasItemId(item)) {
						stop();
						return;
					}
					
					if (p.getSkill().locked()) {
						return;
					}
					
					p.getSkill().lock(2);
					
					p.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
					p.getClient().queueOutgoingPacket(new SendMessage("You sacrifice the " + Item.getDefinition(bones.id).getName() + " at the altar."));
					
					p.getUpdateFlags().sendAnimation(645, 5);
					p.getInventory().remove(item);
					p.skillPoints += 20;
					handleFallyShield1(p);
					handleFallyShield2(p);
					handleFallyShield3(p);
					p.getSkill().addExperience(5, bones.experience * 2.0);
				}

				@Override
				public void onStop() {
				}
			});


			return true;
		}

		return false;
	}
	public static void handleFallyShield1(Player player) {

		int random = Utility.randomNumber(300);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13117, 1)) {
				player.getEquipment().remove(new Item(13117, 1));
				player.getEquipment().equip(new Item(13118), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Falador shield has been upgraded!")); }
			}
		}

	public static void handleFallyShield2(Player player) {

		int random = Utility.randomNumber(500);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13118, 1)) {
				player.getEquipment().remove(new Item(13118, 1));
				player.getEquipment().equip(new Item(13119), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Falador shield has been upgraded!")); }
		}
	}
	public static void handleFallyShield3(Player player) {

		int random = Utility.randomNumber(1500);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13119, 1)) {
				player.getEquipment().remove(new Item(13119, 1));
				player.getEquipment().equip(new Item(13120), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Falador shield has been upgraded!")); }
		}
	}
}
