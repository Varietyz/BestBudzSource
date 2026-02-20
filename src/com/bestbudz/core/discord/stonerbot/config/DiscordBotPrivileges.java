package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DiscordBotPrivileges
{

	public static boolean isDiscordBot(Stoner stoner) {
		return stoner instanceof DiscordBotStoner ||
			(stoner.getUsername() != null &&
				stoner.getUsername().equals(DiscordBotDefaults.DEFAULT_USERNAME));
	}

	public static boolean hasInventorySpace(Stoner stoner, int requiredSlots) {
		if (isDiscordBot(stoner)) {

			if (stoner instanceof DiscordBotStoner) {
				DiscordBotStoner bot = (DiscordBotStoner) stoner;
				if (bot.getBox().getFreeSlots() < requiredSlots) {
					bot.performAutoBanking();
				}
			}
			return true;
		}

		return stoner.getBox().getFreeSlots() >= requiredSlots;
	}

	public static boolean hasRequiredTool(Stoner stoner, String toolType) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return false;
	}

	public static boolean hasQuarryingTool(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot using virtual pickaxe");
			return true;
		}

		return checkForPickaxeInEquipment(stoner);
	}

	public static boolean hasLumberingTool(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot using virtual axe");
			return true;
		}

		return checkForAxeInEquipment(stoner);
	}

	private static boolean checkForPickaxeInEquipment(Stoner stoner) {

		int[] pickaxeIds = {6575};

		for (int pickaxeId : pickaxeIds) {
			if (stoner.getEquipment().contains(pickaxeId)) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkForAxeInEquipment(Stoner stoner) {

		int[] axeIds = {6575};

		for (int axeId : axeIds) {
			if (stoner.getEquipment().contains(axeId)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSkillLevel(Stoner stoner, int skillId, int requiredLevel) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return stoner.getGrades()[skillId] >= requiredLevel;
	}

	public static boolean hasLumberingLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return stoner.getProfession().getGrades()[8] >= requiredLevel;
	}

	public static boolean hasQuarryingLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return stoner.getProfession().getGrades()[14] >= requiredLevel;
	}

	public static boolean hasAutoCombatEnabled(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return true;
		}

		return false;
	}

	public static boolean hasQuestRequirement(Stoner stoner, String questName) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return false;
	}

	public static boolean hasRequiredItem(Stoner stoner, int itemId, int amount) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		boolean hasInInventory = stoner.getBox().totalAmount(itemId) >= amount;
		boolean hasInEquipment = stoner.getEquipment().contains(itemId);

		if (!hasInInventory && hasInEquipment && amount > 1) {

			hasInEquipment = false;
		}

		return hasInInventory || (hasInEquipment && amount <= 1);
	}

	public static boolean hasCombatLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return stoner.getProfession().calcCombatGrade() >= requiredLevel;
	}

	public static boolean canAccessLocation(Stoner stoner, String locationName) {
		if (isDiscordBot(stoner)) {

			return true;
		}

		return true;
	}

	public static void handleInventoryManagement(Stoner stoner) {
		if (isDiscordBot(stoner) && stoner instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) stoner;

			if (bot.getBox().getTakenSlots() >= 20) {
				bot.performAutoBanking();
			}
		}
	}

	public static void addItemWithBanking(Stoner stoner, int itemId, int amount) {
		if (isDiscordBot(stoner) && stoner instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) stoner;

			if (bot.getBox().getFreeSlots() >= amount) {
				bot.getBox().add(itemId, amount);
			} else {

				bot.performAutoBanking();

				if (bot.getBox().getFreeSlots() >= amount) {
					bot.getBox().add(itemId, amount);
				} else {

					bot.addItemToBank(itemId, amount);
				}
			}
		} else {

			stoner.getBox().add(itemId, amount);
		}
	}

	public static boolean hasMovementEnergy(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return true;
		}

		return true;
	}

	public static boolean needsFood(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return false;
		}

		return stoner.getGrades()[3] < stoner.getMaxGrades()[3] * 0.5;
	}

	public static boolean canDie(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return false;
		}

		return true;
	}

	public static boolean canTrade(Stoner stoner, int itemId) {
		if (isDiscordBot(stoner)) {
			return true;
		}

		return true;
	}

	public static String getPrivilegeLevel(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return "DISCORD BOT - Full Privileges (No restrictions, auto-banking, virtual tools, can perform any skill)";
		} else {
			return "REGULAR PLAYER - Standard restrictions apply";
		}
	}

	public static boolean canPerformActivity(Stoner stoner, String activityName,
											 int[] requiredSkills, int[] requiredLevels,
											 int[] requiredItems, int[] itemAmounts) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot performing " + activityName + " with full privileges");

			handleInventoryManagement(stoner);

			return true;
		}

		for (int i = 0; i < requiredSkills.length; i++) {
			if (!hasSkillLevel(stoner, requiredSkills[i], requiredLevels[i])) {
				return false;
			}
		}

		for (int i = 0; i < requiredItems.length; i++) {
			if (!hasRequiredItem(stoner, requiredItems[i], itemAmounts[i])) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPerformSkillActivity(Stoner stoner, String skillName, int requiredLevel, String toolType) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot performing " + skillName + " activity with virtual " + toolType);
			handleInventoryManagement(stoner);
			return true;
		}

		boolean hasLevel = false;
		boolean hasTool = false;

		switch (skillName.toLowerCase()) {
			case "lumbering":
				hasLevel = hasLumberingLevel(stoner, requiredLevel);
				hasTool = hasLumberingTool(stoner);
				break;
			case "quarrying":
				hasLevel = hasQuarryingLevel(stoner, requiredLevel);
				hasTool = hasQuarryingTool(stoner);
				break;
			default:

				hasLevel = hasSkillLevel(stoner, getSkillIdByName(skillName), requiredLevel);
				hasTool = hasRequiredTool(stoner, toolType);
				break;
		}

		return hasLevel && hasTool;
	}

	private static int getSkillIdByName(String skillName) {
		switch (skillName.toLowerCase()) {
			case "lumbering": return 8;
			case "quarrying": return 14;

			default: return 0;
		}
	}
}
