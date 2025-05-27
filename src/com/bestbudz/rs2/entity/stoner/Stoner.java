package com.bestbudz.rs2.entity.stoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.bestbudz.Server;
import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.FinishTeleportingTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.Utility.Stopwatch;
import com.bestbudz.core.util.NameUtil;
import com.bestbudz.rs2.content.Emotes;
import com.bestbudz.rs2.content.Box;
import com.bestbudz.rs2.content.LoyaltyShop;
import com.bestbudz.rs2.content.MoneyPouch;
import com.bestbudz.rs2.content.StonerProperties;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.PriceChecker;
import com.bestbudz.rs2.content.PrivateMessaging;
import com.bestbudz.rs2.content.RunEnergy;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.clanchat.Clan;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatInterface;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.StonerCombatInterface;
import com.bestbudz.rs2.content.combat.impl.Skulling;
import com.bestbudz.rs2.content.combat.impl.SpecialAssault;
import com.bestbudz.rs2.content.consumables.Consumables;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.impl.Tutorial;
import com.bestbudz.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.CreditTab;
import com.bestbudz.rs2.content.interfaces.impl.MiscInterfaces;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.content.io.StonerSaveUtil;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.minigames.StonerMinigames;
import com.bestbudz.rs2.content.minigames.barrows.Barrows.Brother;
import com.bestbudz.rs2.content.minigames.duelarena.Dueling;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrDetails;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrGame;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGame;
import com.bestbudz.rs2.content.pets.BossPets;
import com.bestbudz.rs2.content.shopping.Shopping;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.cultivation.Cultivation;
import com.bestbudz.rs2.content.profession.fisher.Fisher;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.Melee;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.content.profession.sagittarius.SagittariusProfession;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.profession.summoning.Summoning;
import com.bestbudz.rs2.content.trading.Trade;
import com.bestbudz.rs2.content.wilderness.TargetSystem;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.InterfaceManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.following.StonerFollowing;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.ItemDegrading;
import com.bestbudz.rs2.entity.item.impl.LocalGroundItems;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.mob.RareDropEP;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.movement.StonerMovementHandler;
import com.bestbudz.rs2.entity.object.LocalObjects;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendExpCounter;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendLoginResponse;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendLogout;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMapRegion;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfessionGoal;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;
import org.javacord.api.entity.channel.TextChannel;
import com.bestbudz.GameDataLoader;

public class Stoner extends Entity {

	/* Client */
	private final Client client;

	/* Stopwatch used for delaying anything */
	private Stopwatch delay = new Stopwatch();

	private boolean advanceColors;

	/* Uids */
	private String uid;
	private String lastKnownUID;

	/* Drop Table */
	public int monsterSelected = 0;

	private boolean hitZulrah;
	public boolean playingMB = false;

	/* Weapon Game */
	private int weaponKills;
	private int weaponPoints;

	/* Report */
	public long lastReport = 0;
	public String lastReported = "";
	public String reportName = "";
	public int reportClicked = 0;

	/* Member */
	private boolean isMember = false;

	/* Money Pouch */
	private long moneyPouch;
	private boolean pouchPayment;

	public boolean payment(int amount) {
	if (isPouchPayment()) {
		if (getMoneyPouch() < amount) {
			send(new SendMessage("Insufficient funds on your Debit card!"));
			return false;
		}
		setMoneyPouch(getMoneyPouch() - amount);
		send(new SendString(getMoneyPouch() + "", 8135));
		return true;
	} else {
		if (!getBox().hasItemAmount(995, amount)) {
			send(new SendMessage("You do not have enough BestBucks to do this!"));
			return false;
		}
		getBox().remove(995, amount);
		return true;
	}
	}

	/* Delay */
	public long shopDelay;
	public long tradeDelay;

	/* Mage Arena Points */
	private int arenaPoints;

	/* Bongbase */
	private long lastLike;
	private byte likesGiven;
	private int likes, dislikes, profileViews;
	public String viewing;

	public List<StonerTitle> unlockedTitles = new ArrayList<>();

	/* Kraken */
	public int whirlpoolsHit = 0;
	public List<Mob> tentacles = new ArrayList<>();

	/* Teleport variable */
	private int teleportTo;
	public boolean homeTeleporting;

	/* The achievement variables */
	private HashMap<AchievementList, Integer> stonerAchievements = new HashMap<AchievementList, Integer>(AchievementList.values().length) {
		{
			for (AchievementList achievement : AchievementList.values()) {
				put(achievement, 0);
			}
		}

		private static final long serialVersionUID = -4629357800141530574L;
	};

	/* The cannacredits variables */
	private Set<CreditPurchase> unlockedCredits = new HashSet<CreditPurchase>(CreditPurchase.values().length);;

	/* Wilderness variables */
	public String targetName = "";
	public int targetIndex;
	private int bountyPoints;

	/* PvP variables */
	private ArrayList<String> lastKilledStoners = new ArrayList<String>();
	private int kills = 0;
	private int deaths = 0;
	private int rogueKills = 0;
	private int rogueRecord = 0;
	private int hunterKills = 0;
	private int hunterRecord = 0;

	/* Stoner Profiler */
	private boolean profilePrivacy;

	/* Pets */
	private Mob bossPet;
	private int bossID;

	/* CannaCredits */
	private int moneySpent;
	private int cannacredits;

	/* Advance variable */
	private int totalAdvances;
	private int advancePoints;
	private int[] professionAdvances = new int[Professions.PROFESSION_COUNT];

	private int[] cluesCompleted = new int[4];

	/* Bank */
	public boolean enteredPin = false;
	private String pin;

	/* Shopping variables */
	private String shopMotto;
	private String shopColor;
	private long shopCollection;

	/* Accomplisher variable */
	public boolean isCracking;

	/* Clan Chat variables */
	public Clan clan;
	public String lastClanChat = "bestbudz";

	public Clan getClan() {
	if (Server.clanManager.clanExists(getUsername())) {
		return Server.clanManager.getClan(getUsername());
	}
	return null;
	}

	private Location currentRegion = new Location(0, 0, 0);

	private final List<Stoner> stoners = new LinkedList<Stoner>();

	private StonerAnimations stonerAnimations = new StonerAnimations();

	private RunEnergy runEnergy = new RunEnergy(this);

	private MovementHandler movementHandler = new StonerMovementHandler(this);

	private final CombatInterface combatInterface = new StonerCombatInterface(this);

	private Following following = new StonerFollowing(this);

	private PrivateMessaging privateMessaging = new PrivateMessaging(this);

	private Box box = new Box(this);

	private Bank bank = new Bank(this);

	private MoneyPouch pouch = new MoneyPouch(this);

	private Trade trade = new Trade(this);

	private StonerAssistant stonerAssistant = new StonerAssistant(this);

	private Shopping shopping = new Shopping(this);

	private Equipment equipment = new Equipment(this);

	private SpecialAssault specialAssault = new SpecialAssault(this);

