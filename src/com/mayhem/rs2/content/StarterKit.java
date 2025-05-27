package com.mayhem.rs2.content;

import java.util.HashMap;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.impl.PlayerModeDialogue;
import com.mayhem.rs2.content.io.PlayerSaveUtil;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendColor;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.entity.player.net.out.impl.SendUpdateItemsAlt;

public class StarterKit {
	
	private static int selected = 202051;
	
	public enum StarterData {
		
		NORMAL(202051, 51766, 0, new Item[] { new Item(995, 500000), new Item(386, 75), new Item(2437, 10), new Item(2441, 10), new Item(2443, 10), new Item(2445, 10), new Item(3041, 10), new Item(3025, 10), new Item(6603, 1), new Item(9705, 1), new Item(9706, 500), new Item(10025, 1), new Item(6828, 1), new Item(20703, 1) }, "Play FalconExpress as a Regular Player, Extreme Player or Classic Player.", "You will have access to everything available.", "Extreme & Classic mode has bonus drop rates but smaller xp rates."),
		IRON_MAN(202052, 51767, 11, new Item[] { new Item(1351, 1), new Item(590, 1), new Item(303, 1), new Item(315, 1), new Item(1925, 1), new Item(1931, 1), new Item(2309, 1), new Item(1265, 1), new Item(1205, 1), new Item(1277, 1), new Item(1171, 1), new Item(841, 1), new Item(882, 25), new Item(556, 25), new Item(558, 15), new Item(555, 6), new Item(557, 4), new Item(559, 2), new Item(12810, 1), new Item(12811, 1), new Item(12812, 1), new Item(20703) }, "Play FalconExpress as an Iron Man.", "An Iron Man does not recieve items or assistance from other players. They cannot trade,", "stake, recieve PK loot, scavenge dropped items, nor play certain multiplayer minigames."),
		ULTIMATE_IRON_MAN(202053, 51768, 12, new Item[] {	new Item(1351, 1), new Item(590, 1), new Item(303, 1), new Item(315, 1), new Item(1925, 1), new Item(1931, 1), new Item(2309, 1), new Item(1265, 1), new Item(1205, 1), new Item(1277, 1), new Item(1171, 1), new Item(841, 1), new Item(882, 25), new Item(556, 25), new Item(558, 15), new Item(555, 6), new Item(557, 4), new Item(559, 2), new Item(12813, 1), new Item(12814, 1), new Item(12815, 1), new Item(20703) }, "Play FalconExpress as an Ultimate Iron Man. ", "In addition to the standard Iron Man rules, an", "Ultimate Iron Man cannot use banks, nor retain any items on death in dangerous areas."),
		HARDCORE_IRON_MAN(202058, 51772, 11, new Item[] { new Item(1351, 1), new Item(590, 1), new Item(303, 1), new Item(315, 1), new Item(1925, 1), new Item(1931, 1), new Item(2309, 1), new Item(1265, 1), new Item(1205, 1), new Item(1277, 1), new Item(1171, 1), new Item(841, 1), new Item(882, 25), new Item(556, 25), new Item(558, 15), new Item(555, 6), new Item(557, 4), new Item(559, 2), new Item(12810, 1), new Item(12811, 1), new Item(12812, 1), new Item(20703) }, "Play FalconExpress as a Hardcore Iron Man. YOU HAVE 1 LIFE", "An Iron Man does not recieve items or assistance from other players. They cannot trade,", "stake, recieve PK loot, scavenge dropped items, nor play certain multiplayer minigames.");
	
		private final int button;
		private final int stringId;
		private final int rights;
		private final Item items[];
		private final String[] descriptions;
		private StarterData(int button, int stringId, int rights, Item[] items, String... descriptions) {
			this.button = button;
			this.stringId = stringId;
			this.rights = rights;
			this.items = items;
			this.descriptions = descriptions;
		}
		
		public int getButton() {
			return button;
		}
		
		public int getString() {
			return stringId;
		}
		
		public int getRights() {
			return rights;
		}
		
		public Item[] getItems() {
			return items;
		}
		
		public String[] getDescription() {
			return descriptions;
		}
		
		private static HashMap<Integer, StarterData> starterKits = new HashMap<Integer, StarterData>();

		static {
			for (final StarterData starters : StarterData.values()) {
				StarterData.starterKits.put(starters.button, starters);
			}
		}
	}
	
