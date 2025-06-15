package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * ENHANCED: Universal bypass system for Discord Bot privileges
 * Now includes lumbering tool support
 */
public class DiscordBotPrivileges
{

	/**
	 * Check if player is the Discord bot
	 */
	public static boolean isDiscordBot(Stoner stoner) {
		return stoner instanceof DiscordBotStoner ||
			(stoner.getUsername() != null &&
				stoner.getUsername().equals(DiscordBotDefaults.DEFAULT_USERNAME));
	}

	/**
	 * BYPASS: Inventory space check - Discord bot always has "space" due to auto-banking
	 */
	public static boolean hasInventorySpace(Stoner stoner, int requiredSlots) {
		if (isDiscordBot(stoner)) {
			// Auto-bank if needed and return true
			if (stoner instanceof DiscordBotStoner) {
				DiscordBotStoner bot = (DiscordBotStoner) stoner;
				if (bot.getBox().getFreeSlots() < requiredSlots) {
					bot.performAutoBanking();
				}
			}
			return true; // Discord bot always has "space"
		}

		// Normal players use regular inventory check
		return stoner.getBox().getFreeSlots() >= requiredSlots;
	}

	/**
	 * ENHANCED: Tool requirement check - Discord bot doesn't need tools (supports both quarrying and lumbering)
	 */
	public static boolean hasRequiredTool(Stoner stoner, String toolType) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing " + toolType + " requirement");
			return true; // Bot always has "virtual" tools
		}

		// For normal players, implement actual tool checks here
		// This would check their equipment for the required tool
		return false; // Implement actual tool checking logic
	}

	/**
	 * ENHANCED: Quarrying tool check - Discord bot has virtual pickaxe
	 */
	public static boolean hasQuarryingTool(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot using virtual pickaxe");
			return true; // Bot always has virtual pickaxe
		}

		// Normal players check for actual pickaxe
		// Check equipment for pickaxe IDs
		return checkForPickaxeInEquipment(stoner);
	}

	/**
	 * NEW: Lumbering tool check - Discord bot has virtual axe
	 */
	public static boolean hasLumberingTool(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot using virtual axe");
			return true; // Bot always has virtual axe
		}

		// Normal players check for actual axe
		// Check equipment for axe IDs (like the AXES array in LumberingTask)
		return checkForAxeInEquipment(stoner);
	}

	/**
	 * Helper method to check for pickaxe in equipment
	 */
	private static boolean checkForPickaxeInEquipment(Stoner stoner) {
		// Define pickaxe IDs (similar to quarrying system)
		int[] pickaxeIds = {6575}; // Tool ring or other pickaxe IDs

		for (int pickaxeId : pickaxeIds) {
			if (stoner.getEquipment().contains(pickaxeId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method to check for axe in equipment
	 */
	private static boolean checkForAxeInEquipment(Stoner stoner) {
		// Define axe IDs (from LumberingTask.AXES)
		int[] axeIds = {6575}; // Tool ring acts as axe too

		for (int axeId : axeIds) {
			if (stoner.getEquipment().contains(axeId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * BYPASS: Skill level requirement check - Discord bot can do anything
	 */
	public static boolean hasSkillLevel(Stoner stoner, int skillId, int requiredLevel) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing skill level requirement: " + requiredLevel + " for skill " + skillId);
			return true; // Bot can always do any activity regardless of level
		}

		// Normal players use actual skill check
		return stoner.getGrades()[skillId] >= requiredLevel;
	}

	/**
	 * ENHANCED: Specific lumbering level check
	 */
	public static boolean hasLumberingLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing lumbering level requirement: " + requiredLevel);
			return true; // Bot can chop any tree
		}

		// Normal players check lumbering skill (ID 8)
		return stoner.getProfession().getGrades()[8] >= requiredLevel;
	}

	/**
	 * ENHANCED: Specific quarrying level check
	 */
	public static boolean hasQuarryingLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing quarrying level requirement: " + requiredLevel);
			return true; // Bot can mine any rock
		}

		// Normal players check quarrying skill (ID 14)
		return stoner.getProfession().getGrades()[14] >= requiredLevel;
	}

	/**
	 * Discord bot auto-combat privilege
	 */
	public static boolean hasAutoCombatEnabled(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return true; // Bot always has auto-combat enabled
		}

		// Normal players use their toggle
		return false; // Check actual player auto-combat setting
	}

	/**
	 * BYPASS: Quest requirement check - Discord bot doesn't need quests
	 */
	public static boolean hasQuestRequirement(Stoner stoner, String questName) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing quest requirement: " + questName);
			return true; // Bot doesn't need to complete quests
		}

		// Normal players use actual quest check
		// return stoner.getQuestManager().isQuestComplete(questName);
		return false; // Implement actual quest checking logic
	}

	/**
	 * BYPASS: Item requirement check - Discord bot doesn't need specific items
	 */
	public static boolean hasRequiredItem(Stoner stoner, int itemId, int amount) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing item requirement: " + itemId + " x" + amount);
			return true; // Bot always has "virtual" required items
		}

		// Normal players use actual item check
		// Fixed: Use Box's totalAmount() method and Equipment's contains() method
		boolean hasInInventory = stoner.getBox().totalAmount(itemId) >= amount;
		boolean hasInEquipment = stoner.getEquipment().contains(itemId); // This only checks if item exists, not amount

		// If checking equipment and amount > 1, need to manually check
		if (!hasInInventory && hasInEquipment && amount > 1) {
			// For equipment, typically you only have 1 of each item equipped
			// so if amount > 1, this would fail for normal players
			hasInEquipment = false;
		}

		return hasInInventory || (hasInEquipment && amount <= 1);
	}

	/**
	 * BYPASS: Combat level requirement check
	 */
	public static boolean hasCombatLevel(Stoner stoner, int requiredLevel) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing combat level requirement: " + requiredLevel);
			return true; // Bot can access any combat-level restricted content
		}

		// Normal players use actual combat level check
		return stoner.getProfession().calcCombatGrade() >= requiredLevel;
	}

	/**
	 * BYPASS: Location access check - Discord bot can go anywhere
	 */
	public static boolean canAccessLocation(Stoner stoner, String locationName) {
		if (isDiscordBot(stoner)) {
			//System.out.println("Discord bot bypassing location access requirement: " + locationName);
			return true; // Bot can teleport/access any location
		}

		// Normal players use actual access checks
		return true; // Implement actual location access logic
	}

	/**
	 * ENHANCED: Auto-banking for Discord bot during any activity
	 */
	public static void handleInventoryManagement(Stoner stoner) {
		if (isDiscordBot(stoner) && stoner instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) stoner;

			// Auto-bank when inventory gets moderately full
			if (bot.getBox().getTakenSlots() >= 20) {
				bot.performAutoBanking();
			}
		}
	}

	/**
	 * ENHANCED: Resource gathering bypass - items go directly to bank
	 */
	public static void addItemWithBanking(Stoner stoner, int itemId, int amount) {
		if (isDiscordBot(stoner) && stoner instanceof DiscordBotStoner) {
			DiscordBotStoner bot = (DiscordBotStoner) stoner;

			// Try to add to inventory first
			if (bot.getBox().getFreeSlots() >= amount) {
				bot.getBox().add(itemId, amount);
			} else {
				// Auto-bank existing items and try again
				bot.performAutoBanking();

				if (bot.getBox().getFreeSlots() >= amount) {
					bot.getBox().add(itemId, amount);
				} else {
					// Send directly to bank if inventory still can't fit
					bot.addItemToBank(itemId, amount);
				}
			}
		} else {
			// Normal players add to inventory normally
			stoner.getBox().add(itemId, amount);
		}
	}

	/**
	 * ENHANCED: Movement cost bypass - Discord bot doesn't use energy/stamina
	 */
	public static boolean hasMovementEnergy(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return true; // Bot never gets tired
		}

		// Normal players use actual energy/stamina check
		// return stoner.getMovement().hasEnergy();
		return true; // Implement actual energy checking logic
	}

	/**
	 * ENHANCED: Food requirement bypass - Discord bot doesn't need food
	 */
	public static boolean needsFood(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return false; // Bot never needs food
		}

		// Normal players need food when health is low
		return stoner.getGrades()[3] < stoner.getMaxGrades()[3] * 0.5; // Below 50% health
	}

	/**
	 * ENHANCED: Death bypass - Discord bot doesn't die
	 */
	public static boolean canDie(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return false; // Bot is immortal
		}

		return true; // Normal players can die
	}

	/**
	 * ENHANCED: Trade restriction bypass - Discord bot can trade anything
	 */
	public static boolean canTrade(Stoner stoner, int itemId) {
		if (isDiscordBot(stoner)) {
			return true; // Bot can trade any item
		}

		// Normal players use actual trade restrictions
		// return !ItemManager.isUntradeable(itemId);
		return true; // Implement actual trade checking logic
	}

	/**
	 * Get privilege level description
	 */
	public static String getPrivilegeLevel(Stoner stoner) {
		if (isDiscordBot(stoner)) {
			return "DISCORD BOT - Full Privileges (No restrictions, auto-banking, virtual tools, can perform any skill)";
		} else {
			return "REGULAR PLAYER - Standard restrictions apply";
		}
	}

	/**
	 * ENHANCED: Apply all relevant bypasses for a given activity (now supports more skills)
	 */
	public static boolean canPerformActivity(Stoner stoner, String activityName,
											 int[] requiredSkills, int[] requiredLevels,
											 int[] requiredItems, int[] itemAmounts) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot performing " + activityName + " with full privileges");

			// Auto-manage inventory
			handleInventoryManagement(stoner);

			return true; // Bot can do anything
		}

		// Normal players need to meet all requirements
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

	/**
	 * NEW: Comprehensive skill activity check
	 */
	public static boolean canPerformSkillActivity(Stoner stoner, String skillName, int requiredLevel, String toolType) {
		if (isDiscordBot(stoner)) {
			System.out.println("Discord bot performing " + skillName + " activity with virtual " + toolType);
			handleInventoryManagement(stoner);
			return true;
		}

		// Check skill level and tool for normal players
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
				// Generic skill check
				hasLevel = hasSkillLevel(stoner, getSkillIdByName(skillName), requiredLevel);
				hasTool = hasRequiredTool(stoner, toolType);
				break;
		}

		return hasLevel && hasTool;
	}

	/**
	 * Helper method to get skill ID by name
	 */
	private static int getSkillIdByName(String skillName) {
		switch (skillName.toLowerCase()) {
			case "lumbering": return 8;
			case "quarrying": return 14;
			// Add other skills as needed
			default: return 0;
		}
	}
}