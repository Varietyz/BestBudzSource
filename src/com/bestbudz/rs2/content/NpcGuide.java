package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDropDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNpcDisplay;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

/**
 * A helper class for instantiating the Npc Guide interface.
 * 
 * @author Jaybane
 */
public class NpcGuide {

	/**
	 * The integer configuration representing the interface.
	 */
	private static final int MONSTER_GUIDE_INTERFACE_ID = 59800;

	/**
	 * The starting index for the Item container in the interface.
	 */
	private static final int INTERFACE_ITEM_CONTAINER = 59806;

	/**
	 * The integer configuration for the title's string placement.
	 */
	private static final int INTERFACE_TITLE_ID = 59805;

	/**
	 * The starting index for the String container in the interface.
	 */
	private static final int INTERFACE_STRING_CONTAINER = 59821;

	/**
	 * Validates the drop table for the specified npc id and returns a generated
	 * interface based on the results.
	 * 
	 * @param stoner
	 *                   The stoner to send the interface to.
	 * @param npcId
	 *                   The npc id.
	 */
	public static void open(Stoner stoner, final int npcId) {

	NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

	if (npcDef == null) {
		stoner.send(new SendMessage("@red@The ID " + Utility.format(npcId) + " does not exist in our database! Please check the NPC list."));
		return;
	}

	ItemDropDefinition table = GameDefinitionLoader.getItemDropDefinition(npcId);
	if (table == null) {
		clear(stoner);

		stoner.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
		stoner.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
		stoner.send(new SendString("@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));
		for (int i = 0; i <= 3; i++) {
			stoner.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
		}
		return;
	}

	Item[] drops = table.getMostExpensiveDrops(8);

	if (drops == null) {
		DialogueManager.sendStatement(stoner, "Comparable returned null array.");
		return;
	}

	clear(stoner);

	stoner.send(new SendInterface(MONSTER_GUIDE_INTERFACE_ID));
	stoner.send(new SendNpcDisplay(npcId, npcDef.getSize() > 1 ? 40 : 100));
	stoner.send(new SendString("@or1@Monster Information | @gre@" + npcDef.getName(), INTERFACE_TITLE_ID));

	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, drops));

	for (int i = 0; i <= 3; i++) {
		stoner.send(new SendString(getInfo(npcDef, i), INTERFACE_STRING_CONTAINER + i));
	}

	}

	/**
	 * Sends configurations to the client to erase the interface.
	 * 
	 * @param stoner
	 *                   The stoner that the configurations are being sent to.
	 */
	private static void clear(Stoner stoner) {
	// stoner.getPacketBuilder().sendMobHeadModel(0, INTERFACE_NPC_HEAD_ID);
	for (int loop = 0; loop < 3; loop++) {
		stoner.send(new SendString("", INTERFACE_STRING_CONTAINER + loop));
	}
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(INTERFACE_ITEM_CONTAINER, null));
	}

	/**
	 * Generates a string based on the {@link NpcDefinition} supplied.
	 * 
	 * @param npcDef
	 *                   The {@link NpcDefinition}.
	 * @param index
	 *                   The index identifier which decides which string to return.
	 * @return The generated string.
	 */
	private static String getInfo(NpcDefinition npcDef, int index) {
	switch (index) {
	case 0:
		return "@or1@".concat(npcDef.getName() + ":");
	case 1:
		return "@or1@ID: @gre@".concat(String.valueOf(npcDef.getId()));
	case 2:
		return "@or1@Grade: @gre@".concat(String.valueOf(npcDef.getGrade()));
	case 3:
		return "@or1@Can assault: @gre@".concat(String.valueOf(npcDef.isAssaultable()));
	default:
		return "Error";
	}
	}
}