	public static boolean handle(Player player, int buttonId) {
	
		StarterData data = StarterData.starterKits.get(buttonId);

		if (data == null) {
			if (extraButton(player, buttonId)) {
				return false;
			}
			return false;
		}
		
		int color = 0xBA640D;
		
		if (buttonId == 202051) {
			player.send(new SendColor(51766, color));
			player.send(new SendColor(51767, 0xF7AA25));
			player.send(new SendColor(51768, 0xF7AA25));
			player.send(new SendColor(51773, 0xF7AA25));	
		} else if (buttonId == 202052) {
			player.send(new SendColor(51766, 0xF7AA25));
			player.send(new SendColor(51767, color));
			player.send(new SendColor(51768, 0xF7AA25));
			player.send(new SendColor(51773, 0xF7AA25));	
		} else if (buttonId == 202053) {
			player.send(new SendColor(51766, 0xF7AA25));
			player.send(new SendColor(51767, 0xF7AA25));
			player.send(new SendColor(51768, color));
			player.send(new SendColor(51773, 0xF7AA25));	
		} else if (buttonId == 202058) {
			player.send(new SendColor(51766, 0xF7AA25));
			player.send(new SendColor(51767, 0xF7AA25));
			player.send(new SendColor(51768, 0xF7AA25));
			player.send(new SendColor(51773, color));	
		}
		
		selected = buttonId;
		player.send(new SendColor(data.getString(), 0xC71C1C));
		String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
		player.send(new SendString("Starter Items (@red@" + name + "</col>):", 51757));
		
		for (int i = 0; i < 30; i++) {
			player.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(51758, 0, 0, i));
		}
		
		for (int i = 0; i < data.getItems().length; i++) {
			player.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(51758, data.getItems()[i].getId(), data.getItems()[i].getAmount(), i));
		}
		
		for (int i = 0; i < data.getDescription().length; i++) {
			player.send(new SendString(data.getDescription()[i], 51760 + i));
		}
		
		return true;
	
	}
	
	public static boolean extraButton(Player player, int button) {
		if (player.getInterfaceManager().main != 51750) {
			return false;
		}
		switch (button) {
			case 202054:
				player.send(new SendConfig(1085, 0));
				StarterKit.handle(player, 202051);
				return true;
			case 202055:
				player.send(new SendConfig(1085, 1));
				StarterKit.handle(player, 202052);
				return true;
			case 202056:
				player.send(new SendConfig(1085, 2));
				StarterKit.handle(player, 202053);
				return true;
			case 202057:
				StarterKit.confirm(player);
				return true;
			case 202059:
				player.send(new SendConfig(1085, 3));
				StarterKit.handle(player, 202058);
				return true;
			}
		return false;
	
	}
	
	public static void confirm(Player player) {
	
		StarterData data = StarterData.starterKits.get(selected);

		if (data == null || player.getDelay().elapsed() < 1000) {
			return;
		}
		
		if (player.getInterfaceManager().main != 51750) {
			player.send(new SendRemoveInterfaces());
			return;
		}
		
		player.getDelay().reset();
		String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
		player.send(new SendRemoveInterfaces());
		player.send(new SendMessage("@red@You will now be playing as " + Utility.getAOrAn(name) + " " + name + " player."));
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		player.setStarter(false);
		player.send(new SendInterface(3559));
		player.setWantTrivia(true);
		
		for (int i = 0; i < PlayerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
			player.send(new SendSidebarInterface(i, PlayerConstants.SIDEBAR_INTERFACE_IDS[i]));
		}
	
		player.send(new SendSidebarInterface(5, 5608));
		player.send(new SendSidebarInterface(6, 1151));
		player.setRights(data.getRights());
		player.getUpdateFlags().setUpdateRequired(true);
	
		switch (selected) {
		
		case 202051:
			if (!PlayerSaveUtil.hasReceived2Starters(player) || player.getLastLoginYear() != 0) {
			}
			player.setRights(0);
			player.setIron(false);
			player.setUltimateIron(false);
			player.setHardcoreIron(false);
			PlayerSaveUtil.setReceivedStarter(player);
			player.getInventory().addItems(data.getItems());
			player.start(new PlayerModeDialogue(player));
			break;
		
		case 202052:
			player.setIron(true);
			player.getInventory().addItems(data.getItems());
			player.start(new PlayerModeDialogue(player));
			break;
			
		case 202053:
			player.setUltimateIron(true);
			player.getInventory().addItems(data.getItems());
			player.start(new PlayerModeDialogue(player));
			break;
			
		case 202058:
			player.setHardcoreIron(true);
			player.getInventory().addItems(data.getItems());
			player.start(new PlayerModeDialogue(player));
			break;
			
		}
	}

}