	private Consumables consumables = new Consumables(this);

	private LocalGroundItems groundItems = new LocalGroundItems(this);

	private final ItemDegrading degrading = new ItemDegrading();

	private Profession profession = new Profession(this);

	private MageProfession mage = new MageProfession(this);

	private SagittariusProfession sagittarius = new SagittariusProfession(this);

	private Melee melee = new Melee();

	private NecromanceBook necromance = new NecromanceBook(this);

	private Fisher fisher = new Fisher(this);

	private Mercenary mercenary = new Mercenary(this);

	private final Summoning summoning = new Summoning(this);

	private final PriceChecker priceChecker = new PriceChecker(this, 28);

	private final RareDropEP rareDropEP = new RareDropEP();

	private StonerOwnedShops stonerShop = new StonerOwnedShops(this);

	private Dialogue dialogue = null;

	private Skulling skulling = new Skulling();

	private LocalObjects objects = new LocalObjects(this);

	private Controller controller = ControllerManager.DEFAULT_CONTROLLER;

	private InterfaceManager interfaceManager = new InterfaceManager();

	private StonerMinigames minigames = new StonerMinigames(this);

	private Dueling dueling = new Dueling(this);

	private TzharrDetails jadDetails = new TzharrDetails();

	private Cultivation cultivation = new Cultivation(this);

	private StonerTitle stonerTitle;

	private ToxicBlowpipe toxicBlowpipe = new ToxicBlowpipe(null, 0);

	private TridentOfTheSeas seasTrident = new TridentOfTheSeas(0);

	private TridentOfTheSwamp swampTrident = new TridentOfTheSwamp(0);

	private SerpentineHelmet serpentineHelment = new SerpentineHelmet(0);

	private StonerProperties properties = new StonerProperties(this);

	private boolean starter = false;
	private String username;
	private String password;
	private long usernameToLong;
	private int rights = 0;

	private long generalDelay;

	private long lastRequestedLookup;

	private boolean visible = true;
	private int currentSongId = -1;
	private int chatColor;
	private int chatEffects;
	private byte[] chatText;
	private byte gender = 0;
	private int[] appearance = new int[7];
	private byte[] colors = new byte[5];

	private short npcAppearanceId = -1;
	private boolean appearanceUpdateRequired = false;
	private boolean chatUpdateRequired = false;
	private boolean needsPlacement = false;

	private boolean resetMovementQueue = false;
	private byte screenBrightness = 3;
	private byte multipleMouseButtons = 0;
	private byte chatEffectsEnabled = 0;
	private byte splitPrivateChat = 0;
	private byte transparentPanel = 0;
	private byte transparentChatbox = 0;
	private byte sideStones = 0;

	private byte acceptAid = 0;
	private long currentStunDelay;
	private long setStunDelay;

	private long lastAction = System.currentTimeMillis();
	private int enterXSlot = -1;
	private int enterXInterfaceId = -1;

	private int enterXItemId = 1;

	private boolean jailed = false;
	private long jailLength = 0;
	private long banLength = 0;
	private long muteLength = 0;
	private boolean banned = false;
	private boolean muted = false;

	private boolean yellMuted = false;

	private String yellTitle = "Stoner";

	public long timeout = 0L;

	public long aggressionDelay = System.currentTimeMillis();

	private int necromanceInterface;
	private int yearCreated = 0;

	private int dayCreated = 0;
	private int lastLoginDay = 0;

	private int lastLoginYear = 0;
	private byte musicVolume = 0;

	private byte soundVolume = 0;

	private int chillPoints = 50;
	private int mercenaryPoints = 0;
	private int pestPoints = 0;

	private int blackMarks = 0;

	private byte[] pouches = new byte[4];

	public Stoner() {
	ChangeAppearancePacket.setToDefault(this);
	client = new Client(null);
	usernameToLong = 0L;
	}

	public Stoner(Client client) {
	this.client = client;

	getLocation().setAs(new Location(StonerConstants.HOME));

	setNpc(false);
	}

	public NecromanceBook getNecromance() {
	return necromance;
	}

	public StonerProperties getProperties() {
	return properties;
	}

	public ToxicBlowpipe getToxicBlowpipe() {
	return toxicBlowpipe;
	}

	public void setToxicBlowpipe(ToxicBlowpipe toxicBlowpipe) {
	this.toxicBlowpipe = toxicBlowpipe;
	}

	public long getGeneralDelay() {
	return generalDelay;
	}

	public void setGeneralDelay(long generalDelay) {
	this.generalDelay = generalDelay;
	}

	public long getLastRequestedLookup() {
	return lastRequestedLookup;
	}

	public void setLastRequestedLookup(long lastRequestedLookup) {
	this.lastRequestedLookup = lastRequestedLookup;
	}

	public Cultivation getCultivation() {
	return cultivation;
	}

	public void setCultivation(Cultivation cultivation) {
	this.cultivation = cultivation;
	}

	public StonerTitle getStonerTitle() {
	return stonerTitle;
	}

	public void setStonerTitle(StonerTitle stonerTitle) {
	this.stonerTitle = stonerTitle;
	}

	public void setLastLike(long lastLike) {
	this.lastLike = lastLike;
	}

	public long getLastLike() {
	return lastLike;
	}

	public void addLike() {
	likesGiven++;
	}

	public void setLikesGiven(byte likesGiven) {
	this.likesGiven = likesGiven;
	}

	public byte getLikesGiven() {
	return likesGiven;
	}

