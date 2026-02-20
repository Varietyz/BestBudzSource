package com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.dialogue.impl.AchievementDialogue;
import com.bestbudz.rs2.content.dialogue.impl.GenieResetDialogue;
import com.bestbudz.rs2.content.dialogue.impl.OziachDialogue;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.cannacredits.MysteryBoxMinigame;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGameConstants;
import com.bestbudz.rs2.content.profession.handiness.HideTanning;
import com.bestbudz.rs2.content.profession.handiness.JewelryCreationTask;
import com.bestbudz.rs2.content.profession.mage.spells.BoltEnchanting;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.ReportHandler;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonAssignment extends ClickButtonPacket {
	public static final Map<Integer, Location> TELEPORT_LOCATIONS = new ConcurrentHashMap<>();
	public static final Map<Integer, ShopAction> SHOP_ACTIONS = new ConcurrentHashMap<>();
	public static final Map<Integer, ButtonHandler> BUTTON_HANDLERS = new HashMap<>();
	public static final Map<Integer, String> LEADERBOARD_TYPES = new ConcurrentHashMap<>();

	public static final class ButtonIds {
		static final int RUN_TOGGLE_1 = 74;
		static final int RETALIATE_1 = 150;
		static final int RUN_TOGGLE_2 = 152;
		static final int CANCEL_REPORT = 2094;
		static final int MUSIC_VOLUME_MAX = 3162;
		static final int SOUND_VOLUME_MAX = 3173;
		static final int TRAINING_INTERFACE_2 = 4140;
		static final int PROFESSING_INTERFACE_2 = 4143;
		static final int PVP_INTERFACE_2 = 4146;
		static final int BOSS_INTERFACE_2 = 4150;
		static final int MINIGAME_INTERFACE_2 = 6004;
		static final int OTHER_INTERFACE_2 = 6005;
		static final int CLOSE_INTERFACE_1 = 9118;
		static final int LOGOUT = 9154;
		static final int SPECIAL_ATTACK_RESTORE = 29074;
		static final int RUN_TOGGLE_3 = 33230;
		static final int TRAINING_INTERFACE_1 = 50235;
		static final int PROFESSING_INTERFACE_1 = 50245;
		static final int PVP_INTERFACE_1 = 50253;
		static final int BOSS_INTERFACE_1 = 51005;
		static final int MINIGAME_INTERFACE_1 = 51013;
		static final int OTHER_INTERFACE_1 = 51023;
		static final int COMBAT_FORMULAS = 59097;
		static final int ITEMS_KEPT_ON_DEATH = 59100;
		static final int PRICE_CHECKER_OPEN = 59103;
		static final int MYSTERY_BOX = 66108;
		static final int RUN_TOGGLE_4 = 74214;
		static final int HOME_TELEPORT_2 = 75010;
		static final int CLOSE_INTERFACE_2 = 83051;
		static final int HOME_TELEPORT_3 = 84237;
		static final int RETALIATE_2 = 89061;
		static final int STAFF_COMMANDS = 114229;
		static final int HOME_TELEPORT_1 = 117048;
		static final int SUBMIT_REPORT = 163046;
		static final int PRICE_CHECKER_DEPOSIT_ALL = 189121;
		static final int PRICE_CHECKER_WITHDRAW_ALL = 189194;
		static final int QUEST_TAB = 194042;
		static final int REST_TOGGLE = 211172;
		static final int DROP_TABLE_DISPLAY = 233110;
	}

	public static void initializeButtonHandlers() {

		BUTTON_HANDLERS.put(ButtonIds.RUN_TOGGLE_1, ClickButtonPacket::toggleRunning);

		BUTTON_HANDLERS.put(ButtonIds.RETALIATE_1, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(ButtonIds.RUN_TOGGLE_2, ClickButtonPacket::toggleRunning);

		BUTTON_HANDLERS.put(ButtonIds.CANCEL_REPORT, ClickButtonPacket::cancelReport);

		BUTTON_HANDLERS.put(2202, stoner -> stoner.send(new SendMessage("You have <col=255>" + Utility.format(stoner.getMoneyPouch()) + " </col>BestBucks on your Debit Card.")));

		BUTTON_HANDLERS.put(2203, ClickButtonPacket::handlePaymentOption);

		BUTTON_HANDLERS.put(ButtonIds.MUSIC_VOLUME_MAX, stoner -> {
			stoner.setMusicVolume((byte) 4);
			stoner.getClient().queueOutgoingPacket(new SendConfig(168, 4));
		});

		BUTTON_HANDLERS.put(ButtonIds.SOUND_VOLUME_MAX, stoner -> {
			stoner.setSoundVolume((byte) 4);
			stoner.getClient().queueOutgoingPacket(new SendConfig(169, 4));
		});

		BUTTON_HANDLERS.put(3189, stoner -> {
			stoner.setSplitPrivateChat((byte) (stoner.getSplitPrivateChat() == 0 ? 1 : 0));
			stoner.getClient().queueOutgoingPacket(new SendConfig(287, stoner.getSplitPrivateChat()));
		});

		BUTTON_HANDLERS.put(ButtonIds.CLOSE_INTERFACE_1, stoner -> stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(ButtonIds.LOGOUT, ClickButtonPacket::handleLogout);

		BUTTON_HANDLERS.put(14067, ClickButtonPacket::updateAppearance);

		BUTTON_HANDLERS.put(15062, stoner -> stoner.send(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(24125, stoner -> stoner.getAttributes().remove("manual"));

		BUTTON_HANDLERS.put(24126, stoner -> stoner.getAttributes().set("manual", (byte) 1));

		BUTTON_HANDLERS.put(ButtonIds.SPECIAL_ATTACK_RESTORE, ClickButtonPacket::handleSpecialAttackRestore);

		BUTTON_HANDLERS.put(ButtonIds.RUN_TOGGLE_3, ClickButtonPacket::toggleRunning);

		BUTTON_HANDLERS.put(52035, null);

		BUTTON_HANDLERS.put(50245, null);

		BUTTON_HANDLERS.put(50253, null);

		BUTTON_HANDLERS.put(51005, null);

		BUTTON_HANDLERS.put(51013, null);

		BUTTON_HANDLERS.put(51023, null);

		BUTTON_HANDLERS.put(55095, ClickButtonPacket::handleWeaponUnload);

		BUTTON_HANDLERS.put(55096, stoner -> stoner.send(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(ButtonIds.COMBAT_FORMULAS, ClickButtonPacket::displayCombatFormulas);

		BUTTON_HANDLERS.put(59100, null);

		BUTTON_HANDLERS.put(ButtonIds.PRICE_CHECKER_OPEN, stoner -> stoner.getPriceChecker().open());

		BUTTON_HANDLERS.put(59206, null);

		BUTTON_HANDLERS.put(66108, null);

		BUTTON_HANDLERS.put(70209, stoner -> stoner.setEnterXInterfaceId(6969));

		BUTTON_HANDLERS.put(ButtonIds.RUN_TOGGLE_4, ClickButtonPacket::toggleRunning);

		BUTTON_HANDLERS.put(75007, BoltEnchanting::open);

		BUTTON_HANDLERS.put(ButtonIds.HOME_TELEPORT_2, ClickButtonPacket::handleHomeTeleport);

		BUTTON_HANDLERS.put(ButtonIds.CLOSE_INTERFACE_2, stoner -> stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(83093, stoner -> stoner.getClient().queueOutgoingPacket(new SendInterface(15106)));

		BUTTON_HANDLERS.put(ButtonIds.HOME_TELEPORT_3, ClickButtonPacket::handleHomeTeleport);

		BUTTON_HANDLERS.put(ButtonIds.RETALIATE_2, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(93202, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(93209, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(93217, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(93225, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(94051, stoner -> stoner.setRetaliate(!stoner.isRetaliate()));

		BUTTON_HANDLERS.put(100228, stoner -> stoner.setMultipleMouseButtons((byte) (stoner.getMultipleMouseButtons() == 0 ? 1 : 0)));

		BUTTON_HANDLERS.put(100231, stoner -> stoner.setChatEffectsEnabled((byte) (stoner.getChatEffectsEnabled() == 0 ? 1 : 0)));

		BUTTON_HANDLERS.put(100237, stoner -> stoner.setAcceptAid((byte) (stoner.getAcceptAid() == 0 ? 1 : 0)));

		BUTTON_HANDLERS.put(108005, stoner -> stoner.getClient().queueOutgoingPacket(new SendInterface(19148)));

		BUTTON_HANDLERS.put(110046, stoner -> stoner.send(new SendMessage(":transparentTab:")));

		BUTTON_HANDLERS.put(110047, stoner -> stoner.send(new SendMessage(":transparentChatbox:")));

		BUTTON_HANDLERS.put(110048, stoner -> stoner.send(new SendMessage(":sideStones:")));

		BUTTON_HANDLERS.put(110245, ClickButtonPacket::saveSettings);

		BUTTON_HANDLERS.put(110248, ClickButtonPacket::resetSettings);

		BUTTON_HANDLERS.put(111024, ClickButtonPacket::toggleAdvanceColors);

		BUTTON_HANDLERS.put(114220, ClickButtonPacket::openAchievementTab);

		BUTTON_HANDLERS.put(114226, stoner -> InterfaceHandler.writeText(new QuestTab(stoner)));

		BUTTON_HANDLERS.put(ButtonIds.STAFF_COMMANDS, ClickButtonPacket::handleStaffCommands);

		BUTTON_HANDLERS.put(115065, ClickButtonPacket::showOnlineStoners);

		BUTTON_HANDLERS.put(115070, ClickButtonPacket::openPointsInterface);

		BUTTON_HANDLERS.put(115071, ClickButtonPacket::openPvmTracker);

		BUTTON_HANDLERS.put(115074, ClickButtonPacket::focusedMageBookSwap);

		BUTTON_HANDLERS.put(115075, ClickButtonPacket::aoeMageBookSwap);

		BUTTON_HANDLERS.put(115076, DropTable::open);

		BUTTON_HANDLERS.put(115077, null);

		BUTTON_HANDLERS.put(115107, stoner -> stoner.start(new OziachDialogue(stoner)));

		BUTTON_HANDLERS.put(115108, JewelryCreationTask::sendInterface);

		BUTTON_HANDLERS.put(115109, HideTanning::sendTanningInterface);

		BUTTON_HANDLERS.put(115110, PotionDecanting::decantAll);

		BUTTON_HANDLERS.put(115114, null);

		BUTTON_HANDLERS.put(115115, stoner -> stoner.start(new GenieResetDialogue(stoner)));

		BUTTON_HANDLERS.put(115116, stoner -> stoner.getAutoCombat().toggle());

		BUTTON_HANDLERS.put(115117, ClickButtonPacket::rechargeResonance);

		BUTTON_HANDLERS.put(115118, ClickButtonPacket::handleSkulling);

		BUTTON_HANDLERS.put(115122, null);

		BUTTON_HANDLERS.put(115123, null);

		BUTTON_HANDLERS.put(115127, null);

		BUTTON_HANDLERS.put(115128, MysteryBoxMinigame::open);

		BUTTON_HANDLERS.put(115129, stoner -> stoner.start(new AchievementDialogue(stoner)));

		BUTTON_HANDLERS.put(ButtonIds.HOME_TELEPORT_1, ClickButtonPacket::handleHomeTeleport);

		BUTTON_HANDLERS.put(121028, ClickButtonPacket::openBestBudzTab);

		BUTTON_HANDLERS.put(127000, stoner -> stoner.send(new SendInterface(5292)));

		BUTTON_HANDLERS.put(140185, stoner -> stoner.send(new SendInterface(28200)));

		BUTTON_HANDLERS.put(140186, ClickButtonPacket::openSettingsInterface);

		BUTTON_HANDLERS.put(140189, ClickButtonPacket::openColorChooser);

		BUTTON_HANDLERS.put(151045, stoner -> stoner.getClient().queueOutgoingPacket(new SendInterface(39700)));

		BUTTON_HANDLERS.put(154052, ClickButtonPacket::showOnlineStoners);

		BUTTON_HANDLERS.put(155026, stoner -> stoner.getClient().queueOutgoingPacket(new SendInterface(38700)));

		BUTTON_HANDLERS.put(ButtonIds.SUBMIT_REPORT, ReportHandler::handleReport);

		BUTTON_HANDLERS.put(184163, stoner -> stoner.send(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(ButtonIds.PRICE_CHECKER_DEPOSIT_ALL, stoner -> stoner.getPriceChecker().depositeAll());

		BUTTON_HANDLERS.put(189124, stoner -> stoner.send(new SendMessage("Dont even ask..")));

		BUTTON_HANDLERS.put(ButtonIds.PRICE_CHECKER_WITHDRAW_ALL, stoner -> stoner.getPriceChecker().withdrawAll());

		BUTTON_HANDLERS.put(190116, stoner -> stoner.send(new SendRemoveInterfaces()));

		BUTTON_HANDLERS.put(ButtonIds.QUEST_TAB, ClickButtonPacket::handleQuestTab);

		BUTTON_HANDLERS.put(195087, stoner -> stoner.send(new SendInterface(32500)));

		BUTTON_HANDLERS.put(201051, stoner -> setProfilePrivacy(stoner, true));

		BUTTON_HANDLERS.put(201052, stoner -> setProfilePrivacy(stoner, false));

		BUTTON_HANDLERS.put(201053, stoner -> setProfilePrivacy(stoner, true));

		BUTTON_HANDLERS.put(201054, stoner -> setProfilePrivacy(stoner, false));

		BUTTON_HANDLERS.put(201055, null);

		BUTTON_HANDLERS.put(201059, null);

		BUTTON_HANDLERS.put(209002, ClickButtonPacket::handleSearchOptions);

		BUTTON_HANDLERS.put(ButtonIds.REST_TOGGLE, stoner -> stoner.getRunEnergy().toggleResting());

		BUTTON_HANDLERS.put(ButtonIds.DROP_TABLE_DISPLAY, stoner -> DropTable.displayNpc(stoner, stoner.monsterSelected));
	}

	public static void initializeShopActions() {
		SHOP_ACTIONS.put(115082, new ClickButtonPacket.ShopAction(0, "General shop"));
		SHOP_ACTIONS.put(115083, new ClickButtonPacket.ShopAction(31, "Packs shop"));
		SHOP_ACTIONS.put(115084, new ClickButtonPacket.ShopAction(17, "Professioning shop"));
		SHOP_ACTIONS.put(115085, new ClickButtonPacket.ShopAction(32, "BankStanding shop"));
		SHOP_ACTIONS.put(115086, new ClickButtonPacket.ShopAction(33, "THC-hempistry shop"));
		SHOP_ACTIONS.put(115087, new ClickButtonPacket.ShopAction(15, "Close combat shop"));
		SHOP_ACTIONS.put(115088, new ClickButtonPacket.ShopAction(16, "Sagittarius's shop"));
		SHOP_ACTIONS.put(115089, new ClickButtonPacket.ShopAction(26, "Mages shop"));
		SHOP_ACTIONS.put(115090, new ClickButtonPacket.ShopAction(27, "Pure shop"));
		SHOP_ACTIONS.put(115092, new ClickButtonPacket.ShopAction(20, "Profession cape shop"));
		SHOP_ACTIONS.put(115093, new ClickButtonPacket.ShopAction(45, "Advance cape shop"));
		SHOP_ACTIONS.put(115097, new ClickButtonPacket.ShopAction(92, "Chill Point shop"));
		SHOP_ACTIONS.put(115098, new ClickButtonPacket.ShopAction(5, "Weed protect point shop"));
		SHOP_ACTIONS.put(115099, new ClickButtonPacket.ShopAction(3, "Graceful shop"));
		SHOP_ACTIONS.put(115100, new ClickButtonPacket.ShopAction(89, "Achievement shop"));
		SHOP_ACTIONS.put(115101, new ClickButtonPacket.ShopAction(93, "Advance shop"));
		SHOP_ACTIONS.put(115102, new ClickButtonPacket.ShopAction(6, "Mercenary shop"));
		SHOP_ACTIONS.put(115103, new ClickButtonPacket.ShopAction(7, "Bounty shop"));

		System.out.println("DEBUG: Initialized " + SHOP_ACTIONS.size() + " shop actions");
		System.out.println("DEBUG: Shop actions for failing buttons:");
	}

	public static void initializeTeleportLocations() {
		TELEPORT_LOCATIONS.put(115132, new Location(3443, 2915));
		TELEPORT_LOCATIONS.put(115133, new Location(2843, 4832));
		TELEPORT_LOCATIONS.put(115134, new Location(2787, 4839));
		TELEPORT_LOCATIONS.put(115135, new Location(2718, 4837));
		TELEPORT_LOCATIONS.put(115136, new Location(2660, 4840));
		TELEPORT_LOCATIONS.put(115137, new Location(2583, 4839));
		TELEPORT_LOCATIONS.put(115138, new Location(2524, 4842));
		TELEPORT_LOCATIONS.put(115139, new Location(2144, 4833));
		TELEPORT_LOCATIONS.put(115140, new Location(2273, 4842));
		TELEPORT_LOCATIONS.put(115141, new Location(2400, 4839));
		TELEPORT_LOCATIONS.put(115142, new Location(2464, 4830));
		TELEPORT_LOCATIONS.put(115143, new Location(2205, 4834));
		TELEPORT_LOCATIONS.put(115144, new Location(3417, 2923, 0));
		TELEPORT_LOCATIONS.put(115145, new Location(3117, 9856, 0));
		TELEPORT_LOCATIONS.put(115146, new Location(3293, 3182, 0));
		TELEPORT_LOCATIONS.put(115147, new Location(3362, 2889, 0));
		TELEPORT_LOCATIONS.put(115148, new Location(2321, 3804, 0));
		TELEPORT_LOCATIONS.put(115149, new Location(2710, 9466, 0));
		TELEPORT_LOCATIONS.put(115150, new Location(2884, 9798, 0));
		TELEPORT_LOCATIONS.put(115151, new Location(3428, 3538, 0));
		TELEPORT_LOCATIONS.put(115152, new Location(3202, 3860, 0));
		TELEPORT_LOCATIONS.put(115153, new Location(1747, 5324, 0));
		TELEPORT_LOCATIONS.put(115154, new Location(3184, 3947, 0));
		TELEPORT_LOCATIONS.put(115155, new Location(3047, 4976, 1));
		TELEPORT_LOCATIONS.put(115156, new Location(2747, 3444, 0));
		TELEPORT_LOCATIONS.put(115157, new Location(3044, 9785, 0));
		TELEPORT_LOCATIONS.put(115158, new Location(3186, 3425, 0));
		TELEPORT_LOCATIONS.put(115159, new Location(2840, 3437, 0));
		TELEPORT_LOCATIONS.put(115160, new Location(2722, 3473, 0));
		TELEPORT_LOCATIONS.put(115161, new Location(2806, 3463, 0));
		TELEPORT_LOCATIONS.put(115162, new Location(3087, 3515, 0));
		TELEPORT_LOCATIONS.put(115163, new Location(3244, 3512, 0));
		TELEPORT_LOCATIONS.put(115164, new Location(3333, 3666, 0));
		TELEPORT_LOCATIONS.put(115165, new Location(3002, 3626, 0));
		TELEPORT_LOCATIONS.put(115166, new Location(2540, 4717, 0));
		TELEPORT_LOCATIONS.put(115167, new Location(2997, 3849, 0));
		TELEPORT_LOCATIONS.put(115168, new Location(2336, 3692, 0));
		TELEPORT_LOCATIONS.put(115169, new Location(3806, 2844, 0));
		TELEPORT_LOCATIONS.put(115170, new Location(2948, 4385, 2));
		TELEPORT_LOCATIONS.put(115171, new Location(1909, 4367, 0));
		TELEPORT_LOCATIONS.put(115172, new Location(2882, 5308, 2));
		TELEPORT_LOCATIONS.put(115173, new Location(2268, 3070, 0));
		TELEPORT_LOCATIONS.put(115174, new Location(3696, 5807, 0));
		TELEPORT_LOCATIONS.put(115175, new Location(1760, 5163, 0));
		TELEPORT_LOCATIONS.put(115176, new Location(3284, 3913, 0));
		TELEPORT_LOCATIONS.put(115177, new Location(3283, 3853, 0));
		TELEPORT_LOCATIONS.put(115178, new Location(3233, 3943, 0));
		TELEPORT_LOCATIONS.put(115179, new Location(3210, 3780, 0));
		TELEPORT_LOCATIONS.put(115180, new Location(2981, 3837, 0));
		TELEPORT_LOCATIONS.put(115181, new Location(2975, 3715, 0));
		TELEPORT_LOCATIONS.put(115182, new Location(3565, 3315, 0));
		TELEPORT_LOCATIONS.put(115183, new Location(2869, 3544, 0));
		TELEPORT_LOCATIONS.put(115184, new Location(3365, 3265, 0));
		TELEPORT_LOCATIONS.put(115185, new Location(2662, 2655, 0));
		TELEPORT_LOCATIONS.put(115186, new Location(2439, 5171, 0));
		TELEPORT_LOCATIONS.put(115187, WeaponGameConstants.LOBBY_COODINATES);

		TELEPORT_LOCATIONS.put(115189, StonerConstants.MEMEBER_AREA);
		TELEPORT_LOCATIONS.put(115190, StonerConstants.STAFF_AREA);
		TELEPORT_LOCATIONS.put(115191, new Location(3039, 4834, 0));
		TELEPORT_LOCATIONS.put(115192, new Location(2923, 4819, 0));
	}

	public static void initializeLeaderboardTypes() {
		LEADERBOARD_TYPES.put(185046, "Look at man");
		LEADERBOARD_TYPES.put(185049, "A good man");
		LEADERBOARD_TYPES.put(185052, "A bad man");
		LEADERBOARD_TYPES.put(185055, "What kind of man");
	}

	public enum Emote {
		Professioncape(154, 1, 1),
		Yes(168, 855, -1),
		No(169, 856, -1),
		Bow(164, 858, -1),
		Think(162, 857, -1),
		Wave(163, 863, -1),
		Angry(167, 864, -1),
		Cheer(171, 862, -1),
		Beckon(165, 859, -1),
		Cry(161, 860, -1),
		Laugh(170, 861, -1),
		Clap(172, 865, -1),
		Dance(166, 866, -1),
		Shrug(115206, 2113, -1),
		Jump_For_Joy(115207, 2109, -1),
		Yawn(115208, 2111, -1),
		Jig(115209, 2106, -1),
		Twirl(115210, 2107, -1),
		Headbang(115211, 2108, -1),
		Blow_Kiss(115212, 1368, -1),
		Panic(115213, 2105, -1),
		Rasberry(115214, 2110, -1),
		Salute(115215, 2112, -1),
		Goblin_Bow(115216, 2127, -1),
		Goblin_Salute(115217, 2128, -1),
		Glass_Box(115218, 1131, -1),
		Climb_Rope(115219, 1130, -1),
		Lean(115220, 1129, -1),
		Glass_Wall(115221, 1128, -1),
		Idea(115222, 4276, 712),
		Stomp(115223, 4278, -1),
		Flap(115224, 4280, -1),
		Slap_Head(115225, 4275, -1),
		Zombie_Walk(115226, 3544, -1),
		Zombie_Dance(115227, 3543, -1),
		Scared(115228, 2836, -1),
		Bunny_Hop(115229, 6111, -1),
		CHICKEN(115193, 1835, -1),
		DRUNK(115194, 2770, -1),
		CHILL(115195, 2339, -1),
		JUMPING_JACKS(115196, 2761, -1),
		PUSHUP(115197, 2762, -1),
		SITUP(115198, 2763, -1),
		JOGGING(115199, 2764, -1),
		HURT_FOOT(115200, 779, -1),
		ELEGANT_BOW(115201, 5312, -1),
		ADVANCED_YAWN(115202, 5313, -1),
		POWDERED_ANGRY(115203, 5315, -1),
		FLARED_DANCE(115204, 5316, -1),
		WRITE(115205, 909, -1),
		SMOKE(115231, 884, 354),
		PREACH(115232, 1670, -1),
		FRUSTRATION_KICK(115233, 1746, -1),
		MATRIX(115234, 1110, -1),
		ZOMBIE_HEAD(115235, 2840, -1),
		YO_YO(115236, 1457, -1),
		RESPECT(115237, 1818, -1),
		JUMP(115238, 3067, -1),
		EXCITED_JUMP(115239, 6382, -1),
		THINK_HARD(115240, 6380, -1),
		WATERING(115241, 2293, -1),
		POUR_POTION(115242, 2288, -1),
		RAKE(115243, 2273, -1),
		DIG(115244, 2272, -1),
		CO_OP_HANDSHAKE(115245, 2270, -1),
		BELLY_FLOP(115246, 1115, -1);

		public final int gfxID;
		public final int animID;
		public final int buttonID;

		Emote(int buttonId, int animId, int gfxId) {
			buttonID = buttonId;
			animID = animId;
			gfxID = gfxId;
		}

	}

}
