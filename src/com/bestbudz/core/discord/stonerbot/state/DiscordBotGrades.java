package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfession;

import java.util.logging.Logger;

/**
 * Handles Discord bot grades/levels and profession management
 * FIXED: Works with actual database format and save system
 */
public class DiscordBotGrades {

	private static final Logger logger = Logger.getLogger(DiscordBotGrades.class.getSimpleName());

	private final Stoner discordBot;

	public DiscordBotGrades(Stoner discordBot) {
		this.discordBot = discordBot;
	}

	/**
	 * Setup default grades for the bot - ALL 21 PROFESSIONS
	 * This directly modifies the arrays that get saved to the database
	 */
	public void setupDefaultGrades() {
		try {
			logger.info("Setting up Discord bot default grades for all 21 professions...");

			// Ensure arrays are properly sized
			ensureArraysProperlyInitialized();

			// Set ALL 21 professions
			for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
				long targetGrade = (i == 3) ? 3 : 1; // Life skill = 3, others = 1

				// Set both current and max grades
				discordBot.getGrades()[i] = targetGrade;
				discordBot.getMaxGrades()[i] = targetGrade;

				// Calculate and set the correct experience for this grade
				double requiredExp = discordBot.getProfession().getXPForGrade(i, (int)targetGrade);
				discordBot.getProfession().getExperience()[i] = requiredExp;

				String professionName = (i < Professions.PROFESSION_NAMES.length) ?
					Professions.PROFESSION_NAMES[i] : "Unknown";
				logger.fine("Set profession " + i + " (" + professionName + ") to grade " + targetGrade + " with exp " + requiredExp);
			}

			// Set profession advances - ensure array is properly sized
			if (discordBot.getProfessionAdvances() == null || discordBot.getProfessionAdvances().length != Professions.PROFESSION_COUNT) {
				discordBot.setProfessionAdvances(new int[Professions.PROFESSION_COUNT]);
			}
			for (int i = 0; i < discordBot.getProfessionAdvances().length; i++) {
				discordBot.getProfessionAdvances()[i] = 0;
			}

			// Set total advances and advance points to 0
			discordBot.setTotalAdvances(0);
			discordBot.setAdvancePoints(0);

			// Update the profession system to reflect the new grades
			discordBot.getProfession().updateTotalGrade();

			// Update all profession displays
			updateAllProfessions();

			logger.info("Discord bot grades set successfully for all 21 professions - Life=3, all others=1, Combat Level=" +
				discordBot.getProfession().calcCombatGrade());

		} catch (Exception e) {
			logger.warning("Error setting up Discord bot grades: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Ensure all arrays are properly initialized with correct sizes
	 */
	private void ensureArraysProperlyInitialized() {
		// Check grades array
		if (discordBot.getGrades() == null || discordBot.getGrades().length != Professions.PROFESSION_COUNT) {
			logger.warning("Discord bot grades array wrong size, reinitializing");
			discordBot.setGrades(new long[Professions.PROFESSION_COUNT]);
		}

		// Check max grades array
		if (discordBot.getMaxGrades() == null || discordBot.getMaxGrades().length != Professions.PROFESSION_COUNT) {
			logger.warning("Discord bot max grades array wrong size, reinitializing");
			discordBot.setMaxGrades(new long[Professions.PROFESSION_COUNT]);
		}

		// Check experience array
		if (discordBot.getProfession().getExperience() == null || discordBot.getProfession().getExperience().length != Professions.PROFESSION_COUNT) {
			logger.warning("Discord bot experience array wrong size, reinitializing");
			discordBot.getProfession().setExperience(new double[Professions.PROFESSION_COUNT]);
		}

		// Check profession advances array
		if (discordBot.getProfessionAdvances() == null || discordBot.getProfessionAdvances().length != Professions.PROFESSION_COUNT) {
			logger.warning("Discord bot profession advances array wrong size, reinitializing");
			discordBot.setProfessionAdvances(new int[Professions.PROFESSION_COUNT]);
		}

		logger.fine("All arrays properly sized for " + Professions.PROFESSION_COUNT + " professions");
	}

	/**
	 * Update all profession displays for the bot
	 */
	public void updateAllProfessions() {
		try {
			for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
				updateProfession(i);
			}
		} catch (Exception e) {
			logger.warning("Error updating Discord bot professions: " + e.getMessage());
		}
	}

	/**
	 * Update a specific profession display
	 */
	public void updateProfession(int professionId) {
		try {
			if (professionId >= 0 && professionId < Professions.PROFESSION_COUNT &&
				professionId < discordBot.getGrades().length &&
				professionId < discordBot.getProfession().getExperience().length) {

				discordBot.send(new SendProfession(
					professionId,
					(int)discordBot.getGrades()[professionId],
					(int)discordBot.getProfession().getExperience()[professionId]
				));
			}
		} catch (Exception e) {
			logger.warning("Error updating Discord bot profession " + professionId + ": " + e.getMessage());
		}
	}

	/**
	 * Get current combat level calculation
	 */
	public int getCombatLevel() {
		return discordBot.getProfession().calcCombatGrade();
	}

	/**
	 * Get a summary of the bot's current grades
	 */
	public String getGradesSummary() {
		StringBuilder summary = new StringBuilder();
		summary.append("Discord Bot Grades (All 21 Professions):\n");

		// Show all professions with their actual values
		for (int i = 0; i < Math.min(Professions.PROFESSION_COUNT, discordBot.getGrades().length); i++) {
			String professionName = (i < Professions.PROFESSION_NAMES.length) ?
				Professions.PROFESSION_NAMES[i] : "Unknown";

			long grade = discordBot.getGrades()[i];
			double exp = (i < discordBot.getProfession().getExperience().length) ?
				discordBot.getProfession().getExperience()[i] : 0;

			summary.append(professionName)
				.append(": ")
				.append(grade)
				.append(" (XP: ").append((int)exp).append(")")
				.append("\n");
		}

		summary.append("Combat Level: ").append(getCombatLevel()).append("\n");
		summary.append("Total Experience: ").append(discordBot.getProfession().getTotalExperience()).append("\n");
		summary.append("Total Advances: ").append(discordBot.getTotalAdvances());

		return summary.toString();
	}

	/**
	 * Restore all grades to max (used after combat)
	 */
	public void restoreGrades() {
		try {
			for (int i = 0; i < Math.min(Professions.PROFESSION_COUNT, discordBot.getGrades().length); i++) {
				if (i < discordBot.getMaxGrades().length) {
					discordBot.getGrades()[i] = discordBot.getMaxGrades()[i];
					updateProfession(i);
				}
			}
		} catch (Exception e) {
			logger.warning("Error restoring Discord bot grades: " + e.getMessage());
		}
	}

	/**
	 * Safely add experience to a profession (for when bot does activities)
	 */
	public void addExperience(int professionId, double experience) {
		try {
			if (professionId >= 0 && professionId < Professions.PROFESSION_COUNT) {
				double addedExp = discordBot.getProfession().addExperience(professionId, experience);

				String professionName = (professionId < Professions.PROFESSION_NAMES.length) ?
					Professions.PROFESSION_NAMES[professionId] : "Unknown";
				logger.fine("Added " + addedExp + " experience to profession " + professionId + " (" + professionName + ")");

				// Update the profession display
				updateProfession(professionId);

				// Trigger save since bot gained experience
				if (discordBot instanceof DiscordBotStoner) {
					((DiscordBotStoner) discordBot).getBotPersistence().onBotGradesChange();
				}
			}
		} catch (Exception e) {
			logger.warning("Error adding experience to Discord bot profession " + professionId + ": " + e.getMessage());
		}
	}

	/**
	 * Check if grades are at initial values (for debugging)
	 */
	public boolean areGradesAtInitialValues() {
		try {
			ensureArraysProperlyInitialized();

			for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
				long expectedGrade = (i == 3) ? 3 : 1; // Life=3, others=1
				if (i >= discordBot.getGrades().length || i >= discordBot.getMaxGrades().length) {
					return false;
				}
				if (discordBot.getGrades()[i] != expectedGrade || discordBot.getMaxGrades()[i] != expectedGrade) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			logger.warning("Error checking initial grades: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Validate that all arrays are consistent and properly sized
	 */
	public boolean validateArrayConsistency() {
		try {
			boolean isValid = true;
			StringBuilder issues = new StringBuilder();

			if (discordBot.getGrades() == null) {
				issues.append("Grades array is null; ");
				isValid = false;
			} else if (discordBot.getGrades().length != Professions.PROFESSION_COUNT) {
				issues.append("Grades array length: ").append(discordBot.getGrades().length).append(" != ").append(Professions.PROFESSION_COUNT).append("; ");
				isValid = false;
			}

			if (discordBot.getMaxGrades() == null) {
				issues.append("Max grades array is null; ");
				isValid = false;
			} else if (discordBot.getMaxGrades().length != Professions.PROFESSION_COUNT) {
				issues.append("Max grades array length: ").append(discordBot.getMaxGrades().length).append(" != ").append(Professions.PROFESSION_COUNT).append("; ");
				isValid = false;
			}

			if (discordBot.getProfession().getExperience() == null) {
				issues.append("Experience array is null; ");
				isValid = false;
			} else if (discordBot.getProfession().getExperience().length != Professions.PROFESSION_COUNT) {
				issues.append("Experience array length: ").append(discordBot.getProfession().getExperience().length).append(" != ").append(Professions.PROFESSION_COUNT).append("; ");
				isValid = false;
			}

			if (!isValid) {
				logger.warning("Array consistency issues: " + issues.toString());
			}

			return isValid;
		} catch (Exception e) {
			logger.warning("Error validating array consistency: " + e.getMessage());
			return false;
		}
	}
}