	public boolean canLike() {
	if (likesGiven < 3) {
		return true;
	}
	return lastLike == 0 || TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - lastLike) == 24;
	}

	@Override
	public void afterCombatProcess(Entity assault) {
	combatInterface.afterCombatProcess(assault);
	}

	@Override
	public boolean canAssault() {
	return combatInterface.canAssault();
	}

	public boolean canSave() {
	return controller.canSave();
	}

	public void changeZ(int z) {
	getLocation().setZ(z);
	needsPlacement = true;

	objects.onRegionChange();
	groundItems.onRegionChange();

	getMovementHandler().reset();

	send(new SendMapRegion(this));
	}

	@Override
	public void checkForDeath() {
	combatInterface.checkForDeath();
	}

	public void checkForRegionChange() {
	int deltaX = getLocation().getX() - getCurrentRegion().getRegionX() * 8;
	int deltaY = getLocation().getY() - getCurrentRegion().getRegionY() * 8;

	if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88))
		send(new SendMapRegion(this));
	}

	public void clearClanChat() {
	;
	send(new SendString("Chilling in: ", 18139));
	send(new SendString("Grower: ", 18140));
	for (int j = 18144; j < 18244; j++) {
		send(new SendString("", j));
	}
	}

	public void setClanData() {
	boolean exists = Server.clanManager.clanExists(getUsername());
	if (!exists || clan == null) {
		send(new SendString("Join Cult", 18135));
		send(new SendString("", 18139));
		send(new SendString("", 18140));
	}
	if (!exists) {
		send(new SendString("You been excommunicated", 53706));
		String title = "";
		for (int id = 53707; id < 53717; id += 3) {
			if (id == 53707) {
				title = "Stoners";
			} else if (id == 53710) {
				title = "Stoners";
			} else if (id == 53713) {
				title = "Stoner+";
			} else if (id == 53716) {
				title = "Only stoner";
			}
			send(new SendString(title, id + 2));
		}
		for (int index = 0; index < 100; index++) {
			send(new SendString("", 53723 + index));
		}
		for (int index = 0; index < 100; index++) {
			send(new SendString("", 18424 + index));
		}
		return;
	}
	Clan clan = Server.clanManager.getClan(getUsername());
	send(new SendString(clan.getTitle(), 53706));
	String title = "";
	for (int id = 53707; id < 53717; id += 3) {
		if (id == 53707) {
			title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
		} else if (id == 53710) {
			title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
		} else if (id == 53713) {
			title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
		} else if (id == 53716) {
			title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
		}
		send(new SendString(title, id + 2));
	}
	if (clan.rankedMembers != null) {
		for (int index = 0; index < 100; index++) {
			if (index < clan.rankedMembers.size()) {
				send(new SendString("<clan=" + clan.ranks.get(index) + ">" + clan.rankedMembers.get(index), 43723 + index));
			} else {
				send(new SendString("", 43723 + index));
			}
		}
	}
	if (clan.bannedMembers != null) {
		for (int index = 0; index < 100; index++) {
			if (index < clan.bannedMembers.size()) {
				send(new SendString(clan.bannedMembers.get(index), 43824 + index));
			} else {
				send(new SendString("", 43824 + index));
			}
		}
	}
	}

	public void doAgressionCheck() {

	if (!controller.canAssaultNPC()) {
		return;
	}

	short[] override = new short[3];

	if ((getCombat().inCombat()) && (!inMultiArea())) {
		return;
	}

	if ((getCombat().inCombat()) && (getCombat().getLastAssaultedBy().isNpc())) {
		Mob m = World.getNpcs()[getCombat().getLastAssaultedBy().getIndex()];

		if (m != null) {
			if (m.getId() == 2215) {
				override[0] = 2216;
				override[1] = 2217;
				override[2] = 2218;
			} else if (m.getId() == 3162) {
				override[0] = 3163;
				override[1] = 3164;
				override[2] = 3165;
			} else if (m.getId() == 2205) {
				override[0] = 2206;
				override[1] = 2207;
				override[2] = 2208;
			} else if (m.getId() == 3129) {
				override[0] = 3130;
				override[1] = 3131;
				override[2] = 3132;
			}
		}

	}

	for (Mob i : getClient().getNpcs())
		if ((i.getCombat().getAssaulting() == null) && (i.getCombatDefinition() != null)) {
			boolean overr = false;

			for (short j : override) {
				if ((short) i.getId() == j) {
					overr = true;
					break;
				}
			}

			if (overr && i.inWilderness()) {
				continue;
			}

			if (!overr && GodWarsData.forId(i.getId()) == null) {
				if (System.currentTimeMillis() - aggressionDelay >= 60000 * 8) {
					continue;
				}
			}

			if ((i.getLocation().getZ() == getLocation().getZ()) && (!i.isWalkToHome())) {

				if (getController().equals(ControllerManager.GOD_WARS_CONTROLLER)) {
					GodWarsNpc npc = GodWarsData.forId(i.getId());

					if (npc != null) {
						if (!GodWarsData.isProtected(this, npc) && !i.getCombat().inCombat()) {
							if (Math.abs(getLocation().getX() - i.getLocation().getX()) + Math.abs(getLocation().getY() - i.getLocation().getY()) <= 25) {
								i.getCombat().setAssault(this);
								i.getFollowing().setFollow(this, Following.FollowType.COMBAT);
							}
						}
					}

					continue;
				}

				if (MobConstants.isAggressive(i.getId()) && (!i.getCombat().inCombat() || i.inMultiArea())) {
					if ((MobConstants.isAgressiveFor(i, this))) {
						if ((overr) || (Math.abs(getLocation().getX() - i.getLocation().getX()) + Math.abs(getLocation().getY() - i.getLocation().getY()) <= i.getSize() * 2))
							i.getCombat().setAssault(this);
					}
				}
			}
		}
	}

	public void doFadeTeleport(final Location l, final boolean setController) {
	send(new SendInterface(18460));

	setController(Tutorial.TUTORIAL_CONTROLLER);

	final Stoner stoner = this;

	TaskQueue.queue(new Task(this, 1) {
		byte pos = 0;

		@Override
		public void execute() {
		if (pos++ >= 3) {
			if (pos == 3) {
				teleport(l);
				send(new SendInterface(18452));
			} else if (pos == 5) {
				send(new SendRemoveInterfaces());

				if (setController) {
					setController(ControllerManager.DEFAULT_CONTROLLER);
					ControllerManager.setControllerOnWalk(stoner);
				}

				stop();
			}
		}
		}

		@Override
		public void onStop() {
		}
	});
	}

	@Override
	public boolean equals(Object o) {
	if ((o instanceof Stoner)) {
		return ((Stoner) o).getUsernameToLong() == getUsernameToLong();
	}

	return false;
	}

	public byte getAcceptAid() {
	return acceptAid;
	}

	public StonerAnimations getAnimations() {
	return stonerAnimations;
	}

	public int[] getAppearance() {
	return appearance;
	}

	public Bank getBank() {
	return bank;
	}

	public MoneyPouch getPouch() {
	return pouch;
	}

	public long getBanLength() {
	return banLength;
	}

	public int getBlackMarks() {
	return blackMarks;
	}

	public int getChatColor() {
	return chatColor;
	}

	public int getChatEffects() {
	return chatEffects;
	}

	public byte getChatEffectsEnabled() {
	return chatEffectsEnabled;
	}

	public byte[] getChatText() {
	return chatText;
	}

	public Client getClient() {
	return client;
	}

	public byte[] getColors() {
	return colors;
	}

	public Consumables getConsumables() {
	return consumables;
	}

	public Controller getController() {
	if (controller == null) {
		setController(ControllerManager.DEFAULT_CONTROLLER);
	}

	return controller;
	}

	@Override
	public int getCorrectedDamage(int damage) {
	return combatInterface.getCorrectedDamage(damage);
	}

	public Location getCurrentRegion() {
	return currentRegion;
	}

	public int getCurrentSongId() {
	return currentSongId;
	}

	public long getCurrentStunDelay() {
	return currentStunDelay;
	}

	public int getDayCreated() {
	return dayCreated;
	}

	public ItemDegrading getDegrading() {
	return degrading;
	}

	public Dialogue getDialogue() {
	return dialogue;
	}

	public Dueling getDueling() {
	return dueling;
	}

	public int getEnterXInterfaceId() {
	return enterXInterfaceId;
	}

	public int getEnterXItemId() {
	return enterXItemId;
	}

	public int getEnterXSlot() {
	return enterXSlot;
	}

	public Equipment getEquipment() {
	return equipment;
	}

	public Fisher getFisher() {
	return fisher;
	}

	@Override
	public Following getFollowing() {
	return following;
	}

	public byte getGender() {
	return gender;
	}

	public LocalGroundItems getGroundItems() {
	return groundItems;
	}

	public InterfaceManager getInterfaceManager() {
	return interfaceManager;
	}

	public Box getBox() {
	return box;
	}

	public ItemDegrading getItemDegrading() {
	return degrading;
	}

	public TzharrDetails getJadDetails() {
	return jadDetails;
	}

	public long getLastAction() {
	return lastAction;
	}

	public int getLastLoginDay() {
	return lastLoginDay;
	}

	public int getLastLoginYear() {
	return lastLoginYear;
	}

	public MageProfession getMage() {
	return mage;
	}

	@Override
	public int getMaxHit(CombatTypes type) {
	return combatInterface.getMaxHit(type);
	}

	public Melee getMelee() {
	return melee;
	}

	public StonerMinigames getMinigames() {
	return minigames;
	}

	@Override
	public MovementHandler getMovementHandler() {
	return movementHandler;
	}

	public byte getMultipleMouseButtons() {
	return multipleMouseButtons;
	}

	public byte getMusicVolume() {
	return musicVolume;
	}

	public long getMuteLength() {
	return muteLength;
	}

	public int getNpcAppearanceId() {
	return npcAppearanceId;
	}

	public LocalObjects getObjects() {
	return objects;
	}

	public String getPassword() {
	return password;
	}

	public int getPestPoints() {
	return pestPoints;
	}

	public List<Stoner> getStoners() {
	return stoners;
	}

	public StonerOwnedShops getStonerShop() {
	return stonerShop;
	}

	public byte[] getPouches() {
	return pouches;
	}

	public int getNecromanceInterface() {
	return necromanceInterface;
	}

	public PrivateMessaging getPrivateMessaging() {
	return privateMessaging;
	}

	public SagittariusProfession getSagittarius() {
	return sagittarius;
	}

	public RareDropEP getRareDropEP() {
	return rareDropEP;
	}

	public int getRights() {
	return rights;
	}

	public RunEnergy getRunEnergy() {
	return runEnergy;
	}

	public byte getScreenBrightness() {
	return screenBrightness;
	}

	public long getSetStunDelay() {
	return setStunDelay;
	}

	public Shopping getShopping() {
	return shopping;
	}

	public Profession getProfession() {
	return profession;
	}

	public Skulling getSkulling() {
	return skulling;
	}

	public Mercenary getMercenary() {
	return mercenary;
	}

	public int getMercenaryPoints() {
	return mercenaryPoints;
	}

	public void addMercenaryPoints(int amount) {
	mercenaryPoints += amount;
	}

	public byte getSoundVolume() {
	return soundVolume;
	}

	public SpecialAssault getSpecialAssault() {
	return specialAssault;
	}

	public byte getSplitPrivateChat() {
	return splitPrivateChat;
	}

	public Summoning getSummoning() {
	return summoning;
	}

	public PriceChecker getPriceChecker() {
	return priceChecker;
	}

	public Trade getTrade() {
	return trade;
	}

	public StonerAssistant getPA() {
	return stonerAssistant;
	}

	public String getUsername() {
	return username;
	}

	public long getUsernameToLong() {
	return usernameToLong;
	}

	public int getChillPoints() {
	return chillPoints;
	}

	public int getYearCreated() {
	return yearCreated;
	}

	@Override
	public void hit(Hit hit) {
	combatInterface.hit(hit);
	}

	public void incrDeaths() {
	deaths = ((short) (deaths + 1));
	InterfaceHandler.writeText(new QuestTab(this));
	}

	public boolean isAppearanceUpdateRequired() {
	return appearanceUpdateRequired;
	}

	public boolean isBanned() {
	return banned;
	}

	public boolean isBusy() {
	return (interfaceManager.hasBankOpen()) || interfaceManager.hasInterfaceOpen() || (trade.trading()) || (dueling.isStaking());
	}

	public boolean isBusyNoInterfaceCheck() {
	return (interfaceManager.hasBankOpen()) || (trade.trading()) || (dueling.isStaking());
	}

	public boolean isChatUpdateRequired() {
	return chatUpdateRequired;
	}

	@Override
	public boolean isIgnoreHitSuccess() {
	return combatInterface.isIgnoreHitSuccess();
	}

	public boolean isMuted() {
	return muted;
	}

	public boolean isResetMovementQueue() {
	return resetMovementQueue;
	}

	public boolean isStarter() {
	return starter;
	}

	public boolean isVisible() {
	return visible;
	}

	public boolean isYellMuted() {
	return yellMuted;
	}

	/**
	 * Adds the connecting user the the default clan chat channel
	 */
	public void addDefaultChannel() {
	if (clan == null) {
		Clan localClan = Server.clanManager.getClan("bestbudz");
		if (localClan != null)
			localClan.addMember(this);
		else {
			send(new SendMessage(Utility.formatStonerName("") + " has not created a Cult yet."));
		}
	}
	}

	/**
	 * Handles login
	 * 
	 * @param starter
	 * @return
	 * @throws Exception
	 */
	public boolean login(boolean starter) throws Exception {
	this.starter = starter;

	username = NameUtil.uppercaseFirstLetter(username);

	usernameToLong = Utility.nameToLong(username.toLowerCase());

	int response = 2;

	if ((password.length() == 0) || (username.length() == 0) || (username.length() > 12)) {
		response = 3;
	} else if ((banned) || (StonerSaveUtil.isIPBanned(this))) {
		response = 4;
	} else if ((password != null) && (!password.equals(client.getEnteredPassword()))) {
		response = 3;
	} else if (World.isUpdating()) {
		response = 14;
	} else if (World.getStonerByName(username) != null) {
		response = 5;
	} else if (World.register(this) == -1) {
		response = 7;
	}

	if (response != 2) {
		StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
		resp.writeByte(response);
		resp.writeByte(rights);
		resp.writeByte(0);
		client.send(resp.getBuffer());
		return false;
	}

	new SendLoginResponse(response, this.getRights()).execute(client);

	send(new SendString(getMoneyPouch() + "", 8135));

	if (StonerSaveUtil.isIPMuted(this)) {
		setMuted(true);
		setMuteLength(-1);
	}

	if (this.inCyclops()) {
		this.teleport(StonerConstants.HOME);
	}

	ControllerManager.setControllerOnWalk(this);

	if (Region.getRegion(getLocation().getX(), getLocation().getY()) == null) {
		teleport(new Location(StonerConstants.HOME));
		send(new SendMessage("You been saved from the unknown."));
	}

	if (isJailed() && !inJailed()) {
		teleport(new Location(StonerConstants.JAILED_AREA));
		send(new SendMessage("You were put to jail man!"));
	}

	if (this.inWGGame()) {
		WeaponGame.leaveGame(this, false);
	}

	movementHandler.getLastLocation().setAs(new Location(getLocation().getX(), getLocation().getY() + 1, getLocation().getZ()));

	for (int i = 0; i < StonerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
		if (i != 5 && i != 6) {
			send(new SendSidebarInterface(i, StonerConstants.SIDEBAR_INTERFACE_IDS[i]));
		}
	}

	if (mage.getMageBook() == 0) {
		mage.setMageBook(1151);
	}

	if (necromanceInterface == 0) {
		necromanceInterface = 5608;
		necromance = new NecromanceBook(this);
	}

	send(new SendSidebarInterface(5, necromanceInterface));

	if (starter) {
		ChangeAppearancePacket.setToDefault(this);

		this.start(new Tutorial(this));

		if (lastLoginYear == 0) {
			yearCreated = Utility.getYear();
			dayCreated = Utility.getDayOfYear();
		}
	}

	if (!ChangeAppearancePacket.validate(this)) {
		ChangeAppearancePacket.setToDefault(this);
	}

	equipment.onLogin();
	profession.onLogin();
	mage.onLogin();
	this.setScreenBrightness((byte) 4);
	privateMessaging.connect();
	runEnergy.update();
	necromance.disable();

	bank.update();

	if (this.getEquipment().getItems()[5] != null && this.getEquipment().getItems()[5].getId() == 13742) {
		this.getEquipment().getItems()[5].setId(11283);
		this.getEquipment().update();
	}

	jadDetails.setStage(0);

	this.getRunEnergy().setRunning(true);

	Emotes.onLogin(this);

	InterfaceHandler.writeText(new QuestTab(this));
	InterfaceHandler.writeText(new CreditTab(this));
	send(new SendString("</col>CannaCredits: @gre@" + Utility.format(this.getCredits()), 52504));

	box.update();

	send(new SendStonerOption("Stalk", 4));
	send(new SendStonerOption("Deal with", 5));

	send(new SendConfig(166, screenBrightness));
	send(new SendConfig(171, multipleMouseButtons));
	send(new SendConfig(172, chatEffectsEnabled));
	send(new SendConfig(287, splitPrivateChat));
	send(new SendConfig(427, acceptAid));
	send(new SendConfig(172, isRetaliate() ? 1 : 0));
	send(new SendConfig(173, getRunEnergy().isRunning() ? 1 : 0));
	send(new SendConfig(168, musicVolume));
	send(new SendConfig(169, soundVolume));
	send(new SendConfig(876, 0));
	send(new SendConfig(1032, profilePrivacy ? 1 : 2));

	cultivation.doCalculations();
	cultivation.getAllotment().updateAllotmentsStates();
	cultivation.getWeeds().updateWeedsStates();
	cultivation.getTrees().updateTreeStates();
	cultivation.getFruitTrees().updateFruitTreeStates();
	cultivation.getFlowers().updateFlowerStates();
	cultivation.getSpecialPlantOne().updateSpecialPlants();
	cultivation.getSpecialPlantTwo().updateSpecialPlants();
	cultivation.getHops().updateHopsStates();
	cultivation.getBushes().updateBushesStates();

	for (int i = 0; i < 4; i++) {
		cultivation.getCompost().updateCompostBin(i);
	}

	send(new SendExpCounter(0, 0));

	LoyaltyShop.load(this);

	for (int i = 0; i < professionGoals.length; i++) {
		send(new SendProfessionGoal(i, professionGoals[i][0], professionGoals[i][1], professionGoals[i][2]));
	}

	send(new SendConfig(77, 0));

	getUpdateFlags().setUpdateRequired(true);
	appearanceUpdateRequired = true;
	needsPlacement = true;

	send(new SendMessage("<img=2>You landed in Bestbudz, only to get lifted by best buds.<img=2>"));

	if (BestbudzConstants.doubleExperience) {
		send(new SendMessage("<img=3>@bla@Get lit yall, it's Double Gains!<img=3>"));
	}

	controller.onControllerInit(this);

	stonerShop.setName(username);

	for (Necromance necromance : Necromance.values()) {
		send(new SendConfig(630 + necromance.ordinal(), this.necromance.isQuickNecromance(necromance) ? 1 : 0));
		send(new SendConfig(necromance.getConfigId(), 0));
	}

	clearClanChat();
	setClanData();
	if (lastClanChat != null && lastClanChat.length() > 0) {
		Clan clan = Server.clanManager.getClan(lastClanChat);
		if (clan != null)
			clan.addMember(this);
	} else {
		addDefaultChannel();
	}
	MiscInterfaces.startUp(this);
	if (StonerConstants.isStaff(this)) {
		send(new SendString("Staff tab", 29413));
	} else {
		send(new SendString("", 29413));
	}
	send(new SendConfig(1990, getTransparentPanel()));
	send(new SendConfig(1991, getTransparentChatbox()));
	send(new SendConfig(1992, getSideStones()));
	TextChannel channel = (TextChannel) GameDataLoader.discord.getChannelById("947618084708044861").get();
	String ts = "";
	ts = ts + "**" + this.getUsername() + " came to get high asf.**";
	channel.sendMessage(ts);
	return true;
	}

	public void logout(boolean force) {
	if (isActive()) {
		if (force) {
			ControllerManager.onForceLogout(this);

		} else if ((controller != null) && (!controller.canLogOut())) {
			return;
		}

		World.remove(client.getNpcs());

		if (controller != null) {
			controller.onDisconnect(this);
		}

		if (trade.trading()) {
			trade.end(false);
		}

		if (this.getInterfaceManager().main == 48500) {
			this.getPriceChecker().withdrawAll();
		}

		if (clan != null) {
			clan.removeMember(getUsername());
		}

		if (TargetSystem.getInstance().stonerHasTarget(this)) {
			TargetSystem.getInstance().resetTarget(this, true);
		}

		if (dueling.isStaking()) {
			dueling.decline();
		}

		if (summoning.hasFamiliar()) {
			summoning.removeForLogout();
		}

		if (getBossPet() != null) {
			BossPets.onLogout(this);
		}

		if (DwarfMultiCannon.hasCannon(this)) {
			DwarfMultiCannon.getCannon(this).onLogout();
		}

		StonerSave.save(this);

		if (!BestbudzConstants.DEV_MODE) {
		}
	}

	TextChannel channel = (TextChannel) GameDataLoader.discord.getChannelById("947618084708044861").get();
	String ts = "";
	ts = ts + "**" + this.getUsername() + " is way too stoned.**";
	channel.sendMessage(ts);

	World.unregister(this);
	client.setStage(Client.Stages.LOGGED_OUT);
	setActive(false);

	new SendLogout().execute(client);
	client.disconnect();
	}

	public boolean needsPlacement() {
	return needsPlacement;
	}

	@Override
	public void onAssault(Entity assault, int hit, CombatTypes type, boolean success) {
	combatInterface.onAssault(assault, hit, type, success);
	}

	@Override
	public void onCombatProcess(Entity assault) {
	combatInterface.onCombatProcess(assault);
	}

	public void onControllerFinish() {
	controller = ControllerManager.DEFAULT_CONTROLLER;
	}

	@Override
	public void onHit(Entity e, Hit hit) {
	combatInterface.onHit(e, hit);

	if (e.isNpc()) {
		Mob m = World.getNpcs()[e.getIndex()];

		if (m != null) {
			rareDropEP.forHitOnMob(this, m, hit);
		}
	}
	}

	@Override
	public void poison(int start) {
	if (isPoisoned()) {
		return;
	}

	super.poison(start);

	if (isActive())
		send(new SendMessage("You smoked spice and went on a BAD trip!"));
	}

	@Override
	public void process() throws Exception {

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

	if (controller != null) {
		controller.tick(this);
	}

	shopping.update();

	necromance.drain();

	following.process();

	getCombat().process();

	doAgressionCheck();
	}

	@Override
	public void reset() {
	following.updateWaypoint();
	appearanceUpdateRequired = false;
	chatUpdateRequired = false;
	resetMovementQueue = false;
	needsPlacement = false;
	getMovementHandler().resetMoveDirections();
	getUpdateFlags().setUpdateRequired(false);
	getUpdateFlags().reset();
	}

	public void resetAggression() {
	aggressionDelay = System.currentTimeMillis();
	}

	@Override
	public void retaliate(Entity assaulted) {
	if (assaulted != null) {
		if (isRetaliate() && getCombat().getAssaulting() == null && !getMovementHandler().moving()) {
			getCombat().setAssault(assaulted);
		}
	}
	}

	public void send(OutgoingPacket o) {
	client.queueOutgoingPacket(o);
	}

	public void setAcceptAid(byte acceptAid) {
	this.acceptAid = acceptAid;
	}

	public void setAppearance(int[] appearance) {
	this.appearance = appearance;
	}

	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
	if (appearanceUpdateRequired) {
		getUpdateFlags().setUpdateRequired(true);
	}
	this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public void setBanLength(long banLength) {
	this.banLength = banLength;
	}

	public void setBanned(boolean banned) {
	this.banned = banned;
	}

	public void setBlackMarks(int blackMarks) {
	this.blackMarks = blackMarks;
	}

	public void setChatColor(int chatColor) {
	this.chatColor = chatColor;
	}

	public void setChatEffects(int chatEffects) {
	this.chatEffects = chatEffects;
	}

	public void setChatEffectsEnabled(byte chatEffectsEnabled) {
	this.chatEffectsEnabled = chatEffectsEnabled;
	}

	public void setChatText(byte[] chatText) {
	this.chatText = chatText;
	}

	public void setChatUpdateRequired(boolean chatUpdateRequired) {
	if (chatUpdateRequired) {
		getUpdateFlags().setUpdateRequired(true);
	}
	this.chatUpdateRequired = chatUpdateRequired;
	}

	public void setColors(byte[] colors) {
	this.colors = colors;
	}

	public boolean setController(Controller controller) {
	this.controller = controller;
	controller.onControllerInit(this);
	return true;
	}

	public boolean setControllerNoInit(Controller controller) {
	this.controller = controller;
	return true;
	}

	public void setCurrentRegion(Location currentRegion) {
	this.currentRegion = currentRegion;
	}

	public void setCurrentSongId(int currentSongId) {
	this.currentSongId = currentSongId;
	}

	public void setCurrentStunDelay(long delay) {
	currentStunDelay = delay;
	}

	public void setDayCreated(int dayCreated) {
	this.dayCreated = dayCreated;
	}

	public void setDialogue(Dialogue d) {
	dialogue = d;
	}

	public void setEnterXInterfaceId(int enterXInterfaceId) {
	this.enterXInterfaceId = enterXInterfaceId;
	}

	public void setEnterXItemId(int enterXItemId) {
	this.enterXItemId = enterXItemId;
	}

	public void setEnterXSlot(int enterXSlot) {
	this.enterXSlot = enterXSlot;
	}

	public void setGender(byte gender) {
	this.gender = gender;
	}

	public void setLastAction(long lastAction) {
	this.lastAction = lastAction;
	}

	public void setLastLoginDay(int lastLoginDay) {
	this.lastLoginDay = lastLoginDay;
	}

	public void setLastLoginYear(int lastLoginYear) {
	this.lastLoginYear = lastLoginYear;
	}

	public void setMultipleMouseButtons(byte multipleMouseButtons) {
	this.multipleMouseButtons = multipleMouseButtons;
	}

	public void setMusicVolume(byte musicVolume) {
	this.musicVolume = musicVolume;
	}

	public void setMuted(boolean muted) {
	this.muted = muted;
	}

	public void setMuteLength(long muteLength) {
	this.muteLength = muteLength;
	}

	public void setNeedsPlacement(boolean needsPlacement) {
	this.needsPlacement = needsPlacement;
	}

	public void setNpcAppearanceId(short npcAppearanceId) {
	this.npcAppearanceId = npcAppearanceId;
	}

	public void setPassword(String password) {
	this.password = password;
	}

	public void setPestPoints(int pestPoints) {
	this.pestPoints = pestPoints;
	}

	public void setPouches(byte[] pouches) {
	this.pouches = pouches;
	}

	public void setNecromanceInterface(int necromanceInterface) {
	this.necromanceInterface = necromanceInterface;
	}

	public void setResetMovementQueue(boolean resetMovementQueue) {
	this.resetMovementQueue = resetMovementQueue;
	}

	public void setRights(int rights) {
	this.rights = rights;
	}

	public void setScreenBrightness(byte screenBrightness) {
	this.screenBrightness = screenBrightness;
	}

	public void setSetStunDelay(long delay) {
	setStunDelay = delay;
	}

	public void setMercenaryPoints(int mercenaryPoints) {
	this.mercenaryPoints = mercenaryPoints;
	}

	public void setSoundVolume(byte soundVolume) {
	this.soundVolume = soundVolume;
	}

	public void setSplitPrivateChat(byte splitPrivateChat) {
	this.splitPrivateChat = splitPrivateChat;
	}

	public void setStarter(boolean starter) {
	this.starter = starter;
	}

	public void setUsername(String username) {
	this.username = username;
	}

	public void setDisplay(String display) {
	this.display = display;
	}

	public String display;

	private int[][] professionGoals = new int[Professions.PROFESSION_COUNT + 1][3];

	private double expCounter;

	private int achievementsPoints;

	/* Barrows variable */
	private boolean[] killRecord = new boolean[Brother.values().length];

	private int killCount;
	private boolean chestClicked;

	public boolean isChestClicked() {
	return chestClicked;
	}

	public void setBarrowsKC(int killCount) {
	this.killCount = killCount;
	}

	public int getBarrowsKC() {
	return killCount;
	}

	public void setChestClicked(boolean chestClicked) {
	this.chestClicked = chestClicked;
	}

	public void setKillRecord(boolean[] killRecord) {
	this.killRecord = killRecord;
	}

	public boolean[] getKillRecord() {
	return killRecord;
	}

	public String getDisplay() {
	return display;
	}

	public void setVisible(boolean visible) {
	this.visible = visible;
	}

	public void setChillPoints(int chillPoints) {
	this.chillPoints = chillPoints;
	}

	public void setYearCreated(int yearCreated) {
	this.yearCreated = yearCreated;
	}

	public void setYellMuted(boolean yellMuted) {
	this.yellMuted = yellMuted;
	}

	public void start() {
	runEnergy.tick();
	startRegeneration();
	specialAssault.tick();
	summoning.onLogin();
	skulling.tick(this);
	if (jadDetails.getStage() != 0) {
		TzharrGame.loadGame(this);
	}
	if (getTeleblockTime() > 0) {
		tickTeleblock();
	}
	}

	public void start(Dialogue dialogue) {
	this.dialogue = dialogue;
	if (dialogue != null) {
		dialogue.setNext(0);
		dialogue.setStoner(this);
		dialogue.execute();
	} else if (getAttributes().get("pauserandom") != null) {
		getAttributes().remove("pauserandom");
	}
	}

	@Override
	public void teleblock(int i) {
	super.teleblock(i);
	}

	public void teleport(Location location) {
	boolean zChange = location.getZ() != getLocation().getZ();

	setTakeDamage(false);
	getLocation().setAs(location);
	setResetMovementQueue(true);
	setNeedsPlacement(true);
	movementHandler.getLastLocation().setAs(new Location(getLocation().getX(), getLocation().getY() + 1));
	getAttributes().remove("combatsongdelay");

	send(new SendRemoveInterfaces());
	send(new SendWalkableInterface(-1));

	ControllerManager.setControllerOnWalk(this);

	TaskQueue.cancelHitsOnEntity(this);
	TaskQueue.queue(new FinishTeleportingTask(this, 5));

	movementHandler.reset();

	if (!inClanWarsFFA()) {
		if (zChange) {
			send(new SendMapRegion(this));
		} else {
			checkForRegionChange();
		}
	}

	if (trade.trading()) {
		trade.end(false);
	} else if (dueling.isStaking()) {
		dueling.decline();
	}

	if (this.getBossPet() != null) {
		this.getBossPet().remove();
		final Mob mob = new Mob(this, this.getBossID(), false, false, true, this.getLocation());
		mob.getFollowing().setIgnoreDistance(true);
		mob.getFollowing().setFollow(this);
		this.setBossPet(mob);
	}

	TaskQueue.onMovement(this);
	}

	@Override
	public String toString() {
	return "Stoner(" + getUsername() + ":" + getPassword() + " - " + client.getHost() + ")";
	}

	@Override
	public void updateCombatType() {
	/**
	 * This shit was gone?
	 */
	CombatTypes type;
	if (mage.getSpellCasting().isCastingSpell())
		type = CombatTypes.MAGE;
	else {
		type = EquipmentConstants.getCombatTypeForWeapon(this);
	}

	if (type != CombatTypes.MAGE) {
		send(new SendConfig(333, 0));
	}

	getCombat().setCombatType(type);

	switch (type) {
	case MELEE:
		equipment.updateMeleeDataForCombat();
		break;
	case SAGITTARIUS:
		equipment.updateSagittariusDataForCombat();
		break;
	default:
		break;
	}
	}

	public boolean withinRegion(Location other) {
	int deltaX = other.getX() - currentRegion.getRegionX() * 8;
	int deltaY = other.getY() - currentRegion.getRegionY() * 8;

	if ((deltaX < 2) || (deltaX > 110) || (deltaY < 2) || (deltaY > 110)) {
		return false;
	}

	return true;
	}

	/**
	 * Detequarrys the rank
	 * 
	 * @return
	 */
	public String deterquarryRank(Stoner stoner) {
	switch (this.getRights()) {

	case 0:
		return "Stoner";
	case 1:
		return "<col=006699>Moderator</col>";
	case 2:
		return "<col=E6E600>Adminstrator</col>";
	case 3:
		return "<col=AB1818>Owner</col>";
	case 4:
		return "<col=CF1DCF>Developer</col>";
	case 5:
		return "<col=B20000>Babylonian</col>";
	case 6:
		return "<col=223ca9>Ganjaman</col>";
	case 7:
		return "<col=2EB8E6>Rastaman</col>";
	case 8:
		return "<col=971FF2>Waldo</col>";
	case 11:
		return "@gry@Dealer</col>";
	case 12:
		return "@gre@Grower</col>";

	}
	return "Unknown!";
	}

	public String deterquarryIcon(Stoner stoner) {
	switch (this.getRights()) {
	case 0:
		return "<img=11>";
	case 1:
		return "<img=0>";
	case 2:
		return "<img=1>";
	case 3:
		return "<img=2>";
	case 4:
		return "<img=3>";
	case 5:
		return "<img=4>";
	case 6:
		return "<img=5>";
	case 7:
		return "<img=6>";
	case 8:
		return "<img=7>";
	case 9:
		return "<img=8>";
	case 11:
		return "<img=10>";
	case 12:
		return "<img=11>";
	}
	return "";
	}

	public int getAchievementsPoints() {
	return achievementsPoints;
	}

	public void addAchievementPoints(int points) {
	achievementsPoints = points;
	}

	public HashMap<AchievementList, Integer> getStonerAchievements() {
	return stonerAchievements;
	}

	public int getTeleportTo() {
	return teleportTo;
	}

	public void setTeleportTo(int teleportTo) {
	this.teleportTo = teleportTo;
	}

	public int[][] getProfessionGoals() {
	return professionGoals;
	}

	public double getCounterExp() {
	return expCounter;
	}

	public void addCounterExp(double exp) {
	expCounter += exp;
	}

	public void setProfessionGoals(int[][] professionGoals) {
	this.professionGoals = professionGoals;
	}

	public String getShopMotto() {
	return shopMotto;
	}

	public void setShopMotto(String shopMotto) {
	this.shopMotto = shopMotto;
	}

	public long getShopCollection() {
	return shopCollection;
	}

	public void setShopCollection(long shopCollection) {
	this.shopCollection = shopCollection;
	}

	public int getKills() {
	return kills;
	}

	public void setKills(int kills) {
	this.kills = kills;
	}

	public int getDeaths() {
	return deaths;
	}

	public void setDeaths(int deaths) {
	this.deaths = deaths;
	}

	public int getBountyPoints() {
	return bountyPoints;
	}

	public int setBountyPoints(int amount) {
	return bountyPoints = amount;
	}

	public int getCredits() {
	return cannacredits;
	}

	public void setCredits(int cannacredits) {
	this.cannacredits = cannacredits;
	}

	public int getMoneySpent() {
	return moneySpent;
	}

	public void setMoneySpent(int moneySpent) {
	this.moneySpent = moneySpent;
	}

	public String getPin() {
	return pin;
	}

	public void setPin(String pin) {
	this.pin = pin;
	}

	public ArrayList<String> getLastKilledStoners() {
	return lastKilledStoners;
	}

	public void setLastKilledStoners(ArrayList<String> lastKilledStoners) {
	this.lastKilledStoners = lastKilledStoners;
	}

	public int getTotalAdvances() {
	return totalAdvances;
	}

	public void setTotalAdvances(int totalAdvances) {
	this.totalAdvances = totalAdvances;
	}

	public int getAdvancePoints() {
	return advancePoints;
	}

	public void setAdvancePoints(int advancePoints) {
	this.advancePoints = advancePoints;
	}

	public int[] getProfessionAdvances() {
	return professionAdvances;
	}

	public void setProfessionAdvances(int[] professionAdvances) {
	this.professionAdvances = professionAdvances;
	}

	public Mob getBossPet() {
	return bossPet;
	}

	public void setBossPet(Mob bossPet) {
	this.bossPet = bossPet;
	}

	public boolean getProfilePrivacy() {
	return profilePrivacy;
	}

	public void setProfilePrivacy(boolean profilePrivacy) {
	this.profilePrivacy = profilePrivacy;
	}

	public int getLikes() {
	return likes;
	}

	public void setLikes(int likes) {
	this.likes = likes;
	}

	public int getDislikes() {
	return dislikes;
	}

	public void setDislikes(int dislikes) {
	this.dislikes = dislikes;
	}

	public int getProfileViews() {
	return profileViews;
	}

	public void setProfileViews(int profileViews) {
	this.profileViews = profileViews;
	}

	public String getYellTitle() {
	return yellTitle;
	}

	public void setYellTitle(String yellTitle) {
	this.yellTitle = yellTitle;
	}

	public void unlockCredit(CreditPurchase purchase) {
	unlockedCredits.add(purchase);
	}

	public boolean isCreditUnlocked(CreditPurchase purchase) {
	return unlockedCredits.contains(purchase);
	}

	public Set<CreditPurchase> getUnlockedCredits() {
	return unlockedCredits;
	}

	public void setUnlockedCredits(Set<CreditPurchase> unlockedCredits) {
	this.unlockedCredits = unlockedCredits;
	}

	public int getArenaPoints() {
	return arenaPoints;
	}

	public void setArenaPoints(int arenaPoints) {
	this.arenaPoints = arenaPoints;
	}

	public Stopwatch getDelay() {
	return delay;
	}

	public void setDelay(Stopwatch delay) {
	this.delay = delay;
	}

	public String getShopColor() {
	return shopColor;
	}

	public void setShopColor(String shopColor) {
	this.shopColor = shopColor;
	}

	public String getUid() {
	return uid;
	}

	public void setUid(String uid) {
	this.uid = uid;
	}

	public String getLastKnownUID() {
	return lastKnownUID;
	}

	public void setLastKnownUID(String uid) {
	this.lastKnownUID = uid;
	}

	public long getJailLength() {
	return jailLength;
	}

	public void setJailLength(long jailLength) {
	this.jailLength = jailLength;
	}

	public boolean isJailed() {
	return jailed;
	}

	public void setJailed(boolean jailed) {
	this.jailed = jailed;
	}

	public int getBossID() {
	return bossID;
	}

	public void setBossID(int bossID) {
	this.bossID = bossID;
	}

	public int[] getCluesCompleted() {
	return cluesCompleted;
	}

	public void setCluesCompleted(int[] cluesCompleted) {
	this.cluesCompleted = cluesCompleted;
	}

	public void setCluesCompleted(int index, int value) {
	cluesCompleted[index] = value;
	}

	public long getMoneyPouch() {
	return moneyPouch;
	}

	public void setMoneyPouch(long moneyPouch) {
	this.moneyPouch = moneyPouch;
	}

	public boolean isPouchPayment() {
	return pouchPayment;
	}

	public void setPouchPayment(boolean pouchPayment) {
	this.pouchPayment = pouchPayment;
	}

	public int getWeaponKills() {
	return weaponKills;
	}

	public void setWeaponKills(int weaponKills) {
	this.weaponKills = weaponKills;
	}

	public boolean isMember() {
	return isMember;
	}

	public void setMember(boolean isMember) {
	this.isMember = isMember;
	}

	public int getWeaponPoints() {
	return weaponPoints;
	}

	public void setWeaponPoints(int weaponPoints) {
	this.weaponPoints = weaponPoints;
	}

	public boolean isHitZulrah() {
	return hitZulrah;
	}

	public void setHitZulrah(boolean hitZulrah) {
	this.hitZulrah = hitZulrah;
	}

	public SerpentineHelmet getSerpentineHelment() {
	return serpentineHelment;
	}

	public void setSerpentineHelment(SerpentineHelmet serpentineHelment) {
	this.serpentineHelment = serpentineHelment;
	}

	public TridentOfTheSeas getSeasTrident() {
	return seasTrident;
	}

	public void setSeasTrident(TridentOfTheSeas trident) {
	this.seasTrident = trident;
	}

	public int getHunterKills() {
	return hunterKills;
	}

	public void setHunterKills(int hunterKills) {
	this.hunterKills = hunterKills;
	}

	public int getRogueKills() {
	return rogueKills;
	}

	public void setRogueKills(int rogueKills) {
	this.rogueKills = rogueKills;
	}

	public int getRogueRecord() {
	return rogueRecord;
	}

	public void setRogueRecord(int rogueRecord) {
	this.rogueRecord = rogueRecord;
	}

	public int getHunterRecord() {
	return hunterRecord;
	}

	public void setHunterRecord(int hunterRecord) {
	this.hunterRecord = hunterRecord;
	}

	public byte getTransparentPanel() {
	return transparentPanel;
	}

	public void setTransparentPanel(byte transparentPanel) {
	this.transparentPanel = transparentPanel;
	}

	public byte getTransparentChatbox() {
	return transparentChatbox;
	}

	public void setTransparentChatbox(byte transparentChatbox) {
	this.transparentChatbox = transparentChatbox;
	}

	public byte getSideStones() {
	return sideStones;
	}

	public void setSideStones(byte sideStones) {
	this.sideStones = sideStones;
	}

	public boolean isAdvanceColors() {
	return advanceColors;
	}

	public void setAdvanceColors(boolean advanceColors) {
	this.advanceColors = advanceColors;
	}

	public TridentOfTheSwamp getSwampTrident() {
	return swampTrident;
	}

	public void setSwampTrident(TridentOfTheSwamp swampTrident) {
	this.swampTrident = swampTrident;
	}

	public void hit(com.bestbudz.rs2.entity.stoner.net.in.command.impl.Hit hit) {
	}

}