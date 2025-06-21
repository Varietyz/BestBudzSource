package com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.*;
import com.bestbudz.rs2.content.achievements.AchievementButtons;
import com.bestbudz.rs2.content.combat.formula.MageFormulas;
import com.bestbudz.rs2.content.combat.formula.MeleeFormulas;
import com.bestbudz.rs2.content.combat.formula.RangeFormulas;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.*;
import com.bestbudz.rs2.content.membership.CreditHandler;
import com.bestbudz.rs2.content.minigames.duelarena.DuelingConstants;
import com.bestbudz.rs2.content.profession.ProfessionGoal;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.handiness.HideTanning;
import com.bestbudz.rs2.content.profession.mage.Autocast;
import com.bestbudz.rs2.content.profession.mage.MageProfession.SpellBookTypes;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryFinishedPotionTask;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryUnfinishedPotionTask;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.ReportHandler.ReportData;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import static com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons.ButtonAssignment.*;
import com.bestbudz.rs2.entity.stoner.net.out.impl.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

public class ClickButtonPacket extends IncomingPacket {


	static {
		initializeTeleportLocations();
		initializeShopActions();
		initializeLeaderboardTypes();
		initializeButtonHandlers();

		// CRITICAL DEBUG: Check for shop buttons in wrong map
		System.out.println("DEBUG: Checking for shop buttons in BUTTON_HANDLERS (should be NONE):");
		for (Integer shopButtonId : SHOP_ACTIONS.keySet()) {
			if (BUTTON_HANDLERS.containsKey(shopButtonId)) {
				System.out.println("ERROR: Shop button " + shopButtonId + " found in BUTTON_HANDLERS - THIS IS THE BUG!");
			}
		}
		System.out.println("DEBUG: BUTTON_HANDLERS size: " + BUTTON_HANDLERS.size());
		System.out.println("DEBUG: SHOP_ACTIONS size: " + SHOP_ACTIONS.size());
	}

	static {
		initializeTeleportLocations();
		initializeShopActions();
		initializeLeaderboardTypes();
		initializeButtonHandlers();
	}

	// Helper methods for button actions
	public static void handleStaffCommands(Stoner stoner) {
		if (!StonerConstants.isStaff(stoner)) return;

		String accessibility = "";
		if (StonerConstants.isModerator(stoner)) {
			accessibility = "You have access to a few commands!";
		} else if (StonerConstants.isAdministrator(stoner)) {
			accessibility = "You have access to most commands!";
		} else if (StonerConstants.isOwner(stoner)) {
			accessibility = "You have access to all commands!";
		}

		stoner.send(new SendString(accessibility, 49704));
		stoner.send(new SendString("</col>Rank: " + stoner.deterquarryIcon(stoner) + " " + stoner.deterquarryRank(stoner), 49705));
		stoner.send(new SendSidebarInterface(2, 49700));
		stoner.send(new SendOpenTab(2));
		stoner.send(new SendMessage("<col=25236>Consequences upon abuse."));
	}

	public static void handleQuestTab(Stoner stoner) {
		if (!StonerConstants.isStaff(stoner)) return;

		InterfaceHandler.writeText(new QuestTab(stoner));
		stoner.send(new SendSidebarInterface(2, 29400));
		stoner.send(new SendOpenTab(2));
	}

	public static void cancelReport(Stoner stoner) {
		stoner.send(new SendRemoveInterfaces());
		stoner.reportClicked = 0;
		stoner.reportName = "";
		stoner.send(new SendInterface(41750));
	}

	public static void handleHomeTeleport(Stoner stoner) {
		if (stoner.getMage().isTeleporting() || stoner.inJailed()) {
			return;
		}

		stoner.getMage().teleport(
			StonerConstants.HOME.getX(),
			StonerConstants.HOME.getY(),
			StonerConstants.HOME.getZ(),
			TeleportTypes.SPELL_BOOK
		);
		stoner.send(new SendMessage("Home weed home. " + stoner.deterquarryIcon(stoner)));
	}

