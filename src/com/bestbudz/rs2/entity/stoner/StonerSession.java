package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.NameUtil;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.Emotes;
import com.bestbudz.rs2.content.LoyaltyShop;
import com.bestbudz.rs2.content.StarterKit;
import com.bestbudz.rs2.content.profession.consumer.io.ConsumerSaveManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChatBridgeManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.*;
import com.bestbudz.rs2.content.io.sqlite.SaveWorker;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.bestbudz.core.discord.DiscordManager;
import com.bestbudz.rs2.entity.pets.PetManager;

/**
 * Handles session management, login/logout, timeouts, and connection state
 */
public class StonerSession {
	private final Stoner stoner;

	// Session state
	private boolean active = false;
	private long timeout = 0L;
	private long lastAction = System.currentTimeMillis();
	private boolean needsPlacement = false;
	private boolean resetMovementQueue = false;

	// Login/logout data
	private int yearCreated = 0;
	private int dayCreated = 0;
	private int lastLoginDay = 0;
	private int lastLoginYear = 0;
	private boolean starter = false;
	private long generalDelay;
	private long lastRequestedLookup;

	// Connection management
	private String uid;
	private String lastKnownUID;

	public StonerSession(Stoner stoner) {
		this.stoner = stoner;
	}

	public boolean handleLogin(boolean starter) throws Exception {
		this.starter = starter;

		stoner.setUsername(NameUtil.uppercaseFirstLetter(stoner.getUsername()));
		long usernameToLong = Utility.nameToLong(stoner.getUsername());

		int response = 2;

		if ((stoner.getPassword().length() == 0) || (stoner.getUsername().length() == 0) || (stoner.getUsername().length() > 12)) {
			response = 3;
		} else if ((stoner.isBanned()) || Boolean.TRUE.equals(stoner.getAttributes().get("banned_ip"))) {
			response = 4;
		} else if ((stoner.getPassword() != null) && (!stoner.getPassword().equals(stoner.getClient().getEnteredPassword()))) {
			response = 3;
		} else if (World.isUpdating()) {
			response = 14;
		} else if (World.getStonerByName(stoner.getUsername()) != null) {
			response = 5;
		} else if (World.register(stoner) == -1) {
			response = 7;
		}

		if (response != 2) {
			StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
			resp.writeByte(response);
			resp.writeByte(stoner.getRights());
			resp.writeByte(0);
			stoner.getClient().send(resp.getBuffer());
			return false;
		}

		new SendLoginResponse(response, stoner.getRights()).execute(stoner.getClient());

		stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));

		if (Boolean.TRUE.equals(stoner.getAttributes().get("muted_ip"))) {
			stoner.setMuted(true);
			stoner.setMuteLength(-1);
		}

		if (stoner.inCyclops()) {
			stoner.teleport(StonerConstants.HOME);
		}

		ControllerManager.setControllerOnWalk(stoner);

		if (Region.getRegion(stoner.getLocation().getX(), stoner.getLocation().getY()) == null) {
			stoner.teleport(new Location(StonerConstants.HOME));
			stoner.send(new SendMessage("You been saved from the unknown."));
		}

		if (stoner.isJailed() && !stoner.inJailed()) {
			stoner.teleport(new Location(StonerConstants.JAILED_AREA));
			stoner.send(new SendMessage("You were put to jail man!"));
		}

		if (stoner.inWGGame()) {
			WeaponGame.leaveGame(stoner, false);
		}

		stoner.getMovementHandler()
			.getLastLocation()
			.setAs(new Location(stoner.getLocation().getX(), stoner.getLocation().getY() + 1, stoner.getLocation().getZ()));

		for (int i = 0; i < StonerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
			if (i != 5 && i != 6) {
				stoner.send(new SendSidebarInterface(i, StonerConstants.SIDEBAR_INTERFACE_IDS[i]));
			}
		}

		if (stoner.getMage().getMageBook() == 0) {
			stoner.getMage().setMageBook(1151);
		}

		if (stoner.getResonanceInterface() == 0) {
			stoner.setResonanceInterface(5608);
			// Initialize resonance
		}

		stoner.send(new SendSidebarInterface(5, stoner.getResonanceInterface()));

		if (starter) {
			ChangeAppearancePacket.setToDefault(stoner);
			StarterKit.giveStarterItems(stoner);

			if (lastLoginYear == 0) {
				yearCreated = Utility.getYear();
				dayCreated = Utility.getDayOfYear();
			}
		}

		if (!ChangeAppearancePacket.validate(stoner)) {
			ChangeAppearancePacket.setToDefault(stoner);
		}

		stoner.getEquipment().onLogin();
		stoner.getProfession().onLogin();
		stoner.getMage().onLogin();
		stoner.setScreenBrightness((byte) 4);
		stoner.getPrivateMessaging().connect();
		stoner.getRunEnergy().update();
		stoner.getResonance().disable();

		stoner.getBank().update();

		if (stoner.getEquipment().getItems()[5] != null
			&& stoner.getEquipment().getItems()[5].getId() == 13742) {
			stoner.getEquipment().getItems()[5].setId(11283);
			stoner.getEquipment().update();
		}

		stoner.getBloodTrialDetails().setStage(0);
		stoner.getRunEnergy().setRunning(true);

		Emotes.onLogin(stoner);

		InterfaceHandler.writeText(new QuestTab(stoner));
		stoner.send(new SendString("</col>CannaCredits: @gre@" + Utility.format(stoner.getCredits()), 52504));

		stoner.getBox().update();

		stoner.send(new SendStonerOption("Stalk", 4));
		stoner.send(new SendStonerOption("Deal with", 5));

		// Send all config packets
		stoner.send(new SendConfig(166, stoner.getScreenBrightness()));
		stoner.send(new SendConfig(171, stoner.getMultipleMouseButtons()));
		stoner.send(new SendConfig(172, stoner.getChatEffectsEnabled()));
		stoner.send(new SendConfig(287, stoner.getSplitPrivateChat()));
		stoner.send(new SendConfig(427, stoner.getAcceptAid()));
		stoner.send(new SendConfig(172, stoner.isRetaliate() ? 1 : 0));
		stoner.send(new SendConfig(173, stoner.getRunEnergy().isRunning() ? 1 : 0));
		stoner.send(new SendConfig(168, stoner.getMusicVolume()));
		stoner.send(new SendConfig(169, stoner.getSoundVolume()));
		stoner.send(new SendConfig(876, 0));
		stoner.send(new SendConfig(1032, stoner.getProfilePrivacy() ? 1 : 2));

		stoner.send(new SendExpCounter(0, 0));

		LoyaltyShop.load(stoner);

		for (int i = 0; i < stoner.getProfessionGoals().length; i++) {
			stoner.send(
				new SendProfessionGoal(
					i, stoner.getProfessionGoals()[i][0],
					stoner.getProfessionGoals()[i][1],
					stoner.getProfessionGoals()[i][2]));
		}

		stoner.send(new SendConfig(77, 0));

		stoner.getUpdateFlags().setUpdateRequired(true);
		stoner.setAppearanceUpdateRequired(true);
		needsPlacement = true;

		stoner.send(new SendMessage("<img=2>You landed in Bestbudz, only to get lifted by best buds.<img=2>"));

		if (BestbudzConstants.doubleExperience) {
			stoner.send(new SendMessage("<img=3>@bla@Get lit yall, it's Double Gains!<img=3>"));
		}

		stoner.getController().onControllerInit(stoner);

		stoner.clearClanChat();
		stoner.setClanData();

			stoner.addDefaultChannel();

		if (StonerConstants.isStaff(stoner)) {
			stoner.send(new SendString("Staff tab", 29413));
		} else {
			stoner.send(new SendString("", 29413));
		}

		stoner.send(new SendConfig(1990, stoner.getTransparentPanel()));
		stoner.send(new SendConfig(1991, stoner.getTransparentChatbox()));
		stoner.send(new SendConfig(1992, stoner.getSideStones()));

		ConsumerSaveManager.loadConsumerData(stoner);

		String ts = "**" + stoner.getUsername() + " came to get high asf.**";

		return true;
	}

	public void handleLogout(boolean force) {
		if (stoner.isActive()) {
			stoner.getBankStanding().forceStop();
			stoner.getBankStanding().cleanup();
			stoner.clearAnimationLock();

			if (force) {
				ControllerManager.onForceLogout(stoner);
			} else if ((stoner.getController() != null) && (!stoner.getController().canLogOut())) {
				return;
			}

			World.remove(stoner.getClient().getNpcs());

			if (stoner.getController() != null) {
				stoner.getController().onDisconnect(stoner);
			}

			if (stoner.getTrade().trading()) {
				stoner.getTrade().end(false);
			}

			if (stoner.getInterfaceManager().main == 48500) {
				stoner.getPriceChecker().withdrawAll();
			}

			if (stoner.getDueling().isStaking()) {
				stoner.getDueling().decline();
			}

			PetManager.handleLogout(stoner);

			if (stoner.getProfessions() != null) {
				stoner.getProfessions().save();
			}

			if (DwarfMultiCannon.hasCannon(stoner)) {
				DwarfMultiCannon.getCannon(stoner).onLogout();
			}

			SaveWorker.enqueueSave(stoner);
			StonerSave.save(stoner);
			ConsumerSaveManager.saveConsumerData(stoner);
			// In your logout/disconnect handler
			if (stoner.getPetMaster() != null) {
				stoner.getPetMaster().save();
			}

			if (!World.isDiscordBot(stoner)) {
				DiscordManager.getInstance().onPlayerCountChanged();
			}

			if (!BestbudzConstants.DEV_MODE) {}
		}

		String ts = "**" + stoner.getUsername() + " is way too stoned.**";
		ChatBridgeManager.notifyPlayerLeave(stoner.getUsername());

		World.unregister(stoner);
		stoner.getClient().setStage(Client.Stages.LOGGED_OUT);
		stoner.setActive(false);

		new SendLogout().execute(stoner.getClient());
		stoner.getClient().disconnect();
	}

	public void process() throws Exception {
		if (stoner.isPetStoner()) {
			if (stoner.getClient() != null) {
				stoner.getClient().resetLastPacketReceived();
			}
			return;
		}

		if (Math.abs(World.getCycles() - stoner.getClient().getLastPacketTime()) >= 9) {
			if (stoner.getCombat().inCombat() && !stoner.getCombat().getLastAssaultedBy().isNpc()) {
				if (timeout == 0) {
					timeout = System.currentTimeMillis() + 180000;
				} else if (timeout <= System.currentTimeMillis() || !stoner.getCombat().inCombat()) {
					handleLogout(false);
					System.out.println("Stoner timed out: " + stoner.getUsername());
				}
			} else {
				System.out.println("Stoner timed out: " + stoner.getUsername());
				handleLogout(false);
			}
		}

		if (stoner.getController() != null) {
			stoner.getController().tick(stoner);
		}
	}

	public void reset() {
		resetMovementQueue = false;
		needsPlacement = false;
	}

	public void updateTimeout() {
		lastAction = System.currentTimeMillis();
	}

	public boolean isTimedOut() {
		return timeout > 0 && System.currentTimeMillis() > timeout;
	}

	// Getters and setters
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public long getTimeout() { return timeout; }
	public void setTimeout(long timeout) { this.timeout = timeout; }
	public long getLastAction() { return lastAction; }
	public void setLastAction(long lastAction) { this.lastAction = lastAction; }
	public boolean needsPlacement() { return needsPlacement; }
	public void setNeedsPlacement(boolean needsPlacement) { this.needsPlacement = needsPlacement; }
	public boolean isResetMovementQueue() { return resetMovementQueue; }
	public void setResetMovementQueue(boolean resetMovementQueue) { this.resetMovementQueue = resetMovementQueue; }
	public int getYearCreated() { return yearCreated; }
	public void setYearCreated(int yearCreated) { this.yearCreated = yearCreated; }
	public int getDayCreated() { return dayCreated; }
	public void setDayCreated(int dayCreated) { this.dayCreated = dayCreated; }
	public int getLastLoginDay() { return lastLoginDay; }
	public void setLastLoginDay(int lastLoginDay) { this.lastLoginDay = lastLoginDay; }
	public int getLastLoginYear() { return lastLoginYear; }
	public void setLastLoginYear(int lastLoginYear) { this.lastLoginYear = lastLoginYear; }
	public boolean isStarter() { return starter; }
	public void setStarter(boolean starter) { this.starter = starter; }
	public long getGeneralDelay() { return generalDelay; }
	public void setGeneralDelay(long generalDelay) { this.generalDelay = generalDelay; }
	public long getLastRequestedLookup() { return lastRequestedLookup; }
	public void setLastRequestedLookup(long lastRequestedLookup) { this.lastRequestedLookup = lastRequestedLookup; }
	public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
	public String getLastKnownUID() { return lastKnownUID; }
	public void setLastKnownUID(String lastKnownUID) { this.lastKnownUID = lastKnownUID; }
}