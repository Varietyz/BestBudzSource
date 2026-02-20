package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.Utility.Stopwatch;
import com.bestbudz.rs2.auto.combat.AutoCombat;
import com.bestbudz.rs2.content.Box;
import com.bestbudz.rs2.content.MoneyPouch;
import com.bestbudz.rs2.content.PriceChecker;
import com.bestbudz.rs2.content.PrivateMessaging;
import com.bestbudz.rs2.content.RunEnergy;
import com.bestbudz.rs2.content.StonerProperties;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.impl.SpecialAssault;
import com.bestbudz.rs2.content.profession.consumer.allergies.AllergySystem;
import com.bestbudz.rs2.content.profession.consumer.consumables.Consumables;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.cannacredits.CannaCreditUnlocks;
import com.bestbudz.rs2.content.minigames.StonerMinigames;
import com.bestbudz.rs2.content.minigames.duelarena.Dueling;
import com.bestbudz.rs2.content.minigames.bloodtrial.core.BloodTrialDetails;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.bankstanding.BankStanding;
import com.bestbudz.rs2.content.profession.fisher.Fisher;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.Melee;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.content.profession.petmaster.PetMaster;
import com.bestbudz.rs2.content.profession.resonance.Resonance;
import com.bestbudz.rs2.content.profession.sagittarius.SagittariusProfession;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.shopping.Shopping;
import com.bestbudz.rs2.content.trading.Trade;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.InterfaceManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.ItemDegrading;
import com.bestbudz.rs2.entity.item.impl.LocalGroundItems;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.RareDropEP;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.object.LocalObjects;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetCombatSystem;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Stoner extends Entity {

	private String username;
	private String password;
	private long usernameToLong;
	private int rights = 0;
	private boolean visible = true;
	private String display;

	private final Client client;
	private final StonerSession session;
	private final StonerStats stats;
	private final StonerCombat combat;
	private final StonerInventory inventory;
	private final StonerInteraction interaction;
	private final StonerMovement movement;
	private final StonerSocial social;
	private final StonerProfessions professions;
	private final StonerMinigame minigames;
	private final StonerSettings settings;
	private final StonerAppearance appearance;
	private final StonerPets pets;

	private AllergySystem allergySystem;

	private final HashMap<AchievementList, Integer> stonerAchievements;
	public static final Map<Location, Integer> pathMemory = new HashMap<>();
	private final List<Stoner> stoners = new LinkedList<Stoner>();
	private Stopwatch delay = new Stopwatch();

	public int monsterSelected = 0;
	public boolean playingMB = false;
	public long lastReport = 0;
	public String lastReported = "";
	public String reportName = "";
	public int reportClicked = 0;
	public long shopDelay;
	public long tradeDelay;
	public String viewing;
	public List<StonerTitle> unlockedTitles = new ArrayList<>();
	public int whirlpoolsHit = 0;
	public List<Mob> tentacles = new ArrayList<>();
	public boolean homeTeleporting;
	public String targetName = "";
	public int targetIndex;
	public boolean enteredPin = false;
	public boolean isCracking;
	public String lastClanChat = "bestbudz";
	public long timeout = 0L;
	public long aggressionDelay = System.currentTimeMillis();

	public Stoner() {
		ChangeAppearancePacket.setToDefault(this);
		client = new Client(null);
		usernameToLong = 0L;

		session = new StonerSession(this);
		stats = new StonerStats(this);
		combat = new StonerCombat(this);
		inventory = new StonerInventory(this);
		interaction = new StonerInteraction(this);
		movement = new StonerMovement(this);
		social = new StonerSocial(this);
		professions = new StonerProfessions(this);
		minigames = new StonerMinigame(this);
		settings = new StonerSettings(this);
		appearance = new StonerAppearance(this);
		pets = new StonerPets(this);

		this.stonerAchievements = new HashMap<AchievementList, Integer>(AchievementList.values().length) {
			private static final long serialVersionUID = -4629357800141530574L;
			{
				for (AchievementList achievement : AchievementList.values()) {
					put(achievement, 0);
				}
			}
		};
	}

	public Stoner(Client client) {
		this.client = client;
		getLocation().setAs(new Location(StonerConstants.HOME));
		setNpc(false);

		session = new StonerSession(this);
		stats = new StonerStats(this);
		combat = new StonerCombat(this);
		inventory = new StonerInventory(this);
		interaction = new StonerInteraction(this);
		movement = new StonerMovement(this);
		social = new StonerSocial(this);
		professions = new StonerProfessions(this);
		minigames = new StonerMinigame(this);
		settings = new StonerSettings(this);
		appearance = new StonerAppearance(this);
		pets = new StonerPets(this);

		this.stonerAchievements = new HashMap<AchievementList, Integer>(AchievementList.values().length) {
			private static final long serialVersionUID = -4629357800141530574L;
			{
				for (AchievementList achievement : AchievementList.values()) {
					put(achievement, 0);
				}
			}
		};
	}

	public static void recordCollision(Location loc) {
		pathMemory.put(loc, pathMemory.getOrDefault(loc, 0) + 1);
	}

	public void decayPathMemory() {
		pathMemory.entrySet().removeIf(e -> {
			int newVal = e.getValue() - 1;
			if (newVal <= 0) return true;
			e.setValue(newVal);
			return false;
		});
	}

	public int getMemoryPenalty(Location loc) {
		return pathMemory.getOrDefault(loc, 0);
	}

	public boolean login(boolean starter) throws Exception {
		return session.handleLogin(starter);
	}

	public void logout(boolean force) {
		session.handleLogout(force);
	}

	@Override
	public void process() throws Exception {

		if (isPetStoner()) {
			getClient().resetLastPacketReceived();

		} else {
		  if (Math.abs(World.getCycles() - client.getLastPacketTime()) >= 9) {
			if (getCombat().inCombat() && !getCombat().getLastAssaultedBy().isNpc()) {
			  if (timeout == 0) {
				timeout = System.currentTimeMillis() + 180000;
			  } else if (timeout <= System.currentTimeMillis() || !getCombat().inCombat()) {
				logout(false);
				System.out.println("Stoner timed out: " + getUsername());
			  }
			} else {
			  System.out.println("Stoner timed out: " + getUsername());
			  logout(false);
			}
		  }
		}

		if (getController() != null) {
			getController().tick(this);
		}

    	if (!isPetStoner()) {
		  getShopping().update();
		  getResonance().drain();
		  getBankStanding().process();
		  getCombat().process();
		  doAgressionCheck();
		  getAutoCombat().process();
		} else if (isPetStoner()) {
			getCombat().process();
			PetCombatSystem.processPetCombat(this);
		}
		getFollowing().process();
	}

	@Override
	public void reset() {
		if (isPetStoner()) {

			getMovementHandler().resetMoveDirections();
			getFollowing().updateWaypoint();
			getUpdateFlags().reset();
			setNeedsPlacement(false);
			setResetMovementQueue(false);
			setAppearanceUpdateRequired(false);
			setChatUpdateRequired(false);
			getUpdateFlags().setUpdateRequired(false);
			return;
		}

		getFollowing().updateWaypoint();
		setAppearanceUpdateRequired(false);
		setChatUpdateRequired(false);
		setResetMovementQueue(false);
		setNeedsPlacement(false);
		getMovementHandler().resetMoveDirections();
		getUpdateFlags().setUpdateRequired(false);
		getUpdateFlags().reset();
	}

	@Override
	public void afterCombatProcess(Entity assault) {
		combat.afterCombatProcess(assault);
	}

	@Override
	public boolean canAssault() {
		return combat.canAssault();
	}

	@Override
	public void checkForDeath() {
		combat.checkForDeath();
	}

	@Override
	public int getCorrectedDamage(int damage) {
		return combat.getCorrectedDamage(damage);
	}

	@Override
	public int getMaxHit(CombatTypes type) {
		return combat.getMaxHit(type);
	}

	@Override
	public void hit(Hit hit) {
		combat.hit(hit);
	}

	@Override
	public boolean isIgnoreHitSuccess() {
		return combat.isIgnoreHitSuccess();
	}

	@Override
	public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
		combat.onAssault(assault, hit, type, success);
	}

	@Override
	public void onCombatProcess(Entity assault) {
		combat.onCombatProcess(assault);
	}

	@Override
	public void onHit(Entity e, Hit hit) {
		combat.onHit(e, hit);
	}

	@Override
	public void updateCombatType() {
		combat.updateCombatType();
	}

	@Override
	public void retaliate(Entity assaulted) {
		combat.retaliate(assaulted);
	}

	@Override
	public Following getFollowing() {
		return movement.getFollowing();
	}

	@Override
	public MovementHandler getMovementHandler() {
		return movement.getMovementHandler();
	}

	public void teleport(Location location) {
		movement.teleport(location);
	}

	public void doAgressionCheck() {
		combat.doAgressionCheck();
	}

	public void displayCombatStatus() {
		combat.displayCombatStatus();
	}

	public void resetAggression() {
		combat.resetAggression();
	}

	public boolean payment(int amount) {
		return inventory.payment(amount);
	}

	public void clearClanChat() {
		social.clearClanChat();
	}

	public void setClanData() {
	}

	public void addDefaultChannel() {

	}

	public String deterquarryRank(Stoner stoner) {
		return social.deterquarryRank(stoner);
	}

	public String deterquarryIcon(Stoner stoner) {
		return social.deterquarryIcon(stoner);
	}

	public void changeZ(int z) {
		movement.changeZ(z);
	}

	public void checkForRegionChange() {
		movement.checkForRegionChange();
	}

	public boolean withinRegion(Location other) {
		return movement.withinRegion(other);
	}

	public boolean canSave() {
		return interaction.canSave();
	}

	public boolean setController(Controller controller) {
		return interaction.setController(controller);
	}

	public boolean setControllerNoInit(Controller controller) {
		return interaction.setControllerNoInit(controller);
	}

	public void onControllerFinish() {
		interaction.onControllerFinish();
	}

	public void start(Dialogue dialogue) {
		interaction.start(dialogue);
	}

	public void incrDeaths() {
		stats.incrDeaths();
	}

	public void send(OutgoingPacket packet) {
		if (client != null) {
			client.queueOutgoingPacket(packet);
		}
	}

	public Client getClient() { return client; }

	public String getUsername() { return username; }

	public void setUsername(String username) {
		this.username = username;

		this.usernameToLong = Utility.nameToLong(username.toLowerCase());

		if (isPet() || username.equals("BestBud")) {
			System.out.println("Set username for " + username + " -> usernameToLong: " + this.usernameToLong);
		}
	}

	public AllergySystem getAllergySystem() {
		if (allergySystem == null) {
			allergySystem = new AllergySystem(this);
		}
		return allergySystem;
	}

	public long getUsernameToLong() { return usernameToLong; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public int getRights() { return rights; }
	public void setRights(int rights) { this.rights = rights; }
	public boolean isVisible() { return visible; }
	public void setVisible(boolean visible) { this.visible = visible; }
	public String getDisplay() { return display; }
	public void setDisplay(String display) { this.display = display; }

	public StonerSession getSession() { return session; }
	public StonerStats getStats() { return stats; }
	public StonerCombat getStonerCombat() { return combat; }
	public StonerInventory getInventory() { return inventory; }
	public StonerInteraction getInteraction() { return interaction; }
	public StonerMovement getMovement() { return movement; }
	public StonerSocial getSocial() { return social; }
	public StonerProfessions getProfessions() { return professions; }
	public StonerSettings getSettings() { return settings; }
	public StonerPets getPets() { return pets; }

	public boolean isPet() { return pets.isPet(); }
	public void setPet(boolean pet) { pets.setPet(pet); }
	public boolean isPetStoner() { return pets.isPetStoner(); }
	public List<Pet> getActivePets() { return pets.getActivePets(); }

	public Box getBox() { return inventory.getBox(); }
	public Bank getBank() { return inventory.getBank(); }
	public Equipment getEquipment() { return inventory.getEquipment(); }
	public MoneyPouch getPouch() { return inventory.getPouch(); }
	public Trade getTrade() { return inventory.getTrade(); }
	public Shopping getShopping() { return inventory.getShopping(); }
	public PriceChecker getPriceChecker() { return inventory.getPriceChecker(); }
	public LocalGroundItems getGroundItems() { return inventory.getGroundItems(); }
	public ItemDegrading getDegrading() { return inventory.getDegrading(); }
	public Consumables getConsumables() { return inventory.getConsumables(); }

	public Profession getProfession() { return professions.getProfession(); }
	public MageProfession getMage() { return professions.getMage(); }
	public SagittariusProfession getSagittarius() { return professions.getSagittarius(); }
	public Melee getMelee() { return professions.getMelee(); }
	public Fisher getFisher() { return professions.getFisher(); }
	public Mercenary getMercenary() { return professions.getMercenary(); }
	public Resonance getResonance() { return professions.getResonance(); }
	public BankStanding getBankStanding() { return professions.getBankStanding(); }

	public StonerMinigames getMinigames() { return minigames.getStonerMinigames(); }
	public Dueling getDueling() { return minigames.getDueling(); }
	public BloodTrialDetails getBloodTrialDetails() { return minigames.getBloodTrialDetails(); }
	public StonerProperties getProperties() { return minigames.getProperties(); }

	public RunEnergy getRunEnergy() { return movement.getRunEnergy(); }

	public StonerAnimations getAnimations() { return appearance.getAnimations(); }

	public PrivateMessaging getPrivateMessaging() { return social.getPrivateMessaging(); }

	public StonerAssistant getPA() { return interaction.getAssistant(); }
	public InterfaceManager getInterfaceManager() { return interaction.getInterfaceManager(); }
	public LocalObjects getObjects() { return interaction.getObjects(); }
	public Dialogue getDialogue() { return interaction.getDialogue(); }
	public void setDialogue(Dialogue d) { interaction.setDialogue(d); }
	public Controller getController() { return interaction.getController(); }

	public SpecialAssault getSpecialAssault() { return combat.getSpecialAssault(); }
	public RareDropEP getRareDropEP() { return combat.getRareDropEP(); }
	public AutoCombat getAutoCombat() { return combat.getAutoCombat(); }

	public HashMap<AchievementList, Integer> getStonerAchievements() { return stonerAchievements; }
	public int getAchievementsPoints() { return stats.getAchievementsPoints(); }
	public void addAchievementPoints(int points) { stats.addAchievementPoints(points); }
	public double getCounterExp() { return stats.getCounterExp(); }
	public void addCounterExp(double exp) { stats.addCounterExp(exp); }
	public int[][] getProfessionGoals() { return stats.getProfessionGoals(); }
	public void setProfessionGoals(int[][] professionGoals) { stats.setProfessionGoals(professionGoals); }
	public int getKills() { return stats.getKills(); }
	public void setKills(int kills) { stats.setKills(kills); }
	public int getDeaths() { return stats.getDeaths(); }
	public void setDeaths(int deaths) { stats.setDeaths(deaths); }
	public int getBountyPoints() { return stats.getBountyPoints(); }
	public int setBountyPoints(int amount) { return stats.setBountyPoints(amount); }
	public int getCredits() { return stats.getCredits(); }
	public void setCredits(int cannacredits) { stats.setCredits(cannacredits); }
	public int getChillPoints() { return stats.getChillPoints(); }
	public void setChillPoints(int chillPoints) { stats.setChillPoints(chillPoints); }
	public int getMercenaryPoints() { return stats.getMercenaryPoints(); }
	public void setMercenaryPoints(int mercenaryPoints) { stats.setMercenaryPoints(mercenaryPoints); }
	public void addMercenaryPoints(int amount) { stats.addMercenaryPoints(amount); }
	public int getPestPoints() { return stats.getPestPoints(); }
	public void setPestPoints(int pestPoints) { stats.setPestPoints(pestPoints); }
	public int getArenaPoints() { return stats.getArenaPoints(); }
	public void setArenaPoints(int arenaPoints) { stats.setArenaPoints(arenaPoints); }
	public long getMoneyPouch() { return stats.getMoneyPouch(); }
	public void setMoneyPouch(long moneyPouch) { stats.setMoneyPouch(moneyPouch); }
	public boolean isPouchPayment() { return stats.isPouchPayment(); }
	public void setPouchPayment(boolean pouchPayment) { stats.setPouchPayment(pouchPayment); }

	public byte getScreenBrightness() { return settings.getScreenBrightness(); }
	public void setScreenBrightness(byte screenBrightness) { settings.setScreenBrightness(screenBrightness); }
	public byte getMusicVolume() { return settings.getMusicVolume(); }
	public void setMusicVolume(byte musicVolume) { settings.setMusicVolume(musicVolume); }
	public byte getSoundVolume() { return settings.getSoundVolume(); }
	public void setSoundVolume(byte soundVolume) { settings.setSoundVolume(soundVolume); }
	public boolean isJailed() { return settings.isJailed(); }
	public void setJailed(boolean jailed) { settings.setJailed(jailed); }
	public boolean isMuted() { return settings.isMuted(); }
	public void setMuted(boolean muted) { settings.setMuted(muted); }
	public String getPin() { return settings.getPin(); }
	public void setPin(String pin) { settings.setPin(pin); }

	public void setAppearance(int[] appearance) { this.appearance.setAppearance(appearance); }
	public int[] getAppearance() { return appearance.getAppearance(); }
	public byte[] getColors() { return appearance.getColors(); }
	public void setColors(byte[] colors) { appearance.setColors(colors); }
	public byte getGender() { return appearance.getGender(); }
	public void setGender(byte gender) { appearance.setGender(gender); }
	public int getChatColor() { return appearance.getChatColor(); }
	public void setChatColor(int chatColor) { appearance.setChatColor(chatColor); }
	public int getChatEffects() { return appearance.getChatEffects(); }
	public void setChatEffects(int chatEffects) { appearance.setChatEffects(chatEffects); }
	public byte[] getChatText() { return appearance.getChatText(); }
	public void setChatText(byte[] chatText) { appearance.setChatText(chatText); }
	public boolean isAppearanceUpdateRequired() { return appearance.isAppearanceUpdateRequired(); }
	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) { appearance.setAppearanceUpdateRequired(appearanceUpdateRequired); }
	public boolean isChatUpdateRequired() { return appearance.isChatUpdateRequired(); }
	public void setChatUpdateRequired(boolean chatUpdateRequired) { appearance.setChatUpdateRequired(chatUpdateRequired); }
	public int getNpcAppearanceId() { return appearance.getNpcAppearanceId(); }
	public void setNpcAppearanceId(short npcAppearanceId) { appearance.setNpcAppearanceId(npcAppearanceId); }

	public boolean needsPlacement() { return session.needsPlacement(); }
	public void setNeedsPlacement(boolean needsPlacement) { session.setNeedsPlacement(needsPlacement); }
	public boolean isResetMovementQueue() { return session.isResetMovementQueue(); }
	public void setResetMovementQueue(boolean resetMovementQueue) { session.setResetMovementQueue(resetMovementQueue); }
	public long getLastAction() { return session.getLastAction(); }
	public void setLastAction(long lastAction) { session.setLastAction(lastAction); }
	public int getYearCreated() { return session.getYearCreated(); }
	public void setYearCreated(int yearCreated) { session.setYearCreated(yearCreated); }
	public int getDayCreated() { return session.getDayCreated(); }
	public void setDayCreated(int dayCreated) { session.setDayCreated(dayCreated); }
	public int getLastLoginDay() { return session.getLastLoginDay(); }
	public void setLastLoginDay(int lastLoginDay) { session.setLastLoginDay(lastLoginDay); }
	public int getLastLoginYear() { return session.getLastLoginYear(); }
	public void setLastLoginYear(int lastLoginYear) { session.setLastLoginYear(lastLoginYear); }
	public boolean isStarter() { return session.isStarter(); }
	public void setStarter(boolean starter) { session.setStarter(starter); }
	public String getUid() { return session.getUid(); }
	public void setUid(String uid) { session.setUid(uid); }
	public String getLastKnownUID() { return session.getLastKnownUID(); }
	public void setLastKnownUID(String lastKnownUID) { session.setLastKnownUID(lastKnownUID); }

	public Location getCurrentRegion() { return movement.getCurrentRegion(); }
	public void setCurrentRegion(Location currentRegion) { movement.setCurrentRegion(currentRegion); }
	public boolean isHomeTeleporting() { return movement.isHomeTeleporting(); }
	public void setHomeTeleporting(boolean homeTeleporting) { movement.setHomeTeleporting(homeTeleporting); }
	public int getTeleportTo() { return movement.getTeleportTo(); }
	public void setTeleportTo(int teleportTo) { movement.setTeleportTo(teleportTo); }

	public byte getMultipleMouseButtons() { return settings.getMultipleMouseButtons(); }
	public void setMultipleMouseButtons(byte multipleMouseButtons) { settings.setMultipleMouseButtons(multipleMouseButtons); }
	public byte getChatEffectsEnabled() { return settings.getChatEffectsEnabled(); }
	public void setChatEffectsEnabled(byte chatEffectsEnabled) { settings.setChatEffectsEnabled(chatEffectsEnabled); }
	public byte getSplitPrivateChat() { return settings.getSplitPrivateChat(); }
	public void setSplitPrivateChat(byte splitPrivateChat) { settings.setSplitPrivateChat(splitPrivateChat); }
	public byte getAcceptAid() { return settings.getAcceptAid(); }
	public void setAcceptAid(byte acceptAid) { settings.setAcceptAid(acceptAid); }
	public long getJailLength() { return settings.getJailLength(); }
	public void setJailLength(long jailLength) { settings.setJailLength(jailLength); }
	public long getBanLength() { return settings.getBanLength(); }
	public void setBanLength(long banLength) { settings.setBanLength(banLength); }
	public long getMuteLength() { return settings.getMuteLength(); }
	public void setMuteLength(long muteLength) { settings.setMuteLength(muteLength); }
	public boolean isBanned() { return settings.isBanned(); }
	public void setBanned(boolean banned) { settings.setBanned(banned); }
	public boolean isYellMuted() { return settings.isYellMuted(); }
	public void setYellMuted(boolean yellMuted) { settings.setYellMuted(yellMuted); }
	public int getResonanceInterface() { return settings.getResonanceInterface(); }
	public void setResonanceInterface(int resonanceInterface) { settings.setResonanceInterface(resonanceInterface); }
	public int getCurrentSongId() { return settings.getCurrentSongId(); }
	public void setCurrentSongId(int currentSongId) { settings.setCurrentSongId(currentSongId); }
	public boolean isAdvanceColors() { return settings.isAdvanceColors(); }
	public void setAdvanceColors(boolean advanceColors) { settings.setAdvanceColors(advanceColors); }
	public byte[] getPouches() { return settings.getPouches(); }
	public void setPouches(byte[] pouches) { settings.setPouches(pouches); }
	public byte getTransparentPanel() { return settings.getTransparentPanel(); }
	public void setTransparentPanel(byte transparentPanel) { settings.setTransparentPanel(transparentPanel); }
	public byte getTransparentChatbox() { return settings.getTransparentChatbox(); }
	public void setTransparentChatbox(byte transparentChatbox) { settings.setTransparentChatbox(transparentChatbox); }
	public byte getSideStones() { return settings.getSideStones(); }
	public void setSideStones(byte sideStones) { settings.setSideStones(sideStones); }

	public ArrayList<String> getLastKilledStoners() { return stats.getLastKilledStoners(); }
	public void setLastKilledStoners(ArrayList<String> lastKilledStoners) { stats.setLastKilledStoners(lastKilledStoners); }
	public int getTotalAdvances() { return stats.getTotalAdvances(); }
	public void setTotalAdvances(int totalAdvances) { stats.setTotalAdvances(totalAdvances); }
	public int getAdvancePoints() { return stats.getAdvancePoints(); }
	public void setAdvancePoints(int advancePoints) { stats.setAdvancePoints(advancePoints); }
	public int[] getProfessionAdvances() { return stats.getProfessionAdvances(); }
	public void setProfessionAdvances(int[] professionAdvances) { stats.setProfessionAdvances(professionAdvances); }
	public boolean getProfilePrivacy() { return stats.isProfilePrivacy(); }
	public void setProfilePrivacy(boolean profilePrivacy) { stats.setProfilePrivacy(profilePrivacy); }
	public int getLikes() { return stats.getLikes(); }
	public void setLikes(int likes) { stats.setLikes(likes); }
	public int getDislikes() { return stats.getDislikes(); }
	public void setDislikes(int dislikes) { stats.setDislikes(dislikes); }
	public int getProfileViews() { return stats.getProfileViews(); }
	public void setProfileViews(int profileViews) { stats.setProfileViews(profileViews); }
	public long getLastLike() { return stats.getLastLike(); }
	public void setLastLike(long lastLike) { stats.setLastLike(lastLike); }
	public byte getLikesGiven() { return stats.getLikesGiven(); }
	public void setLikesGiven(byte likesGiven) { stats.setLikesGiven(likesGiven); }
	public int[] getCluesCompleted() { return stats.getCluesCompleted(); }
	public void setCluesCompleted(int[] cluesCompleted) { stats.setCluesCompleted(cluesCompleted); }
	public void setCluesCompleted(int index, int value) { stats.setCluesCompleted(index, value); }
	public int getBossID() { return stats.getBossID(); }
	public void setBossID(int bossID) { stats.setBossID(bossID); }
	public boolean isMember() { return stats.isMember(); }
	public void setMember(boolean isMember) { stats.setMember(isMember); }
	public int getWeaponKills() { return stats.getWeaponKills(); }
	public void setWeaponKills(int weaponKills) { stats.setWeaponKills(weaponKills); }
	public int getWeaponPoints() { return stats.getWeaponPoints(); }
	public void setWeaponPoints(int weaponPoints) { stats.setWeaponPoints(weaponPoints); }
	public int getMoneySpent() { return stats.getMoneySpent(); }
	public void setMoneySpent(int moneySpent) { stats.setMoneySpent(moneySpent); }
	public long getShopCollection() { return stats.getShopCollection(); }
	public void setShopCollection(long shopCollection) { stats.setShopCollection(shopCollection); }
	public Set<CannaCreditUnlocks> getUnlockedCredits() { return stats.getUnlockedCredits(); }
	public void setUnlockedCredits(Set<CannaCreditUnlocks> unlockedCredits) { stats.setUnlockedCredits(unlockedCredits); }
	public void unlockCredit(CannaCreditUnlocks purchase) { stats.unlockCredit(purchase); }
	public boolean isCreditUnlocked(CannaCreditUnlocks purchase) { return stats.isCreditUnlocked(purchase); }
	public int getRogueKills() { return stats.getRogueKills(); }
	public void setRogueKills(int rogueKills) { stats.setRogueKills(rogueKills); }
	public int getRogueRecord() { return stats.getRogueRecord(); }
	public void setRogueRecord(int rogueRecord) { stats.setRogueRecord(rogueRecord); }
	public int getHunterKills() { return stats.getHunterKills(); }
	public void setHunterKills(int hunterKills) { stats.setHunterKills(hunterKills); }
	public int getHunterRecord() { return stats.getHunterRecord(); }
	public void setHunterRecord(int hunterRecord) { stats.setHunterRecord(hunterRecord); }
	public int getBlackMarks() { return stats.getBlackMarks(); }
	public void setBlackMarks(int blackMarks) { stats.setBlackMarks(blackMarks); }

	public StonerTitle getStonerTitle() { return social.getStonerTitle(); }
	public void setStonerTitle(StonerTitle stonerTitle) { social.setStonerTitle(stonerTitle); }
	public String getYellTitle() { return social.getYellTitle(); }
	public void setYellTitle(String yellTitle) { social.setYellTitle(yellTitle); }

	public ToxicBlowpipe getToxicBlowpipe() { return inventory.getToxicBlowpipe(); }
	public void setToxicBlowpipe(ToxicBlowpipe toxicBlowpipe) { inventory.setToxicBlowpipe(toxicBlowpipe); }
	public TridentOfTheSeas getSeasTrident() { return inventory.getSeasTrident(); }
	public void setSeasTrident(TridentOfTheSeas trident) { inventory.setSeasTrident(trident); }
	public TridentOfTheSwamp getSwampTrident() { return inventory.getSwampTrident(); }
	public void setSwampTrident(TridentOfTheSwamp swampTrident) { inventory.setSwampTrident(swampTrident); }
	public SerpentineHelmet getSerpentineHelment() { return inventory.getSerpentineHelment(); }
	public void setSerpentineHelment(SerpentineHelmet serpentineHelment) { inventory.setSerpentineHelment(serpentineHelment); }
	public int getEnterXSlot() { return inventory.getEnterXSlot(); }
	public void setEnterXSlot(int enterXSlot) { inventory.setEnterXSlot(enterXSlot); }
	public int getEnterXInterfaceId() { return inventory.getEnterXInterfaceId(); }
	public void setEnterXInterfaceId(int enterXInterfaceId) { inventory.setEnterXInterfaceId(enterXInterfaceId); }
	public int getEnterXItemId() { return inventory.getEnterXItemId(); }
	public void setEnterXItemId(int enterXItemId) { inventory.setEnterXItemId(enterXItemId); }

	public long getAggressionDelay() { return combat.getAggressionDelay(); }
	public void setAggressionDelay(long aggressionDelay) { combat.setAggressionDelay(aggressionDelay); }
	public long getCurrentStunDelay() { return combat.getCurrentStunDelay(); }
	public void setCurrentStunDelay(long delay) { combat.setCurrentStunDelay(delay); }
	public long getSetStunDelay() { return combat.getSetStunDelay(); }
	public void setSetStunDelay(long delay) { combat.setSetStunDelay(delay); }
	public boolean isHitZulrah() { return combat.isHitZulrah(); }
	public void setHitZulrah(boolean hitZulrah) { combat.setHitZulrah(hitZulrah); }

	public boolean[] getKillRecord() { return minigames.getKillRecord(); }
	public void setKillRecord(boolean[] killRecord) { minigames.setKillRecord(killRecord); }
	public boolean isChestClicked() { return minigames.isChestClicked(); }
	public void setChestClicked(boolean chestClicked) { minigames.setChestClicked(chestClicked); }
	public int getBarrowsKC() { return minigames.getBarrowsKC(); }
	public void setBarrowsKC(int killCount) { minigames.setBarrowsKC(killCount); }

	public boolean isBusy() { return getTrade().trading(); }
	public boolean isBusyNoInterfaceCheck() { return getTrade().trading(); }

	public List<Stoner> getStoners() { return stoners; }
	public Stopwatch getDelay() { return delay; }
	public void setDelay(Stopwatch delay) { this.delay = delay; }

	public PetMaster getPetMaster() { return professions.getPetMaster(); }

	public void start() {
		getRunEnergy().tick();
		startRegeneration();
		getSpecialAssault().tick();
		minigames.start();
		if (getTeleblockTime() > 0) {
			tickTeleblock();
		}
	}

	public void displayResonanceStatus() {
		professions.displayResonanceStatus();
	}

	@Override
	public void poison(int start) {
		if (isPoisoned()) {
			return;
		}
		super.poison(start);
		if (isActive()) {
			send(new SendMessage("You smoked spice and went on a BAD trip!"));
		}
	}

	@Override
	public void teleblock(int i) {
		super.teleblock(i);
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof Stoner)) {
			return ((Stoner) o).getUsernameToLong() == getUsernameToLong();
		}
		return false;
	}

	@Override
	public String toString() {
		return "Stoner(" + getUsername() + ":" + getPassword() + " - " + client.getHost() + ")";
	}

	public void getSkulling(long idx)
	{
	}

}
