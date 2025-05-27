package com.bestbudz.rs2.content.moderation;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.Hit.HitTypes;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.content.io.StonerSave.StonerContainer;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBox;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEquipment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class StaffTab {

	private static Stoner target = null;

	public static boolean inputField(Stoner stoner, int id, String text) {
	switch (id) {
	case 49720:
		StaffTab.handle(stoner, text, PunishmentType.CHECK_BANK, 1, 2, 3, 4);
		return true;
	case 49721:
		StaffTab.handle(stoner, text, PunishmentType.KICK, 1, 2, 3, 4);
		return true;
	case 49722:
		StaffTab.handle(stoner, text, PunishmentType.MUTE, 1, 2, 3, 4);
		return true;
	case 49723:
		StaffTab.handle(stoner, text, PunishmentType.UNMUTE, 1, 2, 3, 4);
		return true;
	case 49724:
		StaffTab.handle(stoner, text, PunishmentType.BAN, 1, 2, 3, 4);
		return true;
	case 49725:
		StaffTab.handle(stoner, text, PunishmentType.UNBAN, 1, 2, 3, 4);
		return true;
	case 49726:
		StaffTab.handle(stoner, text, PunishmentType.JAIL, 1, 2, 3, 4);
		return true;
	case 49727:
		StaffTab.handle(stoner, text, PunishmentType.UNJAIL, 1, 2, 3, 4);
		return true;
	case 49728:
		StaffTab.handle(stoner, text, PunishmentType.MOVE_HOME, 1, 2, 3, 4);
		return true;
	case 49729:
		StaffTab.handle(stoner, text, PunishmentType.COPY, 2, 3, 4);
		return true;
	case 49730:
		StaffTab.handle(stoner, text, PunishmentType.FREEZE, 2, 3, 4);
		return true;
	case 49731:
		StaffTab.handle(stoner, text, PunishmentType.INFO, 2, 3, 4);
		return true;
	case 49732:
		StaffTab.handle(stoner, text, PunishmentType.DEMOTE, 2, 3, 4);
		return true;
	case 49733:
		StaffTab.handle(stoner, text, PunishmentType.GIVE_MODERATOR, 2, 3, 4);
		return true;
	case 49734:
		StaffTab.handle(stoner, text, PunishmentType.KILL, 3, 4);
		return true;
	case 49735:
		StaffTab.handle(stoner, text, PunishmentType.TELETO, 1, 2, 3, 4);
		return true;
	case 49736:
		StaffTab.handle(stoner, text, PunishmentType.TELETOME, 1, 2, 3, 4);
		return true;
	case 49737:
		StaffTab.handle(stoner, text, PunishmentType.BOO, 3, 4);
		return true;
	case 49738:
		StaffTab.handle(stoner, text, PunishmentType.RANDOM_NPC, 3, 4);
		return true;
	case 49739:
		StaffTab.handle(stoner, text, PunishmentType.REFRESH, 2, 3, 4);
		return true;

	}
	return false;
	}

	public static boolean createTarget(Stoner stoner, String name, int... rights) {
	target = World.getStonerByName(name);

	if (target == null) {
		target = new Stoner();
		target.setUsername(name);
		try {
			if (!StonerContainer.loadDetails(target)) {
				stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] Stoner <col=255>" + name + "</col> does not exist!"));
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	if (StonerConstants.isHighClass(target) && !stoner.getUsername().equalsIgnoreCase("jaybane")) {
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You may not punish <col=255>" + name + "</col>!"));
		return false;
	}

	boolean access = false;
	for (int i = 0; i < rights.length; i++) {
		if (stoner.getRights() == rights[i]) {
			access = true;
			break;
		}
	}

	if (!access) {
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You do not have access to this!"));
		return false;
	}

	return true;
	}

	public static void handle(Stoner stoner, String user, PunishmentType punishment, int... rights) {
	if (!createTarget(stoner, user, rights)) {
		return;
	}

	String name = Utility.formatStonerName(user);

	switch (punishment) {

	case CHECK_BANK:
		stoner.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
		stoner.send(new SendUpdateItems(5064, target.getBox().getItems()));
		stoner.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
		stoner.send(new SendBox(target.getBox().getItems()));
		stoner.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
		stoner.send(new SendBoxInterface(5292, 5063));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You are now viewing <col=255>" + name + "</col>'s bank!"));
		break;

	case KICK:
		target.logout(true);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully kicked <col=255>" + name + "</col>!"));
		break;

	case MUTE:
		target.setMuted(true);
		target.setMuteLength(System.currentTimeMillis() + 3_600_000);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been muted for 1 hour!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully muted <col=255>" + name + "</col> for 1 hour!"));
		break;

	case UNMUTE:
		target.setMuted(false);
		target.setMuteLength(0);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been unmuted!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully unmuted <col=255>" + name + "</col>!"));
		break;

	case BAN:
		target.setBanned(true);
		target.setBanLength(System.currentTimeMillis() + 3_600_000);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been banned for 1 hour!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully banned <col=255>" + name + "</col> for 1 hour!"));
		break;

	case UNBAN:
		target.setBanned(false);
		target.setBanLength(0);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been unbanned!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully unbanned <col=255>" + name + "</col>!"));
		break;

	case JAIL:
		target.setJailed(true);
		target.setJailLength(System.currentTimeMillis() + 3_600_000);
		target.teleport(StonerConstants.JAILED_AREA);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been jailed for 1 hour!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully jailed <col=255>" + name + "</col> for 1 hour!"));
		break;

	case UNJAIL:
		target.setJailed(false);
		target.setJailLength(0);
		target.teleport(StonerConstants.HOME);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been unjailed!"));
		StonerSave.save(target);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully unjailed <col=255>" + name + "</col>!"));
		break;

	case MOVE_HOME:
		target.teleport(StonerConstants.HOME);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been moved home!"));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully moved <col=255>" + name + "</col>!"));
		break;

	case COPY:
		stoner.getBank().setItems(target.getBank().getItems());
		stoner.getBank().setTabAmounts(target.getBank().getTabAmounts());
		stoner.send(new SendUpdateItems(5064, target.getBox().getItems()));
		stoner.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
		stoner.send(new SendBox(target.getBox().getItems()));
		stoner.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
		stoner.send(new SendBoxInterface(5292, 5063));
		stoner.getBox().clear();

		for (int index = 0; index < target.getEquipment().getItems().length; index++) {
			if (target.getEquipment().getItems()[index] == null) {
				continue;
			}
			stoner.getEquipment().getItems()[index] = new Item(target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount());
			stoner.send(new SendEquipment(index, target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount()));
		}

		for (int index = 0; index < target.getBox().getItems().length; index++) {
			if (target.getBox().items[index] == null) {
				continue;
			}
			stoner.getBox().items[index] = target.getBox().items[index];
		}

		stoner.getBox().update();
		stoner.setAppearanceUpdateRequired(true);
		stoner.getCombat().reset();
		stoner.getEquipment().calculateBonuses();
		stoner.getUpdateFlags().setUpdateRequired(true);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully copied <col=255>" + name + "</col>!"));
		break;

	case FREEZE:
		target.freeze(10, 5);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have successfully froze <col=255>" + name + "</col>!"));
		break;

	case INFO:
		for (int i = 0; i < 50; i++) {
			stoner.send(new SendString("", 8144 + i));
		}
		stoner.send(new SendString("Information Viewer", 8144));
		stoner.send(new SendString("@dre@Username:", 8145));
		stoner.send(new SendString(target.getUsername(), 8146));
		stoner.send(new SendString("@dre@Password:", 8147));
		stoner.send(new SendString(stoner.getRights() == 1 ? "Hidden" : target.getPassword(), 8148));
		stoner.send(new SendString("@dre@IP Address:", 8149));
		stoner.send(new SendString(target.getClient().getHost(), 8150));

		stoner.send(new SendInterface(8134));
		stoner.send(new SendMessage("You are now vieiwing " + target.getUsername() + "'s account details."));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You are now viewing <col=255>" + name + "</col>'s account info!"));
		break;

	case DEMOTE:
		target.setRights(0);
		break;

	case GIVE_MODERATOR:
		target.setRights(1);
		target.getUpdateFlags().setUpdateRequired(true);
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been given moderator status by " + stoner.getUsername() + "!"));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have given <col=255>" + name + "</col> moderator status."));
		break;

	case KILL:
		target.hit(new Hit(target.getProfession().getGrades()[3], HitTypes.DISEASE));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have killed <col=255>" + name + "</col>!"));
		break;

	case TELETO:
		stoner.teleport(target.getLocation());
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have teleported to <col=255>" + name + "</col>!"));
		break;

	case TELETOME:
		target.teleport(stoner.getLocation());
		target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been teleported to <col=255>" + stoner.getUsername() + "</col>!"));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have teleported <col=255>" + name + "</col> to your location!"));
		break;

	case BOO:
		target.send(new SendInterface(18681));
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have boo'd <col=255>" + name + "</col>!"));
		break;

	case RANDOM_NPC:
		short randomNPC = (short) Utility.random(GameDefinitionLoader.getNpcDefinitions().values().size());
		target.setNpcAppearanceId(randomNPC);
		target.setAppearanceUpdateRequired(true);
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(randomNPC);
		if (npcDef == null && randomNPC != -1) {
			stoner.send(new SendMessage("The npc id (" + randomNPC + ") does not exist."));
			return;
		}
		target.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
		target.getAnimations().setRunEmote(npcDef.getWalkAnimation());
		target.getAnimations().setStandEmote(npcDef.getStandAnimation());
		target.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
		target.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
		target.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have transformed <col=255>" + name + "</col> into NPC <col=255>" + npcDef.getName() + " </col>(<col=255>" + npcDef.getId() + "</col>)!"));
		break;

	case REFRESH:
		target.getAnimations().setWalkEmote(819);
		target.getAnimations().setRunEmote(824);
		target.getAnimations().setStandEmote(808);
		target.getAnimations().setTurn180Emote(820);
		target.getAnimations().setTurn90CCWEmote(822);
		target.getAnimations().setTurn90CWEmote(821);
		stoner.send(new SendMessage("[ <col=255>BestBudz</col> ] You have refreshed <col=255>" + name + "</col>!"));
		break;

	default:
		System.out.println("ERROR STAFF TAB");
		break;

	}
	}

	public enum PunishmentType {
		CHECK_BANK,
		KICK,
		MUTE,
		UNMUTE,
		BAN,
		UNBAN,
		JAIL,
		UNJAIL,
		MOVE_HOME,
		COPY,
		FREEZE,
		INFO,
		DEMOTE,
		GIVE_MODERATOR,
		KILL,
		TELETO,
		TELETOME,
		BOO,
		RANDOM_NPC,
		REFRESH

	}

}