	public static void toggleRunning(Stoner stoner) {
		stoner.getRunEnergy().setRunning(!stoner.getRunEnergy().isRunning());
		stoner.getClient().queueOutgoingPacket(new SendConfig(173, stoner.getRunEnergy().isRunning() ? 1 : 0));
	}

	public static void handleLogout(Stoner stoner) {
		if (stoner.getCombat().inCombat()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("Nope, we dont flight, we fight!"));
		} else {
			if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
				stoner.logout(false);
			}
		}
	}

	public static void handlePaymentOption(Stoner stoner) {
		enum PaymentMethod {
			CASH, DEBIT
		}

		PaymentMethod currentMethod = stoner.isPouchPayment() ? PaymentMethod.DEBIT : PaymentMethod.CASH;

		switch (currentMethod) {
			case CASH:
				// Currently cash, switch to debit
				stoner.setPouchPayment(true);
				stoner.send(new SendMessage("You will now be paying with your Debit Card."));
				break;

			case DEBIT:
				// Currently debit, switch to cash
				stoner.setPouchPayment(false);
				stoner.send(new SendMessage("You will now be paying with your Cash."));
				break;
		}
	}

	public static void handleWeaponUnload(Stoner stoner) {
		if (stoner.getAttributes().getInt("ASK_KEY") == 0) {
			ToxicBlowpipe.unload(stoner);
		} else if (stoner.getAttributes().getInt("ASK_KEY") == 1) {
			TridentOfTheSeas.unload(stoner);
			stoner.getBox().remove(11908, 1);
			stoner.getGroundItems().drop(new Item(11908), stoner.getLocation());
		}
		stoner.send(new SendRemoveInterfaces());
	}

	public static void displayCombatFormulas(Stoner stoner) {
		stoner.send(new SendString("</col>Melee Max Hit: @gre@" + MeleeFormulas.calculateBaseDamage(stoner), 15116));
		stoner.send(new SendString("</col>Range Max Hit: @gre@" + RangeFormulas.getSagittariusMaxHit(stoner) + ".0", 15117));
		stoner.send(new SendString("</col>Mage Max Hit: @gre@" + MageFormulas.mageMaxHit(stoner) + ".0", 15118));
		stoner.send(new SendInterface(15106));
	}

	public static void openSettingsInterface(Stoner stoner) {
		stoner.send(new SendMessage(":updateSettings:"));
		stoner.send(new SendSidebarInterface(11, 28400));
		stoner.send(new SendOpenTab(11));
	}

	public static void saveSettings(Stoner stoner) {
		stoner.send(new SendMessage(":saveSettings:"));
		stoner.send(new SendSidebarInterface(11, 904));
		stoner.send(new SendOpenTab(11));
		stoner.send(new SendMessage("@gre@Your settings have been saved!"));
	}

	public static void resetSettings(Stoner stoner) {
		stoner.send(new SendMessage(":defaultSettings:"));
		stoner.send(new SendMessage("@yel@Your settings have been reset!"));
	}

	public static void openColorChooser(Stoner stoner) {
		stoner.send(new SendInterface(37500));
		stoner.send(new SendString("Color chosen: @or2@-", 37506));
	}

	public static void toggleAdvanceColors(Stoner stoner) {
		if (stoner.getDelay().elapsed() < 3_000) {
			stoner.send(new SendMessage("YO CHILL! Wait before doing this again!"));
			return;
		}

		if (stoner.isAdvanceColors()) {
			stoner.setAdvanceColors(false);
			stoner.send(new SendMessage(":advanceColorsFalse:"));
			stoner.getProfession().resetColors();
			stoner.send(new SendMessage("Advance colors will not display in profession tab."));
		} else {
			stoner.setAdvanceColors(true);
			stoner.send(new SendMessage(":advanceColorsTrue:"));
			stoner.getProfession().resetColors();
			stoner.send(new SendMessage("Advance colors will now display in profession tab."));
		}
		stoner.getDelay().reset();
	}

	public static void setProfilePrivacy(Stoner stoner, boolean hidden) {
		int config = hidden ? 1 : 2;
		String message = hidden ? "You have hidden yourself in some bushes." : "You jumped out of the bushes.";

		stoner.send(new SendConfig(1032, config));
		stoner.setProfilePrivacy(hidden);
		stoner.send(new SendMessage(message));
	}

	public static void handleExperienceLock(Stoner stoner) {
		stoner.start(new OptionDialogue(
			"Lock experience",
			p -> {
				stoner.getProfession().setExpLock(true);
				stoner.send(new SendMessage("You have @blu@locked</col> your experience."));
				stoner.send(new SendRemoveInterfaces());
			},
			"Unlock experience",
			p -> {
				stoner.getProfession().setExpLock(false);
				stoner.send(new SendMessage("You have @blu@unlocked</col> your experience."));
				stoner.send(new SendRemoveInterfaces());
			}
		));
	}

	public static void handleSearchOptions(Stoner stoner) {
		stoner.start(new OptionDialogue(
			"Search name",
			p -> {
				stoner.setEnterXInterfaceId(55777);
				stoner.getClient().queueOutgoingPacket(new SendEnterString());
			},
			"Search item",
			p -> {
				stoner.setEnterXInterfaceId(55778);
				stoner.getClient().queueOutgoingPacket(new SendEnterString());
			}
		));
	}

	public static void openAchievementTab(Stoner stoner) {
		InterfaceHandler.writeText(new AchievementTab(stoner));
		stoner.send(new SendSidebarInterface(2, 31000));
	}

	public static void openBestBudzTab(Stoner stoner) {
		InterfaceHandler.writeText(new QuestTab(stoner));
		stoner.send(new SendSidebarInterface(2, 29400));
	}

	public static void openPointsInterface(Stoner stoner) {
		stoner.send(new SendString("@gre@" + stoner.getUsername() + "'s tracked points.", 8144));
		InterfaceHandler.writeText(new PointsInterface(stoner));
		stoner.send(new SendInterface(8134));
	}

	public static void openPvmTracker(Stoner stoner) {
		int linePosition = 8145;
		HashMap<String, Integer> map = stoner.getProperties().getPropertyValues("MOB");

		List<String> alphabetical = new ArrayList<>(map.keySet());
		alphabetical.sort(String.CASE_INSENSITIVE_ORDER);

		for (String key : alphabetical) {
			String line = Utility.formatStonerName(key.toLowerCase().replaceAll("_", " ")) + ": @gre@" + map.get(key);
			stoner.send(new SendString("@gre@PvM Tracker | " + alphabetical.size(), 8144));
			stoner.send(new SendString("</col>" + line, linePosition++));
		}

		map = stoner.getProperties().getPropertyValues("BARROWS");
		for (String key : map.keySet()) {
			String line = Utility.formatStonerName(key.toLowerCase().replaceAll("_", " ")) + ": @gre@" + map.get(key);
			stoner.send(new SendString("</col>" + line, linePosition++));
		}

		while (linePosition < 8193) {
			stoner.send(new SendString("", linePosition++));
		}

		stoner.send(new SendInterface(8134));
	}

	public static void focusedMageBookSwap(Stoner stoner) {

				stoner.getMage().setSpellBookType(SpellBookTypes.MODERN);
				stoner.getMage().setMageBook(1151);
				stoner.getUpdateFlags().sendAnimation(new Animation(6299));
				stoner.getUpdateFlags().sendGraphic(new Graphic(1062));
				stoner.send(new SendMessage("You are now casting focused mage."));
				stoner.send(new SendRemoveInterfaces());

	}

	public static void aoeMageBookSwap(Stoner stoner) {
				stoner.getMage().setSpellBookType(SpellBookTypes.ANCIENT);
				stoner.getMage().setMageBook(12855);
				stoner.getUpdateFlags().sendAnimation(new Animation(6299));
				stoner.getUpdateFlags().sendGraphic(new Graphic(1062));
				stoner.send(new SendMessage("You are now a AoE mage."));
				stoner.send(new SendRemoveInterfaces());
	}

	public static void openAdvanceInterface(Stoner stoner) {
		Advance.update(stoner);
		stoner.send(new SendInterface(51000));
	}

	public static void rechargeResonance(Stoner stoner) {
		if (stoner.getProfession().getGrades()[Professions.RESONANCE] < stoner.getMaxGrades()[Professions.RESONANCE]) {
			stoner.getProfession().setGrade(Professions.RESONANCE, stoner.getMaxGrades()[Professions.RESONANCE]);
			stoner.getClient().queueOutgoingPacket(new SendMessage("You recharge your resonance points."));
			stoner.getUpdateFlags().sendAnimation(new Animation(5864));
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("Your resonance is already full."));
		}
	}

	public static void handleSkulling(Stoner stoner) {
		stoner.send(new SendMessage("You received an overhead weedskull (visual only!."));
			stoner.getUpdateFlags().sendAnimation(new Animation(5315));
			stoner.getUpdateFlags().sendGraphic(new Graphic(1061));
	}

	public static void showOnlineStoners(Stoner stoner) {
		StonersOnline.showStoners(stoner, p -> true);
	}

	public static void updateAppearance(Stoner stoner) {
		stoner.setAppearanceUpdateRequired(true);
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
	}

	public static void handleSpecialAttackRestore(Stoner stoner) {
		if (stoner.getSpecialAssault().getAmount() != 100) {
			stoner.send(new SendMessage("You lack a bit of spec man."));
			return;
		}

		stoner.getUpdateFlags().sendAnimation(new Animation(1056));
		stoner.getUpdateFlags().sendGraphic(new Graphic(246));
		stoner.getSpecialAssault().deduct(100);
		stoner.getSpecialAssault().update();
		stoner.getSpecialAssault().setInitialized(false);

		// Update profession grades
		stoner.getGrades()[Professions.ASSAULT] = (short) (stoner.getMaxGrades()[Professions.ASSAULT] * 0.9);
		stoner.getGrades()[Professions.AEGIS] = (short) (stoner.getMaxGrades()[Professions.AEGIS] * 0.9);
		stoner.getGrades()[Professions.SAGITTARIUS] = (short) (stoner.getMaxGrades()[Professions.SAGITTARIUS] * 0.9);
		stoner.getGrades()[Professions.MAGE] = (short) (stoner.getMaxGrades()[Professions.MAGE] * 0.9);
		stoner.getGrades()[Professions.VIGOUR] = (short) (stoner.getMaxGrades()[Professions.VIGOUR] * 1.2);

		// Update profession displays
		stoner.getProfession().update(Professions.ASSAULT);
		stoner.getProfession().update(Professions.AEGIS);
		stoner.getProfession().update(Professions.SAGITTARIUS);
		stoner.getProfession().update(Professions.MAGE);
		stoner.getProfession().update(Professions.VIGOUR);

		stoner.getUpdateFlags().sendForceMessage("Raarrrrrgggggghhhhhhh!");
	}


	@Override
	public int getMaxDuplicates() {
		return 5;
	}

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
		int buttonId = parseButtonId(in);

		// Early exit conditions
		if (shouldIgnoreClick(stoner)) {
			return;
		}

		// Debug logging for developers
		logButtonClick(stoner, buttonId);

		// CRITICAL DEBUG: Check if this is a shop button
		if (SHOP_ACTIONS.containsKey(buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " is a SHOP BUTTON - should be handled!");
			}
		}

		// Handle special cases first
		if (handleSpecialCases(stoner, buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " handled by special cases");
			}
			return;
		}

		// Handle mapped button actions (from static map)
		ButtonHandler handler = BUTTON_HANDLERS.get(buttonId);
		if (handler != null) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " handled by static handler");
			}
			handler.handle(stoner);
			return;
		} else {
			if (StonerConstants.isOwner(stoner) && SHOP_ACTIONS.containsKey(buttonId)) {
				System.out.println("DEBUG: Shop button " + buttonId + " NOT found in BUTTON_HANDLERS - this is the problem!");
			}
		}

		// Handle shop interfaces FIRST (before complex cases)
		if (handleShopInterfaces(stoner, buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " handled by shop interfaces");
			}
			return;
		}

		// Handle teleport locations
		if (handleTeleportLocations(stoner, buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " handled by teleport locations");
			}
			return;
		}


		// Handle complex cases that require additional logic
		if (handleComplexCases(stoner, buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("DEBUG: Button " + buttonId + " handled by complex cases");
			}
			return;
		}

		// CRITICAL DEBUG: If we reach here with a shop button, something is wrong
		if (SHOP_ACTIONS.containsKey(buttonId)) {
			if (StonerConstants.isOwner(stoner)) {
				System.out.println("ERROR: Shop button " + buttonId + " reached default cases - THIS SHOULD NOT HAPPEN!");
			}
		}

		// Handle default cases (plugins, etc.) LAST
		if (StonerConstants.isOwner(stoner)) {
			if (buttonId == 50001){
				System.out.println("DEBUG: Button " + buttonId + " Blocked from falling through!");
				return;
			}
			System.out.println("DEBUG: Button " + buttonId + " falling through to default cases");
		}
		handleDefaultCases(stoner, buttonId);
	}

	private int parseButtonId(StreamBuffer.InBuffer in) {
		int buttonId = in.readShort();
		in.reset();
		return Utility.hexToInt(in.readBytes(2));
	}

	private boolean shouldIgnoreClick(Stoner stoner) {
		return stoner.isStunned() ||
			(stoner.isDead() && !stoner.getController().canClick()) ||
			StonerConstants.isSettingAppearance(stoner);
	}

	private void logButtonClick(Stoner stoner, int buttonId) {
		if (StonerConstants.isOwner(stoner)) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("@red@Developer - button: " + buttonId));
			System.out.println("button: " + buttonId);
		}
	}

	private boolean handleSpecialCases(Stoner stoner, int buttonId) {
		// Handle drop table search
		if (handleDropTableSearch(stoner, buttonId)) return true;

		// Handle report system
		if (handleReportSystem(stoner, buttonId)) return true;

		// Handle easter ring
		if (handleEasterRing(stoner, buttonId)) return true;

		// Handle various content systems
		if (LoyaltyShop.handleButtons(stoner, buttonId)) return true;
		if (TeleportHandler.selection(stoner, buttonId)) return true;
		if (ProfessionGoal.handle(stoner, buttonId)) return true;
		return Advance.handleActionButtons(stoner, buttonId);
	}

	private boolean handleDropTableSearch(Stoner stoner, int buttonId) {
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> searchButtons =
			(HashMap<Integer, Integer>) stoner.getAttributes().get("DROPTABLE_SEARCH");

		if (searchButtons != null && searchButtons.containsKey(buttonId)) {
			DropTable.displayNpc(stoner, searchButtons.get(buttonId));
			return true;
		}
		return false;
	}

	private boolean handleReportSystem(Stoner stoner, int buttonId) {
		if (ReportData.get(buttonId) != null) {
			stoner.reportClicked = buttonId;
			return true;
		}
		return false;
	}

	private boolean handleEasterRing(Stoner stoner, int buttonId) {
		if (stoner.getController().equals(EasterRing.EASTER_RING_CONTROLLER) && buttonId == 23132) {
			EasterRing.cancel(stoner);
			return true;
		}
		return false;
	}

	private boolean handleComplexCases(Stoner stoner, int buttonId) {
		// Handle volume controls
		if (handleVolumeControls(stoner, buttonId)) return true;

		// Handle brightness controls
		if (handleBrightnessControls(stoner, buttonId)) return true;

		// Handle special attacks
		return handleSpecialAttacks(stoner, buttonId);

		// Handle interface navigation with parameters
	}

	private boolean handleVolumeControls(Stoner stoner, int buttonId) {
		// Music volume controls
		if (buttonId >= 3163 && buttonId <= 3166) {
			byte volume = (byte) (3166 - buttonId);
			stoner.setMusicVolume(volume);
			stoner.getClient().queueOutgoingPacket(new SendConfig(168, volume));
			return true;
		}
		// Sound volume controls
		else if (buttonId >= 3174 && buttonId <= 3177) {
			byte volume = (byte) (3177 - buttonId);
			stoner.setSoundVolume(volume);
			stoner.getClient().queueOutgoingPacket(new SendConfig(169, volume));
			return true;
		}
		return false;
	}

	private boolean handleBrightnessControls(Stoner stoner, int buttonId) {
		byte brightness = switch (buttonId) {
			case 3138 -> 1;
			case 3140 -> 2;
			case 3142 -> 3;
			case 3144 -> 4;
			default -> -1;
		};

		if (brightness != -1) {
			stoner.setScreenBrightness(brightness);
			return true;
		}
		return false;
	}

	private boolean handleSpecialAttacks(Stoner stoner, int buttonId) {
		int[] specialAttackButtons = {29124, 29049, 29199, 29138, 48034, 155, 30108, 29238};

		if (Arrays.stream(specialAttackButtons).anyMatch(id -> id == buttonId)) {
			stoner.getSpecialAssault().clickSpecialButton(buttonId);
			return true;
		}
		return false;
	}

	private boolean handleTeleportLocations(Stoner stoner, int buttonId) {
		Location location = TELEPORT_LOCATIONS.get(buttonId);
		if (location != null) {
			stoner.teleport(location);
			return true;
		}
		return false;
	}

	private boolean handleShopInterfaces(Stoner stoner, int buttonId) {
		ShopAction shopAction = SHOP_ACTIONS.get(buttonId);
		if (shopAction != null) {
			stoner.send(new SendMessage("@gre@You have opened " + shopAction.message + "."));
			stoner.getShopping().open(shopAction.shopId);
			return true;
		}
		return false;
	}

	private void handleDefaultCases(Stoner stoner, int buttonId) {
		// Handle plugin systems in order of priority
		if (CreditHandler.handleClicking(stoner, buttonId)) return;
		if (GenieLamp.handle(stoner, buttonId)) return;
		if (GenieReset.handle(stoner, buttonId)) return;
		if (AchievementButtons.handleButtons(stoner, buttonId)) return;
		if (ProfessionsChat.handle(stoner, buttonId)) return;
		if (stoner.getDialogue() != null && stoner.getDialogue().clickButton(buttonId)) return;
		if (Autocast.clickButton(stoner, buttonId)) return;
		if (Emotes.clickButton(stoner, buttonId)) return;
		if (DuelingConstants.clickDuelButton(stoner, buttonId)) return;
		if (stoner.getTrade().clickTradeButton(buttonId)) return;
		if (stoner.getBank().clickButton(buttonId)) return;
		if (stoner.getMage().clickMageButtons(buttonId)) return;
		if (EquipmentConstants.clickAssaultStyleButtons(stoner, buttonId)) return;
		if (HideTanning.clickButton(stoner, buttonId)) return;

		handleTHChempistryButtons(stoner, buttonId);
	}

	private void handleTHChempistryButtons(Stoner stoner, int buttonId) {
		if (stoner.getAttributes().get("thchempistryitem1") != null) {
			Item item1 = (Item) stoner.getAttributes().get("thchempistryitem1");
			Item item2 = (Item) stoner.getAttributes().get("thchempistryitem2");

			if (item1.getId() == 227 || item2.getId() == 227) {
				THChempistryUnfinishedPotionTask.handleTHChempistryButtons(stoner, buttonId);
			} else {
				THChempistryFinishedPotionTask.handleTHChempistryButtons(stoner, buttonId);
			}
		}
	}

	// Button handler interface for cleaner code organization
	@FunctionalInterface
	public interface ButtonHandler {
		void handle(Stoner stoner);
	}


	// Helper class for shop actions
	public static class ShopAction {
		final int shopId;
		final String message;

		ShopAction(int shopId, String message) {
			this.shopId = shopId;
			this.message = message;
		}
	}


}